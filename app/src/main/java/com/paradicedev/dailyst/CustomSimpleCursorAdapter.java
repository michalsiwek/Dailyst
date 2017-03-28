package com.paradicedev.dailyst;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class CustomSimpleCursorAdapter extends SimpleCursorAdapter {

    public CustomSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to);
    }

    public View newView(Context _context, Cursor _cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(_context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_row, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context Context, final Cursor cursor) {

        int taskId = cursor.getInt(cursor.getColumnIndex(TasksDBManager.COLUMN_ID));
        String taskTitle = cursor.getString(cursor.getColumnIndex(TasksDBManager.COLUMN_TASKTITLE));
        TextView taskTitleTextView = (TextView) view.findViewById(R.id.task_title_in_row);
        taskTitleTextView.setText(taskTitle);

    }

}