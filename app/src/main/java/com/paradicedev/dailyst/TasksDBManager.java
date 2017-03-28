package com.paradicedev.dailyst;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;

public class TasksDBManager extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "tasks.db";
    public static final String TABLE_TASKS = "tasks";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TASKTITLE = "tasktitle";
    public static final String COLUMN_TASKDETAILS = "taskdetails";
    public static final String COLUMN_TASKDEADLINE = "taskdeadline";
    public static final String COLUMN_TASKDAYOFWEEK = "taskdayofweek";

    public TasksDBManager(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public TasksDBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE "
                + TABLE_TASKS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TASKTITLE + " TEXT, "
                + COLUMN_TASKDETAILS + " TEXT, "
                + COLUMN_TASKDEADLINE + " LONG,"
                + COLUMN_TASKDAYOFWEEK + " INTEGER"
                + ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST " + TABLE_TASKS);
        onCreate(db);
    }

    //Add new task to database method
    public void addTask(View view, Task task){

        ContentValues values = new ContentValues();

        values.put(COLUMN_TASKTITLE, task.getTaskTitle());
        values.put(COLUMN_TASKDETAILS, task.getTaskDetails());
        values.put(COLUMN_TASKDEADLINE, task.getTaskDeadline());
        values.put(COLUMN_TASKDAYOFWEEK, task.getTaskDayOfWeek());

        ContentResolver cr = view.getContext().getContentResolver();
        cr.insert(DataListProvider.CONTENT_URI, values);

    }
}
