package com.example.studentportal;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.google.android.gms.tasks.Task;

public class TodoList extends AppCompatActivity implements TaskAdapter.OnTaskSelectedListener {

    private FirebaseHelper firebaseHelper;
    private TaskAdapter taskAdapter;
    private EditText editTextDescription;
    private Spinner spinnerPriority;
    private String selectedTaskId = null; // Store selected task ID for updating

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);

        firebaseHelper = new FirebaseHelper();
        taskAdapter = new TaskAdapter(this); // Pass this as the listener
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);

        editTextDescription = findViewById(R.id.editText_description);
        spinnerPriority = findViewById(R.id.spinner_priority);

        // Observe tasks from Firebase
        firebaseHelper.getTasks().observe(this, tasks -> {
            if (tasks != null) {
                taskAdapter.setTasks(tasks);
            }
        });

        // Set up ItemTouchHelper for swipe-to-delete
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // We don’t support drag & drop
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Task taskToDelete = taskAdapter.getTaskAtPosition(position);
                if (taskToDelete != null) {
                    firebaseHelper.deleteTask(taskToDelete.getId()); // Delete from Firebase
                    taskAdapter.removeTaskAtPosition(position); // Update the RecyclerView
                    Toast.makeText(TodoList.this, "Task deleted", Toast.LENGTH_SHORT).show();
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Add task
        findViewById(R.id.button_add).setOnClickListener(v -> {
            String description = editTextDescription.getText().toString().trim();
            String priority = spinnerPriority.getSelectedItem().toString();
            if (!description.isEmpty()) {
                firebaseHelper.addTask(description, priority);
                Toast.makeText(TodoList.this, "Task added", Toast.LENGTH_SHORT).show();
                clearFields();
            } else {
                Toast.makeText(TodoList.this, "Please enter a description", Toast.LENGTH_SHORT).show();
            }
        });

        // Update task
        findViewById(R.id.button_update).setOnClickListener(v -> {
            String newDescription = editTextDescription.getText().toString().trim();
            String newPriority = spinnerPriority.getSelectedItem().toString();

            if (!newDescription.isEmpty()) {
                boolean taskFound = false; // Track if we found a matching task

                // Check if there's a matching task description
                for (Task task : taskAdapter.getTasks()) { // Assuming you create a method to get tasks
                    if (task.getDescription().equals(newDescription)) {
                        taskFound = true;
                        firebaseHelper.updateTask(task.getId(), newDescription, newPriority, isSuccessful -> {
                            if (isSuccessful) {
                                Toast.makeText(TodoList.this, "Task updated", Toast.LENGTH_SHORT).show();
                                clearFields();
                            } else {
                                Toast.makeText(TodoList.this, "Error: Task not found", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break; // Stop checking after the first match
                    }
                }

                // If no task was found by description, check if a task ID was selected
                if (!taskFound && selectedTaskId != null) {
                    firebaseHelper.updateTask(selectedTaskId, newDescription, newPriority, isSuccessful -> {
                        if (isSuccessful) {
                            Toast.makeText(TodoList.this, "Task updated", Toast.LENGTH_SHORT).show();
                            clearFields();
                        } else {
                            Toast.makeText(TodoList.this, "Error: Task not found", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                // If no task was found and no task ID is selected, show a message
                if (!taskFound && selectedTaskId == null) {
                    Toast.makeText(TodoList.this, "No matching task found, please select a task or enter a matching description", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(TodoList.this, "Please enter a description", Toast.LENGTH_SHORT).show();
            }
        });

        // Delete task by matching description and priority
        findViewById(R.id.button_delete).setOnClickListener(v -> {
            String description = editTextDescription.getText().toString().trim();
            String priority = spinnerPriority.getSelectedItem().toString();
            if (!description.isEmpty()) {
                boolean taskFound = false; // Track if we found a matching task

                // Check if there's a matching task description and priority
                for (Task task : taskAdapter.getTasks()) {
                    if (task.getDescription().equals(description) && task.getPriority().equals(priority)) {
                        taskFound = true;
                        firebaseHelper.deleteTask(task.getId()); // Delete from Firebase
                        taskAdapter.removeTask(task); // Update the RecyclerView
                        Toast.makeText(TodoList.this, "Task deleted", Toast.LENGTH_SHORT).show();
                        break; // Stop checking after the first match
                    }
                }

                // If no task was found by description and priority
                if (!taskFound) {
                    Toast.makeText(TodoList.this, "No matching task found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(TodoList.this, "Please enter a description", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearFields() {
        editTextDescription.setText("");
        spinnerPriority.setSelection(0);
        selectedTaskId = null;
    }

    @Override
    public void onTaskSelected(Task task) {
        // Handle task selection logic
        selectedTaskId = task.getId();
        editTextDescription.setText(task.getDescription());
        spinnerPriority.setSelection(((ArrayAdapter<String>) spinnerPriority.getAdapter()).getPosition(task.getPriority()));
    }
}
