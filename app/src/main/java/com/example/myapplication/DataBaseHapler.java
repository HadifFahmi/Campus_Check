package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DataBaseHapler {
    private DatabaseReference databaseReference;
    private Context context;

    public DataBaseHapler(Context context) {
        this.databaseReference = FirebaseDatabase.getInstance().getReference("students");
        this.context = context;
    }

    // ✅ Add Student to Firebase
    public void addStudent(StudentModel model) {
        String studentId = databaseReference.child("students").push().getKey(); // Generate unique ID
        if (studentId != null) {
            databaseReference.child("students").child(studentId).setValue(model)
                    .addOnSuccessListener(aVoid -> Log.d("Firebase", "Student added successfully"))
                    .addOnFailureListener(e -> Log.e("Firebase", "Failed to add student: " + e.getMessage()));
        }
    }

    // Add Teacher to Firebase
    public void addTeacher(TeacherModel model) {
        String teacherId = databaseReference.child("teachers").push().getKey();
        if (teacherId != null) {
            databaseReference.child("teachers").child(teacherId).setValue(model)
                    .addOnSuccessListener(aVoid -> Log.d("Firebase", "Teacher added successfully"))
                    .addOnFailureListener(e -> Log.e("Firebase", "Failed to add teacher: " + e.getMessage()));
        }
    }

    // Add Attendance Record to Firebase
    public boolean addAttendance(AttendanceModel model) {
        String attendanceId = databaseReference.child("attendance").push().getKey();
        if (attendanceId != null) {
            databaseReference.child("attendance").child(attendanceId).setValue(model)
                    .addOnSuccessListener(aVoid -> Log.d("Firebase", "Attendance recorded successfully"))
                    .addOnFailureListener(e -> Log.e("Firebase", "Failed to add attendance: " + e.getMessage()));
        }
        return false;
    }

    public void getAllStudentsFromFirebase(DataCallback<List<StudentModel>> callback) {
        DatabaseReference studentsRef = databaseReference.child("students");

        studentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<StudentModel> studentList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    StudentModel student = data.getValue(StudentModel.class);
                    studentList.add(student);
                }
                callback.onCallback(studentList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to fetch students: " + error.getMessage());
            }
        });
    }

    public void getAllTeachersFromFirebase(DataCallback<List<TeacherModel>> callback) {
        DatabaseReference teachersRef = databaseReference.child("teachers");

        teachersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<TeacherModel> teacherList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    TeacherModel teacher = data.getValue(TeacherModel.class);
                    teacherList.add(teacher);
                }
                callback.onCallback(teacherList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to fetch teachers: " + error.getMessage());
            }
        });
    }

    public void getAllAttendanceFromFirebase(DataCallback<List<AttendanceModel>> callback) {
        DatabaseReference attendanceRef = databaseReference.child("attendance");

        attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<AttendanceModel> attendanceList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    AttendanceModel attendance = data.getValue(AttendanceModel.class);
                    attendanceList.add(attendance);
                }
                callback.onCallback(attendanceList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to fetch attendance: " + error.getMessage());
            }
        });
    }

    // Register a New Student
    public void saveUserToFirebase(StudentModel students, DatabaseCallback callback) {
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
