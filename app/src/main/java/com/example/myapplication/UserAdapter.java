package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.AttendanceModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private Context context;
    private List<AttendanceModel> attendanceList;
    private DatabaseReference databaseReference;

    public UserAdapter(Context context, List<AttendanceModel> attendanceList) {
        this.context = context;
        this.attendanceList = attendanceList;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("attendance");
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.attendance_recyclerview, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        AttendanceModel attendance = attendanceList.get(position);
        holder.textName.setText(attendance.getName());
        holder.textUser.setText(attendance.getUser());

        // Edit Button
        holder.btnEdit.setOnClickListener(v -> showEditDialog(attendance, position));

        // Delete Button
        holder.btnDelete.setOnClickListener(v -> {
            databaseReference.child(attendance.getId()).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        attendanceList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Delete Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textUser;
        Button btnEdit, btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.tvName2);
            textUser = itemView.findViewById(R.id.tvUser2);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    // Method to show edit dialog
    private void showEditDialog(AttendanceModel attendance, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.attendance_dialog_edit, null);
        builder.setView(dialogView);

        EditText editName = dialogView.findViewById(R.id.etName);
        EditText editUser = dialogView.findViewById(R.id.etUser);

        // Set current values
        editName.setText(attendance.getName());
        editUser.setText(attendance.getUser());

        builder.setTitle("Edit Attendance");
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = editName.getText().toString().trim();
            String newUser = editUser.getText().toString().trim();

            if (!newName.isEmpty() && !newUser.isEmpty()) {
                updateAttendance(attendance.getId(), newName, newUser, position);
            } else {
                Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateAttendance(String id, String newName, String newUser, int position) {
        DatabaseReference ref = databaseReference.child(id);

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", newName);
        updates.put("user", newUser);

        ref.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    attendanceList.get(position).setName(newName);
                    attendanceList.get(position).setUser(newUser);
                    notifyItemChanged(position);
                    Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Update Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
