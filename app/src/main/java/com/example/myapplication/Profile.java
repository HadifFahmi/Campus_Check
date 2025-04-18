package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class Profile extends AppCompatActivity {

    public Button btnback, btnlogout, btnchangepass, btnseeattend;
    public TextView tvid, tvname, tvuser, tvemail, tvphone, tvinout, tvuniname, tvfield, tvdivision;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        btnback = findViewById(R.id.btnBackPf);
        btnchangepass = findViewById(R.id.btnChangePass);
        btnlogout = findViewById(R.id.btnLogoutIntern);
        btnseeattend = findViewById(R.id.btnSeeAttend);
        tvid = findViewById(R.id.tvId);
        tvuser = findViewById(R.id.tvUsername);
        tvname = findViewById(R.id.tvName);
        tvemail = findViewById(R.id.tvEmail);
        tvphone = findViewById(R.id.tvPhone);
        tvinout = findViewById(R.id.tvInOut);
        tvuniname = findViewById(R.id.tvUniName);
        tvfield = findViewById(R.id.tvField);
        tvdivision = findViewById(R.id.tvDivision);

        SessionManager sessionManager = new SessionManager(this);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("sdetails");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uname = snapshot.child("username").getValue(String.class);
                    if (uname.equals(sessionManager.getUsername())) {
                        String fname = snapshot.child("fullName").getValue(String.class);
                        String id = snapshot.child("studentId").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String phone = snapshot.child("phoneNumber").getValue(String.class);
                        String inOut = snapshot.child("inOutStatus").getValue(String.class);
                        String uniName = snapshot.child("universityName").getValue(String.class);
                        String fos = snapshot.child("fieldOfStudy").getValue(String.class);
                        String division = snapshot.child("division").getValue(String.class);

                        tvname.setText("Full Name: " + fname);
                        tvuser.setText(("Username: " + uname));
                        tvid.setText("Student ID: " + id);
                        tvemail.setText("Email: " + email);
                        tvphone.setText("Phone Number: " + phone);
                        tvinout.setText("Inside/Outside: " + inOut);
                        tvuniname.setText("University Name: " + uniName);
                        tvfield.setText("Field of Study: " + fos);
                        tvdivision.setText("Division: " + division);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("InternView", "Database Error (Check-In): " + error.getMessage());
            }
        });

        btnback.setOnClickListener(v ->
        {
            startActivity(new Intent(this, InternView.class));
            finish();
        });

        btnchangepass.setOnClickListener(v ->
        {
            startActivity(new Intent(this, ChangePassword.class));
        });

        btnseeattend.setOnClickListener(v ->
        {
            startActivity(new Intent(this, AttendanceViewIntern.class));
        });

        btnlogout.setOnClickListener(v ->
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