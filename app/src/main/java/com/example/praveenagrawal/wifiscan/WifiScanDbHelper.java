package com.example.praveenagrawal.wifiscan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.praveenagrawal.wifiscan.FeedReaderContract.FeedEntry;
import com.example.praveenagrawal.wifiscan.TimeReaderContract.TimeEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.praveenagrawal.wifiscan.TimeReaderContract.dateFormat;
import static com.example.praveenagrawal.wifiscan.TimeReaderContract.timeFormat;

/**
 * Created by praveen.agrawal on 30/01/17.
 */

public class WifiScanDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "WifiScan.db";
    public WifiScanDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FeedReaderContract.SQL_CREATE_ENTRIES);
        db.execSQL(TimeReaderContract.SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(FeedReaderContract.SQL_DELETE_ENTRIES);
        db.execSQL(TimeReaderContract.SQL_CREATE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addSSID(String SSID)
    {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_SSID, SSID);
        values.put(FeedEntry.COLUMN_NAME_TYPE,"2");
        values.put(FeedEntry.COLUMN_NAME_TIME,"false");

// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(FeedEntry.TABLE_NAME, null, values);
    }

    public void removeSSID(String SSID)
    {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        // Define 'where' part of query.
        String selection = FeedEntry.COLUMN_NAME_SSID + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {SSID};
        // Issue SQL statement.
        db.delete(FeedEntry.TABLE_NAME, selection, selectionArgs);
    }

    public void updateType(String SSID, String type)
    {
        SQLiteDatabase db = this.getReadableDatabase();

// New value for one column
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_TYPE, type);

// Which row to update, based on the title
        String selection = FeedEntry.COLUMN_NAME_SSID + " LIKE ?";
        String[] selectionArgs = {SSID};

        int count = db.update(
                FeedEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        if (count > 0)
        {
            Log.w("update db" , "table is updated");
        }
    }

    public void updateTime(String SSID, String time)
    {
        SQLiteDatabase db = this.getReadableDatabase();

// New value for one column
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_TIME, time);

// Which row to update, based on the title
        String selection = FeedEntry.COLUMN_NAME_SSID + " LIKE ?";
        String[] selectionArgs = {SSID};

        int count = db.update(
                FeedEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        if (count > 0)
        {
            Log.w("update db" , "table is updated");
        }
    }

    public ArrayList<FeedEntryData> getSavedList()
    {
        ArrayList<FeedEntryData> dataList = new ArrayList<FeedEntryData>();
        SQLiteDatabase db = this.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                // FeedEntry._ID,
                FeedEntry.COLUMN_NAME_SSID,
                FeedEntry.COLUMN_NAME_TYPE,
                FeedEntry.COLUMN_NAME_TIME
        };

// Filter results WHERE "title" = 'My Title'
        //      String selection = FeedEntry.COLUMN_NAME_TITLE + " = ?";

        Cursor cursor = db.query(
                FeedEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null, //selection,                                // The columns for the WHERE clause
                null, //selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null //sortOrder                                 // The sort order
        );
        try {
            while(cursor.moveToNext()) {
                FeedEntryData data = new FeedEntryData(cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_SSID)),cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TYPE)),cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TIME)));
                dataList.add(data);
            }
        }
        catch (Exception ex)
        {
            Log.w("getSavedList",ex.getMessage());
        }
        finally {

            cursor.close();
            return dataList;
        }
    }

    public void addInTime(String ssid)
    {
        String date = new SimpleDateFormat(dateFormat).format(Calendar.getInstance().getTime());
        String inTime = new SimpleDateFormat(timeFormat).format(Calendar.getInstance().getTime());
        if (shouldAddInTime(ssid,date))
        {
            // Gets the data repository in write mode
            SQLiteDatabase db = this.getWritableDatabase();
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(TimeEntry.COLUMN_NAME_SSID, ssid);
            values.put(TimeEntry.COLUMN_NAME_IN_TIME,inTime);
            values.put(TimeEntry.COLUMN_NAME_OUT_TIME,"0");
            values.put(TimeEntry.COLUMN_NAME_DATE,date);
            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(TimeEntry.TABLE_NAME, null, values);
        }
    }
    public void updateOutTime()
    {
        String date = new SimpleDateFormat(dateFormat).format(Calendar.getInstance().getTime());
        String outTime = new SimpleDateFormat(timeFormat).format(Calendar.getInstance().getTime());
        SQLiteDatabase db = this.getReadableDatabase();

         // New value for one column
        ContentValues values = new ContentValues();
        values.put(TimeEntry.COLUMN_NAME_OUT_TIME, outTime);

         // Which row to update, based on the title
        String selection = TimeEntry.COLUMN_NAME_DATE + " LIKE ? and " + TimeEntry.COLUMN_NAME_OUT_TIME + " LIKE ?";
        String[] selectionArgs = {date,"0"};
        int count = db.update(
                TimeEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        if (count > 0)
        {
            Log.w("update db" , "table is updated for today");
        }
        values = new ContentValues();
        values.put(TimeEntry.COLUMN_NAME_OUT_TIME, "24:00:00");

        // Which row to update, based on the title
        selection =  TimeEntry.COLUMN_NAME_OUT_TIME + " LIKE ?";
        String[] selectionArgsPast = {"0"};
        count = db.update(
                TimeEntry.TABLE_NAME,
                values,
                selection,
                selectionArgsPast);
        if (count > 0)
        {
            Log.w("update db" , "table is updated for past");
        }
    }

    public ArrayList<TimeEntryData> getTimeEntryList(String ssid)
    {
        ArrayList<TimeEntryData> dataList = new ArrayList<TimeEntryData>();
        SQLiteDatabase db = this.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                // FeedEntry._ID,
                TimeEntry.COLUMN_NAME_SSID,
                TimeEntry.COLUMN_NAME_IN_TIME,
                TimeEntry.COLUMN_NAME_OUT_TIME,
                TimeEntry.COLUMN_NAME_DATE
        };

// Which row to select, based on the title
        String selection = TimeEntry.COLUMN_NAME_SSID + " LIKE ?";
        String[] selectionArgs = {ssid};

        Cursor cursor = db.query(
                TimeEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection, //selection,                                // The columns for the WHERE clause
                selectionArgs, //selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null //sortOrder                                 // The sort order
        );

        try {
            while(cursor.moveToNext()) {
                TimeEntryData data = new TimeEntryData(cursor.getString(cursor.getColumnIndexOrThrow(TimeEntry.COLUMN_NAME_SSID)),cursor.getString(cursor.getColumnIndexOrThrow(TimeEntry.COLUMN_NAME_IN_TIME)),cursor.getString(cursor.getColumnIndexOrThrow(TimeEntry.COLUMN_NAME_OUT_TIME)),cursor.getString(cursor.getColumnIndexOrThrow(TimeEntry.COLUMN_NAME_DATE)));
                dataList.add(data);
            }
        }
        catch (Exception ex)
        {
            Log.w("getTimeEntryList",ex.getMessage());
        }
        finally {

            cursor.close();
            return dataList;
        }
    }

    public Boolean shouldAddInTime(String ssid, String date)
    {
        ArrayList<TimeEntryData> dataList = new ArrayList<TimeEntryData>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                // FeedEntry._ID,
                TimeEntry.COLUMN_NAME_SSID,
                TimeEntry.COLUMN_NAME_IN_TIME,
                TimeEntry.COLUMN_NAME_OUT_TIME,
                TimeEntry.COLUMN_NAME_DATE
        };

        // Which row to select, based on the title
        String selection = TimeEntry.COLUMN_NAME_SSID + " LIKE ? and " + TimeEntry.COLUMN_NAME_DATE + " LIKE ? and " + TimeEntry.COLUMN_NAME_OUT_TIME + " LIKE ?";
        String[] selectionArgs = {ssid,date,"0"};

        Cursor cursor = db.query(
                TimeEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection, //selection,                                // The columns for the WHERE clause
                selectionArgs, //selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null //sortOrder                                 // The sort order
        );
        if (cursor.getCount() < 1)
        {
            return true;
        }
        return false;
    }
}
