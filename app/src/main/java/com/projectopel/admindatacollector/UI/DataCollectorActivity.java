package com.projectopel.admindatacollector.UI;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.projectopel.admindatacollector.Location.Constraints;
import com.projectopel.admindatacollector.Location.LocationService;
import com.projectopel.admindatacollector.R;
import com.projectopel.admindatacollector.UI.ViewData.GatheredDataActivity;
import com.projectopel.admindatacollector.UI.Visualization.CalculatorActivity;
import com.projectopel.admindatacollector.wifi.WifiScanReceiver;

public class DataCollectorActivity extends AppCompatActivity {

    private ListView wifiList;
    private WifiManager wifiManager;
    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    private final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 2;
    WifiScanReceiver receiverWifi;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 3;

    private static final int REQUEST_FINE_LOCATION = 0;

    Context context = DataCollectorActivity.this;


    Button openGather, stopL, buttonScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collector);

        openGather = findViewById(R.id.check_data);
        stopL = findViewById(R.id.location_stop);
        wifiList = findViewById(R.id.wifiList);
        buttonScan = findViewById(R.id.scanBtn);
        mayRequestLocation();


        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "Turning WiFi ON...", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }


        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DataCollectorActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
                    ActivityCompat.requestPermissions(DataCollectorActivity.this, new String[]{ACCESS_FINE_LOCATION}, MY_PERMISSIONS_ACCESS_FINE_LOCATION);
                    ActivityCompat.requestPermissions(DataCollectorActivity.this, new String[]{ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
                } else {
                    startLocationService();
                    wifiManager.startScan();
                }
            }
        });


        openGather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, GatheredDataActivity.class);
                startActivity(i);

            }
        });

        stopL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopLocationService();
            }
        });
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        receiverWifi = new WifiScanReceiver(wifiManager, wifiList);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(receiverWifi, intentFilter);
        getWifi();
    }


    private boolean mayRequestLocation() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        requestPermissions(new String[]{ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);

        return false;
    }

    private void getWifi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Toast.makeText(context, "version> = marshmallow", Toast.LENGTH_SHORT).show();
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
               // Toast.makeText(context, "location turned off", Toast.LENGTH_SHORT).show();
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
                requestPermissions(new String[]{ACCESS_FINE_LOCATION}, MY_PERMISSIONS_ACCESS_FINE_LOCATION);
                ActivityCompat.requestPermissions(DataCollectorActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
            } else {
               // Toast.makeText(context, "location turned on", Toast.LENGTH_SHORT).show();
                wifiManager.startScan();
            }
        } else {
            //Toast.makeText(context, "scanning", Toast.LENGTH_SHORT).show();
            wifiManager.startScan();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiverWifi);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_COARSE_LOCATION:

            case REQUEST_CODE_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   // Toast.makeText(context, "LOCATION permission granted", Toast.LENGTH_SHORT).show();
                    startLocationService();
                    wifiManager.startScan();
                } else {
                  //  Toast.makeText(context, "LOCATION permission not granted", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            //Toast.makeText(context, "FINE LOCATION permission granted", Toast.LENGTH_SHORT).show();
            // Toast.makeText(context, "FINE LOCATION permission not granted", Toast.LENGTH_SHORT).show();
            case REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // The requested permission is granted.

                    startLocationService();
                    wifiManager.startScan();
                } else {
                    // The user disallowed the requested permission.
                }
                return;
            }
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equals(service.service.getClassName())) {
                    if (service.foreground) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }


    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constraints.ACTION_START_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "LocationService Started", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constraints.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "LocationService Stopped", Toast.LENGTH_SHORT).show();
        }
    }
}