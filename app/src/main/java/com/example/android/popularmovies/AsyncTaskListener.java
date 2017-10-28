package com.example.android.popularmovies;

/**
 * Created by mm090d on 8/5/2017.
 */

public interface AsyncTaskListener<T>{
    public void onPreTaskStart();
    public void onTaskComplete(T result);
}
