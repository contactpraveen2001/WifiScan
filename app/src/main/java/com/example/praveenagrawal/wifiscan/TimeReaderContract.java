package com.example.praveenagrawal.wifiscan;

import android.provider.BaseColumns;

/**
 * Created by praveen.agrawal on 09/05/17.
 */

public final class TimeReaderContract {

    private TimeReaderContract() {}

    public static class TimeEntry implements BaseColumns {
        public static final String TABLE_NAME = "timeEntry";
        public static final String COLUMN_NAME_SSID = "SSID";
        public static final String COLUMN_NAME_DATE = "DATE";
        public static final String COLUMN_NAME_IN_TIME = "IN_TIME";
        public static final String COLUMN_NAME_OUT_TIME = "OUT_Time";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TimeEntry.TABLE_NAME + " (" +
                    TimeEntry._ID + " INTEGER PRIMARY KEY," +
                    TimeEntry.COLUMN_NAME_SSID + " TEXT," +
                    TimeEntry.COLUMN_NAME_DATE + " TEXT," +
                    TimeEntry.COLUMN_NAME_IN_TIME + " TEXT," +
                    TimeEntry.COLUMN_NAME_OUT_TIME + " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TimeEntry.TABLE_NAME;

    public static final String dateFormat = "dd/MM/yyyy";
    public static final String timeFormat = "HH:mm:ss";
}
