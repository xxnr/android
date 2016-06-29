package com.ksfc.newfarmer;

public class App {
    private static RndApplication app;

    public static void setApp(RndApplication application) {
        app = application;
    }

    public static RndApplication getApp() {
        return app;
    }
}
