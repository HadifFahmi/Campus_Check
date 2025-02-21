package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class InternView extends AppCompatActivity {

    public TextView tv_Id, tv_Username, tv_Name;
    public Button btn_scanner;
    public Button btn_logout, btn_checkout;
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

        sharedPreferences = getSharedPreferences("com.example.myapplication.myrefrences", 0);
        btn_scanner = findViewById(R.id.scanner);
        btn_logout = findViewById(R.id.btnLogout);
        btn_checkout = findViewById(R.id.btnCheckout);
        tv_Id = findViewById(R.id.tvId);
        tv_Username = findViewById(R.id.tvUsername);
        tv_Name = findViewById(R.id.tvName);

        SessionManager sessionManager = new SessionManager(this);
        DataBaseHapler dbHelper = new DataBaseHapler(this);

        tv_Id.setText("ID : " + sessionManager.getId());
        tv_Username.setText("USERNAME : " + sessionManager.getUsername());
        tv_Name.setText("NAME : " + sessionManager.getName());

        btn_scanner.setOnClickListener(v ->
        {
           openQRScanner();
        });



        btn_logout.setOnClickListener(v ->
        {
            sessionManager.logout();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
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