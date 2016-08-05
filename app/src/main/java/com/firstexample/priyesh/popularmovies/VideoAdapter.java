package com.firstexample.priyesh.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.firstexample.priyesh.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by PRIYESH on 01-08-2016.
 */
public class VideoAdapter extends CursorAdapter {

    final String BASE_URI = "http://img.youtube.com/vi/";
    final String IMG_PATH = "0.jpg";

    public VideoAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView nameView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_image_video);
            nameView = (TextView) view.findViewById(R.id.list_text_video);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.list_item_video;
        View view = LayoutInflater.from(context).inflate(layoutId,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder)view.getTag();

        String videoName = cursor.getString(cursor.getColumnIndex(MovieContract.VideoEntry.COLUMN_VIDEO_NAME));
        viewHolder.nameView.setText(videoName);
        String key = cursor.getString(cursor.getColumnIndex(MovieContract.VideoEntry.COLUMN_VIDEO_KEY));

        Uri builtThumbUri = Uri.parse(BASE_URI).buildUpon()
                .appendPath(key)
                .appendPath(IMG_PATH)
                .build();
        Picasso.with(context).load(builtThumbUri).into(viewHolder.iconView);
    }
}
