package com.example.itvalley;

import jakarta.jms.*;
import com.rabbitmq.jms.admin.RMQConnectionFactory;

public class ClientsAsyncServer {
 
    private final static String QUEUE_NAME = "project_requests";

    public static void main(String[] args) {
        RMQConnectionFactory connectionFactory = new RMQConnectionFactory();
        try (JMSContext context = connectionFactory.createContext()) {
            Destination queue = context.createQueue(QUEUE_NAME);
            JMSProducer producer = context.createProducer();

            for (int i = 1; i <= 100; i++) {
                String projectDetails = String.format("Project Name: Project %d, Length: %ds, Topic: Topic %d, Info: Information about Project %d", i, i * 10, i, i);
                producer.send(queue, projectDetails);
                System.out.println("Sent: " + projectDetails);
            }
        }
    }
}
