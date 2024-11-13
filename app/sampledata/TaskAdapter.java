package com.example.studentportal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks = new ArrayList<>();
    private OnTaskSelectedListener listener; // Listener for task selection

    // Interface to handle task selection
    public interface OnTaskSelectedListener {
        void onTaskSelected(Task task); // Method to be implemented in MainActivity
    }

    // Constructor accepting the listener
    public TaskAdapter(OnTaskSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    // ViewHolder class to bind task data to views
    public class TaskViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewDescription;
        private TextView textViewPriority;

        public TaskViewHolder(View itemView) {
            super(itemView);
            textViewDescription = itemView.findViewById(R.id.textView_description);
            textViewPriority = itemView.findViewById(R.id.textView_priority);

            // Set item click listener to notify the listener when a task is selected
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskSelected(tasks.get(getAdapterPosition()));
                }
            });
        }

        // Bind task data to views
        public void bind(Task task) {
            textViewDescription.setText(task.getDescription());
            textViewPriority.setText(task.getPriority());
        }
    }
}
