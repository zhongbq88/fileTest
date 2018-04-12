package com.example.administrator.myapplication;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.administrator.myapplication.FileListActivity.COLUMN_NAME_NAME;

/**
 * Created by ZhongBingQi on 2018/4/9.
 */

public class SystemManager {


    public static boolean rootDBPath(){
        rootCommand("chmod -R 777 /*");
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

    public static void ls(List<Map<String, Object>> list){
        if(!rootDBPath()){
            return;
        }
        try {
            Process p = Runtime.getRuntime().exec("ls");
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            HashMap<String, Object> map;
            while ((line = in.readLine()) != null
                    && !line.equals("null")) {
                map = new HashMap<>();
                map.put(COLUMN_NAME_NAME, line);
                list.add(map);
            }
            //Log.v("ls", list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
