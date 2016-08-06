package com.firstexample.priyesh.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firstexample.priyesh.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by PRIYESH on 06-08-2016.
 */
public class RecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private Cursor mVideoCursor, mReviewCursor;
    private static final int VIDEO_CURSOR = 0;
    private static final int REVIEW_CURSOR = 1;
    final String BASE_URI = "http://img.youtube.com/vi/";
    final String IMG_PATH = "0.jpg";

    public RecycleAdapter(Context context, Cursor videoCursor, Cursor reviewCursor) {
        this.mContext = context;
        this.mVideoCursor = videoCursor;
        this.mReviewCursor = reviewCursor;
    }

    @Override
    public int getItemViewType(int position) {
        int temp = mVideoCursor.getCount();
        if(position <= temp)
            return VIDEO_CURSOR;
        else
            return REVIEW_CURSOR;
        //return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType)
        {
            case VIDEO_CURSOR:
            {
                View itemType = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_video, parent, false);
                viewHolder = new ViewHolderVideos(itemType);
                break;
            }
            case REVIEW_CURSOR:
            {
                View itemType = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_review, parent, false);
                viewHolder = new ViewHolderReviews(itemType);
                break;
            }
            /*default:
            {
                View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
                viewHolder = new RecyclerViewSimpleTextViewHolder(v);
                break;
            }*/
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position))
        {
            case VIDEO_CURSOR:
                ViewHolderVideos videos = (ViewHolderVideos) holder;
                videos.bindViews(mContext, mVideoCursor);
                break;
            case REVIEW_CURSOR:
                ViewHolderReviews reviews = (ViewHolderReviews) holder;
                reviews.bindViews(mContext, mReviewCursor);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return (mVideoCursor.getCount() + mReviewCursor.getCount());
    }

    /*public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }*/

    public class ViewHolderVideos extends RecyclerView.ViewHolder{

        public final ImageView iconView;
        public final TextView nameView;

        public ViewHolderVideos(View itemView)
        {
            super(itemView);
            iconView = (ImageView) itemView.findViewById(R.id.list_image_video);
            nameView = (TextView) itemView.findViewById(R.id.list_text_video);
        }

        public void bindViews(Context context, Cursor cursor)
        {
            String videoName = cursor.getString(cursor.getColumnIndex(MovieContract.VideoEntry.COLUMN_VIDEO_NAME));
            String key = cursor.getString(cursor.getColumnIndex(MovieContract.VideoEntry.COLUMN_VIDEO_KEY));

            nameView.setText(videoName);

            Uri builtThumbUri = Uri.parse(BASE_URI).buildUpon()
                    .appendPath(key)
                    .appendPath(IMG_PATH)
                    .build();
            Picasso.with(context).load(builtThumbUri).into(iconView);
        }
    }

    public class ViewHolderReviews extends RecyclerView.ViewHolder{

        public final TextView author_view;
        public final TextView content_view;

        public ViewHolderReviews(View itemView) {
            super(itemView);
            author_view = (TextView) itemView.findViewById(R.id.list_item_author_view);
            content_view = (TextView) itemView.findViewById(R.id.list_item_content_view);
        }

        public void bindViews(Context context, Cursor cursor)
        {
            String author_name = cursor.getString(cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_AUTHOR));
            String content = cursor.getString(cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_CONTENT));

            author_view.setText(author_name);
            content_view.setText(content);
        }
    }
}