package com.example.lucas.porterapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Activity to display user specific data
 */
public class PersonalScreenActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int REQUEST_BARCODE_RESULT = 1;

    private DatabaseReference mRef;
    private DatabaseHelper myDB;
    private FirebaseAuth mAuth;
    private SharedPreferences.Editor editor;
    private TaskInfo task, taskInfo;
    private ListView completedListView;
    private TextView inProgressOriginView, inProgressDestinationView, inProgressPatientIDView,
            inProgressPatientNameView, inProgressTimerView, inProgressTransportModeIconView;
    private ImageView inProgressTimerIcon;
    private String CHECK_STATUS = "com.example.lucas.porterapp.StatusCheck", orderBy;
    private Spinner sortBySpinner;
    private Button inProgressCameraButton, inProgressConfirmTaskButton;
    private boolean flag;
    private String currentPatientID = null;
    ViewFlipper inProgressViewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_screen);
        getSupportActionBar().setTitle("Personal Work List");

        initializeInProgressViews();

        completedListView = (ListView) findViewById(R.id.CompletedListView);

        //Create a SQLite database
        myDB = new DatabaseHelper(this);

        // Retrieve checkSelected flag from shared preference
        boolean checkSelected = Boolean.parseBoolean(statusCheck());

        // Retrieve intent passed from TaskList activity, a WorkList object
        Intent i = getIntent();
        if (i.hasExtra("taskObject")) {

            task = (TaskInfo) i.getSerializableExtra("taskObject");
            //Set Text to inProgress TextView
            populateInProgressView(task);

            flag = false;
            new RespondOnFinish().execute();

        }else if(checkSelected){
            flag = true;
            new RespondOnFinish().execute();
        }else if(!checkSelected){
            inProgressViewFlipper.setDisplayedChild(1);
        }

        populateList(orderBy);
        idScannerLauncher();
        confirmTaskButtonListener();

        // spinner to sort the completed task list
        sortBySpinner = (Spinner) findViewById(R.id.sortBySpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_by_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sortBySpinner.setAdapter(adapter);
        sortBySpinner.setOnItemSelectedListener(this);

    }

// -------------------------------------------------------------------------------------------------
    public void initializeInProgressViews(){
        // Initialize views used in code
        View inProgressTextViewHolder = findViewById(R.id.InProgressTextViewHolder);

        inProgressOriginView =
                (TextView) inProgressTextViewHolder.findViewById((R.id.inProgressOriginView));
        inProgressDestinationView =
                (TextView) inProgressTextViewHolder.findViewById((R.id.inProgressDestinationView));
        inProgressPatientNameView =
                (TextView) inProgressTextViewHolder.findViewById((R.id.inProgressPatientNameView));
        inProgressPatientIDView =
                (TextView) inProgressTextViewHolder.findViewById((R.id.inProgressPatientIDView));
        inProgressTimerView =
                (TextView) inProgressTextViewHolder.findViewById((R.id.inProgressTimerView));
        inProgressTransportModeIconView =
                (TextView) inProgressTextViewHolder.findViewById((R.id.inProgressTransportModeIconView));

        inProgressTimerIcon =
                (ImageView) inProgressTextViewHolder.findViewById(R.id.inProgressTimerIcon);

        inProgressCameraButton =
                (Button) inProgressTextViewHolder.findViewById(R.id.inProgressCameraButton);
        inProgressConfirmTaskButton =
                (Button) inProgressTextViewHolder.findViewById(R.id.inProgressConfirmTaskButton);

        inProgressConfirmTaskButton.setEnabled(false); // prevents user from clicking until patient has been scanned

        inProgressViewFlipper = (ViewFlipper)findViewById(R.id.inProgressViewFlipper);
    }

// -------------------------------------------------------------------------------------------------
    public void populateInProgressView(TaskInfo xTask){

        Typeface font = Typeface.createFromAsset( getAssets(), "fontawesome-webfont.ttf" );

        String iconWheelchair = getString(R.string.icon_wheelchair);
        String iconBed = getString(R.string.icon_bed);
        String iconWalking = getString(R.string.icon_walking);
        String iconArrow = getString(R.string.icon_right_arrow);
        String iconEllipsis = getString(R.string.icon_ellipsis);

        inProgressOriginView.setText("From " + xTask.getWard());
        inProgressDestinationView.setText(iconArrow+ " To " + xTask.getDestination());
        inProgressTimerView.setText(xTask.getMinutes()+" Mins");
        inProgressPatientNameView.setText("Name: " + xTask.getPatientName());
        inProgressPatientIDView.setText("Patient ID: " + xTask.getPatientID());
        currentPatientID = xTask.getPatientID();

        // Set Icon for transport moder
        if(xTask.getTransportMode() == 1){
            inProgressTransportModeIconView.setText(iconWheelchair);
        }else if(xTask.getTransportMode() == 2){
            inProgressTransportModeIconView.setText(iconBed);
        }else if(xTask.getTransportMode() == 3){
            inProgressTransportModeIconView.setText(iconWalking);
        }

        // Set icon colour for priority
        if(xTask.getPriority() == 1){
            DrawableCompat.setTint(inProgressTimerIcon.getDrawable(),
                    ContextCompat.getColor(getApplicationContext(), R.color.colourTimer1));
        }else if(xTask.getPriority() == 2){
            DrawableCompat.setTint(inProgressTimerIcon.getDrawable(),
                    ContextCompat.getColor(getApplicationContext(), R.color.colourTimer2));
        }else{
            DrawableCompat.setTint(inProgressTimerIcon.getDrawable(),
                    ContextCompat.getColor(getApplicationContext(), R.color.colourTimer3));
        }

        inProgressOriginView.setTypeface(font);
        inProgressTransportModeIconView.setTypeface(font);
        inProgressDestinationView.setTypeface(font);
    }
// -------------------------------------------------------------------------------------------------

    public void idScannerLauncher() {

        inProgressCameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                scanBarcode(v);
            }
        });
    }

    // ---------------------------------------------------------------------------------------------
    public void confirmTaskButtonListener() {

        inProgressConfirmTaskButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                boolean checkSelected = Boolean.parseBoolean(statusCheck());
                if(checkSelected){

                    inProgressViewFlipper.setDisplayedChild(1);
                    confirmTask(taskInfo); //enter item into SQLite local database
                    statusEdit("false"); // set checkSelected to false so user can accept tasks again
                    populateList(orderBy); // refresh and populate completed listView
                    mRef.child(taskInfo.getTaskID()).removeValue(); //remove item from database

                }else{
                    Toast.makeText(getApplicationContext(),
                        "No Task to confirm", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Parses the item confirmed completed into the SQLite db
     * @param task
     */
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
        String userID = task.getUserID();
//        myDB.deleteAll(); //Clear the DB

        //insert data into the DB
        boolean result = myDB.insertDataCompleted(taskId, wardName, patientName, destination,
                timeStampCreated, timeStampCompleted,  timeTaken, userID);
    }
    // ---------------------------------------------------------------------------------------------
    /**
     * Implementation of the ongetDataListener interface to handle asynchronous tasks
     * @return
     */
    public TaskInfo readDataListener(){

        mRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://porterapp-3178d.firebaseio.com/Tasks");
        Query query = mRef.orderByChild("userID").equalTo(getUserID());

        readData(query, new OnGetDataListener() {
            @Override
            public void onSucess(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    taskInfo = child.getValue(TaskInfo.class);
                    if(flag){
                        populateInProgressView(taskInfo);
//                        inProgressTextViewMain.setText(taskInfo.getWard() + " " +
//                                taskInfo.getPatientName() + " " + taskInfo.getDestination());
                    }
                }
            }

            @Override
            public void onStart() {}

            @Override
            public void onFailure() {}
        });
        return taskInfo;
    }
    // ---------------------------------------------------------------------------------------------
    /**
     * Listeners for onGetDataListener interface methods. Returns when the task is finished or failed
     * @param ref firebase query
     * @param listener listener
     */
    public void readData(Query ref, final OnGetDataListener listener) {
        listener.onStart();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {listener.onSucess(dataSnapshot);}

            @Override
            public void onCancelled(DatabaseError databaseError) {listener.onFailure();}
        });
    }
    // ---------------------------------------------------------------------------------------------
    /**
     * Retrieves user specific ID from Firebase Authentication
     * @return User Id of User
     */
    public String getUserID() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
    }
    // ---------------------------------------------------------------------------------------------

    /**
     * Check the variable stored in a shared preference file if the User has already accepted a Task
     * @return true if User has alreadt accepted a task, false if not, or not found if file could
     * not be found.
     */
    public String statusCheck(){
        //Find shared preference file
        SharedPreferences prefs = getSharedPreferences(CHECK_STATUS, MODE_PRIVATE);

        return prefs.getString("checkSelected", "Not found");
    }

    /**
     * Edits a value stored in the shared preference files. The value represents true if use has
     * already accpeted a task, or false if user has not
     * @param status the status true or false to change in the file
     */
    public void statusEdit(String status){
        editor = getSharedPreferences(CHECK_STATUS, MODE_PRIVATE).edit();
        editor.putString("checkSelected", status).apply();
    }

    // ---------------------------------------------------------------------------------------------

    public void populateList(String orderBy){

        Context context = getApplicationContext();
        Toast.makeText(context, orderBy, Toast.LENGTH_LONG);
        CursorAdapter adapter;

        Cursor cursor = myDB.createCursor(orderBy);
        if(cursor != null) {
            adapter = new CursorAdapter(this, R.layout.row_layout_completed, cursor, 0 );
            completedListView.setAdapter(adapter);
        } else {}
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

// -------------------------------------------------------------------------------------------------

    // required because of spinner
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    // ---------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case R.id.menu_tasklist:
                Toast.makeText(this, "Tasklist", Toast.LENGTH_LONG).show();
                intent = new Intent(this, Tasklist.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.menu_my_page:
                Toast.makeText(this, "My Page", Toast.LENGTH_LONG).show();
                break;
            case R.id.menu_contacts:
                Toast.makeText(this, "Contacts", Toast.LENGTH_LONG).show();
                intent = new Intent(this, PhoneDirectory.class);
                startActivity(intent);
                break;
            case R.id.menu_settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_LONG).show();
                intent = new Intent(this, Settings.class);
                startActivity(intent);
                break;
            case R.id.menu_sign_out:
                Toast.makeText(this, "Sign Out", Toast.LENGTH_LONG).show();
                (MainActivity.mAuth).getInstance().signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

// -------------------------------------------------------------------------------------------------
    class RespondOnFinish extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){super.onPreExecute();}

        @Override
        protected Void doInBackground(Void... voids) {

            while(readDataListener() == null){
                readDataListener();
                try {
                    TimeUnit.MILLISECONDS.sleep(750);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        public void onPostExecute(Void result) {}
    }

    // ---------------------------------------------------------------------------------------------

    // called to initiate the barcode scanning
    public void scanBarcode(View v) {
        Intent intent = new Intent(this, BarcodeScanner.class);

        // receives the result from the BarcodeScanner activity
        startActivityForResult(intent, REQUEST_BARCODE_RESULT);
    }

    // ---------------------------------------------------------------------------------------------

    // get the result of the barcode back rom the BarcodeScanner
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_BARCODE_RESULT && resultCode == CommonStatusCodes.SUCCESS){
                if(data!=null){
                    Barcode barcode = data.getParcelableExtra("barcode");
                    if(currentPatientID.equals(barcode.displayValue)){
                        inProgressCameraButton.setEnabled(false);
                        inProgressConfirmTaskButton.setEnabled(true);
                        Toast.makeText(this, "Barcode successfully detected!", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(this, "Incorrect barcode detected!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(this, "No barcode detected from camera", Toast.LENGTH_LONG).show();
                }

        }


        super.onActivityResult(requestCode, resultCode, data);
    }
}
