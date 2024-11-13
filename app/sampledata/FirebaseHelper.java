package com.example.studentportal;


import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class FirebaseHelper {

    private DatabaseReference database;
    private MutableLiveData<List<Task>> tasksLiveData;

    public FirebaseHelper() {
        database = FirebaseDatabase.getInstance().getReference("tasks");
        tasksLiveData = new MutableLiveData<>();
        fetchTasks();
    }

    // Get tasks as LiveData
    public LiveData<List<Task>> getTasks() {
        return tasksLiveData;
    }

    private void fetchTasks() {
        database.orderByChild("priority").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Task> taskList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Task task = dataSnapshot.getValue(Task.class);
                    if (task != null) {
                        taskList.add(task);
                    }
                }
                tasksLiveData.setValue(taskList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseHelper", "Data read cancelled or failed", error.toException());
            }
        });
    }

    // Add task
    public void addTask(String description, String priority) {
        String taskId = database.push().getKey();
        Task task = new Task(taskId, description, priority);

        if (taskId != null) {
            database.child(taskId).setValue(task).addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    Log.d("FirebaseHelper", "Task added successfully");
                } else {
                    Log.e("FirebaseHelper", "Task addition failed", task1.getException());
                }
            });
        }
    }

    // Update task with error check
    public void updateTask(String taskId, String newDescription, String newPriority, OnUpdateCompleteListener listener) {
        DatabaseReference taskRef = database.child(taskId);

        taskRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    taskRef.child("description").setValue(newDescription);
                    taskRef.child("priority").setValue(newPriority);
                    listener.onUpdateComplete(true); // Update successful
                } else {
                    listener.onUpdateComplete(false); // Task ID not found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseHelper", "Task update failed", error.toException());
                listener.onUpdateComplete(false);
            }
        });
    }

    // Delete a task by ID
    public void deleteTask(String taskId) {
        database.child(taskId).removeValue();
    }

    // Delete by description and priority
    public void deleteTaskByDescriptionAndPriority(String description, String priority) {
        database.orderByChild("description").equalTo(description).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Task task = dataSnapshot.getValue(Task.class);
                    if (task != null && priority.equals(task.getPriority())) {
                        dataSnapshot.getRef().removeValue();
                        Log.d("FirebaseHelper", "Task deleted successfully");
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseHelper", "Task deletion failed", error.toException());
            }
        });
    }

    // Interface to handle update completion
    public interface OnUpdateCompleteListener {
        void onUpdateComplete(boolean isSuccessful);
    }
}
