package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by mm090d on 11/11/2017.
 */

public class FavoriteMoviesProvider extends ContentProvider {

    public static final int MOVIES = 100;
    public static final int MOVIE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mOpenHelper;

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavoriteMoviesContract.CONTENT_AUTHORITY;

        /* This URI is content://com.example.android.popularmovies/movie/ */
        matcher.addURI(authority, FavoriteMoviesContract.PATH_FAVORITE_MOVIES, MOVIES);

        /*
         * This URI would look something like content://com.example.android.popularmovies/movie/1472214172
         * The "/#" signifies to the UriMatcher that if PATH_FAVORITE_MOVIES is followed by ANY number,
         * that it should return the MOVIE_WITH_ID code
         */
        matcher.addURI(authority, FavoriteMoviesContract.PATH_FAVORITE_MOVIES + "/#", MOVIE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {

            case MOVIE_WITH_ID: {
                String movieId = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{movieId};

                cursor = mOpenHelper.getReadableDatabase().query(
                        FavoriteMoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        FavoriteMoviesContract.MovieEntry._ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);

                break;
            }
            case MOVIES: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        FavoriteMoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        Uri returnUri;

        switch (sUriMatcher.match(uri)) {

            case MOVIES:
                long _id = db.insert(FavoriteMoviesContract.MovieEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    returnUri = ContentUris.withAppendedId(FavoriteMoviesContract.MovieEntry.CONTENT_URI, _id);

                } else {
                    throw new SQLException("Problem while inserting into uri: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numRowsDeleted;
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        /*
         * If we pass null as the selection to SQLiteDatabase#delete, our entire table will be
         * deleted. However, if we do pass null and delete all of the rows in the table, we won't
         * know how many rows were deleted. According to the documentation for SQLiteDatabase,
         * passing "1" for the selection will delete all rows and return the number of rows
         * deleted, which is what the caller of this method expects.
         */
        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {

            case MOVIES:
                numRowsDeleted = db.delete(
                        FavoriteMoviesContract.MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;
            case MOVIE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                numRowsDeleted = db.delete(FavoriteMoviesContract.MovieEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        throw new RuntimeException("Not implementing update...");
    }
}
