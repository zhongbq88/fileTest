package com.example.administrator.myapplication;

import android.os.Environment;

import com.example.administrator.myapplication.sp.SpData;

import static android.os.Environment.getRootDirectory;

/**
 * Created by ZhongBingQi on 2018/4/9.
 */

public class ConstUtil {

    public final static String NEW_IMAGE_KEY = "NEW_IMAGE";

    public static String ROOT_PATH = "/";

    public static String NEW_IMAGE_ROOT_PATH = SpData.getData(NEW_IMAGE_KEY,"/");

    public static String OLD_IMAGE_ROOT_PATH =  Environment.getExternalStorageDirectory()+"/olaImages/";

    public static String DB_ROOT_PATH =  "/data/data/com.link800.CapScanp/databases/";

    public static String DB_NAME_ROOT_PATH =  "/data/data/com.link800.CapScanp/databases/OLA.db";

    public static String DB_TABLE_NAME =  "tblimgdata";

}
