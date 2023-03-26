package com.projectopel.admindatacollector.wifi;

public class WifiLocationColectionModel {
    private String wifi_name, wifi_mac_addr;
    private double wifi_latitude, wifi_longitude,wifi_altitude;
   // private ArrayList<WifiDistanceData> wfd= new ArrayList<>();

    public WifiLocationColectionModel(Object obj){

    }

    public WifiLocationColectionModel(String wifi_name, String wifi_mac_addr, double wifi_latitude, double wifi_longitude, double wifi_altitude) {
        this.wifi_name = wifi_name;
        this.wifi_mac_addr = wifi_mac_addr;
        this.wifi_latitude = wifi_latitude;
        this.wifi_longitude = wifi_longitude;
        this.wifi_altitude = wifi_altitude;

    }

    public String getWifi_name() {
        return wifi_name;
    }

    public void setWifi_name(String wifi_name) {
        this.wifi_name = wifi_name;
    }

    public String getWifi_mac_addr() {
        return wifi_mac_addr;
    }

    public void setWifi_mac_addr(String wifi_mac_addr) {
        this.wifi_mac_addr = wifi_mac_addr;
    }

    public double getWifi_latitude() {
        return wifi_latitude;
    }

    public void setWifi_latitude(double wifi_latitude) {
        this.wifi_latitude = wifi_latitude;
    }

    public double getWifi_longitude() {
        return wifi_longitude;
    }

    public void setWifi_longitude(double wifi_longitude) {
        this.wifi_longitude = wifi_longitude;
    }

    public double getWifi_altitude() {
        return wifi_altitude;
    }

    public void setWifi_altitude(double wifi_altitude) {
        this.wifi_altitude = wifi_altitude;
    }

}
