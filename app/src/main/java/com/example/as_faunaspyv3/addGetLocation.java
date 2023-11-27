package com.example.as_faunaspyv3;


public class addGetLocation {

    private String latitude = "";
    private String longitude = "";

    public addGetLocation() {
    }

    public addGetLocation(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }



    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String lat) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
