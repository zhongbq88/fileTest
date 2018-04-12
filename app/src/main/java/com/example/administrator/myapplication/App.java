package com.example.administrator.myapplication;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by ZhongBingQi on 2018/4/11.
 */

public class App extends Application {

    static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        this.app = this;
        // 百度地图初始化
        SDKInitializer.initialize(getApplicationContext());
    }

    public static App getApp() {
        return app;
    }
}
