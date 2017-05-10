package com.example.praveenagrawal.wifiscan;

import android.provider.BaseColumns;

/**
 * Created by praveen.agrawal on 30/01/17.
 */

public final class FeedReaderContract {


    private FeedReaderContract() {}

    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "wifiList";
        public static final String COLUMN_NAME_SSID = "SSID";
        public static final String COLUMN_NAME_TYPE = "TYPE";
        public static final String COLUMN_NAME_TIME = "Time";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_SSID + " TEXT," +
                    FeedEntry.COLUMN_NAME_TYPE + " TEXT," +
                    FeedEntry.COLUMN_NAME_TIME + " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

}

