package com.firstexample.priyesh.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.firstexample.priyesh.popularmovies.data.MovieContract;

/**
 * Created by PRIYESH on 02-08-2016.
 */
public class ReviewAdapter extends CursorAdapter {

    public ReviewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolder
    {
        public final TextView author_view;
        public final TextView content_view;

        public ViewHolder(View view)
        {
            author_view = (TextView) view.findViewById(R.id.list_item_author_view);
            content_view = (TextView) view.findViewById(R.id.list_item_content_view);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.list_item_review;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String author_name = cursor.getString(cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_AUTHOR));
        String content = cursor.getString(cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_CONTENT));

        viewHolder.author_view.setText(author_name);
        viewHolder.content_view.setText(content);
    }
}
