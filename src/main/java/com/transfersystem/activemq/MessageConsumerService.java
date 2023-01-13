package com.transfersystem.activemq;

import org.springframework.stereotype.Service;

import javax.jms.*;

@Service
public class MessageConsumerService {

    private final ConnectionService connectionService;

    public MessageConsumerService(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    public String receiveMessage(String subject) throws JMSException {
        Session session = connectionService.getSession();
        Destination destination = session.createQueue(subject);
        MessageConsumer consumer = session.createConsumer(destination);
        TextMessage message = (TextMessage) consumer.receive();
        if (message != null) {
            System.out.println("MessageProducerService, successfully received sent from subject: " + subject);
            return message.getText();
        } else {
            System.out.println("No messages received from subject: " + subject);
            return "";
        }
    }
}
