package com.example.studentportal;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.ArrayList;
import com.example.studentportal.model.Task;

import java.util.List;
import java.util.Objects;

public class TodoList extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TaskAdapter taskAdapter;
    private EditText editTextDescription;
    private Spinner spinnerPriority;

    private ListenerRegistration tasksListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todolist);
        // Get the Spinner from the layout
//        Spinner spinnerPriority = findViewById(R.id.spinner_priority);

        // Create an ArrayAdapter using the string array defined in strings.xml
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priority_options, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the Spinner
        spinnerPriority.setAdapter(adapter);

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        // Check if user is logged in
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "You must be logged in to view tasks.", Toast.LENGTH_SHORT).show();
            finish();  // Close the activity if not logged in
            return;
        }

        // Initialize TaskAdapter and RecyclerView
        taskAdapter = new TaskAdapter(this::onTaskSelected);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);

        // Get UI components
        editTextDescription = findViewById(R.id.editText_description);
        spinnerPriority = findViewById(R.id.spinner_priority);

        // Fetch tasks from Firestore for the logged-in user
        fetchTasks();

        // Set up the ItemTouchHelper for swipe-to-delete functionality
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;  // We don't support drag & drop
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Task taskToDelete = taskAdapter.getTaskAtPosition(position);
                if (taskToDelete != null) {
                    deleteTask(((com.example.studentportal.model.Task) taskToDelete).getId());
                    taskAdapter.removeTaskAtPosition(position);
                    Toast.makeText(TodoList.this, "Task deleted", Toast.LENGTH_SHORT).show();
                }
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Inside the onClick listener
        Spinner finalSpinnerPriority = spinnerPriority;
        findViewById(R.id.button_add).setOnClickListener(v -> {
            String description = editTextDescription.getText().toString().trim();

            // Get the selected priority from the Spinner
            String priority = finalSpinnerPriority.getSelectedItem().toString();

            // Check if description is not empty
            if (!description.isEmpty()) {
                // Add the task with description and selected priority
                addTask(description, priority);
                Toast.makeText(TodoList.this, "Task added", Toast.LENGTH_SHORT).show();
                clearFields();
            } else {
                // Show a message if the description is empty
                Toast.makeText(TodoList.this, "Please enter a description", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Fetch tasks for the logged-in user from Firestore
    private void fetchTasks() {
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        tasksListener = db.collection("tasks")
                .whereEqualTo("userId", userId)
                .orderBy("priority")  // Sort tasks by priority
                .addSnapshotListener((QuerySnapshot snapshot, FirebaseFirestoreException e) -> {
                    if (e != null) {
                        Toast.makeText(TodoList.this, "Error fetching tasks.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<Task> tasks = new ArrayList<>();
                    assert snapshot != null;
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Task task = doc.toObject(Task.class);
                        if (task != null) {
                            tasks.add(task);
                        }
                    }
                    taskAdapter.setTasks(tasks);  // Update adapter with new task list
                });

    }

    // Add task to Firestore
    private void addTask(String description, String priority) {
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        Task newTask = new Task(null, description, priority, userId);

        db.collection("tasks")
                .add(newTask)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(TodoList.this, "Task added successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(TodoList.this, "Failed to add task.", Toast.LENGTH_SHORT).show();
                });
    }

    // Delete task from Firestore by task ID
    private void deleteTask(String taskId) {
        db.collection("tasks")
                .document(taskId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(TodoList.this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(TodoList.this, "Failed to delete task.", Toast.LENGTH_SHORT).show();
                });
    }

    // Task selected for editing (not implemented here)
    private void onTaskSelected(Task task) {
        // Handle task selection, e.g., open an edit dialog (not implemented in this version)
    }

    // Clear input fields after adding/updating a task
    private void clearFields() {
        editTextDescription.setText("");
        spinnerPriority.setSelection(0);  // Reset to the first priority option
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tasksListener != null) {
            tasksListener.remove();  // Clean up Firestore listener when activity is destroyed
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(TodoList.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Optionally close EditProfile so it won’t stay in the back stack
    }
}
