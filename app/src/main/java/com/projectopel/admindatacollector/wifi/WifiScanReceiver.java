package com.projectopel.admindatacollector.wifi;


import static com.projectopel.admindatacollector.Helpers.LocationCalculation.calculateDistance;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
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

@SuppressLint("MissingPermission")
public class WifiScanReceiver extends BroadcastReceiver {
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


        this.context= context;

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
                if(i==0){
                    deviceList.add("Location ::  latitude:- "+lat+"  latitude:- "+lon);
                }
                deviceList.add("SSID :-   " + scanResult.SSID + "\nDistance :-   " + distance + " meter");


               // Log.d("LOCATION DATA : ", lat + "  :  " + lon);


                if (lat != 0 || lon != 0) {
                    avoidRewritingDataToNull(scanResult.BSSID, scanResult.SSID);
                    dbRef.child(scanResult.BSSID).child("wfd").child(String.valueOf(lat).replace(".", "_") + "-" + String.valueOf(lon).replace(".", "_")).setValue(new WifiDistanceData(lat, lon, altitu, distance));

                }
                i++;

            }
            ArrayAdapter arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, deviceList.toArray());
            wifiDeviceList.setAdapter(arrayAdapter);
        }
    }

    void avoidRewritingDataToNull(String BSSID, String SSID) {
        dbRef.child(BSSID).child("actual").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    dbRef.child(BSSID).child("actual").setValue(new WifiLocationColectionModel(SSID, BSSID, 0, 0, 0));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    public void addPointsOfLocation(){
        DatabaseReference locationref= database.getReference("location");
    }

    public void LogFromActivity(String data){
        Log.d("String from activity ------",data);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to exit?").setCancelable(
                false).setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


}

