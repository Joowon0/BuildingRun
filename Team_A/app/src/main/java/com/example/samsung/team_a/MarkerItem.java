package com.example.samsung.team_a;

/**
 * Created by TedPark on 16. 4. 26..
 */
public class MarkerItem {


    double lat;
    double lon;
    int ssn;
    String name;
    String address;

    public MarkerItem(double lat, double lon, int ssn, String name ,String address) {
        this.lat = lat;
        this.lon = lon;
        this.ssn = ssn;
        this.name = name;
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

    public int getssn() {
        return ssn;
    }

    public void sessn(int ssn) {
        this.ssn = ssn;
    }

    public String getaddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name ;
    }
}
