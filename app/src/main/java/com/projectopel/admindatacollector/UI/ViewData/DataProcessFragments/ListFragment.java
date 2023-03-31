package com.projectopel.admindatacollector.UI.ViewData.DataProcessFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.projectopel.admindatacollector.R;
import com.projectopel.admindatacollector.UI.ViewData.ListViewHelper.GatheredDataAdapter;
import com.projectopel.admindatacollector.UI.ViewData.ListViewHelper.GatheredDataModel;

import java.util.ArrayList;


public class ListFragment extends Fragment {


    FirebaseDatabase database;
    DatabaseReference dbRef;

    ListView locationList;

    public ListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_list, container, false);



        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("wifi_data");

        locationList = v.findViewById(R.id.result_location_list);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    ArrayList<GatheredDataModel> deviceList = new ArrayList<>();
                    Gson gson = new Gson();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.child("actual").exists()) {
                            deviceList.add(new GatheredDataModel(ds.getKey(), ds.child("actual").child("wifi_name").getValue().toString(), ds.child("actual").child("wifi_latitude").getValue().toString(), ds.child("actual").child("wifi_longitude").getValue().toString()));
                        } else {
                            deviceList.add(new GatheredDataModel(ds.getKey(), "null", "null", "null"));
                        }
                      //  Log.d("Getting Data", ds.child("actual").child("wifi_name").getValue().toString());
                    }
                    GatheredDataAdapter gatheredDataAdapter = new GatheredDataAdapter(getContext(), deviceList);
                    locationList.setAdapter(gatheredDataAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return v;
    }
}