package com.paradicedev.dailyst;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;


public class AddTaskActivity extends AppCompatActivity {

    TasksDBManager tasksDMManager = new TasksDBManager(this, null, null, 1);

    EditText taskTitle;
    EditText taskDetails;

    static TextView deadlineTextView;
    static Calendar deadlineCalendar;
    static Button setDeadlineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        taskTitle = (EditText)findViewById(R.id.task_title_editText);
        taskDetails = (EditText)findViewById(R.id.task_details_editText);
        deadlineTextView = (TextView)findViewById(R.id.deadline_textview);
        setDeadlineButton = (Button) findViewById(R.id.set_deadline_task_button);

        Bundle deadlineInfo = getIntent().getExtras();
        if(deadlineInfo != null) {

            int newTaskDeadlineDayOfWeek = deadlineInfo.getInt("newTaskDeadlineDayOfWeek");

            deadlineCalendar = Calendar.getInstance();
            int todaysDayOfWeek = deadlineCalendar.get(Calendar.DAY_OF_WEEK);

            if (todaysDayOfWeek != newTaskDeadlineDayOfWeek) {
                deadlineCalendar.add(Calendar.DATE,newTaskDeadlineDayOfWeek - todaysDayOfWeek);
            }

            int year = deadlineCalendar.get(Calendar.YEAR);
            int month = deadlineCalendar.get(Calendar.MONTH);
            int day = deadlineCalendar.get(Calendar.DATE);

            int monthToShow = month + 1;

            if(monthToShow < 10 && day < 10){
                deadlineTextView.setText("0" + day + ".0" + monthToShow + "." + year);
            } else if (monthToShow < 10){
                deadlineTextView.setText(day + ".0" + monthToShow + "." + year);
            } else if (day < 10) {
                deadlineTextView.setText("0" + day + "." + monthToShow + "." + year);
            } else {
                deadlineTextView.setText(day + "." + monthToShow + "." + year);
            }

            setDeadlineButton.setText(R.string.change_deadline_button_label);
            deadlineInfo.clear();
        }

        setDeadlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment pickDeadline = new SelectDateFragment();
                pickDeadline.show(getFragmentManager(), "DatePicker");
            }
        });

    }

    // ADD
    public void saveNewTask(View view){
        if(taskTitle.getText().toString().equals("")){
            Snackbar.make(view, R.string.missing_task_title_alert, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else if (deadlineCalendar == null){
            Task task = new Task(taskTitle.getText().toString(), taskDetails.getText().toString(), -1, -1);
            tasksDMManager.addTask(view, task);
            this.finish();
        } else {
            Task task = new Task(taskTitle.getText().toString(), taskDetails.getText().toString(),
                    deadlineCalendar.getTimeInMillis(), deadlineCalendar.get(Calendar.DAY_OF_WEEK));
            tasksDMManager.addTask(view, task);
            deadlineCalendar = null;
            this.finish();
        }
    }

    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

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
            int monthToShow = month + 1;

            if(monthToShow < 10 && day < 10){
                deadlineTextView.setText("0" + day + ".0" + monthToShow + "." + year);
            } else if (monthToShow < 10){
                deadlineTextView.setText(day + ".0" + monthToShow + "." + year);
            } else if (day < 10) {
                deadlineTextView.setText("0" + day + "." + monthToShow + "." + year);
            } else {
                deadlineTextView.setText(day + "." + monthToShow + "." + year);
            }

            deadlineCalendar = Calendar.getInstance();
            deadlineCalendar.set(Calendar.YEAR,year);
            deadlineCalendar.set(Calendar.MONTH,month);
            deadlineCalendar.set(Calendar.DAY_OF_MONTH,day);

            setDeadlineButton.setText(R.string.change_deadline_button_label);
        }
    }

}
