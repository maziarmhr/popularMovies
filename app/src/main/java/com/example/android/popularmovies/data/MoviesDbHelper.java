package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mm090d on 10/28/2017.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "movies.db";

    private static final int DATABaSE_VERSION = 1;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABaSE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIES_TABLE =

                "CREATE TABLE " + FavoriteMoviesContract.MovieEntry.TABLE_NAME + " (" +

                        FavoriteMoviesContract.MovieEntry._ID  + " INTEGER PRIMARY KEY, " +

                        FavoriteMoviesContract.MovieEntry.COLUMN_TITLE       + " TEXT NOT NULL, " +

                        FavoriteMoviesContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL," +

                        FavoriteMoviesContract.MovieEntry.COLUMN_POSTER   + " TEXT, " +

                        FavoriteMoviesContract.MovieEntry.COLUMN_RATING   + " REAL NOT NULL, " +

                        FavoriteMoviesContract.MovieEntry.COLUMN_RELEASE   + " INTEGER NOT NULL ); " ;

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteMoviesContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
