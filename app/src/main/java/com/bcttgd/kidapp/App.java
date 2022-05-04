package com.bcttgd.kidapp;

import android.content.Context;

public class App extends android.app.Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
}
