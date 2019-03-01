package com.suyu.websocket.server;

import com.suyu.websocket.entity.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

	/**
	 *
	 * 用线程安全的CopyOnWriteArraySet来存放客户端连接的信息
	 */
	private static CopyOnWriteArraySet<Client> socketServers = new CopyOnWriteArraySet<>();

	/**
	 *
	 * websocket封装的session,信息推送，就是通过它来信息推送
	 */
	private Session session;

	/**
	 *
	 * 服务端的userName,因为用的是set，每个客户端的username必须不一样，否则会被覆盖。
	 * 要想完成ui界面聊天的功能，服务端也需要作为客户端来接收后台推送用户发送的信息
	 */
	private final static String SYS_USERNAME = "niezhiliang9595";


	/**
	 *
	 * 用户连接时触发，我们将其添加到
	 * 保存客户端连接信息的socketServers中
	 *
	 * @param session
	 * @param userName
	 */
	@OnOpen
	public void open(Session session,@PathParam(value="userName")String userName){
		this.session = session;
		socketServers.add(new Client(userName,session));

		logger.info("客户端:【{}】连接成功",userName);
	}

	/**
	 *
	 * 收到客户端发送信息时触发
	 * 我们将其推送给客户端(niezhiliang9595)
	 * 其实也就是服务端本身，为了达到前端聊天效果才这么做的
	 *
	 * @param message
	 */
	@OnMessage
	public void onMessage(String message){

		Client client = socketServers.stream().filter( cli -> cli.getSession() == session)
				.collect(Collectors.toList()).get(0);
		sendMessage(client.getUserName()+"<--"+message,SYS_USERNAME);

		logger.info("客户端:【{}】发送信息:{}",client.getUserName(),message);
	}

	/**
	 *
	 * 连接关闭触发，通过sessionId来移除
	 * socketServers中客户端连接信息
	 */
	@OnClose
	public void onClose(){
		socketServers.forEach(client ->{
			if (client.getSession().getId().equals(session.getId())) {

				logger.info("客户端:【{}】断开连接",client.getUserName());
				socketServers.remove(client);
			}
		});
	}

	/**
	 *
	 * 发生错误时触发
	 * @param error
	 */
    @OnError
    public void onError(Throwable error) {
		socketServers.forEach(client ->{
			if (client.getSession().getId().equals(session.getId())) {

				logger.error("客户端:【{}】发生异常",client.getUserName());
				error.printStackTrace();
			}
		});
    }

	/**
	 *
	 * 信息发送的方法，通过客户端的userName
	 * 拿到其对应的session，调用信息推送的方法
	 * @param message
	 * @param userName
	 */
	public synchronized static void sendMessage(String message,String userName) {

		socketServers.forEach(client ->{
			if (userName.equals(client.getUserName())) {
				try {
					client.getSession().getBasicRemote().sendText(message);

					logger.info("服务端推送给客户端 :【{}】",client.getUserName(),message);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 *
	 * 获取服务端当前客户端的连接数量，
	 * 因为服务端本身也作为客户端接受信息，
	 * 所以连接总数还要减去服务端
	 * 本身的一个连接数
	 *
	 * 这里运用三元运算符是因为客户端第一次在加载的时候
	 * 客户端本身也没有进行连接，-1 就会出现总数为-1的情况，
	 * 这里主要就是为了避免出现连接数为-1的情况
	 *
	 * @return
	 */
	public synchronized static int getOnlineNum(){
		//避免服务端第一次加载会出现在线人数-1的情况
		return socketServers.size()-1 > 0 ? socketServers.size()-1 : 0;
	}

	/**
	 *
	 * 获取在线用户名，前端界面需要用到
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
	 *
	 * 信息群发，我们要排除服务端自己不接收到推送信息
	 * 所以我们在发送的时候将服务端排除掉
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

		logger.info("服务端推送给所有客户端 :【{}】",message);
	}

	/**
	 *
	 * 多个人发送给指定的几个用户
	 * @param message
	 * @param persons
	 */
	public synchronized static void SendMany(String message,String [] persons) {
		for (String userName : persons) {
			sendMessage(message,userName);
		}
	}
}
