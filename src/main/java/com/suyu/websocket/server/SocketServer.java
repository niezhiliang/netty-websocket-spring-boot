package com.suyu.websocket.server;

import com.suyu.websocket.entity.Client;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

@ServerEndpoint(value = "/socketServer/{userName}")
@Component
public class SocketServer {

	private static CopyOnWriteArraySet<Client> socketServers = new CopyOnWriteArraySet<>();

	private Session session;

	private final static String SYS_USERNAME = "niezhiliang9595";


	/**
	 * 用户连接时触发
	 * @param session
	 */
	@OnOpen
	public void open(Session session,@PathParam(value="userName")String userName){
		this.session = session;
		socketServers.add(new Client(userName,session));
	}

	/**
	 * 收到信息时触发
	 * @param message
	 */
	@OnMessage
	public void onMessage(String message){
		Client client = socketServers.stream().filter( cli -> cli.getSession() == session)
				.collect(Collectors.toList()).get(0);
		sendMessage(client.getUserName()+"<--"+message,SYS_USERNAME);
		System.out.println("发送人:"+client.getUserName()+"内容:"+message);
	}

	/**
	 * 连接关闭触发
	 */
	@OnClose
	public void onClose(){
		socketServers.forEach(client ->{
			if (client.getSession().getId().equals(session.getId())) {
				socketServers.remove(client);
			}
		});
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
	 *信息发送的方法
	 * @param message
	 * @param userName
	 */
	public synchronized static void sendMessage(String message,String userName) {

		socketServers.forEach(client ->{
			if (userName.equals(client.getUserName())) {
				try {
					client.getSession().getBasicRemote().sendText(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 获取当前连接数
	 * @return
	 */
	public synchronized static int getOnlineNum(){
		//避免服务端第一次加载会出现在线人数-1的情况
		return socketServers.size()-1 > 0 ? socketServers.size()-1 : 0;
	}

	/**
	 * 获取在线用户名以逗号隔开
	 * @return
	 */
	public synchronized static List<String> getOnlineUsers(){

		List<String> onlineUsers = socketServers.stream()
				.filter(client -> !client.getUserName().equals(SYS_USERNAME))
				.map(client -> client.getUserName())
				.collect(Collectors.toList());

	    return onlineUsers;
	}

	/**
	 * 信息群发
	 * @param message
	 */
	public synchronized static void sendAll(String message) {
		//群发，不能发送给服务端自己
		socketServers.stream().filter(cli -> cli.getUserName() != SYS_USERNAME)
				.forEach(client -> {
			try {
				client.getSession().getBasicRemote().sendText(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * 多个人发送给指定的几个用户
	 * @param message
	 * @param persons  用户s
	 */
	public synchronized static void SendMany(String message,String [] persons) {
		for (String userName : persons) {
			sendMessage(message,userName);
		}
	}
}
