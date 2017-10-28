package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mm090d on 7/30/2017.
 */

public class Movie implements Parcelable {
    private final long mId;
    private final String mTitle;
    private final String mOverview;
    private final double mRating;
    private final Date mReleaseDate;
    private final String mPoster;

    public Movie(long id, String title, String overview, double rating, Date releaseDate, String poster) {
        this.mId = id;
        this.mTitle = title;
        this.mOverview = overview;
        this.mRating = rating;
        this.mReleaseDate = releaseDate;
        this.mPoster = poster;
    }

    public Movie( Parcel in) {
        this.mId = in.readLong();
        this.mTitle = in.readString();
        this.mOverview = in.readString();
        this.mRating = in.readDouble();
        this.mReleaseDate = (Date) in.readSerializable();
        this.mPoster = in.readString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mTitle);
        dest.writeString(mOverview);
        dest.writeDouble(mRating);
        dest.writeSerializable(mReleaseDate);
        dest.writeString(mPoster);
    }

    public int describeContents() {
        return 0;
    }

    static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {

        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

    public String getOverview() {
        return mOverview;
    }

    public double getRating() {
        return mRating;
    }

    public String getReleaseDate() {
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        return df.format(mReleaseDate);
    }

    public String getPoster() {
        return mPoster;
    }
}
