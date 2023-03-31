package com.projectopel.admindatacollector.UI.ViewData.ListViewHelper;

public class GatheredDataModel {
    String BSSID,SSID,LAT,LON;

    public GatheredDataModel(String BSSID, String SSID, String LAT, String LON) {
        this.BSSID = BSSID;
        this.SSID = SSID;
        this.LAT = LAT;
        this.LON = LON;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getLAT() {
        return LAT;
    }

    public void setLAT(String LAT) {
        this.LAT = LAT;
    }

    public String getLON() {
        return LON;
    }

    public void setLON(String LON) {
        this.LON = LON;
    }
}
