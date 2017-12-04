package com.example.android.popularmovies.data;

/**
 * Created by mm090d on 10/29/2017.
 */

public class Review {

    private String author;
    private String content;


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
