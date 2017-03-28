package com.paradicedev.dailyst;


import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class DataListProvider extends ContentProvider {

    private TasksDBManager tasksDBManager;

    @Override
    public boolean onCreate() {
        tasksDBManager = new TasksDBManager(getContext());
        return true;
    }

    private static final String AUTHORITY = "com.paradicedev.dailyst.DataListProvider";
    public static final int TASKS = 100;
    public static final int TASK_ID = 110;

    private static final String TASKS_BASE_PATH = "tasks";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + TASKS_BASE_PATH);

    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/mt-task";
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/mt-task";

    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);
    private static final String DEBUG_TAG = "DataListProvider";
    static {
        sURIMatcher.addURI(AUTHORITY, TASKS_BASE_PATH, TASKS);
        sURIMatcher.addURI(AUTHORITY, TASKS_BASE_PATH + "/#", TASK_ID);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TasksDBManager.TABLE_TASKS);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case TASK_ID:
                queryBuilder.appendWhere(TasksDBManager.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            case TASKS:
                // filter
                //queryBuilder.buildQuery("SELECT * FROM " TasksDBManager.TABLE_TASKS + "WHERE 1 ORDER BY " + TasksDBManager.COLUMN_TASKTITLE,selection,null)
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }

        Cursor cursor = queryBuilder.query(tasksDBManager.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = tasksDBManager.getWritableDatabase();
        int rowsAffected = 0;
        switch (uriType) {
            case TASKS:
                rowsAffected = sqlDB.delete(TasksDBManager.TABLE_TASKS,
                        selection, selectionArgs);
                break;
            case TASK_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsAffected = sqlDB.delete(TasksDBManager.TABLE_TASKS,
                            TasksDBManager.COLUMN_ID + "=" + id, null);
                } else {
                    rowsAffected = sqlDB.delete(TasksDBManager.TABLE_TASKS,
                            selection + " and " + TasksDBManager.COLUMN_ID + "=" + id,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    @Override
    public String getType(Uri uri) {
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case TASKS:
                return CONTENT_TYPE;
            case TASK_ID:
                return CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        if (uriType != TASKS) {
            throw new IllegalArgumentException("Invalid URI for insert");
        }
        SQLiteDatabase sqlDB = tasksDBManager.getWritableDatabase();
        try {
            long newID = sqlDB.insertOrThrow(TasksDBManager.TABLE_TASKS,
                    null, values);
            if (newID > 0) {
                Uri newUri = ContentUris.withAppendedId(uri, newID);
                getContext().getContentResolver().notifyChange(uri, null);
                return newUri;
            } else {
                throw new SQLException("Failed to insert row into " + uri);
            }
        } catch (SQLiteConstraintException e) {
            Log.i(DEBUG_TAG, "Ignoring constraint failure.");
        }
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = tasksDBManager.getWritableDatabase();

        int rowsAffected;

        switch (uriType) {
            case TASK_ID:
                String id = uri.getLastPathSegment();
                StringBuilder modSelection = new StringBuilder(TasksDBManager.COLUMN_ID
                        + "=" + id);

                if (!TextUtils.isEmpty(selection)) {
                    modSelection.append(" AND " + selection);
                }

                rowsAffected = sqlDB.update(TasksDBManager.TABLE_TASKS,
                        values, modSelection.toString(), null);
                break;
            case TASKS:
                rowsAffected = sqlDB.update(TasksDBManager.TABLE_TASKS,
                        values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

}
