package com.kamilk.ytfeed;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;

/**
 * Created by kamil on 2016-09-03.
 * Window class to be inherited from.
 */

abstract class Window extends JFrame {
    private JPanel mainPanel;

    Window(String title) {
        super(title);

        setResizable(false);

        mainPanel = new JPanel(new GridBagLayout());
    }

    void addComponent(JComponent component, int x, int y, int w, int h) {
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = w;
        c.gridheight = h;
        mainPanel.add(component, c);
    }

    void addMainPanel() {
        add(mainPanel);
    }

    void display() {
        setVisible(true);
    }

    void makeClickable(JComponent component, MouseAdapter mouseAdapter) {
        component.setCursor(new Cursor(Cursor.HAND_CURSOR));
        component.addMouseListener(mouseAdapter);
    }
}
