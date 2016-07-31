package com.firstexample.priyesh.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.firstexample.priyesh.popularmovies.data.MovieContract.*;

/**
 * Created by PRIYESH on 29-07-2016.
 * To create local database for Movie
 */
public class MovieDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movies.db";

    public MovieDBHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_Movie_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME +  " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_FAVOURITE_MOVIE_TABLE = "CREATE TABLE " + FavouriteEntry.TABLE_NAME +  " (" +
                FavouriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavouriteEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                FavouriteEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                FavouriteEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                FavouriteEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                FavouriteEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                FavouriteEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_VIDEO_TABLE = "CREATE TABLE " + VideoEntry.TABLE_NAME +  " (" +
                VideoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                VideoEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_VIDEO_ID + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_VIDEO_NAME + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_VIDEO_KEY + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_VIDEO_TYPE + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME +  " (" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ReviewEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL " +
                " );";

        db.execSQL(SQL_CREATE_Movie_TABLE);
        db.execSQL(SQL_CREATE_FAVOURITE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_VIDEO_TABLE);
        db.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FavouriteEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + VideoEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        onCreate(db);
    }
}
