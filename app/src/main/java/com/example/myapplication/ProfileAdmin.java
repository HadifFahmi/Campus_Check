package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileAdmin extends AppCompatActivity {

    public Button btnbackadmin, btnlogoutadmin, btnchangepassadmin;
    public TextView tvnameadmin, tvuseradmin, tvidadmin,tvemailadmin,tvphoneadmin;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_admin);

        tvnameadmin = findViewById(R.id.tvNameAdmin);
        tvuseradmin = findViewById(R.id.tvUsernameAdmin);
        tvidadmin = findViewById(R.id.tvIdAdmin);
        tvemailadmin = findViewById(R.id.tvEmailAdmin);
        tvphoneadmin = findViewById(R.id.tvPhoneAdmin);

        SessionManager sessionManager = new SessionManager(this);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("tdetails");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uname = snapshot.child("username").getValue(String.class);
                    if (uname.equals(sessionManager.getUsername())) {
                        String fname = snapshot.child("fullName").getValue(String.class);
                        String id = snapshot.child("identification").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String phone = snapshot.child("phoneNumber").getValue(String.class);

                        tvnameadmin.setText("Full Name: " + fname);
                        tvuseradmin.setText(("Username: " + uname));
                        tvidadmin.setText("Staff ID: " + id);
                        tvemailadmin.setText("Email: " + email);
                        tvphoneadmin.setText("Phone Number: " + phone);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("InternView", "Database Error (Check-In): " + error.getMessage());
            }
        });

        btnbackadmin = findViewById(R.id.btnBackPA);
        btnchangepassadmin = findViewById(R.id.btnChangePassAdmin);
        btnlogoutadmin = findViewById(R.id.btnLogoutAdmin);

        btnbackadmin.setOnClickListener(v ->
        {
            startActivity(new Intent(this, AdminView.class));
            finish();
        });

        btnchangepassadmin.setOnClickListener(v ->
        {
            startActivity(new Intent(this, ChangePassword.class));
        });

        btnlogoutadmin.setOnClickListener(v ->
        {
            sessionManager.logout();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}