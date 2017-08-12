package com.example.administrator.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button newBtn, oldBtn,prcBtn;
    TextView name;
    int WIDTH = 445,HEIGHT = 594;
    int MINWITH = 60,MINHEIGHT = 80;;
    String newPath ,oldPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newBtn = (Button) findViewById(R.id.button);
        oldBtn =(Button) findViewById(R.id.button2);
        prcBtn =(Button) findViewById(R.id.button3);
        name = (TextView) findViewById(R.id.textView);
        newBtn.setOnClickListener(this);
        oldBtn.setOnClickListener(this);
        prcBtn.setOnClickListener(this);
    }


    public boolean saveCompressImage(String newPath, final String oldPath) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(newPath,options);
            int width = WIDTH,height = HEIGHT;

            if(options.outHeight>height){
                options.inSampleSize = options.outHeight/height;
            }
            if(options.outWidth>options.outHeight){
                width = HEIGHT;
                height = WIDTH;
                if(options.outWidth>width){
                    options.inSampleSize = options.outWidth/width;
                }

            }
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(newPath, options);
            if (bitmap != null) {
                Matrix matrix = new Matrix();
                File file = new File(oldPath);
                float  scale= 1;
                if(bitmap.getWidth()>bitmap.getHeight()){
                    scale = height*1.0F/bitmap.getHeight();
                    if(scale*bitmap.getWidth()<HEIGHT){
                        scale = HEIGHT*1.0F/bitmap.getWidth();
                    }
                    matrix.postScale(scale, scale);
                    try {
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    scale = width*1.0F/bitmap.getWidth();
                    if(scale*bitmap.getHeight()<HEIGHT){
                        scale = HEIGHT*1.0F/bitmap.getHeight();
                    }
                    matrix.postScale(scale, scale);
                    try {
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0,  bitmap.getWidth(), bitmap.getHeight(), matrix, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Log.e("Test","bitmap="+bitmap.getWidth()+",h="+bitmap.getHeight()+",scale="+scale);
                width =  bitmap.getWidth()*scale>width?width:bitmap.getWidth();
                height = bitmap.getHeight()*scale>width?height:bitmap.getHeight();
                matrix.postScale(1, 1);
                Log.e("Test","w="+width+",h="+height+",scale="+scale);
                bitmap =save( bitmap,width,height ,null,file);
                matrix = new Matrix();
                float scaleWidth = MINWITH*1.0F/bitmap.getWidth();
               // int minWidth = MINWITH;
               // int minHeight = MINHEIGHT;
                if(bitmap.getWidth()>bitmap.getHeight()){
                    scaleWidth = MINWITH*1.0F/bitmap.getHeight();
                    //minHeight = MINWITH;
                   // minWidth = MINHEIGHT;
                }
                matrix.postScale(scaleWidth, scaleWidth);
                file =  new File(file.getParent(),"l"+file.getName());
                save( bitmap, bitmap.getWidth(), bitmap.getHeight(),matrix,file);
                return true;
            }
        } catch (Exception e) {
           e.printStackTrace();
        }
        return false;

    }

    private Bitmap save(Bitmap bitmap,int width,int height,Matrix matrix,File file) throws Exception{

        try {
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileOutputStream os= new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        os.write(baos.toByteArray());
        os.flush();
        os.close();
        os = null;
        return bitmap;
    }

    @Override
    public void onClick(View v) {
        if(v==newBtn){
            Intent intent = new Intent(this,FileListActivity.class);
            startActivityForResult(intent,1);
        }else if(v==oldBtn){
            Intent intent = new Intent(this,MediaPickerActivity.class);
            intent.putExtra(MediaPickerActivity.SHOW_PATH, Environment.getExternalStorageDirectory()+"/pic_old/");
            startActivityForResult(intent,2);
        }else{
            // 加载相册信息,异步
            new AsyncTask<String,Integer,Boolean>(){
                @Override
                protected Boolean doInBackground(String[] params) {
                    return  saveCompressImage(newPath, oldPath);
                }

                @Override
                protected void onPostExecute(Boolean bool) {
                    Toast.makeText(getApplicationContext(),bool?"处理成功！":"处理失败！",Toast.LENGTH_LONG).show();
                }
            }.execute();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==2){
                oldPath = data.getStringExtra(MediaPickerActivity.SHOW_PATH);
                if(!TextUtils.isEmpty(oldPath)){
                    File file = new File(oldPath);
                    name.setText(file.getName().substring(4,9));
                }
            }else{
                newPath = data.getStringExtra(MediaPickerActivity.SHOW_PATH);
            }
        }
    }
}
