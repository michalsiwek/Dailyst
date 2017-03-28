package com.paradicedev.dailyst;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import static com.paradicedev.dailyst.R.id.task_title_in_row;

public class CustomAdapter extends ArrayAdapter<String> {

    public CustomAdapter(Context context, ArrayList<String> cars) {
        super(context, R.layout.custom_row, cars);
    }

    //@NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater myInflater = LayoutInflater.from(getContext());
        View customView = myInflater.inflate(R.layout.custom_row, parent, false);

        String singleTask = getItem(position);
        TextView taskTitle = (TextView) customView.findViewById(task_title_in_row);

        taskTitle.setText(singleTask);
        return customView;
    }
}