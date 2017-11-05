package com.example.lucas.porterapp;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by lucas on 05/11/2017.
 */

public class FirebaseContext extends Application{
    @Override
    public void onCreate(){
        super.onCreate();
        if(!FirebaseApp.getApps(this).isEmpty()){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
    }
}
