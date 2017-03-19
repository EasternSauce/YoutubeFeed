package com.kamilk.ytfeed;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * View class of MVC pattern. Mostly just delegates from window instances.
 */
abstract class WindowView extends JFrame {
    ErrorDialog errorDialog;
    /**
     * Window constructor, sets default window options.
     * @param title window title
     */
    WindowView(String title) {
        super(title);

        errorDialog = new ErrorDialog(this, "error", "error");

        setResizable(true);
    }

    /**
     * The main panel of every window. Windows are supposed to be added to it.
     */
    final JPanel mainPanel = new JPanel(new GridBagLayout());



    /**
     * Adds a component at a position. Parameters are for the grid bag layout.
     * @param component the component to add
     * @param x x position
     * @param y y position
     * @param w width
     * @param h height
     */
    void addComponent(JComponent component, int x, int y, int w, int h, float wx, float wy) {
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = wx;
        c.weighty = wy;
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = w;
        c.gridheight = h;
        mainPanel.add(component, c);
    }

    /**
     * Add the window to the main panel.
     */
    void addMainPanel() {
        changeFont(mainPanel, new Font("Verdana", Font.BOLD, 24));
        add(mainPanel);
    }

    /**
     * Display the window.
     */
    void display() {
        setVisible(true);
    }

    void displayErrorDialogAndExit(String title, String text) {

        errorDialog.setTitle(title);
        errorDialog.setText(text);

        errorDialog.pack();
        errorDialog.setVisible(true);
    }

    void addErrorDialogListener(ActionListener actionListener) {
        errorDialog.addConfirmListener(actionListener);
    }

    /**
     * Makes the component clickable and provides a listener.
     * @param component the component to be made clickable
     * @param mouseAdapter the listener
     */
    static void makeClickable(JComponent component, MouseAdapter mouseAdapter) {
        component.setCursor(new Cursor(Cursor.HAND_CURSOR));
        component.addMouseListener(mouseAdapter);
    }




    static void changeFont (Component component, Font font) {
        component.setFont (font);
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents ()) {
                changeFont(child, font);
            }
        }
    }



}
