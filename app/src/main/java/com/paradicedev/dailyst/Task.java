package com.paradicedev.dailyst;

public class Task {

    private int _id;
    private String taskTitle;
    private String taskDetails;
    private long taskDeadline;
    private int taskDayOfWeek;

    public Task(){}

    public Task(String taskTitle, String taskDetails) {
        this.taskTitle = taskTitle;
        this.taskDetails = taskDetails;
    }

    public Task(String taskTitle, String taskDetails, long taskDeadline, int taskDayOfWeek) {
        this.taskTitle = taskTitle;
        this.taskDetails = taskDetails;
        this.taskDeadline = taskDeadline;
        this.taskDayOfWeek = taskDayOfWeek;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getTaskDayOfWeek() {
        return taskDayOfWeek;
    }

    public void setTaskDayOfWeek(int taskDayOfWeek) {
        this.taskDayOfWeek = taskDayOfWeek;
    }

    public long getTaskDeadline() {
        return taskDeadline;
    }

    public void setTaskDeadline(long taskDeadline) {
        this.taskDeadline = taskDeadline;
    }

    public String getTaskDetails() {
        return taskDetails;
    }

    public void setTaskDetails(String taskDetails) {
        this.taskDetails = taskDetails;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }
}
