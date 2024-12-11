package com.ascent.util;

import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import com.ascent.bean.User;

/**
 * 这个类连接数据服务器来获得数据
 * @author ascent
 * @version 1.0
 */
public class UserDataClient implements ProtocolPort {

	/**
	 * socket引用
	 */
	protected Socket hostSocket;

	/**
	 * 输出流的引用
	 */
	protected ObjectOutputStream outputToServer;

	/**
	 * 输入流的引用
	 */
	protected ObjectInputStream inputFromServer;

	/**
	 * 默认构造方法
	 */
	public UserDataClient() throws IOException {
		this(ProtocolPort.DEFAULT_HOST, ProtocolPort.DEFAULT_PORT);
	}

	/**
	 * 接受主机名和端口号的构造方法
	 */
	public UserDataClient(String hostName, int port) throws IOException {
		log("连接数据服务器..." + hostName + ":" + port);
		try {
			// 添加非空和有效性检查
			if (hostName == null || hostName.trim().isEmpty()) {
				throw new IllegalArgumentException("主机名不能为空");
			}
			
			// 增加超时设置，防止长时间等待
			hostSocket = new Socket();
			hostSocket.connect(new InetSocketAddress(hostName, port), 5000); // 5秒超时
			
			outputToServer = new ObjectOutputStream(hostSocket.getOutputStream());
			inputFromServer = new ObjectInputStream(hostSocket.getInputStream());
			log("连接成功.");
		} catch (ConnectException e) {
			log("连接失败：服务器拒绝连接 " + e.getMessage());
			// 确保在异常情况下关闭socket
			if (hostSocket != null) {
				try {
					hostSocket.close();
				} catch (IOException closeEx) {
					log("关闭socket时发生错误：" + closeEx.getMessage());
				}
			}
			throw e; // 重新抛出异常，让调用者知道连接失败
		} catch (IOException e) {
			log("连接失败：" + e.getMessage());
			// 确保在异常情况下关闭socket
			if (hostSocket != null) {
				try {
					hostSocket.close();
				} catch (IOException closeEx) {
					log("关闭socket时发生错误：" + closeEx.getMessage());
				}
			}
			throw e; // 重新抛出异常，让调用者知道连接失败
		}
	}

	/**
	 * 返回用户
	 * @return userTable 
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String,User> getUsers() {
		HashMap<String,User> userTable = new HashMap<>();
		File dbFile = new File("user.db");
		
		log("尝试读取用户数据文件: " + dbFile.getAbsolutePath());
		if (!dbFile.exists()) {
			log("错误：文件不存在 - " + dbFile.getAbsolutePath());
			return userTable;
		}
		
		try (BufferedReader reader = new BufferedReader(new FileReader(dbFile))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty()) continue;
				
				String[] parts = line.split(",");
				if (parts.length >= 3) {
					String username = parts[0].trim();
					String password = parts[1].trim();
					int authority = Integer.parseInt(parts[2].trim());
					
					User user = new User();
					user.setUsername(username);
					user.setPassword(password);
					user.setAuthority(authority);
					
					userTable.put(username, user);
				}
			}
			log("从本地文件成功读取用户数据");
			
		} catch (FileNotFoundException e) {
			log("错误：找不到user.db文件 - " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			log("错误：读取文件时发生IO异常 - " + e.getMessage());
			e.printStackTrace();
		} catch (NumberFormatException e) {
			log("错误：解析用户权限数据失败 - " + e.getMessage());
			e.printStackTrace();
		}
		
		return userTable;
	}

	/**
	 * 关闭当前SocKet
	 */
	public void closeSocKet() {
		try {
			this.hostSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 日志方法.
	 * @param msg 打印的日志信息
	 */
	protected void log(Object msg) {
		System.out.println("UserDataClient类: " + msg);
	}

	/**
	 * 注册用户
	 * @param username 用户名
	 * @param password 密码
	 * @return boolean true:注册成功，false:注册失败
	 */
	public boolean addUser(String username, String password) {
		HashMap<String,User> map = this.getUsers();
		if (map.containsKey(username)) {
			return false;
		} else {
			try {
				log("发送请求: OP_ADD_USERS  ");
				outputToServer.writeInt(ProtocolPort.OP_ADD_USERS);
				outputToServer.writeObject(new User(username, password, 0));
				outputToServer.flush();
				log("接收数据...");
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return false;
	}

}
