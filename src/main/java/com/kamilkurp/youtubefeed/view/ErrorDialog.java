package com.kamilkurp.youtubefeed.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Created by kamil on 19.03.2017.
 */
public class ErrorDialog extends JDialog{
    private JTextArea textArea = new JTextArea();
    private JButton confirmBtn = new JButton("Confirm");

    public ErrorDialog(JFrame frame, String title, String text) {
        super(frame, title, false);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(500, 200));
        textArea.setFont(new Font("Verdana", Font.BOLD, 24));
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        confirmBtn.setFont(new Font("Verdana", Font.BOLD, 24));
        panel.add(textArea, BorderLayout.CENTER);
        panel.add(confirmBtn, BorderLayout.SOUTH);

        add(panel);
        pack();
        setLocationRelativeTo(frame);
    }

    public String getTextLabelText() {
        return textArea.getText();
    }

    public void addConfirmListener(ActionListener listener) {
        confirmBtn.addActionListener(listener);
    }

    public void setText(String text) {
        textArea.setText(text);
    }
}
