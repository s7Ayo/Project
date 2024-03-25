package com.example.itvalley;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void submitProject(String name, String length, String topic, String information) {
        try {
            // Assume a simple protocol where project details are sent in a single line
            String projectDetails = String.format("Project Name: %s, Length: %s, Topic: %s, Info: %s", name, length, topic, information);
            out.println(projectDetails);

            // Await approval confirmation from the Central Server
            String response = in.readLine();
            System.out.println("Central Server response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.startConnection("127.0.0.1", 6666); // Connect to the server running on localhost
        
        // Submit a sample project instance
        client.submitProject("New Ad Campaign", "30s", "Marketing", "Promotional content for product XYZ");

        client.stopConnection(); // Close the connection after receiving confirmation
    }
}
