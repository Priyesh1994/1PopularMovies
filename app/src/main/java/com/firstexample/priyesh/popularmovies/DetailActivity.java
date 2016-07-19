package com.firstexample.priyesh.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    final String POSTER_PATH = "poster_path";
    final String RELEASE_DATE = "release_date";
    final String OVERVIEW = "overview";
    final String ORIGINAL_TITLE = "original_title";
    final String VOTE_AVERAGE = "vote_average";

    ImageView movie_poster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String original_title = null;
        String overview = null;
        String release_date = null;
        String vote_average = null;
        String poster_path = null;
        if (bundle != null) {
            poster_path = bundle.getString(POSTER_PATH);
            release_date = bundle.getString(RELEASE_DATE);
            overview = bundle.getString(OVERVIEW);
            original_title = bundle.getString(ORIGINAL_TITLE);
            vote_average = bundle.getString(VOTE_AVERAGE);
        }

        ((TextView) findViewById(R.id.movie_title)).setText(original_title);
        ((TextView) findViewById(R.id.movie_synopsis)).setText(overview);
        ((TextView) findViewById(R.id.movie_release_date)).setText(release_date);
        ((TextView) findViewById(R.id.movie_user_rating)).setText(vote_average);

        movie_poster = (ImageView) findViewById(R.id.movie_poster);
        Picasso.with(this).load(poster_path).into(movie_poster);
    }
}
