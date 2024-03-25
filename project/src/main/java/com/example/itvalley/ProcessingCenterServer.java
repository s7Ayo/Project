package com.example.itvalley;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ProcessingCenterServer {
    private static HashMap<String, String> projectDetailsMap = new HashMap<>();

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(6669);
        System.out.println("Processing Center Server started on port 6669");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String projectName = extractProjectName(inputLine);
                projectDetailsMap.put(projectName, inputLine);
                System.out.println("Processing Center received and stored project: " + projectName);
            }

            in.close();
            clientSocket.close();
        }
    }

    private static String extractProjectName(String projectDetails) {
        // Assuming project details start with the project name
        return projectDetails.split(",")[0];
    }
}
