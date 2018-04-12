package com.example.administrator.myapplication.sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.administrator.myapplication.App;
import com.example.administrator.myapplication.ConstUtil;

/**
 * Created by ZhongBingQi on 2018/4/11.
 */

public class SpData {

    public static void setData(String key,String value){
        if(TextUtils.equals(ConstUtil.NEW_IMAGE_KEY,key)){
            if(TextUtils.equals(value,ConstUtil.NEW_IMAGE_ROOT_PATH)){
                return;
            }
            ConstUtil.NEW_IMAGE_ROOT_PATH = value;
        }
        SharedPreferences sp = App.getApp().getSharedPreferences("path_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getData(String key,String defaultValue){
        SharedPreferences sp = App.getApp().getSharedPreferences("path_data", Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }
}
