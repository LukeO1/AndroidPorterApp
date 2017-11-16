package com.example.lucas.porterapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Activity to display user specific data
 */
public class PersonalScreenActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private DatabaseReference mRef;
    private DatabaseHelper myDB;
    private ListView completedListView;
    private TaskInfo task;
    private TextView InProgressTextView;
    private String CHECK_STATUS = "com.example.lucas.porterapp.StatusCheck";
    private SharedPreferences.Editor editor;
    private String orderBy;
    private Spinner sortBySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_screen);
        getSupportActionBar().setTitle("Personal Work List");

        mRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://porterapp-3178d.firebaseio.com/Tasks");

        //Retrieve intent passed from TaskList activity, a WorkList object
        Intent i = getIntent();
        if(i.hasExtra("taskObject")) {
            task = (TaskInfo) i.getSerializableExtra("taskObject");


            //Set Text to inProgress TextView
            InProgressTextView = (TextView) findViewById((R.id.InProgressTextView));
            InProgressTextView.setText(task.getWard() + " " + task.getPatientName() + " " + task.getDestination());
        }

        //Create a SQLite database
        myDB = new DatabaseHelper(this);

        populateList(orderBy);

        Button confirmTaskButton = (Button) findViewById(R.id.confirmTaskButton);
        confirmTaskButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                confirmTask(task);
                InProgressTextView.setText("None in Progress");
                mRef.child("Task 0").removeValue();

                editStatus("false");
                populateList(orderBy);

            }
        });

        // spinner to sort the completed task list
        sortBySpinner = (Spinner) findViewById(R.id.sortBySpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_by_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sortBySpinner.setAdapter(adapter);
        sortBySpinner.setOnItemSelectedListener(this);

    }

    // ---------------------------------------------------------------------------------------------


    public void editStatus(String status){

        editor = getSharedPreferences(CHECK_STATUS, MODE_PRIVATE).edit();
        editor.putString("checkSelected", status).apply();
    }

    // ---------------------------------------------------------------------------------------------

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

    // ---------------------------------------------------------------------------------------------

    public void populateList(String orderBy){

        Context context = getApplicationContext();
        Toast.makeText(context, orderBy, Toast.LENGTH_LONG);

        CursorAdapter adapter;

        // Populate the completed task list view using a custom adapter found at Cursor Adapter file
        completedListView = (ListView) findViewById(R.id.CompletedListView);

        Cursor cursor = myDB.createCursor(orderBy);
        if(cursor != null)
        {
            adapter = new CursorAdapter(this, R.layout.row_layout_completed, cursor, 0 );
            completedListView.setAdapter(adapter);
        } else {

        }


    }

    // ---------------------------------------------------------------------------------------------

    // gets the selected item from the spinner and populates the completed database based on this
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        String selectedOrderBy = sortBySpinner.getSelectedItem().toString();
        Context context = getApplicationContext();
        Toast.makeText(context, selectedOrderBy, Toast.LENGTH_SHORT).show();

        switch(selectedOrderBy){
            case("Patient ID"): {
                orderBy = "TASK_ID"; // NEEDS TO BE CHANGED ONCE DATABASE UPDATED
                populateList(orderBy);
                break;
            }
            case("Patient Name"):{
                orderBy = "PATIENT_NAME";
                populateList(orderBy);
                break;
            }
            case("Task ID"):{
                orderBy = "TASK_ID";
                populateList(orderBy);
                break;
            }
            case("Destination"):{
                orderBy="DESTINATION";
                populateList(orderBy);
                break;
            }
            case("Task Creation Time"):{
                orderBy="TIMESTAMP_CREATED";
                populateList(orderBy);
                break;
            }
            case("Completed Time"):{
                orderBy="TIMESTAMP_COMPLETED";
                populateList(orderBy);
                break;
            }
            case("Task Duration"):{
                orderBy="TIMETAKEN";
                populateList(orderBy);
            }
        }

    }

    // ---------------------------------------------------------------------------------------------

    // required because of spinner
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    // ---------------------------------------------------------------------------------------------
}
