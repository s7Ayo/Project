package com.example.itvalley;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class AccountsDepartmentServer {
    private HashMap<String, String> projectDetailsMap = new HashMap<>();

    public void start(int port) throws Exception {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Accounts Department Server started on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String projectName = extractProjectName(inputLine);
                projectDetailsMap.put(projectName, inputLine);
                System.out.println("Accounts Department received and stored project: " + projectName);
            }

            in.close();
            clientSocket.close();
        }
    }

    private String extractProjectName(String projectDetails) {
        // Assuming project details start with the project name
        return projectDetails.split(",")[0].trim();
    }

    // Main method for standalone testing or running
    public static void main(String[] args) throws Exception {
        AccountsDepartmentServer server = new AccountsDepartmentServer();
        server.start(6670); // Specify the port number for standalone use
    }
}
