package com.example.foodorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterUserActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private EditText nameET, phoneET, countryET, stateET, cityET, addressET, emailET, passwordET, cPasswordET;
    private Button registerBtn;
    private TextView registerSellerTv;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        backBtn = findViewById(R.id.backBtn);
        nameET = findViewById(R.id.nameET);
        phoneET = findViewById(R.id.phoneET);
        countryET = findViewById(R.id.countryET);
        stateET = findViewById(R.id.stateET);
        cityET = findViewById(R.id.cityET);
        addressET = findViewById(R.id.addressET);
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        cPasswordET = findViewById(R.id.cPasswordET);
        registerBtn = findViewById(R.id.registerBtn);
        registerSellerTv = findViewById(R.id.registerSellerTv);

        firebaseAuth = FirebaseAuth.getInstance();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //register user
                inputData();
            }
        });

        registerSellerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterUserActivity.this, RegisterSellerActivity.class));
            }
        });



    }
    private String fullName, phoneNumber, country, state, city, address, email, password, confirmPassword;
    private void inputData() {

        //input data
        fullName = nameET.getText().toString().trim();
        phoneNumber = phoneET.getText().toString().trim();
        country = countryET.getText().toString().trim();
        state = stateET.getText().toString().trim();
        city = cityET.getText().toString().trim();
        address = addressET.getText().toString().trim();
        email = emailET.getText().toString().trim();
        password = passwordET.getText().toString().trim();
        confirmPassword = cPasswordET.getText().toString().trim();

        //data validation
        if (TextUtils.isEmpty(fullName)){
            Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show();
        }


        if (TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
        }


        if (TextUtils.isEmpty(country)){
            Toast.makeText(this, "Enter Country", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(state)){
            Toast.makeText(this, "Enter State", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(city)){
            Toast.makeText(this, "Enter City", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(address)){
            Toast.makeText(this, "Enter Address", Toast.LENGTH_SHORT).show();
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
        }

        if (password.length()<6){
            Toast.makeText(this, "Password must be at least 6 character long", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(confirmPassword)){
            Toast.makeText(this, "Enter Confirm Password", Toast.LENGTH_SHORT).show();
        }

        createAccount();
    }

    private void createAccount() {
        //create Account
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //account created
                saverFirebaseData();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saverFirebaseData() {

        String timestamp = ""+System.currentTimeMillis();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid",""+firebaseAuth.getUid());
        hashMap.put("email",""+email);
        hashMap.put("name",""+fullName);
        hashMap.put("phone",""+phoneNumber);
        hashMap.put("country",""+country);
        hashMap.put("state",""+state);
        hashMap.put("city",""+city);
        hashMap.put("address",""+address);
        hashMap.put("timestamp",""+timestamp);
        hashMap.put("accountType","User");
        hashMap.put("online","true");
        hashMap.put("shopOpen","true");

        //save to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //db updated
                startActivity(new Intent(RegisterUserActivity.this, MainUserActivity.class));
                finish();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        startActivity(new Intent(RegisterUserActivity.this, MainUserActivity.class));
                        finish();
                    }
                });

    }
}