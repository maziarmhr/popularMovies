package com.example.android.popularmovies.data;

/**
 * Created by mm090d on 10/29/2017.
 */

public class Trailer {

    private String name;
    private String key;

    public Trailer(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }
}
