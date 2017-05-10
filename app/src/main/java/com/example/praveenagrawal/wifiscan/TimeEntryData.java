package com.example.praveenagrawal.wifiscan;

/**
 * Created by praveen.agrawal on 09/05/17.
 */

public class TimeEntryData {

    public String ssid;
    public String date;
    public String inTime;
    public String outTime;
    public String totalTime = "";
    public TimeEntryData(String ssidValue, String inTimeValue, String outTimeValue, String dateValue) {
        ssid = ssidValue;
        date = dateValue;
        inTime = inTimeValue;
        outTime = outTimeValue;
    }
}
