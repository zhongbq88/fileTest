package com.example.administrator.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;

/**
 * Created by ZhongBingQi on 2018/4/9.
 */

public class SystemManager {


    public static boolean rootDBPath(){
        return rootCommand("chmod -R 777 "+ConstUtil.DB_ROOT_PATH);
    }

    public static boolean rootCommand(String command)
    {
        Process process = null;
        DataOutputStream os = null;
        try
        {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e)
        {
            Log.d("DEBUG", "ROOT REE" + e.getMessage());
            return false;
        } finally
        {
            try
            {
                if (os != null)
                {
                    os.close();
                }
                process.destroy();
            } catch (Exception e)
            {
            }
        }
        Log.d("DEBUG", "Root SUC ");
        return true;
    }
}
