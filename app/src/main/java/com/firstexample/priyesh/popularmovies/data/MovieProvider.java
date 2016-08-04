package com.firstexample.priyesh.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by PRIYESH on 29-07-2016.
 * UriMatcher is tough than expected
 */
public class MovieProvider extends ContentProvider{

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDBHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int FAVOURITE_MOVIE = 101;
    static final int FAVOURITE_MOVIE_WITH_ID = 102;
    static final int MOVIE_WITH_ID = 103;
    static final int VIDEO = 104;
    static final int VIDEO_WITH_ID = 105;
    static final int REVIEW = 106;
    static final int REVIEW_WITH_ID = 107;

    private static final String sMovieFromID =
            MovieContract.MovieEntry.TABLE_NAME + "." +
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";

    private static final String sFavouriteMovieFromID =
            MovieContract.FavouriteEntry.TABLE_NAME + "." +
                    MovieContract.FavouriteEntry.COLUMN_MOVIE_ID + " = ? ";

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        switch (sUriMatcher.match(uri))
        {
            case MOVIE:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case FAVOURITE_MOVIE:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavouriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case FAVOURITE_MOVIE_WITH_ID:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavouriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case MOVIE_WITH_ID:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case VIDEO:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.VideoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case VIDEO_WITH_ID:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.VideoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case REVIEW:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case REVIEW_WITH_ID:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri:" +uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match)
        {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case FAVOURITE_MOVIE:
                return MovieContract.FavouriteEntry.CONTENT_TYPE;
            case FAVOURITE_MOVIE_WITH_ID:
                return MovieContract.FavouriteEntry.CONTENT_ITEM_TYPE;
            case MOVIE_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case REVIEW:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case REVIEW_WITH_ID:
                return MovieContract.ReviewEntry.CONTENT_ITEM_TYPE;
            case VIDEO:
                return MovieContract.VideoEntry.CONTENT_TYPE;
            case VIDEO_WITH_ID:
                return MovieContract.VideoEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (sUriMatcher.match(uri))
        {
            case MOVIE:
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if(_id > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case FAVOURITE_MOVIE:
                _id = db.insert(MovieContract.FavouriteEntry.TABLE_NAME, null, values);
                if(_id > 0)
                    returnUri = MovieContract.FavouriteEntry.buildFavouriteMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        db.close();
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Student: Start by getting a writable database
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match)
        {
            case MOVIE:
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case FAVOURITE_MOVIE:
                rowsDeleted = db.delete(MovieContract.FavouriteEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case VIDEO:
                rowsDeleted = db.delete(MovieContract.VideoEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case REVIEW:
                rowsDeleted = db.delete(MovieContract.ReviewEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Oh, and you should notify the listeners here.

        // Student: A null value deletes all rows.  In my implementation of this, I only notified
        // the uri listeners (using the content resolver) if the rowsDeleted != 0 or the selection
        // is null.
        db.close();
        if(rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Student: return the actual rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match)
        {
            case MOVIE:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            case FAVOURITE_MOVIE:
                rowsUpdated = db.update(MovieContract.FavouriteEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        db.close();
        // Oh, and you should notify the listeners here.
        if(rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;

    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount;
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case VIDEO:
                db.beginTransaction();
                returnCount = 0;
                try{
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.VideoEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case REVIEW:
                db.beginTransaction();
                returnCount = 0;
                try{
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority,MovieContract.PATH_MOVIE,MOVIE);
        matcher.addURI(authority,MovieContract.PATH_MOVIE + "?*",MOVIE_WITH_ID);
        matcher.addURI(authority,MovieContract.PATH_FAVOURITE,FAVOURITE_MOVIE);
        matcher.addURI(authority,MovieContract.PATH_FAVOURITE + "?*",FAVOURITE_MOVIE_WITH_ID);
        matcher.addURI(authority,MovieContract.PATH_VIDEO,VIDEO);
        matcher.addURI(authority,MovieContract.PATH_VIDEO + "?*",VIDEO_WITH_ID);
        matcher.addURI(authority,MovieContract.PATH_REVIEW,REVIEW);
        matcher.addURI(authority,MovieContract.PATH_REVIEW + "?*",REVIEW_WITH_ID);
        return matcher;
    }
}