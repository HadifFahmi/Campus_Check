package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.mindrot.jbcrypt.BCrypt;

public class ForgotPassword extends AppCompatActivity {

    public Button btnconfnew;

    public EditText etnforUsername, etforID, etnewpass, etnewconfpass;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        etnforUsername = findViewById(R.id.etForUsername);
        etforID = findViewById(R.id.etForID);
        etnewpass = findViewById(R.id.etNewPassword);
        etnewconfpass = findViewById(R.id.etNewConfPass);
        btnconfnew = findViewById(R.id.btnConfNew);

        btnconfnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confnewpass();
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void confnewpass() {
        String uname = etnforUsername.getText().toString().trim();
        String id = etforID.getText().toString().trim();
        String password = etnewpass.getText().toString().trim();
        String repassword = etnewconfpass.getText().toString().trim();

        if (uname.isEmpty() || uname.isEmpty() || id.isEmpty() || password.isEmpty() || repassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(repassword)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("students");
        databaseReference.orderByChild("user").equalTo(uname)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            boolean userFound = false;

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                String storedId = snapshot.child("id").getValue(String.class);

                                if (storedId != null && storedId.equals(id)) {
                                    userFound = true;

                                    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
                                    snapshot.getRef().child("pass").setValue(hashedPassword)
                                            .addOnSuccessListener(unused -> {
                                                Toast.makeText(ForgotPassword.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(ForgotPassword.this, MainActivity.class));
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(ForgotPassword.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
                                            });
                                    break; // Stop searching after updating
                                }
                            }

                            // If no user matches both username & ID
                            if (!userFound) {
                                Toast.makeText(ForgotPassword.this, "User ID does not match!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ForgotPassword.this, "Username not found!", Toast.LENGTH_SHORT).show();
                        }
                    }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ForgotPassword.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}