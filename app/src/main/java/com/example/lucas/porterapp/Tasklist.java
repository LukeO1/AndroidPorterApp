package com.example.lucas.porterapp;

import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.data;
import static android.R.id.list;

public class Tasklist extends AppCompatActivity {
    private Button mAddToDB;
    private Button mReadFromDB;
    private DatabaseReference mRef;
    public String[] myCurrentTask;
    public String[] tasklist;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasklist);
        //mAddToDB = (Button)findViewById(R.id.sendData);
        //mReadFromDB = (Button)findViewById(R.id.readDB);
        listView = (ListView)findViewById(R.id.listview1);

        mRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://porterapp-3178d.firebaseio.com/Tasks");

        // Write a message to the database
//        myCurrentTask = new String[]{"NorthEast Radiology JaneSmith"};
//        mAddToDB.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mRef.setValue(myCurrentTask[0]);
//            }
//        });

        ListAdapter adapter = new FirebaseListAdapter<String>(this, String.class, android.R.layout.simple_list_item_1, mRef){
            @Override
            protected void populateView(View v, String model, int position) {
                TextView tv = (TextView)v.findViewById(android.R.id.text1);
                tv.setText(model);
            }
        };
        listView.setAdapter(adapter);
        // Read from the database
//        mReadFromDB.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                for(int i = 0; i < tasklist.length; i++){
//                    Toast.makeText(Tasklist.this, tasklist[i], Toast.LENGTH_LONG).show();
//                }
//            }
//        });
    }

    protected void onStop(){
        super.onStop();
        FirebaseAuth.getInstance().signOut();
    }
}
