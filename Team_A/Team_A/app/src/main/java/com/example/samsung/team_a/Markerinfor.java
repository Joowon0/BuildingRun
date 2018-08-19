package com.example.samsung.team_a;

public class Markerinfor {
    double lat;
    double lon;
    int SSN;

    public Markerinfor(double lat, double lon, int SSN) {
        this.lat = lat;
        this.lon = lon;
        this.SSN = SSN;
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

    public int getSSN() {
        return SSN;
    }

    public void setSSN(int SSN) {
        this.SSN = SSN;
    }
}
