package com.paradicedev.dailyst;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;

public class DailyTasksActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    CustomSimpleCursorAdapter mAdapter;
    int dayOfWeek;
    String mCurFilter;
    View[] lastItemSelected;

        @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            String[] SELECTION = new String[] {
                    TasksDBManager.COLUMN_ID,
                    TasksDBManager.COLUMN_TASKTITLE,
                    TasksDBManager.COLUMN_TASKDETAILS,
                    TasksDBManager.COLUMN_TASKDEADLINE,
                    TasksDBManager.COLUMN_TASKDAYOFWEEK,
            };

            Bundle dayLabelData = getIntent().getExtras();
            if(dayLabelData == null) {
                return null;
            }
            dayOfWeek = dayLabelData.getInt("dayOfWeek");

            final Calendar today = Calendar.getInstance();
            today.add(Calendar.DATE,7);
            long dailyListsMargin = today.getTimeInMillis();

        Uri baseUri;
        if (mCurFilter != null) {
            baseUri = Uri.withAppendedPath(DataListProvider.CONTENT_URI, Uri.encode(mCurFilter));
        } else {
            baseUri = DataListProvider.CONTENT_URI;
        }

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        String select = TasksDBManager.COLUMN_TASKDAYOFWEEK + " = " + dayOfWeek + " AND " + TasksDBManager.COLUMN_TASKDEADLINE + " < " + dailyListsMargin;
        return new CursorLoader(getApplicationContext(), baseUri, SELECTION, select, null,
                TasksDBManager.COLUMN_TASKTITLE + " ASC");

    }

    @Override
    public void onResume()
    {
        super.onResume();
        getLoaderManager().initLoader(0, null, this);
        if (lastItemSelected[0] != null) {
            deselectItemForDone(lastItemSelected[0]);
            deselectItemForMore(lastItemSelected[0]);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> p1, Cursor cursor)
    {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursor)
    {
        mAdapter.swapCursor(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_tasks);

        Bundle dayLabelData = getIntent().getExtras();
        if(dayLabelData == null) {
            return;
        }
        String dayLabelString = dayLabelData.getString("dayLabel");
        final TextView dayLabel = (TextView)findViewById(R.id.day_label_textview);
        dayLabel.setText(dayLabelString);

        String[] uiBindFrom = { TasksDBManager.COLUMN_TASKTITLE};
        int[] uiBindTo = { R.id.task_title_in_row };

        mAdapter = new CustomSimpleCursorAdapter(this, R.layout.custom_row, null, uiBindFrom, uiBindTo);

        final ListView dailyTasksList = (ListView) findViewById(R.id.daily_tasks_list);
        dailyTasksList.setAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);

        lastItemSelected = new View[1];

        dailyTasksList.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {

                        if (lastItemSelected[0] != null) {
                            deselectItemForDone(lastItemSelected[0]);
                            deselectItemForMore(lastItemSelected[0]);
                        }
                        selectItemForDone(view);
                        lastItemSelected[0] = view;

                        view.findViewById(R.id.done_task_button).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ContentResolver cr = getContentResolver();
                                cr.delete(DataListProvider.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build(),
                                        null, null);
                            }
                        });
                    }
                }
        );

        dailyTasksList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, final long id) {

                if (lastItemSelected[0] != null) {
                    deselectItemForDone(lastItemSelected[0]);
                    deselectItemForMore(lastItemSelected[0]);
                }
                selectItemForMore(view);
                lastItemSelected[0] = view;

                view.findViewById(R.id.more_task_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int taskId;
                        String taskTitle = new String();
                        String taskDetails = new String();
                        long taskDeadline;
                        int taskDeadlineDayOfWeek;

                        Intent intent = new Intent(getApplicationContext(), SingleTaskDetailsActivity.class);

                        Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                        taskId = cursor.getInt(cursor.getColumnIndex(TasksDBManager.COLUMN_ID));
                        taskTitle = cursor.getString(cursor.getColumnIndex(TasksDBManager.COLUMN_TASKTITLE));
                        taskDetails = cursor.getString(cursor.getColumnIndex(TasksDBManager.COLUMN_TASKDETAILS));
                        taskDeadline = cursor.getLong(cursor.getColumnIndex(TasksDBManager.COLUMN_TASKDEADLINE));
                        taskDeadlineDayOfWeek = cursor.getInt(cursor.getColumnIndex(TasksDBManager.COLUMN_TASKDAYOFWEEK));


                        intent.putExtra("taskTitle",taskTitle);
                        intent.putExtra("taskDetails",taskDetails);
                        intent.putExtra("taskId",taskId);
                        intent.putExtra("taskDeadline", taskDeadline);
                        intent.putExtra("taskDeadlineDayOfWeek", taskDeadlineDayOfWeek);

                        startActivity(intent);
                    }
                });
                return false;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask(v);
            }
        });
    }

    public void addTask (View view) {

        Intent intent = new Intent(this, AddTaskActivity.class);
        intent.putExtra("newTaskDeadlineDayOfWeek", dayOfWeek);
        startActivity(intent);
    }

    private void selectItemForDone(View v) {
        v.findViewById(R.id.done_task_button).setVisibility(View.VISIBLE);
        v.findViewById(R.id.done_task_button).setFocusable(false);
    }

    private void deselectItemForDone(View v) {
        v.findViewById(R.id.done_task_button).setVisibility(View.GONE);
    }

    private void selectItemForMore(View v) {
        v.findViewById(R.id.more_task_button).setVisibility(View.VISIBLE);
    }

    private void deselectItemForMore(View v) {
        v.findViewById(R.id.more_task_button).setVisibility(View.GONE);
    }
}
