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
    private Socket marketingSocket;
    private PrintWriter marketingOut;
    private BufferedReader marketingIn;

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Central Server started on port " + port);

            // Connect to the Marketing Department Server
            marketingSocket = new Socket("localhost", 6667);
            marketingOut = new PrintWriter(marketingSocket.getOutputStream(), true);
            marketingIn = new BufferedReader(new InputStreamReader(marketingSocket.getInputStream()));

            // Continuously listen for client connections
            while (true) {
                clientSocket = serverSocket.accept();
                System.out.println("Client connected");

                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received project instance from client: " + inputLine);
                    // Forward project instance to Marketing Department Server
                    marketingOut.println(inputLine);

                    // Await approval decision from Marketing Department
                    String decision = marketingIn.readLine();
                    System.out.println("Received decision from Marketing Department: " + decision);
                    
                    // Relay decision back to client
                    out.println(decision);
                }

                in.close();
                out.close();
                clientSocket.close();
                System.out.println("Client disconnected");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    public void stop() {
        try {
            if (marketingIn != null) marketingIn.close();
            if (marketingOut != null) marketingOut.close();
            if (marketingSocket != null) marketingSocket.close();
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
            System.out.println("Central Server stopped");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CentralServer server = new CentralServer();
        server.start(6666);
    }
}
