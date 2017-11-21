package com.example.lucas.porterapp;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by lucas on 21/11/2017.
 * Retrieved from https://stackoverflow.com/questions/30659569/wait-until-firebase-retrieves-data
 */

public interface OnGetDataListener {
    //this is for callbacks
    void onSucess(DataSnapshot dataSnapshot);
    void onStart();
    void onFailure();
}
