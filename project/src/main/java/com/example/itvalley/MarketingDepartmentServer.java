package com.example.itvalley;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class MarketingDepartmentServer {
    private ServerSocket serverSocket;
    private HashMap<String, String> projectApprovalStatus;

    public MarketingDepartmentServer() {
        projectApprovalStatus = new HashMap<>();
    }

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Marketing Department Server started on port " + port);

            while (true) { // Keep the server running indefinitely
                System.out.println("Waiting for connections...");
                Socket centralServerSocket = serverSocket.accept();
                System.out.println("Central Server connected");

                try (
                    PrintWriter out = new PrintWriter(centralServerSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(centralServerSocket.getInputStream()))
                ) {
                    String projectInstanceDetails;
                    while ((projectInstanceDetails = in.readLine()) != null) {
                        System.out.println("Received project instance from Central Server: " + projectInstanceDetails);
                        boolean isApproved = promptForApproval(projectInstanceDetails);

                        String response = isApproved ? "Project instance approved: " : "Project instance rejected: ";
                        response += projectInstanceDetails;
                        out.println(response);

                        if (isApproved) {
                            projectApprovalStatus.put(projectInstanceDetails, "Approved");
                        } else {
                            projectApprovalStatus.put(projectInstanceDetails, "Rejected");
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error handling central server connection");
                    e.printStackTrace();
                } finally {
                    System.out.println("Central Server connection closed");
                    // No need to close centralServerSocket here, it's managed by the try-with-resources block
                }
            }
        } catch (Exception e) {
            System.err.println("Marketing Department Server encountered an error");
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    private boolean promptForApproval(String projectDetails) {
        String projectName = projectDetails.split(",")[0].split(":")[1].trim();
        if (projectName.contains("9")) {
            System.out.println("Project instance rejected: " + projectName + " (contains number 9)");
            return false;
        } else {
            System.out.println("Project instance approved: " + projectName);
            return true;
        }
    }

    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Marketing Department Server stopped");
            }
        } catch (Exception e) {
            System.err.println("Error stopping Marketing Department Server");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MarketingDepartmentServer marketingServer = new MarketingDepartmentServer();
        marketingServer.start(6667);
    }
}
