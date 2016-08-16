package com.firstexample.priyesh.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firstexample.priyesh.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by PRIYESH on 06-08-2016.
 */
public class RecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String LOG_TAG = RecycleAdapter.class.getSimpleName();
    private final Context mContext;
    private Cursor mVideoCursor, mReviewCursor,mMovieCursor;
    private static final int MOVIE_CURSOR = 0;
    private static final int VIDEO_CURSOR = 1;
    private static final int REVIEW_CURSOR = 2;
    private static final int VIDEO_HEADER = 3;
    private static final int REVIEW_HEADER = 4;
    final String BASE_URI = "http://img.youtube.com/vi/";
    final String IMG_PATH = "0.jpg";

    public RecycleAdapter(Context context, Cursor videoCursor, Cursor reviewCursor, Cursor mMovieCursor) {
        this.mContext = context;
        this.mVideoCursor = videoCursor;
        this.mReviewCursor = reviewCursor;
        this.mMovieCursor = mMovieCursor;
    }

    @Override
    public int getItemViewType(int position) {
        Log.v("Position",position+"");
        if (position == 0)
        {
            return MOVIE_CURSOR;
        }
        else if(mVideoCursor != null && position == 1)
        {
            return VIDEO_HEADER;
        }
        else if((mReviewCursor != null && mVideoCursor != null && position == mVideoCursor.getCount() + 2)
                || (mReviewCursor != null && mVideoCursor == null && position == 1))
        {
            return REVIEW_HEADER;
        }
        else {
            if (mVideoCursor != null  && position <= mVideoCursor.getCount() + 1
                    )
            {
                //TODO: check for null in cursor
                //if (position <= mVideoCursor.getCount()) {
                    return VIDEO_CURSOR;
                //}
            }
            else
            //if (mReviewCursor != null)
                return REVIEW_CURSOR;
        }
        //return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType)
        {
            case MOVIE_CURSOR:
            {
                Log.v("Entered","MovieDetails");
                View itemType = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.movie_details_recycler_view, parent, false);
                viewHolder = new ViewHolderMovieDetails(itemType);
                break;
            }
            case VIDEO_CURSOR:
            {
                Log.v("Entered","Video");
                View itemType = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_video, parent, false);
                viewHolder = new ViewHolderVideos(itemType);
                break;
            }
            case REVIEW_CURSOR:
            {
                Log.v("Entered","Review");
                View itemType = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_review, parent, false);
                viewHolder = new ViewHolderReviews(itemType);
                break;
            }
            case VIDEO_HEADER:
            {
                View itemType = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.video_item, parent, false);
                viewHolder = new ViewHolderVideoHeader(itemType);
                break;
            }
            case REVIEW_HEADER:
            {
                View itemType = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.review_item, parent, false);
                viewHolder = new ViewHolderReviewHeader(itemType);
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(position == 0)
        {
            Log.v("Bind", "MovieDetails");
            mMovieCursor.moveToFirst();
            ViewHolderMovieDetails movieDetails = (ViewHolderMovieDetails) holder;
            movieDetails.bindViews(mContext, mMovieCursor);
        }
        else if(mVideoCursor != null && position == 1)
        {
            //return VIDEO_HEADER;
        }
        //1: 1 for VideoHeader
        else if((mReviewCursor != null && mVideoCursor != null && position == mVideoCursor.getCount() + mMovieCursor.getCount() + 1)
                || (mReviewCursor != null && mVideoCursor == null && position == mMovieCursor.getCount()))
        {
            //return REVIEW_HEADER;
        }
        else
        {
            int a = mVideoCursor.getCount();
            if (mVideoCursor != null && position <= mVideoCursor.getCount() + 1)
            {
                Log.v("Bind", "Video");
                //1 for video header
                mVideoCursor.moveToPosition(position - mMovieCursor.getCount() - 1);
                ViewHolderVideos videos = (ViewHolderVideos) holder;
                videos.iconView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mVideoCursor.moveToPosition(position - mMovieCursor.getCount());
                        String key = mVideoCursor.getString(mVideoCursor.getColumnIndex(MovieContract.VideoEntry.COLUMN_VIDEO_KEY));
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                        } catch (ActivityNotFoundException ex) {
                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://www.youtube.com/watch?v=" + key));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                        }
                    }
                });
                videos.bindViews(mContext, mVideoCursor);
            }
            else
            {
                Log.v("Bind", "Review");
                int temp;
                if(mVideoCursor == null)
                {
                    //1 for review header
                    temp = position - mMovieCursor.getCount() - 1;
                }
                else
                {
                    //1 for review header
                    //1 for video header
                    temp = position - mMovieCursor.getCount() - mVideoCursor.getCount() - 1 - 1;
                }
                mReviewCursor.moveToPosition(temp);
                ViewHolderReviews reviews = (ViewHolderReviews) holder;
                reviews.bindViews(mContext, mReviewCursor);
            }
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        //1 is added for header
        if(mVideoCursor != null) count = count + mVideoCursor.getCount() + 1 ;
        //1 is added for header
        if (mReviewCursor != null) count = count + mReviewCursor.getCount() + 1 ;
        if (mMovieCursor != null) count = count + mMovieCursor.getCount();
        return count;
    }



    /*public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }*/

    public class ViewHolderMovieDetails extends RecyclerView.ViewHolder{

        public final ImageView iconView;
        public final TextView titleView,releaseDateView,userRatingView,synopsisView;
        public final Button btn;

        public ViewHolderMovieDetails(View itemView)
        {
            super(itemView);
            btn = (Button) itemView.findViewById(R.id.movie_favourite_button);
            iconView = (ImageView) itemView.findViewById(R.id.movie_poster);
            titleView = (TextView) itemView.findViewById(R.id.movie_title);
            releaseDateView = (TextView) itemView.findViewById(R.id.movie_release_date);
            userRatingView = (TextView) itemView.findViewById(R.id.movie_user_rating);
            synopsisView = (TextView) itemView.findViewById(R.id.movie_synopsis);
        }

        public void bindViews(Context context, Cursor cursor)
        {
            final String movie_id = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
            final String original_title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE));
            final String poster_path = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
            final String release_date = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
            final String overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW));
            final String vote_average = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE));

            String formatted_release_date = Utility.getFormattedMonthDay(release_date);
            titleView.setText(original_title);
            releaseDateView.setText(formatted_release_date);
            userRatingView.setText(vote_average);
            synopsisView.setText(overview);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri uri = MovieContract.FavouriteEntry.buildFavouriteMovieUriFromId(movie_id);
                    Cursor cur = mContext.getContentResolver().query(
                            uri,
                            new String[]{MovieContract.FavouriteEntry._ID},
                            MovieContract.FavouriteEntry.COLUMN_MOVIE_ID + " = ?",
                            new String[]{movie_id},
                            null);
                    if(cur.getCount() == 0)
                    {
                        Uri fav = MovieContract.FavouriteEntry.CONTENT_URI;
                        ContentValues cv = new ContentValues();
                        cv.put(MovieContract.FavouriteEntry.COLUMN_MOVIE_ID,movie_id);
                        cv.put(MovieContract.FavouriteEntry.COLUMN_ORIGINAL_TITLE,original_title);
                        cv.put(MovieContract.FavouriteEntry.COLUMN_OVERVIEW,overview);
                        cv.put(MovieContract.FavouriteEntry.COLUMN_POSTER_PATH,poster_path);
                        cv.put(MovieContract.FavouriteEntry.COLUMN_RELEASE_DATE,release_date);
                        cv.put(MovieContract.FavouriteEntry.COLUMN_VOTE_AVERAGE,vote_average);

                        Uri resultUri = mContext.getContentResolver().insert(fav,cv);
                        Toast.makeText(mContext,"Movie added to favourites",Toast.LENGTH_SHORT).show();
                        Log.v(LOG_TAG + "Data Inserted",resultUri.toString());
                        cur.close();
                    }
                    else
                    {
                        Toast.makeText(mContext,"Movie already added to favourites",Toast.LENGTH_SHORT).show();
                    }
                }
            });

            /*Uri builtThumbUri = Uri.parse(BASE_URI).buildUpon()
                    .appendPath(key)
                    .appendPath(IMG_PATH)
                    .build();*/
            Picasso.with(context).load(poster_path).into(iconView);
        }
    }

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

    public class ViewHolderVideoHeader extends RecyclerView.ViewHolder
    {
        public ViewHolderVideoHeader(View itemView) {
            super(itemView);
        }
    }

    public class ViewHolderReviewHeader extends RecyclerView.ViewHolder
    {
        public ViewHolderReviewHeader(View itemView) {
            super(itemView);
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
