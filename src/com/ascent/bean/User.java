package com.ascent.bean;

/**
 * 实体类User，用来描述用户的信息类
 * @author ascent
 * @version 1.0
 */
public class User implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private String username; // 用户名

	private String password; // 密码

	private int authority; // 用户权限

	/**
	 * 默认构造方法
	 */
	public User() {
	}

	/**
	 * 带两个参数的构造方法
	 * @param username 用户名
	 * @param password 密码
	 */
	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	/**
	 * 带所有参数构造方法
	 * @param username 用户名
	 * @param password 密码
	 * @param authority 用户权限
	 */
	public User(String username, String password, int authority) {
		this.username = username;
		this.password = password;
		this.authority = authority;
	}

	/**
	 * @return the authority
	 */
	public int getAuthority() {
		return authority;
	}

	/**
	 * @param authority the authority to set
	 */
	public void setAuthority(int authority) {
		this.authority = authority;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return this.getUsername() + "  " + this.getPassword();
	}
}
