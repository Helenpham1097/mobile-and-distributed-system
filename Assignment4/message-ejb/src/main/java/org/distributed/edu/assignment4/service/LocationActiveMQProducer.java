package org.distributed.edu.assignment4.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.*;

@Stateless
public class LocationActiveMQProducer {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Resource
    private ConnectionFactory connectionFactory;
    @Resource(name = "queue/locationQueue")
    private Queue locationQueue;

    public void produceLocation(String deviceId, Location location) throws JMSException, JsonProcessingException{
        Session session = null;
        Connection connection = null;

        try {
            connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create a MessageProducer from the Session to the Topic or Queue
            MessageProducer producer = session.createProducer(locationQueue);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // Create a message
            TextMessage message = session.createTextMessage(
                    objectMapper.writeValueAsString(LocationMessage.builder()
                            .location(location)
                            .deviceId(deviceId)
                            .build())

            );

            // Tell the producer to send the message
            producer.send(message);
        } finally {
            // Clean up
            if (session != null) session.close();
            if (connection != null) connection.close();
        }
    }
}
