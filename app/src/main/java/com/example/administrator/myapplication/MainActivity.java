package com.example.administrator.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.administrator.myapplication.sp.SpData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.administrator.myapplication.SystemManager.rootDBPath;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button newBtn, oldBtn,prcBtn;
    TextView name;
    int WIDTH = 445,HEIGHT = 594;
    float MINWITH = 60;
    String newPath ,oldPath;
    EditText uploadtime,lontitude,latitude,province,city,district,street,addr;
    View edit_layout;
    ImageView new_img,old_img;
    TextView new_name,old_name;


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
        initEditView();

        new_img = (ImageView) findViewById(R.id.new_img);
        old_img =(ImageView) findViewById(R.id.old_img);
        new_name =(TextView) findViewById(R.id.new_name);
        old_name = (TextView) findViewById(R.id.old_name);
    }

    private void initEditView(){
        edit_layout = findViewById(R.id.edit_layout);
        uploadtime = (EditText) findViewById(R.id.uploadtime);
        lontitude = (EditText) findViewById(R.id.lontitude);
        latitude = (EditText) findViewById(R.id.latitude);
        province = (EditText) findViewById(R.id.province);
        city = (EditText) findViewById(R.id.city);
        district = (EditText) findViewById(R.id.district);
        street = (EditText) findViewById(R.id.street);
        addr = (EditText) findViewById(R.id.addr);
        province.addTextChangedListener(textWatcher);
        city.addTextChangedListener(textWatcher);
        district.addTextChangedListener(textWatcher);
        street.addTextChangedListener(textWatcher);

    }


    @Override
    protected void onResume() {
        super.onResume();
        rootDBPath();
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
                if(width>height && bitmap.getHeight()>height){
                    scale = height*1.0F/bitmap.getHeight();
                    matrix.postScale(scale, scale);
                    try {
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else  if(height>width && bitmap.getWidth()>width){
                    scale = width*1.0F/bitmap.getWidth();
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
                float scaleWidth = MINWITH/width;
                if(width>height){
                    scaleWidth = MINWITH/height;
                }
                matrix.postScale(scaleWidth, scaleWidth);
                //保存小图
                file =  new File(file.getParent(),"l"+file.getName());
                save( bitmap, width, height,matrix,file);
                //保存大图
                File file2new = new File(newPath);
                File file2old = new File(oldPath);
                copy(file2new,file2old);
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
            intent.putExtra(MediaPickerActivity.SHOW_PATH, ConstUtil.OLD_IMAGE_ROOT_PATH);
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
                    if(!TextUtils.isEmpty(oldPath)){
                        File file = new File(oldPath);
                        Glide.with(MainActivity.this).load(file).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(old_img);
                        viewData(file.getName());
                    }
                    //Toast.makeText(getApplicationContext(),bool?"处理成功！":"处理失败！",Toast.LENGTH_LONG).show();
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
                Log.e("Test","oldPath="+oldPath);
                if(!TextUtils.isEmpty(oldPath)){
                    File file = new File(oldPath);
                    name.setText(file.getName().substring(3,file.getName().length()-18));
                    Glide.with(this).load(file).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(old_img);
                    old_name.setText(file.getName());
                }
            }else{
                newPath = data.getStringExtra(MediaPickerActivity.SHOW_PATH);
                Log.e("Test","newPath="+newPath);
                Glide.with(this).load(newPath).into(new_img);
                File file = new File(newPath);
                SpData.setData(ConstUtil.NEW_IMAGE_KEY,file.getParent()+"/");
                new_name.setText(file.getName());
            }
        }
    }

    /**
     * 复制单个文件
     * @return boolean
     */
    private void copy(File src, File dst)  {
        try {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            Toast.makeText(getApplicationContext(), "copy ok " , Toast.LENGTH_LONG).show();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    SQLiteDatabase db;

    private SQLiteDatabase getDb(){
        if(db==null ||!db.isOpen()){
            /*if(!rootDBPath()){
                return null;
            }*/
            File file = new File(ConstUtil.DB_NAME_ROOT_PATH);
            Log.d("DEBUG", "file = "+file.exists());
            if(!file.exists()){
                return null;
            }
            db = SQLiteDatabase.openOrCreateDatabase(file.getAbsolutePath(), null);
            Log.d("DEBUG", "db = "+db.isOpen());
        }
        return db;
    }


    private void viewData(String photodata){
        SQLiteDatabase db = getDb();
        if(db==null){
            return;
        }
        Cursor cursor = null;
        try {
            String sl = "select * from "+ConstUtil.DB_TABLE_NAME+" where photodata='"+photodata+"'";
            Log.d("DEBUG", "sl = "+sl);
            cursor = getDb().rawQuery(sl,null);
            Log.d("DEBUG", "cursor = "+cursor+",cursor.moveToFirst()="+cursor.moveToFirst());
            if(cursor!=null&&cursor.moveToFirst()){
                uploadtime.setText(getString( cursor,"uploadtime"));
                lontitude.setText(getString( cursor,"lontitude"));
                latitude.setText(getString( cursor,"latitude"));
                province.setText(getString( cursor,"province"));
                city.setText(getString( cursor,"city"));
                district.setText(getString( cursor,"district"));
                street.setText(getString( cursor,"street"));
                addr.setText(getString( cursor,"addr"));
                edit_layout.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }
    }

    public void saveClcik(View view){
        try {
            String sl = " UPDATE "+ConstUtil.DB_TABLE_NAME+" SET " +
                    " uploadtime='"+uploadtime.getText()+"'," +
                    "lontitude='"+lontitude.getText()+"'," +
                    "latitude='"+latitude.getText()+"'," +
                    "province='"+province.getText()+"'," +
                    "city='"+city.getText()+"'," +
                    "district='"+district.getText()+"'," +
                    "street='"+street.getText()+"'," +
                    "addr='"+addr.getText()+"' where photodata='"+new File(oldPath).getName()+"'";
            getDb().execSQL(sl);
            Toast.makeText(getApplicationContext(),"保存成功！",Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getString(Cursor cursor,String columnName){
        String text = cursor.getString(cursor.getColumnIndex(columnName));
        if(TextUtils.isEmpty(text)){
            return "";
        }
        return text;
    }

    final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(province.getText());
            stringBuilder.append(city.getText());
            stringBuilder.append(district.getText());
            stringBuilder.append(street.getText());
            addr.setText(stringBuilder);
        }
    };
    private static final int GO_FOR_BAIDU_MAP = 1;
    public void locationClcik(View view){
        /*Intent intent = new Intent(MainActivity.this, LocationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("lontitude", lontitude.getText() + "");
        bundle.putString("latitude",latitude.getText() + "");
        bundle.putString("province", province.getText()+"");
        bundle.putString("city",city.getText()+"");
        bundle.putString("district", district.getText()+"");
        bundle.putString("street", street.getText()+"");
        intent.putExtras(bundle);
        startActivityForResult(intent, GO_FOR_BAIDU_MAP);*/
        ApiService.getLocation(lontitude.getText().toString(), latitude.getText().toString(), new ApiService.LocationCallback() {
            @Override
            public void onResponse(final JSONObject json) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            province.setText(json.getString("province"));
                            city.setText(json.getString("city"));
                            district.setText(json.getString("district"));
                            street.setText(json.getString("street"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
