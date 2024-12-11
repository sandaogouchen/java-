package com.ascent.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import com.ascent.bean.Product;

/**
 * 产品收藏夹管理器
 */
public class FavoritesManager {

    private static FavoritesManager instance = new FavoritesManager();
    private ArrayList<Product> favoriteList = new ArrayList<Product>();
    private String userId;

    private FavoritesManager() {}

    /**
     * 获取收藏夹实例
     */
    public static FavoritesManager getInstance() {
        return instance;
    }

    /**
     * 设置用户ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 获取文件路径
     */
    private String getFilePath() {
        return "favorites_" + userId + ".txt";
    }

    /**
     * 清空当前内存中的收藏夹数据
     */
    public void clearCurrentFavorites() {
        favoriteList.clear();
    }

    /**
     * 清空当前内存中的收藏夹数据并保存到文件
     */
    public void clearCurrentFavoritesAndSave() {
        favoriteList.clear();
        saveFavorites();
    }

    /**
     * 添加产品到收藏夹
     */
    public void addProduct(Product product) {
        if (!favoriteList.contains(product)) {
            favoriteList.add(product);
            saveFavorites();
        }
    }

    /**
     * 从收藏夹移除产品
     */
    public void removeProduct(Product product) {
        favoriteList.remove(product);
        saveFavorites();
    }

    /**
     * 获取所有收藏的产品
     */
    public ArrayList<Product> getFavoriteList() {
        return favoriteList;
    }

    /**
     * 清空收藏夹
     */
    public void clearFavorites() {
        favoriteList.clear();
        saveFavorites();
    }

    /**
     * 保存收藏夹数据到文件
     */
    public void saveFavorites() {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getFilePath()), StandardCharsets.UTF_8))) {
            for (Product product : favoriteList) {
                writer.write(product.toString()); // Assuming Product class has a proper toString method
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从文件加载收藏夹数据
     */
    public void loadFavorites() {
        favoriteList.clear();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(getFilePath()), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Product product = parseProduct(line);
                if (product != null) { // 仅添加有效产品
                    favoriteList.add(product);
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
        return null; // 返回 null 表示解析失败
    }
}
