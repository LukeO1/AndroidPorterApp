package com.example.lucas.porterapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Activity to display user specific data
 */
public class personalScreenActivity extends AppCompatActivity {

    private DatabaseReference mRef;
    private DatabaseHelper myDB;
    private ListView completedListView;
    private TaskInfo task;
    private TextView InProgressTextView;
    private String CHECK_STATUS = "com.example.lucas.porterapp.StatusCheck";
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_screen);

        mRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://porterapp-3178d.firebaseio.com/Tasks");

        //Retrieve intent passed from TaskList activity, a WorkList object
        Intent i = getIntent();
        task = (TaskInfo) i.getSerializableExtra("taskObject");

        //Set Text to inProgress TextView
        InProgressTextView = (TextView) findViewById((R.id.InProgressTextView));
        InProgressTextView.setText(task.getWard() +" "+ task.getPatientName() +" "+ task.getDestination());

        //Create a SQLite database
        myDB = new DatabaseHelper(this);

        populateList();

        Button confirmTaskButton = (Button) findViewById(R.id.confirmTaskButton);
        confirmTaskButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                confirmTask(task);
                InProgressTextView.setText("None in Progress");
                mRef.child("Task 0").removeValue();

                editStatus("false");
                populateList();

            }
        });

    }




    public void editStatus(String status){

        editor = getSharedPreferences(CHECK_STATUS, MODE_PRIVATE).edit();
        editor.putString("checkSelected", status).apply();
    }

    public void confirmTask(TaskInfo task){

        String currentTime = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        //Prepare data to be inserted into SqlDatabase
        String taskId = task.getTaskID();
        String wardName = task.getWard();
        String patientName = task.getPatientName();
        String destination = task.getDestination();
        String timeStampCreated = task.getTimeStamp();
        String timeStampCompleted = currentTime;
        String timeTaken = task.getMinutes();

//        myDB.deleteAll(); //Clear the DB

        //insert data into the DB
        boolean result = myDB.insertData(taskId, wardName, patientName, destination, timeStampCreated, timeStampCompleted,  timeTaken);
    }

    public void populateList(){

        CursorAdapter adapter;

        // Populate the completed task list view using a custom adapter found at Cursor Adapter file
        completedListView = (ListView) findViewById(R.id.CompletedListView);

        Cursor cursor = myDB.createCursor();
        if(cursor != null)
        {
            adapter = new CursorAdapter(this, R.layout.row_layout_completed, myDB.createCursor(), 0 );
            // Add header to the complete task list view
//            LayoutInflater myinflater = getLayoutInflater();
//            ViewGroup myHeader = (ViewGroup)myinflater.inflate(R.layout.completedlistview_header_layout, completedListView, false);
//            completedListView.addHeaderView(myHeader, null, false);

            completedListView.setAdapter(adapter);
        } else {

        }


    }


}
