package com.ascent.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import com.ascent.bean.Product;

/**
 * 购物车管理类
 */
public class ShoppingCart {

    private static ShoppingCart instance = new ShoppingCart();
    private ArrayList<Product> cartList = new ArrayList<Product>();

    private ShoppingCart() {}

    /**
     * 获取购物车实例
     */
    public static ShoppingCart getInstance() {
        return instance;
    }

    /**
     * 添加产品到购物车
     */
    public void addProduct(Product product) {
        if (!cartList.contains(product)) {
            cartList.add(product);
            saveCart();
        }
    }

    /**
     * 保存购物车数据到文件
     */
    public void saveCart() {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("cart.txt"), StandardCharsets.UTF_8))) {
            for (Product product : cartList) {
                writer.write(product.toString()); // Assuming Product class has a proper toString method
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从文件加载购物车数据
     */
    public void loadCart() {
        cartList.clear();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("cart.txt"), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Product product = parseProduct(line);
                if (product != null) {
                    cartList.add(product);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析产品数据
     */
    private Product parseProduct(String line) {
        try {
            // 假设产品数据格式为 "productName:cas:structure:formula:price:realstock:category"
            String[] parts = line.split(":");
            if (parts.length == 7) {
                String productName = parts[0];
                String cas = parts[1];
                String structure = parts[2];
                String formula = parts[3];
                String price = parts[4];
                String realstock = parts[5];
                String category = parts[6];
                return new Product(productName, cas, structure, formula, price, realstock, category);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从购物车移除产品
     */
    public void removeProduct(Product product) {
        cartList.remove(product);
        saveCart();
    }

    /**
     * 获取购物车中的所有产品
     */
    public ArrayList<Product> getCartList() {
        return cartList;
    }

    /**
     * 清空购物车
     */
    public void clearCart() {
        cartList.clear();
        saveCart();
    }
}
