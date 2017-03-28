package com.paradicedev.dailyst;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Calendar;

public class GeneralTasksTab extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    CustomSimpleCursorAdapter mAdapter;
    String mCurFilter;
    View listHeader;
    View[] lastItemSelected;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] SELECTION = new String[] {
                TasksDBManager.COLUMN_TASKTITLE + " as deadline",
                TasksDBManager.COLUMN_ID,
                TasksDBManager.COLUMN_TASKTITLE,
                TasksDBManager.COLUMN_TASKDETAILS,
                TasksDBManager.COLUMN_TASKDEADLINE,
                TasksDBManager.COLUMN_TASKDAYOFWEEK,
        };

        final Calendar today = Calendar.getInstance();
        today.add(Calendar.DATE,6);
        long dailyListsMargin = today.getTimeInMillis();

        Uri baseUri;
        if (mCurFilter != null) {
            baseUri = Uri.withAppendedPath(DataListProvider.CONTENT_URI, Uri.encode(mCurFilter));
        } else {
            baseUri = DataListProvider.CONTENT_URI;
        }

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        String select = TasksDBManager.COLUMN_TASKDEADLINE + " = -1 OR " + TasksDBManager.COLUMN_TASKDEADLINE + " > " + dailyListsMargin;
        return new CursorLoader(getContext(), baseUri, SELECTION, select, null,
                TasksDBManager.COLUMN_TASKDEADLINE + " ASC, " + TasksDBManager.COLUMN_TASKTITLE + " ASC");

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = getActivity();

        String[] uiBindFrom = { TasksDBManager.COLUMN_ID, TasksDBManager.COLUMN_TASKTITLE };
        int[] uiBindTo = { R.id.task_title_in_row };

        mAdapter = new CustomSimpleCursorAdapter(context, R.layout.custom_row, null, uiBindFrom, uiBindTo);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.general_tasks_tab, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);

        ListView generalTasksListView = getListView();

        lastItemSelected = new View[1];

        generalTasksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
                if (lastItemSelected[0] != null) {
                    deselectItemForDone(lastItemSelected[0]);
                    deselectItemForMore(lastItemSelected[0]);
                }
                selectItemForDone(view);

                lastItemSelected[0] = view;

                view.findViewById(R.id.done_task_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContentResolver cr = getContext().getContentResolver();
                        cr.delete(DataListProvider.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build(),
                                null, null);
                    }
                });
            }
                                                    }
        );

        generalTasksListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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

                        Intent intent = new Intent(getContext(), SingleTaskDetailsActivity.class);

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