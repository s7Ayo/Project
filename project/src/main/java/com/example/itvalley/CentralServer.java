package com.example.itvalley;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

@SuppressWarnings("unused")
public class CentralServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    // Sockets for department servers
    private Socket editingSocket, processingSocket, accountsSocket;
    private PrintWriter editingOut, processingOut, accountsOut;

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Central Server started on port " + port);

            // Connect to all department servers
            connectToDepartmentServers();

            // Continuously listen for client connections
            while (true) {
                clientSocket = serverSocket.accept();
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received project instance from client: " + inputLine);
                    // Forward project instance to all department servers
                    forwardProjectDetails(inputLine);
                }

                closeClientConnection();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    private void connectToDepartmentServers() throws Exception {
        editingSocket = new Socket("localhost", 6668);
        processingSocket = new Socket("localhost", 6669);
        accountsSocket = new Socket("localhost", 6670);
        editingOut = new PrintWriter(editingSocket.getOutputStream(), true);
        processingOut = new PrintWriter(processingSocket.getOutputStream(), true);
        accountsOut = new PrintWriter(accountsSocket.getOutputStream(), true);
    }

    private void forwardProjectDetails(String details) {
        editingOut.println(details);
        processingOut.println(details);
        accountsOut.println(details);
    }

    private void closeClientConnection() throws Exception {
        in.close();
        out.close();
        clientSocket.close();
    }

    public void stop() {
        try {
            // Close all connections
            if (editingOut != null) editingOut.close();
            if (processingOut != null) processingOut.close();
            if (accountsOut != null) accountsOut.close();
            if (editingSocket != null) editingSocket.close();
            if (processingSocket != null) processingSocket.close();
            if (accountsSocket != null) accountsSocket.close();
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
