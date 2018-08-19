package com.example.samsung.team_a;


public class MarkerItem {


    double lat;
    double lon;

    String address;

    public MarkerItem(double lat, double lon, String address) {
        this.lat = lat;
        this.lon = lon;
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }


    public String getaddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
