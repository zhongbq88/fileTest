package com.example.administrator.myapplication;

import android.text.TextUtils;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ZhongBingQi on 2018/4/16.
 */

public class ApiService {

    public static void getLocation(String lontitude,String latitude,final LocationCallback callback){
        OkHttpClient client = new OkHttpClient();
        final String url = "http://api.map.baidu.com/geocoder/v2/?ak=k5gf9YxxaGppGKwpI9owa631E6jQpL8k&location="+latitude+","+lontitude+"&output=json&mcode=28:22:F0:6E:0F:FC:9E:7F:C4:3A:D4:6E:C4:FE:F0:0E:46:7A:3C:6A;com.example.administrator.myapplication";
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if(response!=null){
                        String json = response.body().string();
                        JSONObject jsonObject = new JSONObject(json);
                        if(TextUtils.equals(jsonObject.getString("status"),"0")){
                            final JSONObject addressComponent = jsonObject.getJSONObject("result").getJSONObject("addressComponent");
                            callback.onResponse(addressComponent);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface LocationCallback{
        void onResponse(JSONObject json);
    }
}

