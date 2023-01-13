package com.transfersystem.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.stereotype.Service;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;

@Service
public class ConnectionService {

    private Connection connection;
    private Session session;

    public void initializeSession() {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() throws JMSException {
        if (connection != null) {
            connection.stop();
            connection.close();
        }
    }

    public Session getSession() throws JMSException {
        if (session == null) {
            return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        }
        return session;
    }
}
