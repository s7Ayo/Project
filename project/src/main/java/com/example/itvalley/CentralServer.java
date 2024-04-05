package com.example.itvalley;

import com.rabbitmq.jms.admin.RMQConnectionFactory;
import jakarta.jms.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class CentralServer {
    private static final String QUEUE_NAME = "project_requests";
    private static final int SOCKET_PORT = 6666;
    private static final int MARKETING_DEPARTMENT_PORT = 6667;
    private static final List<Integer> DEPARTMENT_PORTS = Arrays.asList(6668, 6669, 6670);

    public static void main(String[] args) {
        new Thread(CentralServer::startSocketServer).start();
        startRabbitMQConsumer();
    }

    private static void startSocketServer() {
        try (ServerSocket serverSocket = new ServerSocket(SOCKET_PORT)) {
            System.out.println("Central Server Socket Listening on port: " + SOCKET_PORT);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                    String projectProposal;
                    while ((projectProposal = in.readLine()) != null) {
                        System.out.println("Received project proposal: " + projectProposal);
                        if (getApprovalFromMarketing(projectProposal)) {
                            forwardProjectToDepartments(projectProposal);
                            out.println("Project approved and forwarded to departments");
                        } else {
                            out.println("Project rejected by Marketing Department");
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean getApprovalFromMarketing(String projectProposal) {
        try (Socket marketingSocket = new Socket("localhost", MARKETING_DEPARTMENT_PORT);
             PrintWriter out = new PrintWriter(marketingSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(marketingSocket.getInputStream()))) {
    
            out.println(projectProposal);
            String response = in.readLine();
    
            if (response == null) {
                System.err.println("No response received from Marketing Department");
                return false;
            }
    
            System.out.println("Marketing Department response: " + response);
            return response.contains("approved");
    
        } catch (IOException e) {
            System.err.println("Failed to communicate with Marketing Department");
            e.printStackTrace();
            return false;
        }
    }
    

    private static void forwardProjectToDepartments(String approvedProject) {
        DEPARTMENT_PORTS.forEach(port -> {
            try (Socket departmentSocket = new Socket("localhost", port);
                 PrintWriter out = new PrintWriter(departmentSocket.getOutputStream(), true)) {
                out.println(approvedProject);
                System.out.println("Forwarded approved project to department on port " + port);
            } catch (IOException e) {
                System.err.println("Failed to forward project to department on port " + port);
                e.printStackTrace();
            }
        });
    }

    private static void startRabbitMQConsumer() {
        RMQConnectionFactory connectionFactory = new RMQConnectionFactory();
        try {
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(QUEUE_NAME);
            MessageConsumer consumer = session.createConsumer(queue);

            System.out.println("Central Server RabbitMQ Consumer Listening");
            connection.start();

            consumer.setMessageListener(message -> {
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        String projectProposal = textMessage.getText();
                        System.out.println("Received asynchronous project proposal: " + projectProposal);
                        if (getApprovalFromMarketing(projectProposal)) {
                            forwardProjectToDepartments(projectProposal);
                        }
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
