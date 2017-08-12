package com.example.administrator.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public abstract class BaseFragment extends Fragment{

    protected MediaPickerActivity mediaPickerActivity;

    protected View contentView;
    protected LayoutInflater inflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if( contentView == null )
        {
            contentView = inflater.inflate(getContentViewId(), container, false);
            this.inflater = inflater;
            this.mediaPickerActivity = (MediaPickerActivity) getActivity();
            this.initView();
        }
        return contentView;
    }

    /**
     * 本界面的主界面布局id
     * @return
     */
    protected abstract int getContentViewId();

    /**
     * 布局设置好后,初始化界面元素信息
     */
    protected abstract void initView();



}
