package com.example.myapplication;

import android.content.Context;
import android.se.omapi.Session;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DataBaseHapler {
    private DatabaseReference databaseReference;
    private SessionManager sessionManager;
    private Context context;

    public DataBaseHapler(Context context) {
        this.databaseReference = FirebaseDatabase.getInstance().getReference("students");
        this.sessionManager = new SessionManager(context);
        this.context = context;
    }

    // Register a New Student
    public void registerUserToFirebase(StudentModel students, DatabaseCallback callback) {
        String userId = databaseReference.push().getKey(); // Unique ID

        databaseReference.child(userId).setValue(students).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show();
                callback.onSuccess();
            } else {
                callback.onFailure("Registration Failed!");
            }
        });
    }

    public void registerSDetailsToFirebase(SDetailsModel sdetails, DatabaseCallback callback) {
        DatabaseReference sDetailsReference = FirebaseDatabase.getInstance().getReference("sdetails");
        String userId = sDetailsReference.push().getKey(); // Generate unique ID

        if (userId != null) {
            sDetailsReference.child(userId).setValue(sdetails).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    callback.onSuccess();
                } else {
                    callback.onFailure("Registration Failed!");
                }
            });
        }
    }

    // Save Teacher to databse
    public void registerTeacherToFirebase(TeacherModel teachers, DatabaseCallback callback) {
        DatabaseReference teacherReference = FirebaseDatabase.getInstance().getReference("teacher");
        String userId = teacherReference.push().getKey(); // Unique ID

        teacherReference.child(userId).setValue(teachers).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Teacher Registration Successful!", Toast.LENGTH_SHORT).show();
                callback.onSuccess();
            } else {
                callback.onFailure("Teacher Registration Failed!");
            }
        });
    }

    public void registerTDetailsToFirebase(TDetailsModel tdetails, DatabaseCallback callback) {
        DatabaseReference sDetailsReference = FirebaseDatabase.getInstance().getReference("tdetails");
        String userId = sDetailsReference.push().getKey(); // Generate unique ID

        if (userId != null) {
            sDetailsReference.child(userId).setValue(tdetails).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Teacher Registration Successful!", Toast.LENGTH_SHORT).show();
                    callback.onSuccess();
                } else {
                    callback.onFailure("Teacher Registration Failed!");
                }
            });
        }
    }

    public void saveAttendanceToFirebase(DatabaseCallback callback) {
        SessionManager sessionManager = new SessionManager(context);
        String studentId = sessionManager.getId();
        String username = sessionManager.getUsername();
        String fullName = sessionManager.getName();

        // Get current timestamp (can also get the current date here if needed)
        long currenttime = System.currentTimeMillis();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(currenttime)); // To store the date
        String timestamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date(currenttime));

        // Get a reference to the "attendance" node in Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("attendance");

        // Query to check if there's an existing record for the student on the current date
        databaseReference.child(formattedDate).orderByChild("id").equalTo(studentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // If a record exists, delete it and inform the user that it is being overwritten
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().removeValue();  // Remove the existing attendance record
                        Toast.makeText(context, "Previous attendance record overwritten.", Toast.LENGTH_SHORT).show();
                    }
                }

                // Create a new attendance entry with the student data
                AttendanceModel attendance = new AttendanceModel(studentId, fullName, username, timestamp, formattedDate);

                // Add the new attendance data to the database
                databaseReference.child(formattedDate).push().setValue(attendance)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Attendance saved successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Failed to save attendance.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error if any occurs during the database query
                Toast.makeText(context, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveCheckoutToFirebase() {
        SessionManager sessionManager = new SessionManager(context);
        String studentId = sessionManager.getId();
        String username = sessionManager.getUsername();
        String fullName = sessionManager.getName();

        // Get current timestamp (can also get the current date here if needed)
        long currenttime = System.currentTimeMillis();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(currenttime)); // To store the date
        String timestamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date(currenttime));

        // Get a reference to the "checkoutlist" node in Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("checkoutlist");
        DatabaseReference attendanceReference = FirebaseDatabase.getInstance().getReference("attendance");

        attendanceReference.child(formattedDate).orderByChild("id").equalTo(studentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // If a record exists, delete it and inform the user that it is being overwritten
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Query to check if there's an existing record for the student on the current date
                        databaseReference.child(formattedDate).orderByChild("id").equalTo(studentId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // If a record exists, delete it and inform the user that it is being overwritten
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        snapshot.getRef().removeValue();  // Remove the existing attendance record
                                        Toast.makeText(context, "Previous checkout record overwritten.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                // Create a new checkoutlist entry with the student data
                                AttendanceModel attendance = new AttendanceModel(studentId, fullName, username, timestamp, formattedDate);

                                // Add the new checkoutlist data to the database
                                databaseReference.child(formattedDate).push().setValue(attendance)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(context, "Checkout saved successfully!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, "Failed to save checkout.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle error if any occurs during the database query
                                Toast.makeText(context, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                else {
                    Toast.makeText(context, "You haven't checked in first", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error if any occurs during the database query
                Toast.makeText(context, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkTargetAttendance() {
        DatabaseReference attendancetest = FirebaseDatabase.getInstance().getReference("attendance");

        attendancetest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = sessionManager.getUsername();
                int recordCount = 0;

                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot recordSnapshot : dateSnapshot.getChildren()) {
                        String recordedUsername = recordSnapshot.child("user").getValue(String.class);
                        if (recordedUsername != null && recordedUsername.equals(username)) {
                            recordCount++;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Error", error.getMessage());
            }
        });
    }

    public void totalAttendance() {
        DatabaseReference totalAttendanceRef = FirebaseDatabase.getInstance().getReference("totalattendance");
        totalAttendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int requiredScore = snapshot.getValue(Integer.class);
                    int scorePerRecord = 20;
                } else {
                    Log.e("Firebase Error", "Total attendance value not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Error", error.getMessage());
            }
        });
    }

    public void verifyScannedQR(String scannedCode) {
        DatabaseReference currentqrsession = FirebaseDatabase.getInstance().getReference("currentqrsession");
        currentqrsession.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String storedQR = snapshot.getValue(String.class);
                    if (scannedCode.equals(storedQR)) {
                        Toast.makeText(context, "QR Match!", Toast.LENGTH_SHORT).show();
                        saveAttendanceToFirebase(new DataBaseHapler.DatabaseCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(context, "Attendance saved successfully!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Toast.makeText(context, "Outdated QR!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "No QR Session Found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Firebase Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface DataCallback<T> {
        void onCallback(T data);
    }

    public interface DatabaseCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

}
