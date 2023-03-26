package com.projectopel.admindatacollector.Login;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projectopel.admindatacollector.R;
import com.projectopel.admindatacollector.UI.DataCollectorActivity;

public class AuthActivity extends AppCompatActivity {

    EditText password;
    Button loginButton;


    Intent intent;
    String email, pass;


    FirebaseDatabase database;
    DatabaseReference reference;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        intent = getIntent();
        email = intent.getStringExtra("emailID");

        password = findViewById(R.id.auth_password);
        loginButton = findViewById(R.id.auth_button_login);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (password.getText().toString().isEmpty()) {
                    Toast.makeText(AuthActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                } else {
                    validateAndLogin(email, password.getText().toString());
                }
            }
        });
    }

    private void validateAndLogin(String email, String pass) {

        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success");
                    //FirebaseUser user = auth.getCurrentUser();
                    //checkFaceData(user);

                    startActivity(new Intent(AuthActivity.this, DataCollectorActivity.class));
                    finish();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(AuthActivity.this, "Authentication failed. Try Again !",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


}