package com.example.studentportal;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button buttonLogout, buttonCalculateHolidays, buttonEditProfile, buttonUploadNotes, buttonTodoList;
    TextView textView;
    FirebaseUser user;
    private ImageView themeToggle;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        loadTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        themeToggle = findViewById(R.id.themeToggle);
        updateIcon();

        themeToggle.setOnClickListener(view -> toggleTheme());


        auth = FirebaseAuth.getInstance();
        buttonLogout = findViewById(R.id.logout);
        buttonCalculateHolidays = findViewById(R.id.calculate_holidays);
        buttonEditProfile = findViewById(R.id.edit_profile);
        buttonUploadNotes = findViewById(R.id.upload_notes);
        buttonTodoList = findViewById(R.id.todo_list);
        textView = findViewById(R.id.user_details);

        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            textView.setText(user.getEmail());
        }

        buttonLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        buttonCalculateHolidays.setOnClickListener(view -> {
            // Create an Intent to start CalculateHolidays Activity
            Intent intent = new Intent(getApplicationContext(), CalculateHolidays.class);
            startActivity(intent);
            finish();
        });

        buttonEditProfile.setOnClickListener(view -> {
            // Create an Intent to start EditProfile Activity
            Intent intent = new Intent(getApplicationContext(), EditProfile.class);
            startActivity(intent);
            finish();
        });

        buttonUploadNotes.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), UploadNotes.class);
            startActivity(intent);
            finish();
        });

        buttonTodoList.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), TodoList.class);
            startActivity(intent);
            finish();
        });


//        // Create a new user with a first and last name
//        Map<String, Object> user = new HashMap<>();
//        user.put("first", "Ada");
//        user.put("last", "Lovelace");
//        user.put("born", 1815);
//
//// Add a new document with a generated ID
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        db.collection("users")
//                .add(user)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });
//
//
//        // Create a new user with a first, middle, and last name
//        Map<String, Object> user1 = new HashMap<>();
//        user1.put("first", "Alan");
//        user1.put("middle", "Mathison");
//        user1.put("last", "Turing");
//        user1.put("born", 1912);
//
//// Add a new document with a generated ID
//        db.collection("users")
//                .add(user1)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });

    }
    private void loadTheme() {
        sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("isDarkMode", false);

        // Apply the theme based on saved preference or system default
        if (sharedPreferences.contains("isDarkMode")) {
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }


    private void toggleTheme() {
        boolean isDarkMode = sharedPreferences.getBoolean("isDarkMode", false);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (isDarkMode) {
            // Switch to light mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            editor.putBoolean("isDarkMode", false);
        } else {
            // Switch to dark mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            editor.putBoolean("isDarkMode", true);
        }
        editor.apply();

        // Update the icon based on the new mode
        updateIcon();

        // Recreate the activity to apply the new theme instantly
        recreate();
    }

    private void updateIcon() {
        boolean isDarkMode = sharedPreferences.getBoolean("isDarkMode", false);
        if (isDarkMode) {
            themeToggle.setImageResource(R.drawable.moon); // Light mode icon
        } else {
            themeToggle.setImageResource(R.drawable.contrast); // Dark mode icon
        }
    }
}