package com.transfersystem.activemq;

import org.springframework.stereotype.Service;

import javax.jms.*;

@Service
public class MessageProducerService {

    private final ConnectionService connectionService;

    public MessageProducerService(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    public void sendMessage(String message, String subject) throws JMSException {
        Session session = connectionService.getSession();
        Destination destination = session.createQueue(subject);
        MessageProducer producer = session.createProducer(destination);
        TextMessage textMessage = session.createTextMessage(message);
        producer.send(textMessage);
        System.out.println("MessageProducerService, successfully message sent to subject: " + subject);
    }
}
