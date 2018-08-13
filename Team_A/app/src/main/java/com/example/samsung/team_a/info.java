package com.example.samsung.team_a;

public class info {
    double SO2,NO2,O3,CO,Temp,PM25;

    public info(double SO2,  double NO2,double O3,double CO,double Temp,double PM25) {
        this.SO2 = SO2;
        this.NO2 = NO2;
        this.O3 = O3;
        this.CO = CO;
        this.Temp = Temp;
        this.PM25 = PM25;
    }

    @Override
    public String toString() {
        return "info{" +
                "SO2=" + SO2 +
                ", NO2=" + NO2 +
                ", O3=" + O3 +
                ", CO=" + CO +
                ", Temp=" + Temp +
                ", PM25=" + PM25 +
                '}';
    }

    public void setSO2(double SO2) {
        this.SO2 = SO2;
    }

    public void setNO2(double NO2) {
        this.NO2 = NO2;
    }

    public void setO3(double o3) {
        O3 = o3;
    }

    public void setCO(double CO) {
        this.CO = CO;
    }

    public void setTemp(double temp) {
        Temp = temp;
    }

    public void setPM25(double PM25) {
        this.PM25 = PM25;
    }

    public double getSO2() {

        return SO2;
    }

    public double getNO2() {
        return NO2;
    }

    public double getO3() {
        return O3;
    }

    public double getCO() {
        return CO;
    }

    public double getTemp() {
        return Temp;
    }

    public double getPM25() {
        return PM25;
    }
}