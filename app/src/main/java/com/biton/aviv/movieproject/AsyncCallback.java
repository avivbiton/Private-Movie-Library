package com.biton.aviv.movieproject;

/**
 * Created by Aviv on 26-Mar-18.
 */

public interface AsyncCallback {

    void OnFinishExecuting(String result);
    void OnPreExecute();
    void OnFailedExecuting();
}
