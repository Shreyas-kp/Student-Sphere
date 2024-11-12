package com.example.studentportal;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CalculateHolidays extends AppCompatActivity {

    private EditText totalLecs, attendedLecs, subject;
    private TextView attendancePercentage, lecsLeft;
    private Button saveButton;
    private FirebaseFirestore db;
    private FirebaseUser user;
    FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_holidays);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        totalLecs = findViewById(R.id.total_lecs);
        attendedLecs = findViewById(R.id.attended_lecs);
        subject = findViewById(R.id.sub);
        attendancePercentage = findViewById(R.id.attendance_percentage);
        lecsLeft = findViewById(R.id.lecs_left);
        Button calculateButton = findViewById(R.id.calculateButton);

        calculateButton.setOnClickListener(v -> calculateAttendance());
        saveButton.setOnClickListener(v -> saveAttendanceData());

//        if (user != null) {
//            subject.getText();
//            loadAttendanceData();
//        }
    }

    // Calculate the attendance percentage and lectures left
    private void calculateAttendance() {
        String totalLecsStr = totalLecs.getText().toString();
        String attendedLecsStr = attendedLecs.getText().toString();

        if (TextUtils.isEmpty(totalLecsStr) || TextUtils.isEmpty(attendedLecsStr)) {
            Toast.makeText(this, "Please enter both total and attended lectures", Toast.LENGTH_SHORT).show();
            return;
        }

        int total = Integer.parseInt(totalLecsStr);
        int attended = Integer.parseInt(attendedLecsStr);

        if (total <= 0 || attended < 0 || attended > total) {
            Toast.makeText(this, "Invalid lecture counts", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate percentage
        double percentage = ((double) attended / total) * 100;
        attendancePercentage.setText("Attendance: " + String.format("%.2f", percentage) + "%");

        // Calculate required lectures to reach 80% if below 80%
        if (percentage < 80) {
            int requiredLectures = (int) Math.ceil((0.8 * total - attended) / 0.2);
            lecsLeft.setText("Lectures to reach 80%: " + requiredLectures);
        } else {
            lecsLeft.setText("You have met the 80% attendance requirement.");
        }
    }

    // Save the attendance data to Firestore
    private void saveAttendanceData() {
        String subjectText = subject.getText().toString();
        String totalLecsStr = totalLecs.getText().toString();
        String attendedLecsStr = attendedLecs.getText().toString();
        String percentageStr = attendancePercentage.getText().toString();
        String lecsLeftStr = lecsLeft.getText().toString();

        if (TextUtils.isEmpty(subjectText) || TextUtils.isEmpty(totalLecsStr) || TextUtils.isEmpty(attendedLecsStr)) {
            Toast.makeText(this, "Please calculate attendance before saving", Toast.LENGTH_SHORT).show();
            return;
        }

        if (user != null) {
            // Prepare data to save
            Map<String, Object> attendanceData = new HashMap<>();
            attendanceData.put("subject", subjectText);
            attendanceData.put("totalLectures", Integer.parseInt(totalLecsStr));
            attendanceData.put("attendedLectures", Integer.parseInt(attendedLecsStr));
            attendanceData.put("attendancePercentage", percentageStr);
            attendanceData.put("lecturesLeft", lecsLeftStr);

            // Store data under the user's ID in Firestore
            db.collection("AttendanceRecords")
                    .document(user.getUid())
                    .collection("Subjects")
                    .document(subjectText)
                    .set(attendanceData)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(CalculateHolidays.this, "Data saved successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(CalculateHolidays.this, "Error saving data", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CalculateHolidays.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Optionally close EditProfile so it won’t stay in the back stack
    }
}
