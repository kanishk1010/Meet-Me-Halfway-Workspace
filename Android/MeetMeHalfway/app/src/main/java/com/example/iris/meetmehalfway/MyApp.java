package com.example.iris.meetmehalfway;

/**
 * Created by Iris on 12/10/17.
 */

import android.app.Application;

import com.facebook.appevents.AppEventsLogger;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppEventsLogger.activateApp(this);
    }
}
