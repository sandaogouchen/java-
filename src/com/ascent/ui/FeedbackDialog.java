package com.ascent.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * 用户反馈对话框
 */
public class FeedbackDialog extends JDialog {

    private JTextArea feedbackTextArea;
    private JButton submitButton;
    private JButton cancelButton;

    public FeedbackDialog(Frame owner) {
        super(owner, "用户反馈", true);
        setLayout(new BorderLayout());

        feedbackTextArea = new JTextArea(10, 30);
        add(new JScrollPane(feedbackTextArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        submitButton = new JButton("提交");
        cancelButton = new JButton("取消");
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        submitButton.addActionListener(new SubmitActionListener());
        cancelButton.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(owner);
    }

    private class SubmitActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String feedback = feedbackTextArea.getText().trim();
            if (!feedback.isEmpty()) {
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("feedback.txt", true), StandardCharsets.UTF_8))) {
                    writer.write(feedback);
                    writer.newLine();
                    JOptionPane.showMessageDialog(FeedbackDialog.this, "感谢您的反馈！", "提交成功", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (IOException ioException) {
                    JOptionPane.showMessageDialog(FeedbackDialog.this, "无法保存反馈：" + ioException.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(FeedbackDialog.this, "反馈内容不能为空。", "警告", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
