package com.kamilk.ytfeed;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;

/**
 * JFrame window class to be inherited from by concrete window classes.
 */
abstract class Window extends JFrame {
    /**
     * The main panel of every window. Windows are supposed to be added to it.
     */
    private final JPanel mainPanel = new JPanel(new GridBagLayout());

    /**
     * Window constructor, sets default window options.
     * @param title window title
     */
    Window(final String title) {
        super(title);

        setResizable(false);
    }

    /**
     * Adds a component at a position. Parameters are for the grid bag layout.
     * @param component the component to add
     * @param x x position
     * @param y y position
     * @param w width
     * @param h height
     */
    void addComponent(final JComponent component, final int x, final int y, final int w, final int h) {
        final GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = w;
        c.gridheight = h;
        mainPanel.add(component, c);
    }

    /**
     * Add the window to the main panel.
     */
    void addToMainPanel() {
        add(mainPanel);
    }

    /**
     * Display the window.
     */
    void display() {
        setVisible(true);
    }

    /**
     * Makes the component clickable and provides a listener.
     * @param component the component to be made clickable
     * @param mouseAdapter the listener
     */
    void makeClickable(final JComponent component, final MouseAdapter mouseAdapter) {
        component.setCursor(new Cursor(Cursor.HAND_CURSOR));
        component.addMouseListener(mouseAdapter);
    }
}
