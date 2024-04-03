package com.example.itvalley;

public class MainRunner {

    public static void main(String[] args) {
        startServer(EditingDepartmentServer.class);
        startServer(ProcessingCenterServer.class);
        startServer(AccountsDepartmentServer.class);
        startServer(MarketingDepartmentServer.class);
        startServer(CentralServer.class);

        // Wait for 1 second to allow servers to initialize
        waitForServersToInitialize(1000); // Waiting time in milliseconds

        // Start Client as its own "server"
        startServer(Client.class);
    }

    private static void startServer(Class<?> serverClass) {
        new Thread(() -> {
            try {
                serverClass.getDeclaredMethod("main", String[].class)
                           .invoke(null, (Object) new String[]{}); // Empty String array as `main` method argument
                System.out.println(serverClass.getSimpleName() + " started.");
            } catch (Exception e) {
                System.err.println("Failed to start " + serverClass.getSimpleName());
                e.printStackTrace();
            }
        }).start();
    }

    private static void waitForServersToInitialize(int milliseconds) {
        try {
            System.out.println("Waiting for servers to initialize...");
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Preserve interrupt status
            System.err.println("Main thread interrupted while waiting for servers to initialize.");
            e.printStackTrace();
        }
    }
}
