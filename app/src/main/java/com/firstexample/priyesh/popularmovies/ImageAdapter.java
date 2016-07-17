package com.firstexample.priyesh.popularmovies;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by PRIYESH on 15-07-2016.
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private String[] mPosterArray;

    static boolean isAirplaneModeOn(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        return Settings.System.getInt(contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }

    public ImageAdapter(Context c, String[] posterArray)
    {
        mContext = c;
        mPosterArray = posterArray;
    }

    @Override
    public int getCount() {
        return mPosterArray.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if(convertView == null)
        {
            imageView = new ImageView(mContext);
            //imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            //imageView.setPadding(8,8,8,8);
        }
        else
        {
            imageView = (ImageView)convertView;
        }
        Picasso.with(mContext).load(mPosterArray[position]).into(imageView);
        //imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    private Integer[] mThumbIds = {
            R.drawable.sample_0,R.drawable.sample_1,
            R.drawable.sample_2,R.drawable.sample_3,
            R.drawable.sample_4,R.drawable.sample_5,
            R.drawable.sample_6,R.drawable.sample_7,
            R.drawable.sample_0,R.drawable.sample_1,
            R.drawable.sample_2,R.drawable.sample_3,
            R.drawable.sample_4,R.drawable.sample_5,
            R.drawable.sample_6,R.drawable.sample_7,
            R.drawable.sample_0,R.drawable.sample_1,
            R.drawable.sample_2,R.drawable.sample_3,
            R.drawable.sample_4,R.drawable.sample_5,
            R.drawable.sample_6,R.drawable.sample_7
    };
}
