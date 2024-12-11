package com.ascent.ui;

import javax.swing.*;
import javax.swing.event.*;

import com.ascent.bean.Product;
import com.ascent.util.ProductDataClient;
import com.ascent.util.FavoritesManager;
import com.ascent.util.ShoppingCart;
import com.ascent.bean.User;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import java.io.*;

/**
 * 这个类构建产品面板
 * @author ascent
 * @version 1.0
 */
@SuppressWarnings("serial")
public class ProductPanel extends JPanel {

	protected JLabel selectionLabel;

	protected JComboBox categoryComboBox;

	protected JPanel topPanel;

	protected JList productListBox;

	protected JScrollPane productScrollPane;

	protected JButton detailsButton;

	protected JButton clearButton;

	protected JButton exitButton;

	protected JButton shoppingButton;

	protected JButton favoriteButton;

	protected JPanel bottomPanel;

	protected MainFrame parentFrame;

	protected ArrayList<Product> productArrayList;

	protected ProductDataClient myDataClient;

	// 新增的管理员功能按钮
	protected JButton addProductButton;
	protected JButton deleteProductButton;
	protected JButton updateProductButton;

	/**
	 * 构建产品面板的构造方法
	 * @param theParentFrame 面板的父窗体
	 */
	public ProductPanel(MainFrame theParentFrame) {
		try {
			parentFrame = theParentFrame;
			myDataClient = new ProductDataClient();
			selectionLabel = new JLabel("选择类别");
			categoryComboBox = new JComboBox();
			categoryComboBox.addItem("-------");

			ArrayList categoryArrayList = myDataClient.getCategories();

			Iterator iterator = categoryArrayList.iterator();
			String aCategory;

			while (iterator.hasNext()) {
				aCategory = (String) iterator.next();
				categoryComboBox.addItem(aCategory);
			}

			topPanel = new JPanel();
			productListBox = new JList();
			productListBox.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			productScrollPane = new JScrollPane(productListBox);

			detailsButton = new JButton("详细...");
			clearButton = new JButton("清空");
			exitButton = new JButton("退出");
			shoppingButton = new JButton("查看购物车");
			favoriteButton = new JButton("添加到收藏夹");

			// 初始化管理员功能按钮
			addProductButton = new JButton("添加药品");
			deleteProductButton = new JButton("删除药品");
			updateProductButton = new JButton("修改药品");

			bottomPanel = new JPanel();

			this.setLayout(new BorderLayout());

			topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			topPanel.add(selectionLabel);
			topPanel.add(categoryComboBox);

			this.add(BorderLayout.NORTH, topPanel);
			this.add(BorderLayout.CENTER, productScrollPane);

			bottomPanel.setLayout(new FlowLayout());
			bottomPanel.add(shoppingButton);
			bottomPanel.add(detailsButton);
			bottomPanel.add(favoriteButton);
			bottomPanel.add(clearButton);
			bottomPanel.add(exitButton);

			// 仅在用户为管理员时添加管理员功能按钮
			User currentUser = parentFrame.getCurrentUser();
			if (currentUser != null && currentUser.getAuthority() == 1) {
				bottomPanel.add(addProductButton);
				bottomPanel.add(deleteProductButton);
				bottomPanel.add(updateProductButton);
			}

			this.add(BorderLayout.SOUTH, bottomPanel);

			detailsButton.addActionListener(new DetailsActionListener());
			clearButton.addActionListener(new ClearActionListener());
			exitButton.addActionListener(new ExitActionListener());
			shoppingButton.addActionListener(new ShoppingActionListener());
			categoryComboBox.addItemListener(new GoItemListener());
			productListBox.addListSelectionListener(new ProductListSelectionListener());
			favoriteButton.addActionListener(new FavoriteActionListener());

			// 添加管理员功能按钮的事件监听器
			addProductButton.addActionListener(new AddProductActionListener());
			deleteProductButton.addActionListener(new DeleteProductActionListener());
			updateProductButton.addActionListener(new UpdateProductActionListener());

			detailsButton.setEnabled(false);
			clearButton.setEnabled(false);
			shoppingButton.setEnabled(false);

		} catch (IOException exc) {
			JOptionPane.showMessageDialog(this, "网络问题 " + exc, "网络问题", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	/**
	 * 设置下拉列选中的分类选项
	 */
	protected void populateListBox() {
		try {
			String category = (String) categoryComboBox.getSelectedItem();
			if (!category.startsWith("---")) {
				productArrayList = myDataClient.getProducts(category);
			} else {
				productArrayList = new ArrayList<Product>();
			}

			Object[] theData = productArrayList.toArray();

			System.out.println(productArrayList + ">>>>>>>>>>>");
			productListBox.setListData(theData);
			productListBox.updateUI();

			if (productArrayList.size() > 0) {
				clearButton.setEnabled(true);
			} else {
				clearButton.setEnabled(false);
			}
		} catch (IOException exc) {
			JOptionPane.showMessageDialog(this, "网络问题: " + exc, "网络问题", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		} 
	}

	/**
	 * 处理选择详细...按钮时触发的事件监听器
	 * @author ascent
	 */
	class DetailsActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			int index = productListBox.getSelectedIndex();
			Product product = (Product) productArrayList.get(index);
			ProductDetailsDialog myDetailsDialog = new ProductDetailsDialog(parentFrame, product, shoppingButton);
			myDetailsDialog.setVisible(true);
		}
	}

	/**
	 * 处理选择查看购物车按钮时触发的事件监听器
	 * @author ascent
	 */
	class ShoppingActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			ShoppingCartDialog myShoppingDialog = new ShoppingCartDialog(
					parentFrame, shoppingButton);
			myShoppingDialog.setVisible(true);
		}
	}

	/**
	 * 处理选择退出按钮时触发的事件监听器
	 * @author ascent
	 */
	class ExitActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			parentFrame.dispose();
			System.exit(0);
		}
	}

	/**
	 * 处理选择清空按钮时触发的事件监听器
	 * @author ascent
	 */
	class ClearActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			Object[] noData = new Object[1];
			productListBox.setListData(noData);
			categoryComboBox.setSelectedIndex(0);
		}
	}

	/**
	 * 处理选中分类下拉列选的选项时触发的事件监听器
	 * @author ascent
	 */
	class GoItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent event) {
			if (event.getStateChange() == ItemEvent.SELECTED) {
				populateListBox();
			}
		}
	}

	/**
	 * 处理选中分类列表中选项时触发的事件监听器
	 * @author ascent
	 */
	class ProductListSelectionListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent event) {
			if (productListBox.isSelectionEmpty()) {
				detailsButton.setEnabled(false);
			} else {
				detailsButton.setEnabled(true);
			}
		}
	}

	/**
	 * 处理添加到收藏夹按钮时触发的事件监听器
	 * @author ascent
	 */
	class FavoriteActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Product selectedProduct = (Product) productListBox.getSelectedValue();
			if (selectedProduct != null) {
				FavoritesManager.getInstance().addProduct(selectedProduct);
				JOptionPane.showMessageDialog(ProductPanel.this,
					"已将 " + selectedProduct.getProductname() + " 添加到收藏夹",
					"收藏夹",
					JOptionPane.INFORMATION_MESSAGE);
				parentFrame.favoritesPanel.refreshFavorites();
			}
		}
	}

	// 实现添加药品的事件监听器
	class AddProductActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			// 弹出对话框输入药品信息并保存
			JOptionPane.showMessageDialog(ProductPanel.this, "添加药品功能待实现", "提示", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	// 实现删除药品的事件监听器
	class DeleteProductActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			// 从列表中选择药品并删除
			JOptionPane.showMessageDialog(ProductPanel.this, "删除药品功能待实现", "提示", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	// 实现修改药品的事件监听器
	class UpdateProductActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			// 从列表中选择药品，弹出对话框修改信息并保存
			JOptionPane.showMessageDialog(ProductPanel.this, "修改药品功能待实现", "提示", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}