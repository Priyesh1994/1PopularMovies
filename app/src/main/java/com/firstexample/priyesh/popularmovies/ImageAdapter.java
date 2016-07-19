package com.firstexample.priyesh.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by PRIYESH on 15-07-2016.
 *
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private String[] mPosterArray;

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
            /*Having a issue here.
            ** Not able to dynamically set the row height of the poster.
             */
            imageView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 800));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        else
        {
            imageView = (ImageView)convertView;
        }
        //To enable logging in Picasso
        Picasso.with(mContext).setLoggingEnabled(true);

        Picasso.with(mContext).load(mPosterArray[position]).into(imageView);
        return imageView;
    }
}
