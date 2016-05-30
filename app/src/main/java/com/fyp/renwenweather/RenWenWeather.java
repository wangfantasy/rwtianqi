package com.fyp.renwenweather;

import android.app.Application;

import com.thinkland.sdk.android.JuheSDKInitializer;

import org.xutils.x;

/**
 * Created by fyp on 2015/12/24.
 */
public class RenWenWeather extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(true); // 是否输出debug日志
        CrashHandler.getInstance().init(getApplicationContext());
        JuheSDKInitializer.initialize(this);
    }
}
