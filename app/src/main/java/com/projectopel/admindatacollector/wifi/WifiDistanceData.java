package com.projectopel.admindatacollector.wifi;

public class WifiDistanceData {
    double latitude,longitude,altitude, distance;

    public WifiDistanceData() {

    }

    public WifiDistanceData(double latitude, double longitude, double altitude, double distance) {
        if (latitude != 0) {
            this.latitude = latitude;
        } else {
            this.latitude = 0;
        }
        if (longitude != 0) {
            this.longitude = longitude;
        } else {
            this.longitude = 0;
        }

        if (altitude != 0){
            this.altitude = altitude;
    }else{
            this.altitude=0;
        }
        if (distance!=0) {
            this.distance = distance;
        }
        else{
            this.distance=0;
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getID() {
        return "ID"+latitude+longitude+altitude;
    }
}
