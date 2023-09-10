package com.projectopel.admindatacollector.UI.ViewData.DataProcessFragments;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.projectopel.admindatacollector.Location.Constraints;
import com.projectopel.admindatacollector.Location.LocationService;
import com.projectopel.admindatacollector.R;
import com.projectopel.admindatacollector.UI.ViewData.ListViewHelper.GatheredDataAdapter;
import com.projectopel.admindatacollector.UI.ViewData.ListViewHelper.GatheredDataModel;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment {

    FirebaseDatabase database;
    DatabaseReference dbRef;

    Context context;

    int i = 0;
    private List<LatLng> points;

    private GoogleMap mMap;

    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;


    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            // Add a marker in Sydney and move the camera
            //  LatLng eiffel = new LatLng(LocationService.latitude, LocationService.longitude);
            if (LocationService.latitude != 0 && LocationService.longitude != 0) {
                LatLng currentLoc = new LatLng(LocationService.latitude, LocationService.longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 40));
            }
            enableUserLocation();

            points = new ArrayList<>();


            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    DataSnapshot actualSnaps = snapshot.child("wifi_data");
                    DataSnapshot locationSnaps = snapshot.child("location");

                    if (actualSnaps.exists()) {
                        // Log.d("From Maps Visualization","Snapshot Exist");

                        //Gson gson = new Gson();
                        for (DataSnapshot ds : actualSnaps.getChildren()) {
                            if (ds.child("actual").exists() && !ds.child("actual").child("wifi_latitude").getValue().toString().equals("0")) {


                                //Log.d("From Maps Visualization",ds.child("actual").child("wifi_latitude").getValue().toString());


                                addMarker(new LatLng(Double.parseDouble(ds.child("actual").child("wifi_latitude").getValue().toString()), Double.parseDouble(ds.child("actual").child("wifi_longitude").getValue().toString())), ds.child("actual").child("wifi_name").getValue().toString(), ds.child("actual").child("wifi_mac_addr").getValue().toString());
                                //deviceList.add(new GatheredDataModel(ds.getKey(), ds.child("actual").child("wifi_name").getValue().toString(), ds.child("actual").child("wifi_latitude").getValue().toString(), ds.child("actual").child("wifi_longitude").getValue().toString()));
                            } else {
                                //deviceList.add(new GatheredDataModel(ds.getKey(), "null", "null", "null"));
                            }
                            //Log.d("Getting Data", ds.child("actual").child("wifi_name").getValue().toString());
                        }
                        //GatheredDataAdapter gatheredDataAdapter = new GatheredDataAdapter(getBaseContext(), deviceList);
                        // locationList.setAdapter(gatheredDataAdapter);
                    }
                    if (locationSnaps.exists()) {
                        for (DataSnapshot lds : locationSnaps.getChildren()) {
                            int drawFlag = 0;
                            addMarkerLocation(new LatLng(Double.parseDouble(lds.child("latitude").getValue().toString()), Double.parseDouble(lds.child("longitude").getValue().toString())), lds.child("name").getValue().toString(), lds.child("radius").getValue().toString());
                            if (lds.child("points").exists()) {
                                if (lds.child("points").getChildrenCount() >= 4) {
                                    drawFlag = 1;
                                    for (DataSnapshot pointsDS : lds.child("points").getChildren()) {

                                        String[] latlng = pointsDS.getKey().toString().split("-");


                                        Double lat = Double.valueOf(latlng[0].replace("_", "."));
                                        Double lng = Double.valueOf(latlng[1].replace("_", "."));
                                        //Log.d("Maps Fragment",lat+" : "+lng);
                                        points.add(new LatLng(lat, lng));
                                    }

                                }
                            }

                            if (drawFlag == 1) {
                                PolygonOptions polygonOptions = new PolygonOptions();
                                polygonOptions.addAll(points);
                                polygonOptions.strokeColor(Color.BLUE);
                                polygonOptions.fillColor(Color.argb(20, 0, 0, 255));
                                mMap.addPolygon(polygonOptions);
                                points.clear();
                                drawFlag=0;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Log.d("From Maps Visualization","Cancelled");

                }
            });


        }
    };


    private void addCircle(LatLng latLng, float radius) {

        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
        circleOptions.fillColor(Color.argb(20, 255, 0, 0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        context=getContext();
        View v = inflater.inflate(R.layout.fragment_maps, container, false);


        startLocationService();
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();


        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }


    private void enableUserLocation() {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(@NonNull Location location) {
                    //Log.d("Location",location.getLatitude()+ " "+location.getLongitude()+" -- "+i);
                    if (i == 0) {
                        LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 16));
                        i++;
                    }
                }
            });
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                //We do not have the permission..

            }
        }

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission\
                //Toast.makeText(this, "You can add geofences...", Toast.LENGTH_SHORT).show();
            } else {
                //We do not have the permission..
                //Toast.makeText(this, "Background location access is neccessary for geofences to trigger...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addMarker(LatLng latLng, String SSID, String BSSID) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Name : " + SSID + "\nMAC : " + BSSID).snippet("latitude : " + latLng.latitude + "\nlongitude : " + latLng.longitude).icon(bitmapDescriptorFromVector(context, R.drawable.baseline_wifi_24));
        mMap.addMarker(markerOptions);
    }

    private void addMarkerLocation(LatLng latLng, String name, String radius) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Name : " + name).snippet("radius : " + radius);
        mMap.addMarker(markerOptions);
        addCircle(latLng, Float.parseFloat(radius));
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
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
            Intent intent = new Intent(context, LocationService.class);
            intent.setAction(Constraints.ACTION_START_LOCATION_SERVICE);
            context.startService(intent);
            //Log.d ("LocationService Started",LocationService.latitude+" : "+LocationService.longitude);
            //Toast.makeText(this, "LocationService Started", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent intent = new Intent(context, LocationService.class);
            intent.setAction(Constraints.ACTION_STOP_LOCATION_SERVICE);
            context.startService(intent);
            // Toast.makeText(this, "LocationService Stopped", Toast.LENGTH_SHORT).show();
        }
    }

}