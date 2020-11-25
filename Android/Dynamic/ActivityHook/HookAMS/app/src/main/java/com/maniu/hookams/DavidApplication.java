package com.maniu.hookams;

import android.app.Application;

public class DavidApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        HookAms hookAms = new HookAms();
        try {
            hookAms.hookAms(this);
            hookAms.hookPMS(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
