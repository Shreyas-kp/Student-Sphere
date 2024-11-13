package com.example.studentportal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UploadNotes extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    // Define constant for permissions
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;
    private static final int FILE_PICKER_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Check if the user is authenticated
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Check if permission is granted for reading external storage
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
        }

        // Initialize Upload button
        Button uploadButton = findViewById(R.id.uploadButton); // Assuming this is the button to upload notes
        uploadButton.setOnClickListener(v -> {
            // Open file picker to select a file
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");  // Use "*/*" for any type of file, or specify the MIME type
            startActivityForResult(intent, FILE_PICKER_REQUEST_CODE);
        });
    }

    // Handle the result of permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check if the permission request was successful
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with file selection and upload
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied, show a message or handle accordingly
                Toast.makeText(this, "Permission denied, cannot access files", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Handle file picker result (when the user selects a file)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Get the URI of the selected file
            Uri fileUri = data.getData();
            if (fileUri != null) {
                uploadFileToFirebase(fileUri);
            }
        }
    }

    // Upload the selected file to Firebase Storage and save its details to Firestore
    private void uploadFileToFirebase(Uri fileUri) {
        // Generate a unique ID for the file to avoid overwriting
        StorageReference fileRef = storageRef.child("notes/" + UUID.randomUUID().toString());

        // Upload the file
        fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // File uploaded successfully, get its download URL
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String fileUrl = uri.toString();
                        saveFileDetailsToFirestore(fileUrl);
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle failure in file upload
                    Toast.makeText(UploadNotes.this, "Failed to upload file", Toast.LENGTH_SHORT).show();
                });
    }

    // Save the uploaded file details to Firestore
    private void saveFileDetailsToFirestore(String fileUrl) {
        if (user != null) {
            // Create a map of file details
            String fileName = "UploadedNote_" + System.currentTimeMillis(); // Use a timestamp for a unique name
            Map<String, Object> fileData = new HashMap<>();
            fileData.put("fileName", fileName);
            fileData.put("fileUrl", fileUrl);
            fileData.put("uploadedBy", user.getUid());
            fileData.put("timestamp", System.currentTimeMillis());

            // Save file details to Firestore under the user's document
            db.collection("UploadedNotes")
                    .document(user.getUid())
                    .collection("Files")
                    .document(fileName)
                    .set(fileData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(UploadNotes.this, "File uploaded and saved successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(UploadNotes.this, "Failed to save file details", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(UploadNotes.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Optionally close EditProfile so it won’t stay in the back stack
    }
}
