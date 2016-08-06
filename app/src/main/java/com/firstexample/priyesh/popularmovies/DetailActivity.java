package com.firstexample.priyesh.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firstexample.priyesh.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

//implements LoaderManager.LoaderCallbacks<Cursor>
public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    private static final int VIDEO_LOADER = 0;
    private static final int REVIEW_LOADER = 1;
    private Cursor mVideoCursor;
    private LinearLayout mLinearLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecycleAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    Button btn;
    private static String movie_id;
    String original_title = null, overview = null, release_date = null, vote_average = null, poster_path = null, isFavourite = null;

    ImageView movie_poster;
    private VideoAdapter mVideoAdapter;
    private ReviewAdapter mReviewAdapter;
    private ListView mListViewForVideos;
    private ListView mListViewForReviews;
    private String movieString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mLinearLayout = (LinearLayout) findViewById(R.id.detail_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.video_review_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        btn = (Button) findViewById(R.id.movie_favourite_button);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String formatted_release_date = null;
        if (bundle != null) {
            isFavourite = bundle.getString(getString(R.string.isFavourite));
            Uri uri;
            Cursor cursor;
            if(isFavourite.equals(getString(R.string.isNoFavourite)))
            {
                movie_id = bundle.getString(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
                uri = MovieContract.MovieEntry.buildMovieUriFromId(movie_id);
                cursor = getContentResolver().query(uri,
                        null,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{movie_id},
                        null);
            }
            else
            {
                //Remove the Favourites button
                btn.setVisibility(View.GONE);
                movie_id = bundle.getString(MovieContract.FavouriteEntry.COLUMN_MOVIE_ID);
                uri = MovieContract.FavouriteEntry.buildFavouriteMovieUriFromId(movie_id);
                cursor = getContentResolver().query(uri,
                        null,
                        MovieContract.FavouriteEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{movie_id},
                        null);
            }
            //As column name of both tables are same, we can go with same cursor
            if(cursor.moveToFirst())
            {
                poster_path = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
                release_date = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
                overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW));
                original_title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE));;
                vote_average = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE));
                formatted_release_date = Utility.getFormattedMonthDay(release_date);
            }
            cursor.close();
        }
        ((TextView) findViewById(R.id.movie_title)).setText(original_title);
        ((TextView) findViewById(R.id.movie_synopsis)).setText(overview);
        ((TextView) findViewById(R.id.movie_release_date)).setText(formatted_release_date);
        ((TextView) findViewById(R.id.movie_user_rating)).setText(vote_average);

        movie_poster = (ImageView) findViewById(R.id.movie_poster);
        Picasso.with(this).load(poster_path).into(movie_poster);

        //Get Video Details
        Uri videoUri = MovieContract.VideoEntry.buildVideoUriFromId(movie_id);
        mVideoCursor = getContentResolver().query(videoUri,
                null,
                MovieContract.VideoEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{movie_id},
                null);
        //mListViewForVideos = (ListView) findViewById(R.id.listView_youtube_videos);
        //mVideoAdapter = new VideoAdapter(this, mVideoCursor, 0);
        //mListViewForVideos.setAdapter(mVideoAdapter);
        if (mVideoCursor.getCount() == 0)
        {

            FetchVideoTask fetchVideoTask = new FetchVideoTask(this);
            fetchVideoTask.execute(movie_id);
            /*try {
                movieString = fetchVideoTask.execute(movie_id).get();
                JSONObject object = new JSONObject(movieString);
                JSONArray resultsArray = object.getJSONArray("results");
                for (int position = 0; position < resultsArray.length(); position++)
                {
                    View videoItem = LayoutInflater.from(this).inflate(R.layout.video_item,null);
                    JSONObject videoObject = resultsArray.getJSONObject(position);
                    String key = videoObject.getString("key");
                    String name = videoObject.getString("name");
                    ((TextView)videoItem.findViewById(R.id.list_text_video)).setText(name);
                    mLinearLayout.addView(videoItem);
                    //this.addContentView(videoItem,);
                }

            } catch (InterruptedException | ExecutionException | JSONException e) {
                e.printStackTrace();
            }*/
        }



        /*mListViewForVideos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mVideoCursor.moveToPosition(position);
                String key = mVideoCursor.getString(mVideoCursor.getColumnIndex(MovieContract.VideoEntry.COLUMN_VIDEO_KEY));
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
                    startActivity(intent);
                }catch (ActivityNotFoundException ex) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v=" + key));
                    startActivity(intent);
                }
            }
        });*/

        //Get Review Details
        Uri reviewUri = MovieContract.ReviewEntry.buildReviewUriFromId(movie_id);
        Cursor reviewCursor = getContentResolver().query(reviewUri,
                null,
                MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{movie_id},
                null);
        if (reviewCursor.getCount() == 0)
        {
            FetchReviewTask fetchReviewTask = new FetchReviewTask(this);
            fetchReviewTask.execute(movie_id);
            /*try {
                String reviewString = fetchReviewTask.execute(movie_id).get();
                JSONObject object = new JSONObject(reviewString);
                String movieId = object.getString("id");
                JSONArray resultsArray = object.getJSONArray("results");

                for (int position = 0; position < resultsArray.length(); position++)
                {
                    View reviewItem = LayoutInflater.from(this).inflate(R.layout.review_item,null);
                    JSONObject reviewObject = resultsArray.getJSONObject(position);
                    String content = reviewObject.getString("content");
                    String author = reviewObject.getString("author");
                    ((TextView)reviewItem.findViewById(R.id.list_item_author_view)).setText(author);
                    ((TextView)reviewItem.findViewById(R.id.list_item_content_view)).setText(content);
                    mLinearLayout.addView(reviewItem);
                }
            } catch (InterruptedException | ExecutionException | JSONException e) {
                e.printStackTrace();
            }*/
        }
        mRecycleAdapter = new RecycleAdapter(this,mVideoCursor,reviewCursor);
        mRecyclerView.setAdapter(mRecycleAdapter);
        /*mListViewForReviews = (ListView) findViewById(R.id.listView_reviews);
        mReviewAdapter = new ReviewAdapter(this, reviewCursor, 0);
        mListViewForReviews.setAdapter(mReviewAdapter);*/

        //getSupportLoaderManager().initLoader(VIDEO_LOADER, null, this);
        //getSupportLoaderManager().initLoader(REVIEW_LOADER, null, this);

        //Utility.setDynamicHeight(mListViewForReviews);
    }

    public void onAddToFavourites(View view) {
        Uri uri = MovieContract.FavouriteEntry.buildFavouriteMovieUriFromId(movie_id);
        Cursor cur = getContentResolver().query(
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

            Uri resultUri = getContentResolver().insert(fav,cv);
            Toast.makeText(this,"Movie added to favourites",Toast.LENGTH_SHORT).show();
            Log.v(LOG_TAG + "Data Inserted",resultUri.toString());
            cur.close();
        }
        else
        {
            Toast.makeText(this,"Movie already added to favourites",Toast.LENGTH_SHORT).show();
        }
    }

    /*@Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id)
        {
            case VIDEO_LOADER:
                Uri videoUri = MovieContract.VideoEntry.buildVideoUriFromId(movie_id);

                return new CursorLoader(this,
                        videoUri,
                        null,
                        MovieContract.VideoEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{movie_id},
                        null);

            case REVIEW_LOADER:
                Uri reviewUri = MovieContract.ReviewEntry.buildReviewUriFromId(movie_id);

                return new CursorLoader(this,
                        reviewUri,
                        null,
                        MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{movie_id},
                        null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId())
        {
            case VIDEO_LOADER:
                mVideoAdapter.swapCursor(data);
                break;
            case REVIEW_LOADER:
                mReviewAdapter.swapCursor(data);
                break;
        }
        Utility.setDynamicHeight(mListViewForVideos);
        Utility.setDynamicHeight(mListViewForReviews);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId())
        {
            case VIDEO_LOADER:
                mVideoAdapter.swapCursor(null);
                break;
            case REVIEW_LOADER:
                mReviewAdapter.swapCursor(null);
                break;
        }
    }*/
}
