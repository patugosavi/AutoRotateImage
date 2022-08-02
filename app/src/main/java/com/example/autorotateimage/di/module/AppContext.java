package com.example.autorotateimage.di.module;

import android.app.Application;
import android.content.Context;

public class AppContext extends Application {

    private  static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        AppContext.context=getApplicationContext();
    }
    public  static Context getContext()
    {
        return  AppContext.context;
    }
}
