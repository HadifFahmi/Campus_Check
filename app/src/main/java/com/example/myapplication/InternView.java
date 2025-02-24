package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InternView extends AppCompatActivity {

    public TextView tv_checkin, tv_checkout;
    public Button btn_scanner, btn_settings, btn_checkout;
    SharedPreferences sharedPreferences;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intern_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_scanner = findViewById(R.id.btnScanner);
        btn_checkout = findViewById(R.id.btnCheckout);
        btn_settings = findViewById(R.id.btnSettings);
        tv_checkin = findViewById(R.id.tvCheckIn);
        tv_checkout = findViewById(R.id.tvCheckOut);

        DataBaseHapler dbHelper = new DataBaseHapler(this);

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        DatabaseReference attendanceRef = FirebaseDatabase.getInstance().getReference("attendance").child(currentDate);
        DatabaseReference checkOutRef = FirebaseDatabase.getInstance().getReference("checkoutlist").child(currentDate);

        // Automatically updates the check-in and check-out
        attendanceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        String checkInTime = childSnapshot.child("timeStamp").getValue(String.class);

                        if (checkInTime != null && !checkInTime.isEmpty()) {
                            tv_checkin.setText(checkInTime);
                        } else {
                            tv_checkin.setText("Not yet checked in");
                        }

                        break;
                    }
                } else {
                    tv_checkin.setText("Not yet checked in");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("InternView", "Database Error (Check-In): " + error.getMessage());
            }
        });

        checkOutRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        String checkOutTime = childSnapshot.child("timeStamp").getValue(String.class);

                        if (checkOutTime != null && !checkOutTime.isEmpty()) {
                            tv_checkout.setText(checkOutTime);
                        } else {
                            tv_checkout.setText("Not yet checked-in");
                        }

                        break;
                    }
                } else {
                    tv_checkout.setText("Not yet checked-in");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("InternView", "Database Error (Check-Out): " + error.getMessage());
            }
        });

        btn_settings.setOnClickListener(v ->
        {
            startActivity(new Intent(this, Profile.class));
        });

        btn_scanner.setOnClickListener(v ->
        {
            dbHelper.verifyScannedQR("85d38b45-812a-4187-a25c-db0f4af04baa");
        });

        btn_checkout.setOnClickListener(v ->
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirm Scan");
            builder.setMessage("Are you sure you want to scan the QR code?");

            // YES Button: Proceed with scanning
            builder.setPositiveButton("Yes", (dialog, which) -> {
                dbHelper.saveCheckoutToFirebase();
            });

            // NO Button: Cancel scanning
            builder.setNegativeButton("No", (dialog, which) -> {
                dialog.dismiss();
            });

            // Show the dialog
            AlertDialog alert = builder.create();
            alert.show();
        });

    }

    private String formatTime(String timeStamp) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            Date date = inputFormat.parse(timeStamp);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return timeStamp;
        }
    }

    public void openQRScanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan QR Code");
        options.setBeepEnabled(true);
        options.setOrientationLocked(false);
        options.setCaptureActivity(CaptureActivityPortrait.class);

        qrLauncher.launch(options);
    }

    private final androidx.activity.result.ActivityResultLauncher<ScanOptions> qrLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {
                    DataBaseHapler db = new DataBaseHapler(this);
                    db.verifyScannedQR(result.getContents());
                } else {
                    Toast.makeText(this, "QR Scan Cancelled", Toast.LENGTH_SHORT).show();
                }
            });
}