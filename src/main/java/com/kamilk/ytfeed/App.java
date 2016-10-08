package com.kamilk.ytfeed;

/**
 * Created by kamil on 2016-08-05.
 * Initialization of MVC and running app.
 */

public class App {
    public static void main(String args[]) {
        //MVC creation
        Controller controller = new Controller(new Model(), new View());
        controller.runApp();
    }
}
