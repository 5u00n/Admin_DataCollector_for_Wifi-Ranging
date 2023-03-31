package com.projectopel.admindatacollector.wifi;


import static android.content.Context.MODE_PRIVATE;
import static com.projectopel.admindatacollector.Helpers.LocationCalculation.calculateDistance;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projectopel.admindatacollector.Location.LocationService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressLint("MissingPermission")
public class WifiScanReceiver extends BroadcastReceiver {


    String name = null;
    private boolean addPointsCondition = false;
    WifiManager wifiManager;
    StringBuilder sb;
    ListView wifiDeviceList;
    FirebaseDatabase database;
    DatabaseReference dbRef;


    private static final int DEFALT_UPDATE_INTERVAL = 1;
    private static final int FAST_UPDATE_INTERVAL = 1;
    private static final int PERMISSION_FINE_LOCATION = 99;


    DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm:ss.SSS");
    String date;
    WifiDistanceData wfd;
    private ArrayList<WifiDistanceData> group = new ArrayList<>();

    Context context;

    public void setGroup(ArrayList<WifiDistanceData> group) {
        this.group = group;
    }

    public ArrayList<WifiDistanceData> getGroup() {
        return this.group;
    }

    public void emptyGroup() {
        this.group.clear();
    }

    public WifiScanReceiver(WifiManager wifiManager, ListView wifiDeviceList) {
        this.wifiManager = wifiManager;
        this.wifiDeviceList = wifiDeviceList;
        database = FirebaseDatabase.getInstance();
        //this.act = activity;
        // wfd = new WifiDistanceData();
    }

    public void onReceive(Context context, Intent intent) {

        boolean canWrite = getCanWrite(context);
        String name= getName(context);


        this.context = context;

        database = FirebaseDatabase.getInstance();

        dbRef = database.getReference("wifi_data");

        double lat, lon, altitu;
        String action = intent.getAction();
        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
            sb = new StringBuilder();
            List<ScanResult> wifiList = wifiManager.getScanResults();
            ArrayList<String> deviceList = new ArrayList<>();
            double distance = 0.0;

            //Looping for the
            int i = 0;
            for (ScanResult scanResult : wifiList) {

                sb.append("\n").append(scanResult.SSID).append(" - ").append(scanResult.capabilities);
                distance = calculateDistance(scanResult.level, scanResult.frequency);
                lat = LocationService.latitude;
                lon = LocationService.longitude;
                altitu = LocationService.altitude;
                if (i == 0) {
                    deviceList.add("*** Press here to refresh Buttons and location ***");
                    deviceList.add("Location ::  latitude:- " + lat + "  latitude:- " + lon);
                    setCanWrite(context, false);

                }
                deviceList.add("SSID :-   " + scanResult.SSID + "\nDistance :-   " + distance + " meter");


                // Log.d("LOCATION DATA : ", lat + "  :  " + lon);


                if (lat != 0 || lon != 0) {
                    avoidRewritingDataToNull(scanResult.BSSID, scanResult.SSID);
                    dbRef.child(scanResult.BSSID).child("wfd").child(String.valueOf(lat).replace(".", "_") + "-" + String.valueOf(lon).replace(".", "_")).setValue(new WifiDistanceData(lat, lon, altitu, distance));


                    Log.d("LOCATION POINTS CONDITION AND NAME", name+ " :: "+canWrite);
                    if(canWrite && !name.equals("")){
                        Log.d("CAN Write ", "YES YES YES ");
                        DatabaseReference locationref= database.getReference("location").child(name).child("points").child(String.valueOf(lat).replace(".", "_") + "-" + String.valueOf(lon).replace(".", "_")).child("wifi_data");
                        locationref.child(scanResult.BSSID).child("name").setValue(scanResult.SSID);
                        locationref.child(scanResult.BSSID).child("distance").setValue(distance);
                        setCanWrite(context, false);

                    }
                }
                i++;

            }
            ArrayAdapter arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, deviceList.toArray());
            wifiDeviceList.setAdapter(arrayAdapter);
        }
    }

    void avoidRewritingDataToNull(String BSSID, String SSID) {
        final String[] wifiSSID = {SSID};

        dbRef.child(BSSID).child("actual").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    if(wifiSSID[0].equals("")) {
                        wifiSSID[0] = "NULL";
                    }
                    dbRef.child(BSSID).child("actual").setValue(new WifiLocationColectionModel(wifiSSID[0], BSSID, 0, 0, 0));

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    public void addPointsOfLocation(Context context,String name) {

        SharedPreferences sharedPref = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("canWrite",true);
        editor.putString("name",name);
        editor.apply();

    }

    private void setCanWrite(Context context, boolean value) {
        SharedPreferences sharedPref = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("canWrite", value);
        editor.apply();
    }

    private boolean getCanWrite(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPref.getBoolean("canWrite", false);
    }
    private String getName(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPref.getString("name", "");
    }





}

