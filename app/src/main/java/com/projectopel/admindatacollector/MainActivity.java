package com.projectopel.admindatacollector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.projectopel.admindatacollector.Login.LoginActivity;
import com.projectopel.admindatacollector.UI.DataCollectorActivity;
import com.projectopel.admindatacollector.UI.Maps.MapsActivity;
import com.projectopel.admindatacollector.UI.ViewData.DataProcessing.DataProcessingActivity;
import com.projectopel.admindatacollector.UI.ViewData.DataProcessing.DataProcessingAdapter;
import com.projectopel.admindatacollector.UI.ViewData.GatheredDataActivity;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;

    Button addNew, viewCollected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth= FirebaseAuth.getInstance();

        if(auth.getCurrentUser()==null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
        else{

            initMain();
        }

    }

    private void initMain() {
        setContentView(R.layout.activity_main);
        addNew= findViewById(R.id.main_add_points);
        viewCollected= findViewById(R.id.main_go_to_collected);


        addNew.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this,DataCollectorActivity.class));
        });

        viewCollected.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this,DataProcessingActivity.class));

        });
    }
}