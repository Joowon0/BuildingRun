package com.example.samsung.team_a;

public class SensorList {
    String SensorMAC="", SensorLat="", SensorLong="";

    public String getSensorMAC() {
        return SensorMAC;
    }

    @Override
    public String toString() {
        return "SensorList{" +
                "SensorMAC='" + SensorMAC + '\'' +
                ", SensorLat='" + SensorLat + '\'' +
                ", SensorLong='" + SensorLong + '\'' +
                '}';
    }

    public void setSensorMAC(String sensorMAC) {
        SensorMAC = sensorMAC;
    }

    public String getSensorLat() {
        return SensorLat;
    }

    public void setSensorLat(String sensorLat) {
        SensorLat = sensorLat;
    }

    public String getSensorLong() {
        return SensorLong;
    }

    public void setSensorLong(String sensorLong) {
        SensorLong = sensorLong;
    }
}
