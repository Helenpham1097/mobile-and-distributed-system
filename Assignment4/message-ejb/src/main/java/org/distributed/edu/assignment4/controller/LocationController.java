package org.distributed.edu.assignment4.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.distributed.edu.assignment4.dao.LocationDAO;
import org.distributed.edu.assignment4.service.Location;
import org.distributed.edu.assignment4.service.LocationActiveMQProducer;
import org.distributed.edu.assignment4.service.LocationNotification;

import javax.ejb.EJB;
import javax.jms.JMSException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/location")
@Produces(MediaType.APPLICATION_JSON)
public class LocationController {

    @EJB
    private LocationDAO locationDAO;

    @EJB
    private LocationActiveMQProducer locationActiveMQProducer;

    @Path("/update/{device_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public Response updateLocation(@PathParam("device_id") String deviceId,
                                   Location location) throws JMSException, JsonProcessingException {

        locationActiveMQProducer.produceLocation(deviceId, location);

        return Response.ok(deviceId).build();
    }

    @Path("fetch/{device_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public List<LocationNotification> fetchLocations(@PathParam("device_id") String deviceId) {
        return locationDAO.getLatestLocations().stream()
                .map(l -> LocationNotification.builder()
                        .lat(l.getLat())
                        .lon(l.getLon())
                        .deviceId(l.getDeviceId())
                        .build())
                .filter(l -> !(l.getDeviceId().equals(deviceId)))
                .collect(Collectors.toList());
    }
}
