package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminReport extends AppCompatActivity {
    public TextView tvdisplaybr;
    public Button btnallattend, btnbackar;
    private Spinner spinnerStudents;
    private RecyclerView recyclerView;
    private List<String> studentList = new ArrayList<>();
    private ReportAdapter adapter;
    private List<Object> attendanceList = new ArrayList<>();
    private String selectedUser = "";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_report);

        tvdisplaybr = findViewById(R.id.tvDisplayBR);

        btnbackar = findViewById(R.id.btnBackAR);
        btnallattend = findViewById(R.id.btnAllAttend);

        spinnerStudents = findViewById(R.id.spinner_students);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("attendance");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentList.clear();
                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot recordSnapshot : dateSnapshot.getChildren()) {
                        String studentName = recordSnapshot.child("user").getValue(String.class);
                        if (studentName != null && !studentList.contains(studentName)) { // Check for duplicates
                            studentList.add(studentName);
                        }
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AdminReport.this, android.R.layout.simple_spinner_item, studentList) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        TextView textView = (TextView) super.getView(position, convertView, parent);
                        textView.setTextColor(Color.BLACK);
                        textView.setTextSize(16);
                        return textView;
                    }

                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                        textView.setTextColor(Color.WHITE);
                        textView.setTextSize(16);
                        return textView;
                    }
                };
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerStudents.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        spinnerStudents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedUser = studentList.get(position);
                fetchAttendanceData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //  TEST PURPOSE ONLY  ********************************************

//        spinnerStudents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String selectedStudent = studentList.get(position); // Student name
//                attendanceDates.clear();
//
//                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        int attendedDays = 0;
//
//                        for (DataSnapshot dateSnapshot : snapshot.getChildren()) { // Loop through dates
//                            boolean attended = false;
//                            for (DataSnapshot recordSnapshot : dateSnapshot.getChildren()) { // Loop through student records
//                                String studentName = recordSnapshot.child("user").getValue(String.class);
//                                String attendanceDate = recordSnapshot.child("date").getValue(String.class);
//
//                                if (studentName != null && attendanceDate != null && studentName.equals(selectedStudent)) {
//                                    attended = true;
//                                    attendanceDates.add((attendedDays+1) + ":   " + attendanceDate); // Add matching date
//                                }
//                            }
//                            if (attended) {
//                                attendedDays++;
//                            }
//                        }
//
////                        int finalAttendedDays = attendedDays; // original code, comment and uncomment based on needs
//                        int finalAttendedDays = attendedDays * 15; // debugging purposes
//                        DatabaseReference totalAttendanceRef = FirebaseDatabase.getInstance().getReference("totalattendance");
//                        totalAttendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                if (snapshot.exists()) {
//                                    int totalAttendanceDays = snapshot.getValue(Integer.class);
//                                    String studentStatus = "";
//                                    if (finalAttendedDays >= (totalAttendanceDays * 80 / 100)) {
//                                        studentStatus = "Pass";
//                                        String finalstudentStatus = studentStatus;
//                                        DatabaseReference sDetailsRef = FirebaseDatabase.getInstance().getReference("sdetails");
//                                        sDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                for (DataSnapshot studentSnapshot : snapshot.getChildren()) { // Loop through unique IDs
//                                                    String studentName = studentSnapshot.child("username").getValue(String.class);
//
//                                                    if (studentName != null && studentName.equals(selectedStudent)) {
//                                                        studentSnapshot.getRef().child("status").setValue(finalstudentStatus);
//                                                        break;
//                                                    }
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError error) {
//
//                                            }
//                                        });
//                                    } else if (finalAttendedDays < (totalAttendanceDays * 80 / 100)) {
//                                        studentStatus = "Fail";
//                                        String finalstudentStatus = studentStatus;
//                                        DatabaseReference sDetailsRef = FirebaseDatabase.getInstance().getReference("sdetails");
//                                        sDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                for (DataSnapshot studentSnapshot : snapshot.getChildren()) { // Loop through unique IDs
//                                                    String studentName = studentSnapshot.child("username").getValue(String.class);
//
//                                                    if (studentName != null && studentName.equals(selectedStudent)) {
//                                                        studentSnapshot.getRef().child("status").setValue(finalstudentStatus);
//                                                        break;
//                                                    }
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError error) {
//
//                                            }
//                                        });
//                                    } else {
//                                        // need some placeholder debugging or something
//                                    }
//
//                                    tvdisplaybr.setText(finalAttendedDays + "/" + totalAttendanceDays + "   Status: " + studentStatus);
//                                } else {
//                                    Log.e("Firebase Error", "Total attendance value not found");
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//                                Log.e("Firebase Error", error.getMessage());
//                            }
//                        });
//
//                        // Update ListView
//                        ArrayAdapter<String> dateAdapter = new ArrayAdapter<String>(AdminReport.this, android.R.layout.simple_list_item_1, attendanceDates) {
//                            @Override
//                            public View getView(int position, View convertView, ViewGroup parent) {
//                                TextView textView = (TextView) super.getView(position, convertView, parent);
//                                textView.setTextColor(Color.BLACK); // Change color to blue
//                                textView.setTextSize(16); // Optional: Change text size
//                                return textView;
//                            }
//                        };
//                        listViewAttendance.setAdapter(dateAdapter);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Log.e("Firebase Error", error.getMessage());
//                    }
//                });
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {}
//        });


        //  ***************************************************************

        btnbackar.setOnClickListener(v ->
        {
            startActivity(new Intent(this, AdminView.class));
            finish();
        });

        btnallattend.setOnClickListener(v ->
        {
            startActivity(new Intent(this, AttendanceViewAdmin.class));
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void fetchAttendanceData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("attendance");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, List<AttendanceModel>> groupedData = new LinkedHashMap<>();
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    String date = dateSnapshot.getKey();
                    String monthYear = "";

                    try {
                        Date parsedDate = inputFormat.parse(date);
                        if (parsedDate != null) {
                            monthYear = monthFormat.format(parsedDate);
                        }
                    } catch (Exception e) {
                        Log.e("Firebase", "Date parsing error: " + e.getMessage());
                    }

                    if (!groupedData.containsKey(monthYear)) {
                        groupedData.put(monthYear, new ArrayList<>());
                    }

                    for (DataSnapshot studentSnapshot : dateSnapshot.getChildren()) {
                        AttendanceModel attendance = studentSnapshot.getValue(AttendanceModel.class);
                        if (attendance != null) {
                            // Filter by selected username
                            if (selectedUser.equals("All Users") || attendance.getUser().equals(selectedUser)) {
                                groupedData.get(monthYear).add(attendance);
                            }
                        }
                    }
                }

                // Convert grouped data into a list format for RecyclerView
                attendanceList.clear();
                for (Map.Entry<String, List<AttendanceModel>> entry : groupedData.entrySet()) {
                    if (!entry.getValue().isEmpty()) {
                        attendanceList.add(entry.getKey()); // Month header
                        attendanceList.addAll(entry.getValue()); // Attendance items
                    }
                }

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int attendedDays = 0;

                        for (DataSnapshot dateSnapshot : snapshot.getChildren()) { // Loop through dates
                            boolean attended = false;
                            for (DataSnapshot recordSnapshot : dateSnapshot.getChildren()) { // Loop through student records
                                String studentName = recordSnapshot.child("user").getValue(String.class);
                                String attendanceDate = recordSnapshot.child("date").getValue(String.class);

                                if (studentName != null && attendanceDate != null && studentName.equals(selectedUser)) {
                                    attended = true;
                                }
                            }
                            if (attended) {
                                attendedDays++;
                            }
                        }

                        int finalAttendedDays = attendedDays; // original code, comment and uncomment based on needs
//                        int finalAttendedDays = attendedDays * 15; // debugging purposes
                        DatabaseReference totalAttendanceRef = FirebaseDatabase.getInstance().getReference("totalattendance");
                        totalAttendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    int totalAttendanceDays = snapshot.getValue(Integer.class);
                                    String studentStatus = "";
                                    if (finalAttendedDays >= (totalAttendanceDays * 80 / 100)) {
                                        studentStatus = "Pass";
                                        String finalstudentStatus = studentStatus;
                                        DatabaseReference sDetailsRef = FirebaseDatabase.getInstance().getReference("sdetails");
                                        sDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot studentSnapshot : snapshot.getChildren()) { // Loop through unique IDs
                                                    String studentName = studentSnapshot.child("username").getValue(String.class);

                                                    if (studentName != null && studentName.equals(selectedUser)) {
                                                        studentSnapshot.getRef().child("status").setValue(finalstudentStatus);
                                                        break;
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    } else if (finalAttendedDays < (totalAttendanceDays * 80 / 100)) {
                                        studentStatus = "Fail";
                                        String finalstudentStatus = studentStatus;
                                        DatabaseReference sDetailsRef = FirebaseDatabase.getInstance().getReference("sdetails");
                                        sDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot studentSnapshot : snapshot.getChildren()) { // Loop through unique IDs
                                                    String studentName = studentSnapshot.child("username").getValue(String.class);

                                                    if (studentName != null && studentName.equals(selectedUser)) {
                                                        studentSnapshot.getRef().child("status").setValue(finalstudentStatus);
                                                        break;
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    } else {
                                        // need some placeholder debugging or something
                                    }

                                    tvdisplaybr.setText(finalAttendedDays + "/" + totalAttendanceDays + "   Status: " + studentStatus);
                                } else {
                                    Log.e("Firebase Error", "Total attendance value not found");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("Firebase Error", error.getMessage());
                            }
                        });

                        adapter = new ReportAdapter(attendanceList);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase Error", error.getMessage());
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching data: " + databaseError.getMessage());
            }
        });
    }
}