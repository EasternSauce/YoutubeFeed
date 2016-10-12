package com.kamilk.ytfeed;

/**
 * Initialization of MVC and running app.
 */
public class App {
    /**
     * Main class of the app.
     * @param args no arguments needed or used
     */
    public static void main(final String[] args) {
        //MVC creation
        final Controller controller = new Controller(new Model(), new View());
        controller.runApp();
    }
}
