package com.example.itvalley;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class CentralServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Central Server started on port " + port);

            // Continuously listen for client connections
            while (true) {
                clientSocket = serverSocket.accept();
                System.out.println("Client connected");

                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received project instance from client: " + inputLine);
                    
                    // Simulate the review process by the Marketing Department
                    if (processProjectInstance(inputLine)) {
                        out.println("Project instance approved");
                    } else {
                        out.println("Project instance rejected");
                    }
                }

                in.close();
                out.close();
                clientSocket.close();
                System.out.println("Client disconnected");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stop(); // Clean up resources
        }
    }

    private boolean processProjectInstance(String projectDetails) {
        // Simulate processing the project instance and making an approval decision
        // This is a simplified example; in a real application, this could involve complex logic
        System.out.println("Processing project instance: " + projectDetails);
        
        // For simplicity, let's just approve all project instances
        return true;
    }

    public void stop() {
        try {
            serverSocket.close();
            System.out.println("Central Server stopped");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CentralServer server = new CentralServer();
        server.start(6666); // Start the server on port 6666
    }
}
