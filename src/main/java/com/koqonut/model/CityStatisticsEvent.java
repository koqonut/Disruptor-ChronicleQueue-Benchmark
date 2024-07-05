package com.koqonut.model;

public class CityStatisticsEvent {
    private String city;
    private double minTemperature;
    private double maxTemperature;
    private double sumTemperature;
    private long numOfReadings;

    @Override
    public String toString() {
        return "CityStatisticsEvent{" +
                "city='" + city + '\'' +
                ", minTemperature=" + minTemperature +
                ", maxTemperature=" + maxTemperature +
                ", sumTemperature=" + sumTemperature +
                ", numOfReadings=" + numOfReadings +
                '}';
    }

    public long getNumOfReadings() {
        return numOfReadings;
    }

    public void setNumOfReadings(long numOfReadings) {
        this.numOfReadings = numOfReadings;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public double getSumTemperature() {
        return sumTemperature;
    }

    public void setSumTemperature(double sumTemperature) {
        this.sumTemperature = sumTemperature;
    }


}
