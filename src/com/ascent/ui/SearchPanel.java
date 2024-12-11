package com.ascent.ui;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import com.ascent.bean.Product;
import com.ascent.util.ProductDataClient;
import com.ascent.util.FavoritesManager;

/**
 * 产品搜索面板
 * @author ascent
 * @version 1.0
 */
public class SearchPanel extends JPanel {
    private JTextField searchField;
    private JComboBox searchTypeCombo;
    private JList resultList;
    private DefaultListModel listModel;
    private JFrame parentFrame;
    private ProductDataClient dataClient;
    private ArrayList allProducts;
    private JButton detailsButton;
    private JButton shoppingButton;
    private JButton favoriteButton;

    public SearchPanel(JFrame parent) {
        this.parentFrame = parent;
        try {
            this.dataClient = new ProductDataClient();
            initializeUI();
            loadAllProducts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 搜索条件面板
        JPanel searchConditionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchTypeCombo = new JComboBox(new String[]{"产品名称", "CAS号", "化学式"});
        searchField = new JTextField(20);
        JButton searchButton = new JButton("搜索");
        
        searchConditionPanel.add(new JLabel("搜索类型："));
        searchConditionPanel.add(searchTypeCombo);
        searchConditionPanel.add(new JLabel("关键词："));
        searchConditionPanel.add(searchField);
        searchConditionPanel.add(searchButton);

        // 搜索结果面板
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("搜索结果"));
        
        listModel = new DefaultListModel();
        resultList = new JList(listModel);
        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(resultList);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        detailsButton = new JButton("查看详情");
        shoppingButton = new JButton("加入购物车");
        favoriteButton = new JButton("添加到收藏夹");
        buttonPanel.add(detailsButton);
        buttonPanel.add(shoppingButton);
        buttonPanel.add(favoriteButton);

        resultPanel.add(scrollPane, BorderLayout.CENTER);
        resultPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(searchConditionPanel, BorderLayout.NORTH);
        add(resultPanel, BorderLayout.CENTER);

        // 添加事件监听器
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
        detailsButton.addActionListener(new DetailsActionListener());
        shoppingButton.addActionListener(new AddToCartActionListener());
        favoriteButton.addActionListener(new FavoriteActionListener());
        
        // 初始状态下禁用按钮
        detailsButton.setEnabled(false);
        shoppingButton.setEnabled(false);
        favoriteButton.setEnabled(false);

        // 添加选择监听器
        resultList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    boolean hasSelection = !resultList.isSelectionEmpty();
                    detailsButton.setEnabled(hasSelection);
                    shoppingButton.setEnabled(hasSelection);
                    favoriteButton.setEnabled(hasSelection);
                }
            }
        });

        // 添加搜索框回车监听
        searchField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }
        });
    }

    private void loadAllProducts() {
        try {
            allProducts = new ArrayList();
            ArrayList categories = dataClient.getCategories();
            for (int i = 0; i < categories.size(); i++) {
                String category = (String) categories.get(i);
                ArrayList products = dataClient.getProducts(category);
                allProducts.addAll(products);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "加载产品数据失败: " + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            listModel.clear();
            return;
        }

        String searchType = (String) searchTypeCombo.getSelectedItem();
        ArrayList searchResults = new ArrayList();
        
        for (int i = 0; i < allProducts.size(); i++) {
            Product product = (Product) allProducts.get(i);
            if (matchesSearch(product, searchText, searchType)) {
                searchResults.add(product);
            }
        }

        updateSearchResults(searchResults);
    }

    private boolean matchesSearch(Product product, String searchText, String searchType) {
        if ("产品名称".equals(searchType)) {
            return product.getProductname().toLowerCase().contains(searchText);
        } else if ("CAS号".equals(searchType)) {
            return product.getCas().toLowerCase().contains(searchText);
        } else if ("化学式".equals(searchType)) {
            return product.getFormula().toLowerCase().contains(searchText);
        }
        return false;
    }

    private void updateSearchResults(ArrayList results) {
        listModel.clear();
        for (int i = 0; i < results.size(); i++) {
            listModel.addElement(results.get(i));
        }

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "未找到匹配的产品",
                    "搜索结果",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class DetailsActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            Product selectedProduct = (Product) resultList.getSelectedValue();
            if (selectedProduct != null) {
                ProductDetailsDialog detailsDialog = new ProductDetailsDialog(
                        parentFrame, selectedProduct, shoppingButton);
                detailsDialog.setVisible(true);
            }
        }
    }

    private class AddToCartActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            Product selectedProduct = (Product) resultList.getSelectedValue();
            if (selectedProduct != null) {
                com.ascent.util.ShoppingCart.getInstance().addProduct(selectedProduct);
                JOptionPane.showMessageDialog(SearchPanel.this,
                        "已将 " + selectedProduct.getProductname() + " 添加到购物车",
                        "购物车",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private class FavoriteActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            Product selectedProduct = (Product) resultList.getSelectedValue();
            if (selectedProduct != null) {
                FavoritesManager.getInstance().addProduct(selectedProduct);
                JOptionPane.showMessageDialog(SearchPanel.this,
                        "已将 " + selectedProduct.getProductname() + " 添加到收藏夹",
                        "收藏夹",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
