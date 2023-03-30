package com.projectopel.admindatacollector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.projectopel.admindatacollector.Login.LoginActivity;
import com.projectopel.admindatacollector.UI.DataCollectorActivity;
import com.projectopel.admindatacollector.UI.Maps.MapsActivity;
import com.projectopel.admindatacollector.UI.ViewData.GatheredDataActivity;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth= FirebaseAuth.getInstance();

        if(auth.getCurrentUser()==null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }else {
            //startActivity(new Intent(MainActivity.this, DataCollectorActivity.class));
            startActivity(new Intent(MainActivity.this, MapsActivity.class));
            finish();
        }



    }
}