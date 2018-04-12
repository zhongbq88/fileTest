package com.example.administrator.myapplication;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static com.example.administrator.myapplication.ConstUtil.ROOT_PATH;

public class FileListActivity extends ListActivity {


    public static final String SHOW_PATH ="SHOW_PATH";
    public static final String COLUMN_NAME_NAME = "name";

    private ListAdapter adapter = null;
    private List<Map<String, Object>> itemList;
    private Stack<String> pathHistory = null;
    private String curPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pathHistory = new Stack<String>();
        if(!TextUtils.equals(ConstUtil.NEW_IMAGE_ROOT_PATH,ROOT_PATH)){
            String[] list = ConstUtil.NEW_IMAGE_ROOT_PATH.split("/");
            StringBuilder stringBuilder = new StringBuilder("/");
            pathHistory.push(stringBuilder.toString());
            for (int i=1;i<list.length-1;i++) {
                stringBuilder.append(list[i]+"/");
                pathHistory.push(stringBuilder.toString());
            }
        }
        String sDStateString = Environment.getExternalStorageState();
        if(sDStateString.equals(Environment.MEDIA_MOUNTED)) {
            curPath = ConstUtil.NEW_IMAGE_ROOT_PATH;
            itemList = getData(curPath);
            adapter = new ListAdapter();
            setListAdapter(adapter);
        }

    }

    private List<Map<String, Object>> getData(String path) {
        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(COLUMN_NAME_NAME, path+"..");
        list.add(map);
        if(TextUtils.equals(path, ROOT_PATH)){
            SystemManager.ls(list);
            if(list.size()>1){
                return list;
            }
        }
        File file = new File(path);
        if(file==null|| !file.exists() || file.listFiles()== null){
            return list;
        }
        if (file.listFiles().length > 0) {
            for (File f : file.listFiles()) {
                map = new HashMap<>();
                map.put(COLUMN_NAME_NAME, f.getName());
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
            path = curPath +File.separator+ itemList.get(position).get(COLUMN_NAME_NAME);
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
        adapter = new ListAdapter();
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    final class ListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return itemList!=null?itemList.size():0;
        }

        @Override
        public Map<String, Object> getItem(int i) {
            return itemList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder=null;
            if(view==null){
                view = LayoutInflater.from(getBaseContext()).inflate(R.layout.list_item,null);
                viewHolder = new ViewHolder(view);
            }else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.bindView(getItem(i));
            return view;
        }
    }

    final class ViewHolder{
        ImageView imageView;
        TextView textView;
        View view;
        public ViewHolder(View view) {
            this.view = view;
            imageView = (ImageView) view.findViewById(R.id.imageView);
            textView = (TextView) view.findViewById(R.id.textView);
            view.setTag(this);
        }

        public void bindView(Map<String, Object> map){
            if(map==null || map.get(COLUMN_NAME_NAME)==null){
                return;
            }
            String name = map.get(COLUMN_NAME_NAME).toString();
            if(name.toString().endsWith(".jpg")||name.toString().endsWith(".jpeg")
                    ||name.toString().endsWith(".png")||name.toString().endsWith(".gif")){
                Glide.with(FileListActivity.this).load(curPath+"/"+name).into(imageView);
            }else{
                File file = new File(curPath+"/"+name);
                if(file.isDirectory()|| name.endsWith("..")||name.endsWith("/")){
                    Glide.with(FileListActivity.this).load(R.drawable.icon_folder).into(imageView);
                }else{
                    Glide.with(FileListActivity.this).load(R.drawable.file_unknown).into(imageView);
                }
            }

            textView.setText(name);
        }
    }
}
