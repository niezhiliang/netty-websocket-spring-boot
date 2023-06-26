package com.niezhiliang.netty.websocket.spring.boot.demo.service;

import com.niezhiliang.netty.websocket.starter.annotations.*;
import com.niezhiliang.netty.websocket.starter.socket.Session;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@WsServerEndpoint(value = "/socketServer/{userid}")
@Component
@Slf4j
public class SocketServer {

	private static ConcurrentMap<String,Session> sessionPool = new ConcurrentHashMap<>();
	private static ConcurrentMap<String,String> sessionIds = new ConcurrentHashMap<>();

	private static final String ADMIN = "niezhiliang9595";

	@HandshakeBefore
	public void handshakeBefore(HttpHeaders headers,@PathParam String userid) {
		log.info("handshakeBefore userId: {}  host: {}", userid, headers.get("HOST"));
	}

	/**
	 * 用户连接时触发
	 * @param session
	 * @param userid
	 */
	@OnOpen
	public void open(Session session,@PathParam(value="userid") String userid){
		log.info("client【{}】连接成功",userid);
		sessionPool.put(userid, session);
		sessionIds.put(session.getId(), userid);
	}

	/**
	 * 收到信息时触发
	 * @param message
	 */
	@OnMessage
	public void onMessage(Session session,String message){
		String msg = String.format("%s  <-- %s",sessionIds.get(session.getId()),message);
		sendMessage(msg,ADMIN);
		log.info("client {} send to ADMIN  message : {} ",sessionIds.get(session.getId()),message);
	}

	/**
	 * 连接关闭触发
	 */
	@OnClose
	public void onClose(Session session,@PathParam String userid){
		sessionPool.remove(sessionIds.get(session.getId()));
		sessionIds.remove(session.getId());
		log.info("client【{}】断开连接",userid);
	}

	/**
	 * 发生错误时触发
	 * @param session
	 * @param error
	 */
	@OnError
	public void onError(Session session, Throwable error) {
		error.printStackTrace();
	}

	/**
	 * 发生事件时触发
	 * @param session
	 * @param evt
	 */
	@OnEvent
	public void onEvent(Session session,@PathParam String userid, Object evt) {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
			switch (idleStateEvent.state()) {
				case READER_IDLE:
					log.info("clent : {} heartbeat read timeout event",userid);
					break;
				case WRITER_IDLE:
					log.info("clent : {} heartbeat write timeout event",userid);
					session.close();
					break;
				case ALL_IDLE:
					log.info("clent : {} heartbeat all timeout event",userid);
					break;
				default:
					break;
			}
		}
	}

	/**
	 *信息发送的方法
	 * @param message
	 * @param userId
	 */
	public static void sendMessage(String message,String userId){
		Session s = sessionPool.get(userId);
		if(s!=null){
			s.sendText(message);
		}
	}

	/**
	 * 获取当前连接数
	 * @return
	 */
	public static int getOnlineNum(){
		if(sessionIds.values().contains(ADMIN)) {

			return sessionPool.size()-1;
		}
		return sessionPool.size();
	}

	/**
	 * 获取在线用户名以逗号隔开
	 * @return
	 */
	public static String getOnlineUsers(){
		StringBuffer users = new StringBuffer();
		for (String key : sessionIds.keySet()) {//ADMIN是服务端自己的连接，不能算在线人数
			if (!ADMIN.equals(sessionIds.get(key))) {
				users.append(sessionIds.get(key)+",");
			}
		}
		return users.toString();
	}

	/**
	 * 信息群发
	 * @param msg
	 */
	public static void sendAll(String msg) {
		for (String key : sessionIds.keySet()) {
			if (!ADMIN.equals(sessionIds.get(key))) {
				sendMessage(msg, sessionIds.get(key));
			}
		}
	}

	/**
	 * 多个人发送给指定的几个用户
	 * @param msg
	 * @param persons  用户s
	 */

	public synchronized static void SendMany(String msg,String [] persons) {
		for (String userid : persons) {
			sendMessage(msg, userid);
		}

	}
}
