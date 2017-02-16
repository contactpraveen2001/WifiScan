package com.example.praveenagrawal.wifiscan;

import android.app.Application;
import android.content.Intent;

/**
 * Created by praveen.agrawal on 30/01/17.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this,BackgroundService.class));
    }
}
