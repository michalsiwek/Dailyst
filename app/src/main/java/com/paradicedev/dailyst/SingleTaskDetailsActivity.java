package com.paradicedev.dailyst;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

import static com.paradicedev.dailyst.TasksDBManager.COLUMN_TASKDAYOFWEEK;
import static com.paradicedev.dailyst.TasksDBManager.COLUMN_TASKDEADLINE;
import static com.paradicedev.dailyst.TasksDBManager.COLUMN_TASKDETAILS;
import static com.paradicedev.dailyst.TasksDBManager.COLUMN_TASKTITLE;

public class SingleTaskDetailsActivity extends AppCompatActivity {

    static Calendar deadlineCalendar;
    static TextView taskDeadlineTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_task_details);

        final int taskIdFromListview;
        final long taskDeadline;

        final EditText taskTitle = (EditText) findViewById(R.id.taskdetails_title_editText);
        final EditText taskDetails = (EditText) findViewById(R.id.taskdetails_details_editText);
        taskDeadlineTextView = (TextView) findViewById(R.id.taskdetails_deadline_textview);

        Bundle dayLabelData = getIntent().getExtras();
        if(dayLabelData == null) {
            return;
        }

        taskTitle.setText(dayLabelData.getString("taskTitle"));
        taskDetails.setText(dayLabelData.getString("taskDetails"));
        taskIdFromListview = dayLabelData.getInt("taskId");
        taskDeadline = dayLabelData.getLong("taskDeadline");

        Button setDeadlineButton = (Button) findViewById(R.id.taskdetails_set_deadline_task_button);
        Button doneButton = (Button) findViewById(R.id.taskdetails_done_button);
        Button keepButton = (Button) findViewById(R.id.taskdetails_keep_button);

        if(taskDeadline != -1) {
            taskDeadlineTextView.setText(setDeadlineTextView(taskDeadline));
            setDeadlineButton.setText(R.string.change_deadline_button_label);
        }

        setDeadlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment pickDeadline = new SelectDateFragmentInDetails();
                pickDeadline.show(getFragmentManager(), "DatePicker");
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentResolver cr = v.getContext().getContentResolver();
                cr.delete(DataListProvider.CONTENT_URI.buildUpon().appendPath(String.valueOf(taskIdFromListview)).build(), null, null);
                finish();
            }
        });

        keepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(taskTitle.getText().toString().equals("")){
                    Snackbar.make(v, R.string.missing_task_title_alert, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_TASKTITLE, taskTitle.getText().toString());
                    values.put(COLUMN_TASKDETAILS, taskDetails.getText().toString());
                    if (deadlineCalendar != null) {
                        values.put(COLUMN_TASKDEADLINE, deadlineCalendar.getTimeInMillis());
                        values.put(COLUMN_TASKDAYOFWEEK, deadlineCalendar.get(Calendar.DAY_OF_WEEK));
                    }

                    ContentResolver cr = v.getContext().getContentResolver();
                    cr.update(DataListProvider.CONTENT_URI.buildUpon().appendPath(String.valueOf(taskIdFromListview)).build(), values, null, null);
                    finish();
                }
            }
        });
    }

    public static String setDeadlineTextView(long date){
        String deadline = new String();
        deadlineCalendar = Calendar.getInstance();
        deadlineCalendar.setTimeInMillis(date);

        int year = deadlineCalendar.get(Calendar.YEAR);
        int month = deadlineCalendar.get(Calendar.MONTH);
        int day = deadlineCalendar.get(Calendar.DATE);

        int monthToShow = month + 1;

        if(monthToShow < 10 && day < 10){
            deadline = "0" + day + ".0" + monthToShow + "." + year;
        } else if (monthToShow < 10){
            deadline = day + ".0" + monthToShow + "." + year;
        } else if (day < 10) {
            deadline = "0" + day + "." + monthToShow + "." + year;
        } else {
            deadline = day + "." + monthToShow + "." + year;
        }

        return deadline;
    }

    public static class SelectDateFragmentInDetails extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int yy;
            int mm;
            int dd;
            if(deadlineCalendar != null) {
                yy = deadlineCalendar.get(Calendar.YEAR);
                mm = deadlineCalendar.get(Calendar.MONTH);
                dd = deadlineCalendar.get(Calendar.DATE);
            } else {
                yy = calendar.get(Calendar.YEAR);
                mm = calendar.get(Calendar.MONTH);
                dd = calendar.get(Calendar.DATE);
            }
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, yy, mm, dd);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            deadlineCalendar = Calendar.getInstance();
            deadlineCalendar.set(Calendar.YEAR, year);
            deadlineCalendar.set(Calendar.MONTH, month);
            deadlineCalendar.set(Calendar.DAY_OF_MONTH, day);

            taskDeadlineTextView.setText(setDeadlineTextView(deadlineCalendar.getTimeInMillis()));

        }
    }
}