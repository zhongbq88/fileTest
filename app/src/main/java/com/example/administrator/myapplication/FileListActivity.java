package com.example.administrator.myapplication;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class FileListActivity extends ListActivity {


    public static final String SHOW_PATH ="SHOW_PATH";
    public static final String COLUMN_NAME_NAME = "name";

    private SimpleAdapter adapter = null;
    private List<Map<String, Object>> itemList;
    private Stack<String> pathHistory = null;
    private String curPath;

    String[] from = { COLUMN_NAME_NAME } ;
    int[] to = {R.id.myView1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pathHistory = new Stack<String>();

        String sDStateString = Environment.getExternalStorageState();
        if(sDStateString.equals(Environment.MEDIA_MOUNTED)) {
            curPath = ConstUtil.NEW_IMAGE_ROOT_PATH;
            itemList = getData(curPath);
            adapter = new SimpleAdapter(this, itemList, R.layout.list_item, from, to);
            setListAdapter(adapter);
        }
    }

    private List<Map<String, Object>> getData(String path) {
        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(COLUMN_NAME_NAME, "..");
        list.add(map);

        File file = new File(path);
        if(file==null|| !file.exists() || file.listFiles()== null){
            return list;
        }
        if (file.listFiles().length > 0) {
            for (File f : file.listFiles()) {
                map = new HashMap<String, Object>();
                String name = f.getName();
                if (f.isDirectory()) {
                    name += "/";
                }
                map.put(COLUMN_NAME_NAME, name);
                list.add(map);
            }
        }

        return list;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String path;
        if (position > 0) {
            pathHistory.push(curPath);
            path = curPath + itemList.get(position).get(COLUMN_NAME_NAME);
        } else { // uplevel
            if (!pathHistory.empty())
                path = pathHistory.pop();
            else // root
                path = curPath;
        }
        File file = new File(path);
        if (file.isDirectory()) {
            updateList(path);
            curPath = path;
        } else {
            Intent intent = getIntent();
            intent.putExtra(SHOW_PATH,path);
            setResult(RESULT_OK,intent);
            finish();
        }
    }

    private void updateList(String path) {
        itemList.clear();
        itemList = getData(path);
        adapter = new SimpleAdapter(this, itemList, R.layout.list_item, from, to);
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
