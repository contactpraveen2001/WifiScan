package com.example.praveenagrawal.wifiscan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.praveenagrawal.wifiscan.TimeReaderContract.timeFormat;

public class TimeListActivity extends AppCompatActivity {

    String ssid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_time_list);
        Bundle b = getIntent().getExtras();
        if (b != null)
        {
            ssid = b.getString("ssid");
        }
        WifiScanDbHelper mDbHelper = new WifiScanDbHelper(this);
        ArrayList<TimeEntryData> timeEntryList = getInOutTime(mDbHelper.getTimeEntryList(ssid));
        TimeEntryListAdapter timeEntryListAdapter = new TimeEntryListAdapter(this,timeEntryList);
        ListView listView = (ListView) findViewById(R.id.activity_time_entry_list);
        listView.setAdapter(timeEntryListAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public ArrayList<TimeEntryData> getInOutTime(ArrayList<TimeEntryData> list)
    {
        ArrayList<TimeEntryData> filterList = new ArrayList<TimeEntryData>();
        for (int i = 0; i < list.size(); i++)
        {
            if (i != 0 && filterList.get(filterList.size() - 1).date.equals(list.get(i).date))
            {
                filterList.get(filterList.size() - 1).outTime = list.get(i).outTime;
            }
            else
            {
                filterList.add(list.get(i));
            }
        }
        for (int i = 0; i < filterList.size(); i++)
        {
            if (filterList.get(i).outTime.equals("0"))
            {
                String outTime = new SimpleDateFormat(timeFormat).format(Calendar.getInstance().getTime());
                filterList.get(i).totalTime = Interval(filterList.get(i).inTime , outTime) + " Hours";
            }
            else
            {
                filterList.get(i).totalTime = Interval(filterList.get(i).inTime,filterList.get(i).outTime) + " Hours";
            }
        }
        return  filterList;
    }


    public double Interval(String start, String end)
    {
        DateFormat df = new SimpleDateFormat(timeFormat);
        double interval = 0;
        try {
            Date inTime = df.parse(start);
            Date outTime = df.parse(end);
            interval = ((double) outTime.getTime() - (double) inTime.getTime())/(1000 * 60 * 60);
        }
        catch (Exception ex)
        {
            Log.w("Interval",ex.getMessage());
        }
        return interval;
    }
}
