package com.example.administrator.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MediaPickerActivity extends AppCompatActivity{

    public static final String SHOW_PATH ="SHOW_PATH";
    // 界面相关的变量
    private AlumbsFragment alumbsFragment;              // 相册列表界面
    private View progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediapicker);
        try {
            initView();
            initAction();
        } catch (Exception e) {
        }
    }

    protected void initView() {
        progressView = findViewById(R.id.progressLayout);
        alumbsFragment = new AlumbsFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container, alumbsFragment);
        fragmentTransaction.show(alumbsFragment);
        fragmentTransaction.commit();
    }

    protected void initAction() {

        // 加载相册信息,异步
        new AsyncTask<String,Integer,List<String>>(){
            @Override
            protected List<String> doInBackground(String[] params) {
                List<String> list = new ArrayList<String>();
                File file = new File(params[0]);
                if(file.exists()&&file.listFiles().length>0){
                    for(File f:file.listFiles()){
                        if(!f.getName().startsWith("lola")){
                            list.add(f.getAbsolutePath());
                        }
                    }
                }
                return list;
            }

            @Override
            protected void onPostExecute(List<String> strings) {
                super.onPostExecute(strings);
                progressView.setVisibility(View.GONE);
                alumbsFragment.setDataSource(strings);
            }
        }.execute(getIntent().getStringExtra(SHOW_PATH));
    }

    public void showMediasFragment(String albumEntry) {
        Intent intent = getIntent();
        intent.putExtra(SHOW_PATH,albumEntry);
        setResult(RESULT_OK,intent);
        finish();
    }
}
