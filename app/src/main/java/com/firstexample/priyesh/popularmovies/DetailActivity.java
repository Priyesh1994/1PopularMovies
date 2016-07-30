package com.firstexample.priyesh.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firstexample.priyesh.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    Button btn;
    String movie_id = null;
    String original_title = null;
    String overview = null;
    String release_date = null;
    String vote_average = null;
    String poster_path = null;
    String isFavourite = null;

    ImageView movie_poster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
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
}
