package com.example.lucas.porterapp;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TaskList Activity
 */
public class Tasklist extends AppCompatActivity {

    private DatabaseReference mRef;
    private ListView listView;
    private FirebaseListAdapter adapter;
    private LinearLayout subWorkListItem;
    private String itemKey;
    private String CHECK_STATUS = "com.example.lucas.porterapp.StatusCheck";
    private SharedPreferences.Editor editor;
    private FirebaseAuth mAuth;
    private int itemPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasklist);
        getSupportActionBar().setTitle("Work List");

        statusEdit("false"); //TEMP VALUE - must set value back to false and force user to release
                            // work items before sign out

        boolean checkFileExists = Boolean.parseBoolean(statusCheck());
        if(!checkFileExists){
            editor = getSharedPreferences(CHECK_STATUS, MODE_PRIVATE).edit();
            editor.putString("checkSelected", "false").apply();
        }

        listView = (ListView)findViewById(R.id.listview_worklist);

        // Connection to Firebase
        mRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://porterapp-3178d.firebaseio.com/Tasks");
        Query queryRef = mRef.orderByChild("inProgress").equalTo("NO");

        populateDatabase(); //UnComment to populate the db with randomly generated tasks

        // Populate WorkList with task from DB
        adapter = new FirebaseListAdapter<TaskInfo>(this, TaskInfo.class, R.layout.row_layout, queryRef){
            @Override
            protected void populateView(View v, TaskInfo model, int position) {

                TextView mainWorkListItemWard = (TextView) v.findViewById(R.id.mainWorkListItemWard);
                TextView mainWorkListItemTimer = (TextView) v.findViewById(R.id.mainWorkListItemTimer);
                TextView subWorkListTextView = (TextView) v.findViewById((R.id.subWorkListTextView));

                mainWorkListItemWard.setText(model.getWard());
                mainWorkListItemTimer.setText(model.getMinutes() + "mins");
                subWorkListTextView.setText(model.getDestination() + "  Priority: " + model.getPriority());
            }
        };
        listView.setAdapter(adapter);

        // Click event for list items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view,
                                    final int position, long id) {

            //Toast.makeText(Tasklist.this, "Item clicked, position: "+ position, Toast.LENGTH_SHORT).show();
            subWorkListItem = (LinearLayout) view.findViewById(R.id.subWorkListItem);
            toggleItem();

            Button subWorkListButtonsOK = (Button) view.findViewById(R.id.subWorkListButtonsOK);
            Button subWorkListButtonsNO = (Button) view.findViewById(R.id.subWorkListButtonsNO);

            TaskInfo x = (TaskInfo) listView.getItemAtPosition(position);

            buttonOnClickOK(subWorkListButtonsOK, position, x);
            buttonOnClickNO(subWorkListButtonsNO, position);

            }
        });


        FloatingActionButton openPersonalActivity = (FloatingActionButton) findViewById(R.id.openPersonalWorklist);
        openPersonalActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Tasklist.this, PersonalScreenActivity.class);
                startActivity(intent);
            }
        });

    }

// -------------------------------------------------------------------------------------------------
    /**
     * onStop method for tasklist Activity
     */
    protected void onStop(){
        super.onStop();
//        FirebaseAuth.getInstance().signOut();
    }

// -------------------------------------------------------------------------------------------------

    /**
     * Click event listener for Accept button
     * @param subWorkListButtonsOK Button reference
     * @param position Position of listItem
     * @param taskInfo Object of ListItem clicked contains all relevant info for the task
     */
    public void buttonOnClickOK(Button subWorkListButtonsOK, final int position, final TaskInfo taskInfo){

        // Get Key of parent node of the item list clicked
        final DatabaseReference itemRef = adapter.getRef(position);
        itemKey = itemRef.getKey();
        itemPosition = position;

        subWorkListButtonsOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            // Retrieve checkSelected flag from shared preference
            boolean checkSelected = Boolean.parseBoolean(statusCheck());

            if(!checkSelected){
                // Set userID field with userID and change inProgress value to yes
                mRef.child(itemKey).child("userID").setValue(getUserID());
                mRef.child(itemKey).child("inProgress").setValue("YES");

                // Change checkSelected flag in shared preference to true
                statusEdit("true");

                // Pass WorkList object to personalHomepage Activity and start Activity
                Intent i = new Intent(Tasklist.this, PersonalScreenActivity.class).
                        putExtra("taskObject", taskInfo);
                startActivity(i);

            }else{
                //Display error message to User
                Toast.makeText(Tasklist.this, "Please complete or cancel current task before " +
                        "accepting a new task", Toast.LENGTH_LONG).show();
            }
            toggleItem();
            }
        });
    }

// -------------------------------------------------------------------------------------------------
    /**
     * Click event listener for Decline Button
     * @param subWorkListButtonsNO
     * @param position int value position for listItem clicked
     */
    public void buttonOnClickNO(Button subWorkListButtonsNO, final int position){
        subWorkListButtonsNO.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            toggleItem(); //close the expanded item
            }
        });
    }

// ---------------------------------------------------------------------------------------------
    /**
     * Retrieves user specific ID from Firebase Authentication
     * @return User Id of User
     */
    public String getUserID() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userID = user.getUid();
        return userID;
    }
// -------------------------------------------------------------------------------------------------
    /**
     * Toggle list Item close and expand feature. If item is closed expand the view else v.v
     */
    public void toggleItem(){
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
            if ( subWorkListItem.getVisibility() == View.VISIBLE){
                subWorkListItem.setVisibility(View.GONE);
            }else{ subWorkListItem.setVisibility(View.VISIBLE);}
        } });
    }
// -------------------------------------------------------------------------------------------------
    /**
     * Check the variable stored in a shared preference file if the User has already accepted a Task
     * @return true if User has already accepted a task, false if not, or not found if file could
     * not be found.
     */
    public String statusCheck(){
        //Find shared preference file
        SharedPreferences prefs = getSharedPreferences(CHECK_STATUS, MODE_PRIVATE);
        //Find value by key, checkSelected
        String checkSelected = prefs.getString("checkSelected", "Not found");
        return checkSelected;
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
// -------------------------------------------------------------------------------------------------
    /**
     * Code to randomly generate work list tasks at random
     */
    public void populateDatabase(){

        for(int i=0; i<40; i++){
            int priority = (int)(Math.random()*5 + 1);
            int randomNum = (int)(Math.random()*15 + 1);
            String time = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

            DatabaseReference Task = mRef.child("Task " + i); // ID for each task

            DatabaseReference TaskID = Task.child("taskID"); //Patient name for each task
            TaskID.setValue("Task " + i);

            DatabaseReference ward = Task.child("ward"); // Ward Value for each task
            ward.setValue("Ward " + i);

            DatabaseReference PatientName = Task.child("patientName"); //Patient name for each task
            PatientName.setValue("PatientName " + (i+randomNum));

            DatabaseReference Destination = Task.child("destination"); //Destiantion for task
            Destination.setValue("Destination " + randomNum);

            DatabaseReference p = Task.child("priority"); //priority 1 - 5
            p.setValue(priority);

            DatabaseReference t = Task.child("timeStamp"); //Timestamp for each task
            t.setValue(time);

            DatabaseReference inProgress = Task.child("inProgress"); //In progress status
            inProgress.setValue("NO");

            DatabaseReference inProgressSince = Task.child("inProgressSince"); //In progress Since Time
            inProgressSince.setValue(0);

            DatabaseReference userID = Task.child("userID"); //In progress Since Time
            userID.setValue("0");

            DatabaseReference patientID = Task.child("patientID"); //In progress Since Time
            patientID.setValue(i);
        }
    }

// -------------------------------------------------------------------------------------------------
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
                break;
            case R.id.menu_my_page:
                Toast.makeText(this, "My Page", Toast.LENGTH_LONG).show();
                intent = new Intent(this, PersonalScreenActivity.class);
                startActivity(intent);
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
                Toast.makeText(this, MainActivity.mAuth.toString(), Toast.LENGTH_LONG).show();

                (MainActivity.mAuth).getInstance().signOut();
                startActivity(new Intent(Tasklist.this, MainActivity.class));
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
// -------------------------------------------------------------------------------------------------
}

