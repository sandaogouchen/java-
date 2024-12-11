package com.ascent.util;

import com.ascent.bean.Product;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * 这个类连接数据服务器来获得数据
 * @author ascent
 * @version 1.0
 */
public class ProductDataClient implements ProtocolPort {

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
	public ProductDataClient() throws IOException {
		this(ProtocolPort.DEFAULT_HOST, ProtocolPort.DEFAULT_PORT);
	}

	/**
	 * 接受主机名和端口号的构造方法
	 */
	public ProductDataClient(String hostName, int port) throws IOException {

		log("连接数据服务器..." + hostName + ":" + port);

		hostSocket = new Socket(hostName, port);
		outputToServer = new ObjectOutputStream(hostSocket.getOutputStream());
		outputToServer.flush();
		inputFromServer = new ObjectInputStream(hostSocket.getInputStream());

		log("连接成功.");
	}

	/**
	 * 返回类别集合
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getCategories() throws IOException {
		ArrayList<String> categoryList = new ArrayList<>();
		Set<String> uniqueCategories = new HashSet<>();

		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream("product.db"), "UTF-8"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length >= 7) {
					String category = parts[6].trim();
					if (!uniqueCategories.contains(category)) {
						categoryList.add(category);
						uniqueCategories.add(category);
					}
				}
			}
			log("收到 " + categoryList.size() + " 类别.");
		} catch (IOException exc) {
			log("读取产品类别文件时发生错误: " + exc);
			throw exc;
		}

		return categoryList;
	}

	/**
	 * 返回产品集合
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Product> getProducts(String category) throws IOException {
		ArrayList<Product> productList = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream("product.db"), "UTF-8"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length >= 7) {
					String productName = parts[0].trim();
					String cas = parts[1].trim();
					String structure = parts[2].trim();
					String formula = parts[3].trim();
					String price = parts[4].trim();
					String realstock = parts[5].trim();
					String productCategory = parts[6].trim();

					if (category.startsWith("---") || category.equals(productCategory)) {
						Product product = new Product(productName, cas, structure, formula, price, realstock, productCategory);
						productList.add(product);
					}
				}
			}
			log("收到 " + productList.size() + " 产品.");
		} catch (IOException exc) {
			log("读取产品文件时发生错误: " + exc);
			throw exc;
		}

		return productList;
	}

	/**
	 * 日志方法.
	 */
	protected void log(Object msg) {
		System.out.println("ProductDataClient类: " + msg);
	}
}