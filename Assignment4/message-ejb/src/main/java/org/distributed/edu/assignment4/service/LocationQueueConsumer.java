package org.distributed.edu.assignment4.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.distributed.edu.assignment4.dao.LocationDAO;
import org.distributed.edu.assignment4.model.LocationMessageEntity;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.*;
import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.lang.IllegalStateException;

@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationType",
                        propertyValue = "javax.jms.Queue"),
                @ActivationConfigProperty(propertyName = "destination",
                        propertyValue = "/queue/locationQueue")
        }
)
public class LocationQueueConsumer implements MessageListener {

    @Resource
    private ConnectionFactory connectionFactory;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @EJB
    private LocationDAO locationDAO;

    @Override
    @Transactional
    public void onMessage(final Message message) {
        try {
            final TextMessage textMessage = (TextMessage) message;
            final String json = textMessage.getText();
            System.out.println("Message push:" + json);

            LocationMessage locationMessage = objectMapper.readValue(json, LocationMessage.class);
            saveLocation(locationMessage);

        } catch (JMSException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void saveLocation(LocationMessage locationMessage) {
        LocationMessageEntity entity = new LocationMessageEntity();
        entity.setDeviceId(locationMessage.getDeviceId());
        entity.setLat(locationMessage.getLocation().getLat());
        entity.setLon(locationMessage.getLocation().getLon());
        entity.setCreatedAt(Timestamp.from(Instant.now()));
        locationDAO.save(entity);
    }
}
