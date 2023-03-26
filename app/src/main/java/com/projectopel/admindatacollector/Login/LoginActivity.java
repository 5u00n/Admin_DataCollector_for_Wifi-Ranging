package com.projectopel.admindatacollector.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projectopel.admindatacollector.R;


public class LoginActivity extends AppCompatActivity {

    Button nextButton;
    EditText email;

    FirebaseDatabase database;
    DatabaseReference reference;

    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);




        database = FirebaseDatabase.getInstance();
        reference = database.getReference("EmployeeID");
        firebaseAuth = FirebaseAuth.getInstance();


        nextButton = findViewById(R.id.login_button_next);
        email = findViewById(R.id.login_email);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailString = email.getText().toString();
                if (emailString.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Email Field Blank", Toast.LENGTH_SHORT).show();
                } else {
                    if (isEmailValid(emailString) && emailString.contains("@mitwpu.edu.in")) {
                        checkForNewLogin(emailString);
                    } else {
                        Toast.makeText(LoginActivity.this, "Enter valid Email Provided By Institution !", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    public void checkForNewLogin(String emailS) {

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Boolean fl = false;
                for (DataSnapshot ds : snapshot.getChildren()) {


                    if (emailS.equals(ds.getValue().toString())) {


                        //startActivity(new Intent(LoginActivity.this, AuthActivity.class).putExtra("emailID",emailS));
                        //finish();
                        fl = true;

                    }
                }
                if (fl) {
                    firebaseAuth.fetchSignInMethodsForEmail(emailS)
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                                    boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                                    if (isNewUser) {
                                        Toast.makeText(LoginActivity.this, "Email not found", Toast.LENGTH_SHORT).show();
                                    } else {
                                        startActivity(new Intent(LoginActivity.this, AuthActivity.class).putExtra("emailID", emailS));
                                        finish();
                                    }

                                }
                            });
                } else {
                    Toast.makeText(LoginActivity.this, "Data Not Present. Please Contact Your Administrator!", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}