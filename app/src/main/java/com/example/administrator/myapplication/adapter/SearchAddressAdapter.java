package com.example.administrator.myapplication.adapter;

/**
 * Created by ZhongBingQi on 2018/4/12.
 */
import java.util.List;

import android.content.Context;

import com.baidu.mapapi.search.core.PoiInfo;
import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.adapter.base.BaseViewHolder;
import com.example.administrator.myapplication.adapter.base.MyBaseAdapter;


/**
 * @desc 搜索的地址列表适配器
 */
public class SearchAddressAdapter extends MyBaseAdapter<PoiInfo> {

    public SearchAddressAdapter(Context context, int resource, List<PoiInfo> list) {
        super(context, resource, list);
    }

    @Override
    public void setConvert(BaseViewHolder viewHolder, PoiInfo info) {
        viewHolder.setTextView(R.id.item_address_name_tv, info.name);
        viewHolder.setTextView(R.id.item_address_detail_tv, info.address);
    }

}
