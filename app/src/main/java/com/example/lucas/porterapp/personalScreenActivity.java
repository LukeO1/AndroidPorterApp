package com.example.lucas.porterapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Activity to display user specific data
 */
public class personalScreenActivity extends AppCompatActivity {

    DatabaseHelper myDB;
    ListView completedListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_screen);

        String currentTime = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        //Retrieve intent passed from TaskList activity, a WorkList object
        Intent i = getIntent();
        TaskInfo task = (TaskInfo) i.getSerializableExtra("taskObject");
//        Toast.makeText(this, "WORK!: " + task.getWard() + " "+ task.getDestination(), Toast.LENGTH_SHORT).show();

        //Set Text to inProgress TextView
        TextView InProgressTextView = (TextView) findViewById((R.id.InProgressTextView));
        InProgressTextView.setText(task.getWard() +" "+ task.getPatientName() +" "+ task.getDestination());

        //Prepare data to be inserted into SqlDatabase
        String taskId = task.getTaskID();
        String wardName = task.getWard();
        String patientName = task.getPatientName();
        String destination = task.getDestination();
        String timeStampCreated = task.getTimeStamp();
        String timeStampCompleted = currentTime;
        String timeTaken = task.getMinutes();

        //Create a SQLite database
        myDB = new DatabaseHelper(this);

        //insert data into the DB
        boolean result = myDB.insertData(taskId, wardName, patientName, destination, timeStampCreated, timeStampCompleted,  timeTaken);

        // Populate the completed task list view using a custom adapter found at Cursor Adapter file
        completedListView = (ListView) findViewById(R.id.CompletedListView);
        CursorAdapter adapter = new CursorAdapter(this, R.layout.row_layout_completed, myDB.createCursor(), 0 );

        // Add header to the complete task list view
        LayoutInflater myinflater = getLayoutInflater();
        ViewGroup myHeader = (ViewGroup)myinflater.inflate(R.layout.completedlistview_header_layout, completedListView, false);
        completedListView.addHeaderView(myHeader, null, false);

        completedListView.setAdapter(adapter);

    }


}
