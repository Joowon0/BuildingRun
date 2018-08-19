package com.example.samsung.team_a;

public class dataTransfer {

    private static String mac = "", time = "", result = "";
    private static double CO = 0.0, NO2 = 0.0, temperature = 0.0, O3 = 0.0, SO2 = 0.0, PM25 = 0.0;
    public static String getTime() {
        return time;
    }

    public static void setTime(String time) {
        dataTransfer.time = time;
    }
    public static String getResult() {
        return result;
    }

    public static void setResult(String result) {
        dataTransfer.result = result;
    }

    public static double getCO() {
        return CO;
    }

    public static void setCO(double CO) {
        dataTransfer.CO = CO;
    }

    public static double getNO2() {
        return NO2;
    }

    public static void setNO2(double NO2) {
        dataTransfer.NO2 = NO2;
    }

    public static double getTemperature() {
        return temperature;
    }

    public static void setTemperature(double temperature) {
        dataTransfer.temperature = temperature;
    }

    public static double getO3() {
        return O3;
    }

    public static void setO3(double o3) {
        O3 = o3;
    }

    public static double getSO2() {
        return SO2;
    }

    public static void setSO2(double SO2) {
        dataTransfer.SO2 = SO2;
    }

    public static double getPM25() {
        return PM25;
    }

    public static void setPM25(double PM25) {
        dataTransfer.PM25 = PM25;
    }

    public static void setMac(String ma) {
        dataTransfer.mac = ma;
    }

    public static String getMac() {
        return mac;
    }
}
