package com.firstexample.priyesh.popularmovies;

import android.content.Context;

/**
 * Created by PRIYESH on 07-08-2016.
 */
public interface OnPostExecuteOfAsyncTask {
    void afterVideoPostExecute(Context c);
    void afterReviewPostExecute(Context c);
    void updateRecyclerView();
}
