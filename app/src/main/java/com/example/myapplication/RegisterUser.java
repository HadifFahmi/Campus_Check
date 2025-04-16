package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

    private EditText fnameInput, unameInput, etIdentication, etP, etRP, etemail, etPhoneno, etInout, etUniname, etfield, etdivis;
    private Spinner spinner2;
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
        etemail = findViewById(R.id.etEmAIL);
        etPhoneno = findViewById(R.id.etPhone);
        etInout = findViewById(R.id.etInOut);
        etUniname = findViewById(R.id.etUniName);
        etfield = findViewById(R.id.etField);
        etdivis = findViewById(R.id.etDivision);

        registerCfrm = findViewById(R.id.btnConfirmReg);
        confmBack = findViewById(R.id.btnBack);

        // Initialize database helper
        dbHelper = new DataBaseHapler(this);

        spinner2 = findViewById(R.id.spinner2);

        String[] roles = getResources().getStringArray(R.array.roles);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter);

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1: // Intern
                        fnameInput.setVisibility(View.VISIBLE);
                        unameInput.setVisibility(View.VISIBLE);
                        etIdentication.setVisibility(View.VISIBLE);
                        etP.setVisibility(View.VISIBLE);
                        etRP.setVisibility(View.VISIBLE);
                        registerCfrm.setVisibility(View.VISIBLE);
                        etemail.setVisibility(View.VISIBLE);
                        etPhoneno.setVisibility(View.VISIBLE);
                        etInout.setVisibility(View.VISIBLE);
                        etUniname.setVisibility(View.VISIBLE);
                        etfield.setVisibility(View.VISIBLE);
                        etdivis.setVisibility(View.VISIBLE);
                        break;
                    case 2: // Admin
                        fnameInput.setVisibility(View.VISIBLE);
                        unameInput.setVisibility(View.VISIBLE);
                        etIdentication.setVisibility(View.VISIBLE);
                        etP.setVisibility(View.VISIBLE);
                        etRP.setVisibility(View.VISIBLE);
                        registerCfrm.setVisibility(View.VISIBLE);
                        etemail.setVisibility(View.VISIBLE);
                        etPhoneno.setVisibility(View.VISIBLE);
                        etInout.setVisibility(View.GONE);
                        etUniname.setVisibility(View.GONE);
                        etfield.setVisibility(View.GONE);
                        etdivis.setVisibility(View.GONE);
                        break;
                    default: // Default (No role selected)
                        fnameInput.setVisibility(View.GONE);
                        unameInput.setVisibility(View.GONE);
                        etIdentication.setVisibility(View.GONE);
                        etP.setVisibility(View.GONE);
                        etRP.setVisibility(View.GONE);
                        registerCfrm.setVisibility(View.GONE);
                        etemail.setVisibility(View.GONE);
                        etPhoneno.setVisibility(View.GONE);
                        etInout.setVisibility(View.GONE);
                        etUniname.setVisibility(View.GONE);
                        etfield.setVisibility(View.GONE);
                        etdivis.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });


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
        String email = etemail.getText().toString().trim();
        String phone = etPhoneno.getText().toString().trim();
        String inOut = etInout.getText().toString().trim();
        String uniName = etUniname.getText().toString().trim();
        String field = etfield.getText().toString().trim();
        String division = etdivis.getText().toString().trim();

        String selectedRole2 = spinner2.getSelectedItem().toString();

        if (selectedRole2.equals("Intern")) {
            if (name.isEmpty() || uname.isEmpty() || id.isEmpty() || password.isEmpty() ||
                    repassword.isEmpty() || email.isEmpty() || phone.isEmpty() || inOut.isEmpty() ||
                    uniName.isEmpty() || field.isEmpty() || division.isEmpty()) {
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
                        // Save Details seperately from login details
                        SDetailsModel sdetails = new SDetailsModel(name, uname, id, email, phone, inOut, uniName, field, division);
                        dbHelper.registerSDetailsToFirebase(sdetails, new DataBaseHapler.DatabaseCallback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                Toast.makeText(RegisterUser.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });

                        // Save User Using DataBaseHapler
                        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
                        StudentModel students = new StudentModel(id, name, uname, hashedPassword);
                        dbHelper.registerUserToFirebase(students, new DataBaseHapler.DatabaseCallback() {
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
        } else if (selectedRole2.equals("Admin")) {
            if (name.isEmpty() || uname.isEmpty() || id.isEmpty() || password.isEmpty() ||
                    repassword.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(repassword)) {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("teacher");
            databaseReference.orderByChild("username").equalTo(uname).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(RegisterUser.this, "Username already exists!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Save Details seperately from login details
                        TDetailsModel tdetails = new TDetailsModel(name, uname, id, email, phone);
                        dbHelper.registerTDetailsToFirebase(tdetails, new DataBaseHapler.DatabaseCallback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                Toast.makeText(RegisterUser.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });

                        // Save User Using DataBaseHapler
                        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
                        TeacherModel teacher = new TeacherModel(id, name, uname, hashedPassword);
                        dbHelper.registerTeacherToFirebase(teacher, new DataBaseHapler.DatabaseCallback() {
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

        } else {
            // No Role Selected
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
        }

    }
}