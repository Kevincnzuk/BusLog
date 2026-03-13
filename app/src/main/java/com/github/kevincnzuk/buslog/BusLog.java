package com.github.kevincnzuk.buslog;

import android.app.Application;

import com.google.android.material.color.DynamicColors;

public class BusLog extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Dynamic colour
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}
