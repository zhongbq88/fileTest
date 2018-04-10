package com.example.administrator.myapplication;

import android.os.Environment;

/**
 * Created by ZhongBingQi on 2018/4/9.
 */

public class ConstUtil {

    public static String NEW_IMAGE_ROOT_PATH =  "/mnt/shared/Image/";

    public static String OLD_IMAGE_ROOT_PATH =  Environment.getExternalStorageDirectory()+"/olaImages/";

    public static String DB_ROOT_PATH =  "/data/data/com.link800.CapScanp/databases/";

    public static String DB_NAME_ROOT_PATH =  "/data/data/com.link800.CapScanp/databases/OLA.db";

    public static String DB_TABLE_NAME =  "tblimgdata";
}
