package com.projectopel.admindatacollector.UI.ViewData.DataProcessing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projectopel.admindatacollector.R;

import org.locationtech.jts.algorithm.Centroid;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

public class DataProcessingActivity extends AppCompatActivity {


    TabLayout tabLayout;
    ViewPager viewPager;
    Button cal, back;

    FirebaseDatabase database;
    DatabaseReference dbRef;

    DataProcessingAdapter adapter;



    GeometryFactory geometryFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_processing);

        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("wifi_data");

        geometryFactory = new GeometryFactory();


        tabLayout = findViewById(R.id.tabs_layout);
        viewPager = findViewById(R.id.view_pager);

        cal = findViewById(R.id.calculate_location_btn);
        back = findViewById(R.id.return_main_btn);

        tabLayout.addTab(tabLayout.newTab().setText("Data"));
        tabLayout.addTab(tabLayout.newTab().setText("Maps"));

        adapter = new DataProcessingAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.getCurrentItem();

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cal.setOnClickListener(view -> {
            readAndCalculateData();
        });



    }

    void readAndCalculateData(){
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    for(DataSnapshot ds : snapshot.getChildren()){
                        if(ds.child("actual").child("wifi_longitude").getValue()!="0"){
                            //Log.d("data present : ", ds.child("actual").child("wifi_name").getValue().toString()+" ::: Not Yet Calculated ");
                            if(ds.child("wfd").exists()){

                                if(ds.child("wfd").getChildrenCount()>=4) {

                                    int c=0;
                                    Geometry[] circles = new Geometry[(int) ds.child("wfd").getChildrenCount()];

                                    for (DataSnapshot wds : ds.child("wfd").getChildren()) {
                                       // Log.d("\t\t\t"+c,"--------------------------------------------------------------------------------");
                                        double lon= Double.parseDouble(wds.child("longitude").getValue().toString());
                                        double lat = Double.parseDouble(wds.child("latitude").getValue().toString());
                                        double dist= Double.parseDouble(wds.child("distance").getValue().toString());
                                        //Log.d("\t\t\tLongitude :  ", String.valueOf(lon));
                                       // Log.d("\t\t\tLatitude :  ", String.valueOf(lat));
                                      //  Log.d("\t\t\tDistance :  ", String.valueOf(dist));
                                        circles[c]=geometryFactory.createPoint(new Coordinate(lat,lon)).buffer(dist);

                                        c++;
                                        // if(c>=4) break;
                                    }
                                    //Function Calling
                                    calculateCentroid(circles,ds.getKey());


                                }else{
                                  //  Log.d("**********  Scanned data",ds.child("actual").child("wifi_name").getValue().toString()+ " ::: not enough data ");
                                }
                            }else{
                               // Log.d("**********  Scanned data",ds.child("actual").child("wifi_name").getValue().toString()+ " ::: not present");
                            }

                        }else{
                            //Show data to Layout / or maps --------------- IMP ****************
                        }
                    }


                }else {
                    //Log.d("data present :", "::: False");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    void calculateCentroid(Geometry[] circles,String key){

        Geometry intersection = circles[0];
        for (int i = 1; i < circles.length; i++) {
            intersection = intersection.intersection(circles[i]);
        }

        // Compute the centroid of the intersection polygon
        Centroid centroid = new Centroid(intersection);
        Coordinate centroidCoord = centroid.getCentroid();

        // Print the centroid coordinates
       // Log.d("\t\t\tCentroid coordinates: ", centroidCoord.x + ", " + centroidCoord.y);
        //Log.d("\t\t\tKey",key);

        dbRef.child(key).child("actual").child("wifi_latitude").setValue(centroidCoord.x );
        dbRef.child(key).child("actual").child("wifi_longitude").setValue(centroidCoord.y );

    }
}