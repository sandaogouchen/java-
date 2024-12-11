package com.ascent.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import com.ascent.bean.Product;
import com.ascent.util.FavoritesManager;
import com.ascent.util.ShoppingCart;

/**
 * 收藏夹面板
 */
public class FavoritesPanel extends JPanel {
    private JList<Product> favoritesList;
    private DefaultListModel<Product> listModel;
    private JButton removeButton;
    private JButton clearButton;
    private JButton addToCartButton;

    public FavoritesPanel() {
        initializeUI();
        loadFavorites();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 收藏夹列表
        listModel = new DefaultListModel<>();
        favoritesList = new JList<>(listModel);
        favoritesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(favoritesList);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        removeButton = new JButton("移除");
        clearButton = new JButton("清空");
        addToCartButton = new JButton("添加到购物车");
        buttonPanel.add(removeButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(addToCartButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // 添加事件监听器
        removeButton.addActionListener(e -> removeSelectedProduct());
        clearButton.addActionListener(e -> clearFavorites());
        addToCartButton.addActionListener(e -> addSelectedProductToCart());

        // 初始状态下禁用移除和添加到购物车按钮
        removeButton.setEnabled(false);
        addToCartButton.setEnabled(false);

        // 添加选择监听器
        favoritesList.addListSelectionListener(e -> {
            boolean hasSelection = !favoritesList.isSelectionEmpty();
            removeButton.setEnabled(hasSelection);
            addToCartButton.setEnabled(hasSelection);
        });
    }

    public void loadFavorites() {
        ArrayList<Product> favorites = FavoritesManager.getInstance().getFavoriteList();
        listModel.clear();
        for (Product product : favorites) {
            listModel.addElement(product);
        }
    }

    public void refreshFavorites() {
        loadFavorites();
    }

    private void removeSelectedProduct() {
        Product selectedProduct = favoritesList.getSelectedValue();
        if (selectedProduct != null) {
            FavoritesManager.getInstance().removeProduct(selectedProduct);
            listModel.removeElement(selectedProduct);
        }
    }

    private void clearFavorites() {
        FavoritesManager.getInstance().clearFavorites();
        listModel.clear();
    }

    private void addSelectedProductToCart() {
        Product selectedProduct = favoritesList.getSelectedValue();
        if (selectedProduct != null) {
            ShoppingCart.getInstance().addProduct(selectedProduct);
            JOptionPane.showMessageDialog(this,
                    "已将 " + selectedProduct.getProductname() + " 添加到购物车",
                    "购物车",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
