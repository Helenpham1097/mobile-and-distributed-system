package org.distributed.edu.assignment4.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class LocationNotification {
    private double lat;
    private double lon;
    private String deviceId;
}
