package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

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

import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button btnsubmit, btnreg, btnforgpass;
    EditText user, pass;
    SharedPreferences sharedPreferences;
    private DataBaseHapler dbHelper;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> studentList;
    private List<String> teacherList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences("com.example.myapplication.myrefrences", 0);

        Spinner spinner = findViewById(R.id.spinner);

        String[] roles = getResources().getStringArray(R.array.roles);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        user = findViewById(R.id.user);
        pass = findViewById(R.id.pass);

        btnsubmit = findViewById(R.id.submit);
        btnreg = findViewById(R.id.btnRegister);
        btnforgpass = findViewById(R.id.btnForgPass);

        dbHelper = new DataBaseHapler(this);

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(user.getText().toString().trim()) || TextUtils.isEmpty(pass.getText().toString().trim())) {
                    Toast.makeText(MainActivity.this, "Please input username and password", Toast.LENGTH_SHORT).show();
                } else {
                    String selectedRole = spinner.getSelectedItem().toString();

                    if (selectedRole.equals("Intern")) {
                        // Check Student
                        check(2, user.getText().toString().toLowerCase(), pass.getText().toString().toLowerCase());
                    } else if (selectedRole.equals("Admin")) {
                        // Check Teacher
                        check(1, user.getText().toString().toLowerCase(), pass.getText().toString().toLowerCase());
                    } else {
                        // No Role Selected
                        Toast.makeText(MainActivity.this, "Please select a role", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterUser.class);
                startActivity(intent);
            }
        });

        btnforgpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

    }

    public void check(int role, String username, String password) { //password is not used???
        if (role == 1) { // Admin login
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("teacher");

            // Query Firebase to find the student by username
            databaseReference.orderByChild("user").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // If the username exists in Firebase
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // Get the admin data from Firebase
                            TeacherModel admin = snapshot.getValue(TeacherModel.class);

                            if (admin != null) {
                                String adminId = admin.getId();
                                String adminUser = admin.getUser();
                                String adminName = admin.getName();
                                String storedHashedPassword = admin.getPass();

                                if (BCrypt.checkpw(password, storedHashedPassword)) {
                                    // Save the admin ID and username in the session
                                    SessionManager sessionManager = new SessionManager(MainActivity.this);
                                    sessionManager.createSession("", adminUser, adminName, role);

                                    startActivity(new Intent(MainActivity.this, AdminView.class));  // Navigate to the main activity
                                    finish(); // Close current activity
                                } else {
                                    // Incorrect password
                                    Toast.makeText(MainActivity.this, "Incorrect username or password!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    } else {
                        // If the username does not exist
                        Toast.makeText(MainActivity.this, "Incorrect username or password!", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error if the database query fails
                    Toast.makeText(MainActivity.this, "Error retrieving data", Toast.LENGTH_SHORT).show();
                }
            });

        } else if (role == 2) { // Student login
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("students");

            // Query Firebase to find the student by username
            databaseReference.orderByChild("user").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // If the username exists in Firebase
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // Get the student data from Firebase
                            StudentModel student = snapshot.getValue(StudentModel.class);

                            if (student != null) {
                                String studentId = student.getId();
                                String studentUser = student.getUser();
                                String studentName = student.getName();
                                String storedHashedPassword = student.getPass();

                                if (BCrypt.checkpw(password, storedHashedPassword)) {
                                    // Save the student ID and username in the session
                                    SessionManager sessionManager = new SessionManager(MainActivity.this);
                                    sessionManager.createSession(studentId, studentUser, studentName, role);

                                    startActivity(new Intent(MainActivity.this, InternView.class));  // Navigate to the main activity
                                    finish(); // Close current activity
                                } else {
                                    // Incorrect password
                                    Toast.makeText(MainActivity.this, "Incorrect username or password!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    } else {
                        // If the username does not exist
                        Toast.makeText(MainActivity.this, "Incorrect username or password!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error if the database query fails
                    Toast.makeText(MainActivity.this, "Error retrieving data", Toast.LENGTH_SHORT).show();
                }
            });


        } else {
            // Invalid role
            Toast.makeText(MainActivity.this, "Invalid role, please try again", Toast.LENGTH_SHORT).show();
        }
    }
}