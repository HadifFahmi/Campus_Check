package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    public Context context;
    public SessionManager sessionManager;
    private List<AttendanceModel> attendanceList;
    private List<AttendanceModel> checkoutList;
    private DatabaseReference attendanceReference, checkoutReference;

    public UserAdapter(Context context, List<AttendanceModel> attendanceList, List<AttendanceModel> checkoutList) {
        this.context = context;
        this.attendanceList = attendanceList;
        this.checkoutList = checkoutList;
        this.sessionManager = new SessionManager(context);
        this.attendanceReference = FirebaseDatabase.getInstance().getReference("attendance");
        this.checkoutReference = FirebaseDatabase.getInstance().getReference("checkoutlist");
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.attendance_recyclerview, parent, false);
        return new UserViewHolder(view, sessionManager);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        AttendanceModel attendance = attendanceList.get(position);
        syncLists();
        AttendanceModel checkout;
        if (checkoutList != null && position <= checkoutList.size() && checkoutList.get(position) != null) {
            checkout = checkoutList.get(position);
        } else {
            checkout = new AttendanceModel(); // Default empty object
        }

        holder.text_recycler_name.setText(attendance.getName());
        holder.text_recycler_date.setText("Date: " + attendance.getDate());
        holder.text_recycler_cin.setText("Check In: " + attendance.getTimeStamp());
        if (checkout.getTimeStamp() != null && !checkout.getTimeStamp().isEmpty()) {
            holder.text_recycler_cout.setText("Check Out: " + checkout.getTimeStamp());
        } else {
            holder.text_recycler_cout.setText("Check Out: Not yet checked out");
        }

        Log.d("DEBUG", "attendanceList size: " + attendanceList.size());
        Log.d("DEBUG", "checkoutList size: " + (checkoutList != null ? checkoutList.size() : "null"));
        Log.d("RecyclerView", "Binding item at position: " + position);

        // Edit Button
        holder.btnEdit.setOnClickListener(v -> showEditDialog(attendance, checkout, position));

        // Delete Button
        holder.btnDelete.setOnClickListener(v -> {
            attendanceReference.child(attendance.getId()).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        attendanceList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Deleted 1/2 Successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Delete Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

            if (checkout.getTimeStamp() != null && !checkout.getTimeStamp().isEmpty()) {
                checkoutReference.child(checkout.getId()).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            checkoutList.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(context, "Deleted 2/2 Successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Delete Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }

    public void syncLists() {
        while (checkoutList.size() < attendanceList.size()) {
            checkoutList.add(new AttendanceModel()); // Fill missing entries
        }
    }


    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView text_recycler_name, text_recycler_date, text_recycler_cin, text_recycler_cout;
        Button btnEdit, btnDelete;

        public UserViewHolder(@NonNull View itemView, SessionManager sessionManager) {
            super(itemView);
            text_recycler_name = itemView.findViewById(R.id.tvRecyclerName);
            text_recycler_date = itemView.findViewById(R.id.tvRecyclerDate);
            text_recycler_cin = itemView.findViewById(R.id.tvRecyclerCin);
            text_recycler_cout = itemView.findViewById(R.id.tvRecyclerCout);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            int role = sessionManager.getRole();
            if (role != 1) {
                btnEdit.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
            }
        }
    }

    // Method to show edit dialog
    private void showEditDialog(AttendanceModel attendance,AttendanceModel checkout, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.attendance_dialog_edit, null);
        builder.setView(dialogView);

        EditText editDate = dialogView.findViewById(R.id.etDate);
        EditText editCinTimestamp = dialogView.findViewById(R.id.etCinTime);
        EditText editCoutTimestamp = dialogView.findViewById(R.id.etCoutTime);

        editDate.setText(attendance.getDate());
        editCinTimestamp.setText(attendance.getTimeStamp());
        if (checkout.getTimeStamp() != null && !checkout.getTimeStamp().isEmpty()) {
            editCoutTimestamp.setText(checkout.getTimeStamp());
        } else {
            editCoutTimestamp.setText("Not yet checked out");
            editCoutTimestamp.setEnabled(false);
        }

        builder.setTitle("Edit Attendance");
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newDate = editDate.getText().toString().trim();
            String cinTime = editCinTimestamp.getText().toString().trim();
            String coutTime = editDate.getText().toString().trim();

            if (!newDate.isEmpty() && !cinTime.isEmpty() && !coutTime.isEmpty()) {
                updateAttendance(attendance.getId(), attendance.getName(), attendance.getUser(), cinTime, coutTime, newDate, position);
            } else {
                Toast.makeText(context, "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateAttendance(String id, String name, String user, String cinTime, String coutTime, String newDate, int position) {
        DatabaseReference attendanceRef = FirebaseDatabase.getInstance().getReference("attendance").child(newDate).child(id);
        DatabaseReference checkoutRef = FirebaseDatabase.getInstance().getReference("checkoutlist").child(newDate).child(id);

        AttendanceModel updatedAttendance = new AttendanceModel(id, name, user, cinTime, newDate);
        AttendanceModel updatedCheckOutList = new AttendanceModel(id, name, user, coutTime, newDate);

        // Overwrite the existing attendance record in Firebase
        attendanceRef.setValue(updatedAttendance)
                .addOnSuccessListener(aVoid -> {
                    attendanceList.set(position, updatedAttendance);
                    notifyItemChanged(position);
                    Toast.makeText(context, "Attendance updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to update attendance", Toast.LENGTH_SHORT).show()
                );

        if (coutTime != null && coutTime.isEmpty() && coutTime != "Not yet check out") {
            checkoutRef.setValue(updatedCheckOutList)
                    .addOnSuccessListener(aVoid -> {
                        attendanceList.set(position, updatedCheckOutList);
                        notifyItemChanged(position);
                        Toast.makeText(context, "Attendance updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Failed to update attendance", Toast.LENGTH_SHORT).show()
                    );

        }
    }
}
