package com.projectopel.admindatacollector.UI.ViewData;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.projectopel.admindatacollector.R;

import java.util.ArrayList;

public class GatheredDataActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference dbRef;

    Button res, cal, back;
    ListView locationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gathered_data);


        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("wifi_data");


        res = findViewById(R.id.result_wifi_btn);
        cal = findViewById(R.id.calculate_location_btn);
        back = findViewById(R.id.return_main_btn);

        locationList = findViewById(R.id.result_location_list);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<GatheredDataModel> deviceList = new ArrayList<>();
                Gson gson= new Gson();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    deviceList.add(new GatheredDataModel(ds.getKey(), ds.child("actual").child("wifi_name").getValue().toString(), " ", " "));
                    Log.d("Getting Data", ds.child("actual").child("wifi_name").getValue().toString());
                }
                GatheredDataAdapter gatheredDataAdapter = new GatheredDataAdapter(getBaseContext(), deviceList);
                locationList.setAdapter(gatheredDataAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}