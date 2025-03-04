package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AttendanceViewIntern extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<AttendanceModel> attendanceList, checkoutList;
    private DatabaseReference databaseReference;
    public Button btnCalendar2, Exitbutton2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendance_view_intern);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        attendanceList = new ArrayList<>();
        checkoutList = new ArrayList<>();
        adapter = new UserAdapter(this, attendanceList, checkoutList);
        recyclerView.setAdapter(adapter);

        btnCalendar2 = findViewById(R.id.btn_Calendar2);
        Exitbutton2 = findViewById(R.id.btnBackAVI);

        btnCalendar2.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = String.format("%02d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        Toast.makeText(this, selectedDate, Toast.LENGTH_SHORT).show();

                        DatabaseReference attendanceRef = FirebaseDatabase.getInstance().getReference("attendance").child(selectedDate);
                        DatabaseReference checkoutRef = FirebaseDatabase.getInstance().getReference("checkoutlist").child(selectedDate);

                        // Fetch attendanceList
                        attendanceRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                attendanceList.clear();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    AttendanceModel attendance = dataSnapshot.getValue(AttendanceModel.class);
                                    if (attendance != null) {
                                        attendanceList.add(attendance);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(AttendanceViewIntern.this, "Failed to fetch attendance data", Toast.LENGTH_SHORT).show();
                            }
                        });

                        // Fetch checkoutList
                        checkoutRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                checkoutList.clear();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    AttendanceModel checkout = dataSnapshot.getValue(AttendanceModel.class);
                                    if (checkout != null) {
                                        checkoutList.add(checkout);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(AttendanceViewIntern.this, "Failed to fetch checkout data", Toast.LENGTH_SHORT).show();
                            }
                        });

                    },
                    year, month, day
            );

            datePickerDialog.show();
        });


        Exitbutton2.setOnClickListener(v -> {
            startActivity(new Intent(this, Profile.class));
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}