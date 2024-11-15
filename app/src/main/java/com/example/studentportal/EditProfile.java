package com.example.studentportal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditProfile extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextNumber;
    private Button buttonSave;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private DocumentReference userDocRef;

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;
    private Uri imageUri; // Uri to store the image selected

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImageView = findViewById(R.id.imageViewProfilePhoto);
        // Set an OnClickListener on the ImageView to pick an image
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        // Get references to the UI elements
        editTextName = findViewById(R.id.edit_text_name);
        editTextEmail = findViewById(R.id.edit_text_email);
        editTextNumber = findViewById(R.id.edit_text_number);
        buttonSave = findViewById(R.id.button_save_profile);
        progressBar = findViewById(R.id.progressBar);

        // Check if the user is authenticated
        if (user != null) {
            userDocRef = db.collection("Users").document(user.getUid());
            loadUserProfile();
        } else {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set OnClickListener for Save button
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // Set the selected image to the ImageView
            profileImageView.setImageURI(imageUri);
        }
    }

    private void loadUserProfile() {
        progressBar.setVisibility(View.VISIBLE);
        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        // Retrieve existing profile data
                        String name = document.getString("name");
                        String email = document.getString("email");
                        String phone = document.getString("phone");

                        // Set data to the EditText fields
                        editTextName.setText(name);
                        editTextEmail.setText(email);
                        editTextNumber.setText(phone);
                    } else {
                        Toast.makeText(EditProfile.this, "No profile found. Please enter details.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditProfile.this, "Failed to load profile: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    private void saveUserProfile() {
//        String name = editTextName.getText().toString().trim();
//        String email = editTextEmail.getText().toString().trim();
//        String phone = editTextNumber.getText().toString().trim();
//
//        // Validate input
//        if (TextUtils.isEmpty(name)) {
//            editTextName.setError("Name is required");
//            editTextName.requestFocus();
//            return;
//        }
//        if (TextUtils.isEmpty(email)) {
//            editTextEmail.setError("Email is required");
//            editTextEmail.requestFocus();
//            return;
//        }
//        if (TextUtils.isEmpty(phone)) {
//            editTextNumber.setError("Phone is required");
//            editTextNumber.requestFocus();
//            return;
//        }
//
//        progressBar.setVisibility(View.VISIBLE);
//
//        // Create a Map with the new profile data
//        Map<String, Object> userProfile = new HashMap<>();
//        userProfile.put("name", name);
//        userProfile.put("email", email);
//        userProfile.put("phone", phone);
//
//        // Save data to Firestore
//        userDocRef.set(userProfile)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        progressBar.setVisibility(View.GONE);
//                        Toast.makeText(EditProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
//                        finish();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        progressBar.setVisibility(View.GONE);
//                        Toast.makeText(EditProfile.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

    private void saveUserProfile() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextNumber.getText().toString().trim(); // Make sure phone is treated as String

        // Check if inputs are valid
        if (TextUtils.isEmpty(name)) {
            editTextName.setError("Name is required");
            editTextName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }
        if (!isValidEmail(email)) {
            editTextEmail.setError("Invalid email format");
            editTextEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            editTextNumber.setError("Phone is required");
            editTextNumber.requestFocus();
            return;
        }
        if (phone.length() < 8) {
            editTextNumber.setError("Phone number must be at least 8 digits");
            editTextNumber.requestFocus();
            return;
        }

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Create a Map with the new profile data
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("name", name);
        userProfile.put("email", email);
        userProfile.put("phone", phone);

        // Save data to Firestore
        userDocRef.set(userProfile)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(EditProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                        // Navigate to MainActivity after successful profile update
                        Intent intent = new Intent(EditProfile.this, MainActivity.class);
                        startActivity(intent);  // Start MainActivity
                        finish();  // Close EditProfile activity to prevent user from going back to it
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(EditProfile.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        // Log the failure
                        Log.e("EditProfile", "Profile update failed: " + e.getMessage());
                    }
                });
    }

    /**
     * Validates the email format using a simple regex
     */
    private boolean isValidEmail(String email) {
        // A simple regex for email validation (basic format)
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        return email.matches(emailPattern);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(EditProfile.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Optionally close EditProfile so it won’t stay in the back stack
    }

}
