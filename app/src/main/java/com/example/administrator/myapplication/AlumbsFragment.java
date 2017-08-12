package com.example.administrator.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.List;

import static java.security.AccessController.getContext;

/**
 *
 * 相册列表界面
 */
public class AlumbsFragment extends BaseFragment
    implements AdapterView.OnItemClickListener{


    private GridView albumsGridView;
    private AlbumsAdapter albumsAdapter;
    private List<String> albums;        // 相册信息

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_ablums;
    }

    @Override
    protected void initView() {
        albumsGridView = (GridView) contentView.findViewById(R.id.alumbs_grid);
        albumsGridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String albumEntry = albumsAdapter.getItem(position);
        if(albumEntry != null)
        {
            this.mediaPickerActivity.showMediasFragment(albumEntry);
        }
    }

    /**
     * 设置数据源
     * @param alumbs
     */
    public void setDataSource(List<String> alumbs)
    {
        if(alumbs == null) return;
        this.albums = alumbs;

        if(albumsAdapter == null)
        {
            albumsAdapter = new AlbumsAdapter(this, this.albums);
            albumsGridView.setAdapter(albumsAdapter);
        }
        else
        {
            albumsAdapter.setDataSource(this.albums);
        }
    }

    public void updateView()
    {
        if(albumsAdapter != null)
        {
            this.albumsAdapter.notifyDataSetChanged();
        }
    }
}

/**
 * 相册列表适配器
 */
class AlbumsAdapter extends BaseAdapter
{
    private AlumbsFragment alumbsFragment;
    private List<String> albumEntries;
    private MediaPickerActivity mediaPickerActivity;
    private int width;

    public AlbumsAdapter(AlumbsFragment alumbsFragment, List<String> albumEntries) {
        this.alumbsFragment = alumbsFragment;
        this.albumEntries = albumEntries;
        this.mediaPickerActivity = (MediaPickerActivity) alumbsFragment.getActivity();
        WindowManager wm = (WindowManager) mediaPickerActivity
                .getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth()/4;
    }

    @Override
    public int getCount() {
        return albumEntries.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public String getItem(int position) {
        return albumEntries.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null)
        {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(alumbsFragment.getActivity()).inflate(R.layout.griditem_albums, null);
            holder.coverImageView = (ImageView) convertView.findViewById(R.id.media_photo_image);
            ViewGroup.LayoutParams layoutParams = holder.coverImageView.getLayoutParams();
            layoutParams.height =  layoutParams.width = width;
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        String album = getItem(position);
        setImgBitmap(album,holder.coverImageView);
        return convertView;
    }

    private void setImgBitmap(String path,ImageView imageView){
        //通过openRawResource获取一个inputStream对象
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);
        options.inSampleSize = options.outWidth/width;
        options.inJustDecodeBounds = false;
        Bitmap inputStream = BitmapFactory.decodeFile(path,options);
        imageView.setImageBitmap(inputStream);
    }

    class ViewHolder {
        ImageView coverImageView;
    }

    public void setDataSource(List<String> albumEntries)
    {
        if(albumEntries == null) return;
        this.albumEntries = albumEntries;
        this.notifyDataSetChanged();
    }
}