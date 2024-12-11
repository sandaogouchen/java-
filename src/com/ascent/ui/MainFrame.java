package com.ascent.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.ascent.util.FavoritesManager;
import com.ascent.util.ShoppingCart;
import com.ascent.bean.User;

/**
 * 艾斯医药主框架界面
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	/**
	 * 当前登录用户
	 */
	private User currentUser;

	/**
	 * tabbed pane组件
	 */
	protected JTabbedPane tabbedPane;

	/**
	 * 产品 panel
	 */
	protected ProductPanel productPanel;

	/**
	 * 搜索 panel
	 */
	protected SearchPanel searchPanel;

	/**
	 * 收藏夹 panel
	 */
	protected FavoritesPanel favoritesPanel;

	/**
	 * 默认构造方法
	 */
	public MainFrame(User user) {

		this.currentUser = user; // 设置当前用户

		setTitle("欢迎使用AscentSys应用! ");

		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());

		FavoritesManager.getInstance().loadFavorites();
		ShoppingCart.getInstance().loadCart();

		tabbedPane = new JTabbedPane();

		productPanel = new ProductPanel(this);
		tabbedPane.addTab("药品", productPanel);

		// 添加搜索面板
		searchPanel = new SearchPanel(this);
		tabbedPane.addTab("搜索", searchPanel);

		// 添加收藏夹面板
		favoritesPanel = new FavoritesPanel();
		tabbedPane.addTab("收藏夹", favoritesPanel);

		container.add(BorderLayout.CENTER, tabbedPane);

		JMenuBar myMenuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("文件");

		JMenu openMenu = new JMenu("打开");
		JMenuItem localMenuItem = new JMenuItem("本地硬盘...");
		openMenu.add(localMenuItem);

		JMenuItem networkMenuItem = new JMenuItem("网络...");
		openMenu.add(networkMenuItem);

		JMenuItem webMenuItem = new JMenuItem("互联网...");
		openMenu.add(webMenuItem);
		fileMenu.add(openMenu);

		JMenuItem saveMenuItem = new JMenuItem("保存");
		fileMenu.add(saveMenuItem);

		JMenuItem exitMenuItem = new JMenuItem("退出");
		fileMenu.add(exitMenuItem);

		myMenuBar.add(fileMenu);

		exitMenuItem.addActionListener(new ExitActionListener());

		setupLookAndFeelMenu(myMenuBar);

		JMenu helpMenu = new JMenu("帮助");
		JMenuItem aboutMenuItem = new JMenuItem("关于");
		helpMenu.add(aboutMenuItem);

		JMenuItem feedbackMenuItem = new JMenuItem("用户反馈");
		helpMenu.add(feedbackMenuItem);

		if (currentUser.getAuthority() == 1) { // 仅管理员可见
			JMenuItem viewFeedbackMenuItem = new JMenuItem("查看用户反馈");
			helpMenu.add(viewFeedbackMenuItem);
			viewFeedbackMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					showFeedbackDialog();
				}
			});
		}

		myMenuBar.add(helpMenu);

		aboutMenuItem.addActionListener(new AboutActionListener());

		feedbackMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FeedbackDialog feedbackDialog = new FeedbackDialog(MainFrame.this);
				feedbackDialog.setVisible(true);
			}
		});

		this.setJMenuBar(myMenuBar);

		setSize(500, 400);
		setLocation(100, 100);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				FavoritesManager.getInstance().saveFavorites();
				ShoppingCart.getInstance().saveCart();
				dispose();
				System.exit(0);
			}
		});

		fileMenu.setMnemonic('f');
		exitMenuItem.setMnemonic('x');
		helpMenu.setMnemonic('h');
		aboutMenuItem.setMnemonic('a');
		feedbackMenuItem.setMnemonic('f');

		// 设定快捷键
		exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
				ActionEvent.CTRL_MASK));

		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				ActionEvent.CTRL_MASK));

		aboutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
				ActionEvent.CTRL_MASK));

		// 检查是否有未读反馈信息
		if (currentUser.getAuthority() == 1) { // 仅管理员检查
			int unreadFeedbackCount = getUnreadFeedbackCount();
			if (unreadFeedbackCount > 0) {
				JOptionPane.showMessageDialog(this, "您有 " + unreadFeedbackCount + " 条未读反馈信息。", "未读反馈", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	/**
	 * 显示用户反馈信息的对话框
	 */
	private void showFeedbackDialog() {
		StringBuilder feedbackContent = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader("feedback.txt"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				feedbackContent.append(line).append("\n");
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "无法读取反馈信息：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			return;
		}

		JTextArea textArea = new JTextArea(feedbackContent.toString());
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(400, 300));

		JOptionPane.showMessageDialog(this, scrollPane, "用户反馈信息", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * 获取未读反馈信息的数量
	 */
	private int getUnreadFeedbackCount() {
		int count = 0;
		try (BufferedReader reader = new BufferedReader(new FileReader("feedback.txt"))) {
			while (reader.readLine() != null) {
				count++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * 获取当前用户
	 */
	public User getCurrentUser() {
		return currentUser;
	}

	/**
	 * 设定和选择外观
	 */
	private void setupLookAndFeelMenu(JMenuBar myMenuBar) {
		JMenu lookAndFeelMenu = new JMenu("外观");

		ButtonGroup group = new ButtonGroup();

		JRadioButtonMenuItem metalItem = new JRadioButtonMenuItem("Metal");
		JRadioButtonMenuItem nimbusItem = new JRadioButtonMenuItem("Nimbus");
		JRadioButtonMenuItem motifItem = new JRadioButtonMenuItem("Motif");
		JRadioButtonMenuItem windowsItem = new JRadioButtonMenuItem("Windows");

		group.add(metalItem);
		group.add(nimbusItem);
		group.add(motifItem);
		group.add(windowsItem);

		lookAndFeelMenu.add(metalItem);
		lookAndFeelMenu.add(nimbusItem);
		lookAndFeelMenu.add(motifItem);
		lookAndFeelMenu.add(windowsItem);

		myMenuBar.add(lookAndFeelMenu);

		metalItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			}
		});

		nimbusItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			}
		});

		motifItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			}
		});

		windowsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			}
		});
	}

	/**
	 * 设置外观
	 */
	private void setLookAndFeel(String lookAndFeel) {
		try {
			UIManager.setLookAndFeel(lookAndFeel);
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 退出动作监听器
	 */
	class ExitActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			FavoritesManager.getInstance().saveFavorites();
			ShoppingCart.getInstance().saveCart();
			dispose();
			System.exit(0);
		}
	}

	/**
	 * 关于动作监听器
	 */
	class AboutActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(MainFrame.this,
				"AscentSys 应用程序\n版本 1.0\n作者: ascent",
				"关于",
				JOptionPane.INFORMATION_MESSAGE);
		}
	}
}