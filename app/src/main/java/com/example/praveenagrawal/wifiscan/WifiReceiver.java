package com.example.praveenagrawal.wifiscan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;

import static android.content.Context.AUDIO_SERVICE;

/**
 * Created by praveen.agrawal on 30/01/17.
 */

public class WifiReceiver extends BroadcastReceiver
{

    public void onReceive(Context c, Intent intent)
    {
        Log.w("Scan","receive Scan");
        AudioManager audioManager = (AudioManager) c.getSystemService(AUDIO_SERVICE);
        WifiManager wifiManager = (WifiManager) c.getSystemService (c.WIFI_SERVICE);
        if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL &&  audioManager.getRingerMode() != AudioManager.RINGER_MODE_VIBRATE && audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT)
        {
            return;
        }
        if (wifiManager.isWifiEnabled() == false)
        {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            return;
        }
        WifiInfo info = wifiManager.getConnectionInfo ();
        if (info == null)
        {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            return;
        }
        if (info.getSSID() == null)
        {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            return;
        }
        String ssid  = info.getSSID().trim().replaceAll("\"","");
        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(c);
        ArrayList<String> selectList = mDbHelper.getSavedList();
        for (int i = 0; i < selectList.size(); i++)
        {
            if (ssid.equals(selectList.get(i).split(":")[0]))
            {
                switch (selectList.get(i).split(":")[1])
                {
                    case "1":
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        Log.w("Scan","RINGER_MODE_VIBRATE");
                        return;
                    case "2":
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                        Log.w("Scan","RINGER_MODE_VIBRATE");
                        return;
                    case "3":
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        Log.w("Scan","RINGER_MODE_VIBRATE");
                        return;
                }

            }
            else
            {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            }
        }
    }
}
