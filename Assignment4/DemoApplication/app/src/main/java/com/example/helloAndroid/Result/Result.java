package com.example.helloAndroid.Result;

public class Result {
    private String application;
    private double longitude;
    private double latitude;

    public Result(String application, double longitude, double latitude) {
        this.application = application;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
