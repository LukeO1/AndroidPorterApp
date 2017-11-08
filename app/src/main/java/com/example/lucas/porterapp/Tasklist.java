package com.example.lucas.porterapp;

import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private int itemPosition;
    private LinearLayout workListItem;
    private LinearLayout subWorkListItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasklist);

        //mAddToDB = (Button)findViewById(R.id.sendData);
        //mReadFromDB = (Button)findViewById(R.id.readDB);

        // Write a message to the database
//        myCurrentTask = new String[]{"NorthEast Radiology JaneSmith"};
//        mAddToDB.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mRef.setValue(myCurrentTask[0]);
//            }
//        });

        // ListView containing all worklist items
        listView = (ListView)findViewById(R.id.listview_worklist);

        // Connection to Firebase
        mRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://porterapp-3178d.firebaseio.com/Tasks");
        Query queryRef = mRef.orderByChild("priority"); // Order the list by priority

        // ********Code to randomly generate a bunch of tasks into the database to work with********
//        for(int i=20; i<40; i++){
//            int priority = (int)(Math.random()*5 + 1);
//            int randomNum = (int)(Math.random()*15 + 1);
//            String time = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
//
//            DatabaseReference Task = mRef.child("Task " + i); // ID for each task
//
//            DatabaseReference ward = Task.child("ward"); // Ward Value for each task
//            ward.setValue("Ward " + i);
//
//            DatabaseReference PatientName = Task.child("patientName"); //Patient name for each task
//            PatientName.setValue("PatientName " + (i+randomNum));
//
//            DatabaseReference Destination = Task.child("destination"); //Destiantion for task
//            Destination.setValue("Destination " + randomNum);
//
//            DatabaseReference p = Task.child("priority"); //priority 1 - 5
//            p.setValue(priority);
//
//            DatabaseReference t = Task.child("timeStamp"); //Timestamp for each task
//            t.setValue(time);
//        }

        ListAdapter adapter = new FirebaseListAdapter<TaskInfo>(this, TaskInfo.class, R.layout.row_layout, queryRef){
            @Override
            protected void populateView(View v, TaskInfo model, int position) {

                TextView workListItem = (TextView) v.findViewById(R.id.mainWorkListItemName);
                TextView workListItemTimer = (TextView) v.findViewById(R.id.mainWorkListItemTimer);
                TextView subWorkListItem = (TextView) v.findViewById((R.id.subWorkListTextView));

                // Populate listView items
                workListItem.setText(model.getWard());
                workListItemTimer.setText(model.getTimeStamp() + "mins");
                subWorkListItem.setText(model.getDestination() + "  Priority: " + model.getPriority());
//                Toast.makeText(Tasklist.this, model.getTime(), Toast.LENGTH_LONG).show();
            }
        };
        listView.setAdapter(adapter);

        // Click event for list items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                subWorkListItem = (LinearLayout) view.findViewById(R.id.subWorkListItem);
                workListItem = (LinearLayout) view.findViewById(R.id.WorkListItem);

//                itemPosition = position;
                // New thread for hiding and displaying extra information for each item
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        if ( subWorkListItem.getVisibility() == View.VISIBLE){
                            subWorkListItem.setVisibility(View.GONE);
                        }else{ subWorkListItem.setVisibility(View.VISIBLE);}
                    } });


            Button subWorkListButtonsOK = (Button) listView.findViewById(R.id.subWorkListButtonsOK);

            // Click event for accept button
            subWorkListButtonsOK.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

//                workListItem.setVisibility(View.GONE);

                }
            });
            }
        });


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
