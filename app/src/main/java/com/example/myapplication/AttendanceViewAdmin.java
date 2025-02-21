package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class AttendanceViewAdmin extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<AttendanceModel> attendanceList;
    private DatabaseReference databaseReference;
    public Button btnCalendar, Exitbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendance_view_admin);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        attendanceList = new ArrayList<>();
        adapter = new UserAdapter(this, attendanceList);
        recyclerView.setAdapter(adapter);

        btnCalendar = findViewById(R.id.btn_Calendar);
        Exitbutton = findViewById(R.id.btn_Exit);

        btnCalendar.setOnClickListener(v ->
        {
            // Get the current date
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create a DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Format the date (YYYY-MM-DD)
                        String selectedDate = String.format("%02d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        Toast.makeText(this, selectedDate, Toast.LENGTH_SHORT).show();

                        databaseReference = FirebaseDatabase.getInstance().getReference("attendance").child(selectedDate);

                        // Fetch data from Firebase
                        databaseReference.addValueEventListener(new ValueEventListener() {
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
                                Toast.makeText(AttendanceViewAdmin.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                            }
                        });
                    },
                    year, month, day
            );

                    // Show the DatePickerDialog
            datePickerDialog.show();
        
        });

        Exitbutton.setOnClickListener(v -> {
            startActivity(new Intent(this, Admin.class));
            finish();
        });
    }
}