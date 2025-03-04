package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Profile extends AppCompatActivity {

    public Button btnback, btnlogout, btnchangepass, btnseeattend;
    public TextView tvid, tvname, tvuser;

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

        SessionManager sessionManager = new SessionManager(this);

        tvname.setText("Full Name: " + sessionManager.getName());
        tvuser.setText("Username: " + sessionManager.getUsername());
        tvid.setText("Student ID: " + sessionManager.getId());

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