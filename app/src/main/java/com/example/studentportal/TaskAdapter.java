package com.example.studentportal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentportal.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks = new ArrayList<>();
    private OnTaskSelectedListener listener; // Listener for task selection

    // Interface to handle task selection
    public interface OnTaskSelectedListener {
        void onTaskSelected(Task task);
    }

    // Constructor accepting the listener
    public TaskAdapter(OnTaskSelectedListener listener) {
        this.listener = listener;
    }

    // Method to update the tasks list and notify the adapter
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    // Method to get a task at a specific position
    public Task getTaskAtPosition(int position) {
        return tasks.get(position);
    }

    // Method to remove a task at a specific position
    public void removeTaskAtPosition(int position) {
        tasks.remove(position);
        notifyItemRemoved(position);
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

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView textDescription;
        private final TextView textPriority;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textDescription = itemView.findViewById(R.id.text_description);
            textPriority = itemView.findViewById(R.id.text_priority);

            // Handle item click to notify listener
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Task selectedTask = tasks.get(position);
                    listener.onTaskSelected(selectedTask);
                }
            });
        }

        // Bind task data to the view
        public void bind(Task task) {
            textDescription.setText(task.getDescription());
            textPriority.setText(task.getPriority());
        }
    }
}
