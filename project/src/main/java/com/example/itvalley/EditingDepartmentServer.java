package com.example.itvalley;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class EditingDepartmentServer {
    private static HashMap<String, String> projectDetailsMap = new HashMap<>();

    public void start(int port) throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Editing Department Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String projectName = extractProjectName(inputLine);
                    projectDetailsMap.put(projectName, inputLine);
                    System.out.println("Editing Department received and stored project: " + projectName);
                }

                in.close();
                clientSocket.close();
            }
        }
    }

    private static String extractProjectName(String projectDetails) {
        // Assuming project details start with the project name
        return projectDetails.split(",")[0];
    }

    // You can keep the main method for standalone testing or running.
    public static void main(String[] args) throws Exception {
        EditingDepartmentServer server = new EditingDepartmentServer();
        server.start(6668); // Specify the port number here if running standalone
    }
}
