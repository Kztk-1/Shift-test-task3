package org.example;

public class Main {
    public static void main(String[] args) {
        ApplicationRunner app = new ApplicationRunner();
        int exitCode = app.run(args);
        System.exit(exitCode);

    }
}