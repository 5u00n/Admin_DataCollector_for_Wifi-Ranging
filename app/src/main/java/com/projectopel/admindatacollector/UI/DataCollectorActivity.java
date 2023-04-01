package com.projectopel.admindatacollector.UI;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projectopel.admindatacollector.Location.Constraints;
import com.projectopel.admindatacollector.Location.LocationService;
import com.projectopel.admindatacollector.R;
import com.projectopel.admindatacollector.UI.Maps.MapsActivity;
import com.projectopel.admindatacollector.UI.ViewData.DataProcessing.DataProcessingActivity;
import com.projectopel.admindatacollector.UI.ViewData.GatheredDataActivity;
import com.projectopel.admindatacollector.wifi.WifiScanReceiver;

public class DataCollectorActivity extends AppCompatActivity {

    private static final int GET_DATA_FROM_MAPS = 121;
    public static final double RADIUS = 6371; // Earth's radius in kilometers
    private ListView wifiList;
    private WifiManager wifiManager;
    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    private final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 2;
    WifiScanReceiver receiverWifi;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 3;
    private static final int REQUEST_FINE_LOCATION = 0;

    Context context = DataCollectorActivity.this;


    Button openGather, addLocation, buttonScan;

    int addPointsCount = 0;
    String name=null;
    String lat=null, lng=null,radius=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collector);

        openGather = findViewById(R.id.check_data);
        addLocation = findViewById(R.id.location_stop);
        wifiList = findViewById(R.id.wifiList);

        buttonScan = findViewById(R.id.scanBtn);
        if (myRequestLocation()) {
            startLocationService();
        }


        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "Turning WiFi ON...", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }


        buttonScan.setOnClickListener(v -> {
            rescanWifi();
            addLocationPoints();
        });


        addLocation.setOnClickListener(view -> {
            if(addLocation.getText().toString().equals("START COLLECTING DATA")){
                startActivityForResult(new Intent(DataCollectorActivity.this, MapsActivity.class),GET_DATA_FROM_MAPS);
                addLocation.setText("Finish Adding Points !");
            }
            else if(addLocation.getText().toString().equals("Finish Adding Points !")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DataCollectorActivity.this);
                builder.setMessage("Do you want to exit ?");

                builder.setTitle("Alert !");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                    finish();
                });
                builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.cancel();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

        });

        openGather.setOnClickListener(view -> {
            Intent i = new Intent(context, DataProcessingActivity.class);
            startActivity(i);

        });



        wifiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Log.d("Click Item :"+i, String.valueOf(adapterView.getItemAtPosition(i)));
                if(i==0){
                    if(name!=null) {
                        enableDisableButtonOnLocationNearToCurrentLocation();
                    }
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GET_DATA_FROM_MAPS){

            lat= data.getStringExtra("lat");
            lng= data.getStringExtra("lng");
            name= data.getStringExtra("name");
            radius = data.getStringExtra("radius");

            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference ref = db.getReference("location").child(name);

            ref.child("latitude").setValue(lat);
            ref.child("longitude").setValue(lng);
            ref.child("name").setValue(name);
            ref.child("radius").setValue(radius);

            openGather.setClickable(true);
            openGather.setVisibility(View.VISIBLE);
            buttonScan.setVisibility(View.VISIBLE);
            enableDisableButtonOnLocationNearToCurrentLocation();
        }
    }

    private void addLocationPoints() {

        initReceiver();
        if(name!=null && LocationService.longitude!=0) {
            addPointsCount++;
            buttonScan.setText(addPointsCount+" POINTS ADDED , ADD MORE +");

            receiverWifi.addPointsOfLocation(context,name);
        }
    }


    void rescanWifi() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DataCollectorActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
           // ActivityCompat.requestPermissions(DataCollectorActivity.this, new String[]{ACCESS_FINE_LOCATION}, MY_PERMISSIONS_ACCESS_FINE_LOCATION);
        } else {
            startLocationService();
            //addLocationSpots();
            wifiManager.startScan();
            //addLocationSpots();
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
       initReceiver();
    }

    private void initReceiver() {
        receiverWifi = new WifiScanReceiver(wifiManager, wifiList);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(receiverWifi, intentFilter);
        startLocationService();
        getWifi();
    }


    private boolean myRequestLocation() {
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
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Toast.makeText(context, "location turned off", Toast.LENGTH_SHORT).show();
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
                requestPermissions(new String[]{ACCESS_FINE_LOCATION}, MY_PERMISSIONS_ACCESS_FINE_LOCATION);
                ActivityCompat.requestPermissions(DataCollectorActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
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
    protected void onRestart() {
        super.onRestart();
        startLocationService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationService();
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

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationService();
        // unregisterReceiver(receiverWifi);
    }


    void enableDisableButtonOnLocationNearToCurrentLocation() {


                    double dist =haversine(Double.parseDouble(lat),Double.parseDouble(lng),LocationService.latitude,LocationService.longitude);

                    if(dist<=Integer.parseInt(radius)){
                        buttonScan.setClickable(true);
                        buttonScan.setEnabled(true);
                        addLocation.setText("Finish Adding Points !");
                    }else {
                        buttonScan.setEnabled(false);
                        buttonScan.setClickable(false);
                        addLocation.setText("START COLLECTING DATA");
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("You are not in range of the location you selected on the map , Please go inside the Radius !");

                        builder.setTitle("Alert !");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                        });
                        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                            dialog.cancel();
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                        ///Log.d("")
                    }


    }
    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = RADIUS * c;
        return distance*1000;
    }
}