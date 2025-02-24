package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.UUID;

public class GenerateQR extends AppCompatActivity {

    public Button btn_generate, btn_back;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_generate_qr);

        btn_generate = findViewById(R.id.btnGenerateQR);
        btn_back = findViewById(R.id.btnBackQRG);

        btn_back.setOnClickListener(v ->
        {
            startActivity(new Intent(this, AdminView.class));
            finish();
        });

        btn_generate.setOnClickListener(v->
        {
            ImageView qrImageView = findViewById(R.id.qr);
            generateQRCode(qrImageView);
        });
    }

    public void generateQRCode(ImageView qrImageView) {
        // Generate a unique QR code string
        String qrCodeText = UUID.randomUUID().toString(); // Random string

        // Display QR code in ImageView
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            BitMatrix bitMatrix = new com.google.zxing.qrcode.QRCodeWriter()
                    .encode(qrCodeText, BarcodeFormat.QR_CODE, 400, 400);
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrImageView.setImageBitmap(bitmap); // Set the QR code image
        } catch (WriterException e) {
            e.printStackTrace();
        }

        // Upload the QR code string to Firebase
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("currentqrsession");
        dbRef.setValue(qrCodeText)
                .addOnSuccessListener(aVoid -> {
                    // Successfully uploaded
                    System.out.println("QR Code session updated in Firebase: " + qrCodeText);
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    System.err.println("Failed to update QR session: " + e.getMessage());
                });
    }



}