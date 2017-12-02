package com.example.android.popularmovies.data;

import java.net.URL;

/**
 * Created by mm090d on 10/29/2017.
 */

public class Review {

    private String id;
    private String author;
    private String content;
    private URL url;


    public Review(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
