package com.relhs.asianfinder.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.relhs.asianfinder.R;
import com.relhs.asianfinder.loader.ImageLoader;

import java.io.InputStream;
import java.util.ArrayList;

public class StickersGridAdapter extends BaseAdapter{

    private ArrayList<String> paths;
    private Context mContext;
    private ImageLoader imageLoader;
    private LayoutInflater inflater;

    public StickersGridAdapter(Context context, ArrayList<String> paths) {
        this.mContext = context;
        this.paths = paths;

        imageLoader = new ImageLoader(context);
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        if (v == null) {
            v = inflater.inflate(R.layout.sticker_item, null);
        }

        final String path = paths.get(position);
        Log.d("-- robert", path);

        ImageView image = (ImageView) v.findViewById(R.id.item);
        image.setImageBitmap(getImage(path));

        return v;
    }

    @Override
    public int getCount() {
        return paths.size();
    }

    @Override
    public String getItem(int position) {
        return paths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    private Bitmap getImage (String path) {
        AssetManager mngr = mContext.getAssets();
        InputStream in = null;

        try {
            in = mngr.open("stickers/" + path);
        } catch (Exception e){
            e.printStackTrace();
        }

        //BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inSampleSize = chunks;

        Bitmap temp = BitmapFactory.decodeStream(in ,null ,null);
        return temp;
    }

    public interface KeyClickListener {
        public void keyClickedIndex(String index);
    }
}