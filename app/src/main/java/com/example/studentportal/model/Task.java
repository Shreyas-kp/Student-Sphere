package com.example.studentportal.model;

public class Task {
    private String id;
    private String description;
    private String priority;
    private String userId;

    // Empty constructor needed for Firestore deserialization
    public Task() {}

    // Constructor
    public Task(String id, String description, String priority, String userId) {
        this.id = id;
        this.description = description;
        this.priority = priority;
        this.userId = userId;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Task{id='" + id + "', description='" + description + "', priority='" + priority + "', userId='" + userId + "'}";
    }
}