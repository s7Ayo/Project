package com.example.itvalley;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner; // Import Scanner for reading from the console

public class MarketingDepartmentServer {
    private ServerSocket serverSocket;
    private Socket centralServerSocket;
    private PrintWriter out;
    private BufferedReader in;
    private HashMap<String, String> projectApprovalStatus; // HashMap to store project approval status

    public MarketingDepartmentServer() {
        projectApprovalStatus = new HashMap<>();
    }

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Marketing Department Server started on port " + port);

            centralServerSocket = serverSocket.accept();
            System.out.println("Central Server connected");

            out = new PrintWriter(centralServerSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(centralServerSocket.getInputStream()));

            String projectInstanceDetails;
            while ((projectInstanceDetails = in.readLine()) != null) {
                System.out.println("Received project instance from Central Server: " + projectInstanceDetails);

                // Extract project name as key (assuming it's the first item in the project details)
                String projectName = projectInstanceDetails.split(",")[0].split(":")[1].trim();

                // Prompt for approval in the terminal
                boolean isApproved = promptForApproval(projectInstanceDetails);
                
                if (isApproved) {
                    projectApprovalStatus.put(projectName, "Approved");
                    out.println("Project instance approved: " + projectName);
                } else {
                    projectApprovalStatus.put(projectName, "Rejected");
                    out.println("Project instance rejected: " + projectName);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    private boolean promptForApproval(String projectDetails) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Approve project instance? (y/n): " + projectDetails);

        String input = scanner.nextLine();
        return input.equalsIgnoreCase("y");
    }

    public void stop() {
        try {
            in.close();
            out.close();
            centralServerSocket.close();
            serverSocket.close();
            System.out.println("Marketing Department Server stopped");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MarketingDepartmentServer marketingServer = new MarketingDepartmentServer();
        marketingServer.start(6667);
    }
}
