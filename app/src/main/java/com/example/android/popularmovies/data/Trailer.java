package com.example.android.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by mm090d on 10/29/2017.
 */

public class Trailer implements Parcelable {

    private String name;
    private String key;

    public Trailer(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public Trailer( Parcel in) {
        this.name = in.readString();
        this.key = in.readString();
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    static final Parcelable.Creator<Trailer> CREATOR
            = new Parcelable.Creator<Trailer>() {

        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(key);
    }
}
