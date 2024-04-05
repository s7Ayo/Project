package com.example.itvalley;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import com.rabbitmq.jms.admin.RMQConnectionFactory; // Ensure this import is present

public class CentralServer {

    private static final String QUEUE_NAME = "project_requests";
    private static final int SOCKET_PORT = 6666; // Port for synchronous socket connections

    public static void main(String[] args) {
        new Thread(CentralServer::startSocketServer).start(); // Start synchronous handling in a new thread
        startRabbitMQConsumer(); // Start asynchronous handling
    }

    private static void startSocketServer() {
        try (ServerSocket serverSocket = new ServerSocket(SOCKET_PORT)) {
            System.out.println("Central Server Socket Listening on port: " + SOCKET_PORT);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        System.out.println("Received synchronous project proposal: " + inputLine);
                        // Process synchronous project proposal here
                        out.println("Project proposal received and processed");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startRabbitMQConsumer() {
        try {
            ConnectionFactory connectionFactory = (ConnectionFactory) new RMQConnectionFactory();
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            MessageConsumer consumer = session.createConsumer(session.createQueue(QUEUE_NAME));
            System.out.println("Central Server RabbitMQ Consumer Listening");

            consumer.setMessageListener(message -> {
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        System.out.println("Received asynchronous project proposal: " + textMessage.getText());
                        // Process asynchronous project proposal here
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });

            connection.start(); // Start the connection to begin message consumption
            // Keep the consumer running
            Thread.sleep(Long.MAX_VALUE);
        } catch (JMSException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            e.printStackTrace();
        }
    }
}
