package com.example.praveenagrawal.wifiscan;

/**
 * Created by praveen.agrawal on 09/05/17.
 */

public class FeedEntryData {

    public String ssid;
    public String type;
    public String isTime;

    public FeedEntryData(String ssidValue, String typeValue, String isTimeValue) {
        ssid = ssidValue;
        type = typeValue;
        isTime = isTimeValue;
    }
}
