package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.mindrot.jbcrypt.BCrypt;

public class RegisterUser extends AppCompatActivity {

    private EditText fnameInput, unameInput, etIdentication, etP, etRP;
    private Button registerCfrm, confmBack;
    private DataBaseHapler dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // Initialize views
        fnameInput = findViewById(R.id.etFullname);
        unameInput = findViewById(R.id.etUsername);
        etIdentication = findViewById(R.id.etID);
        etP = findViewById(R.id.etPassword);
        etRP = findViewById(R.id.etRePassword);
        registerCfrm = findViewById(R.id.btnConfirmReg);
        confmBack = findViewById(R.id.btnBack);

        // Initialize database helper
        dbHelper = new DataBaseHapler(this);

        // Register button click listener
        registerCfrm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        confmBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterUser.this, MainActivity.class));
                finish();
            }
        });
    }

    private void registerUser() {
        String name = fnameInput.getText().toString().trim();
        String uname = unameInput.getText().toString().trim();
        String id = etIdentication.getText().toString().trim();
        String password = etP.getText().toString().trim();
        String repassword = etRP.getText().toString().trim();

        if (name.isEmpty() || uname.isEmpty() || id.isEmpty() || password.isEmpty() || repassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(repassword)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("students");
        databaseReference.orderByChild("username").equalTo(uname).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(RegisterUser.this, "Username already exists!", Toast.LENGTH_SHORT).show();
                } else {
                    // Save User Using DataBaseHapler
                   String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
                   StudentModel students = new StudentModel(id, name, uname, hashedPassword);
                   dbHelper.saveUserToFirebase(students, new DataBaseHapler.DatabaseCallback() {
                        @Override
                        public void onSuccess() {
                            startActivity(new Intent(RegisterUser.this, MainActivity.class));
                            finish(); // Close RegisterUser
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Toast.makeText(RegisterUser.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RegisterUser.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}