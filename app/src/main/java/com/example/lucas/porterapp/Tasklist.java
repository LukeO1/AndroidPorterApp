package com.example.lucas.porterapp;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
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

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * TaskList Activity
 */
public class Tasklist extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private DatabaseReference mRef;
    private ListView listView;
    private FirebaseListAdapter adapter;
    private ConstraintLayout subWorkListItem;
    private String itemKey;
    private String CHECK_STATUS = "com.example.lucas.porterapp.StatusCheck", filterBy;
    private SharedPreferences.Editor editor;
    private FirebaseAuth mAuth;
    private int itemPosition;
    private Spinner firebaseFilterSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasklist);
        getSupportActionBar().setTitle("Work List");

        // UnComment to refresh and populate the db with randomly generated tasks
        // populateDatabase();

        // Checks if a sharedPrefFile exist on the android device
        checkIfSharedPrefFileExists();

        // Connection to Firebase
        mRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://porterapp-3178d.firebaseio.com/Tasks");

        // Check if user already exist in db with a task accepted
        checkIfUserExistInDB();

        // Query DataBase for all tasks that are not already taken and populate the list
        Query queryRef = mRef.orderByChild("inProgress").equalTo("NO");

        // Populate WorkList with task from DB
        listView = (ListView) findViewById(R.id.listview_worklist);
        firebasePopulateView(queryRef);

        // Spinner for filtering by ward
        filterByWardSpinner();

        // Click event for list items
        listViewEventListener();

        FloatingActionButton openPersonalActivity = (FloatingActionButton) findViewById(R.id.openPersonalWorklist);
        openPersonalActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Tasklist.this, PersonalScreenActivity.class);
                startActivity(intent);
            }
        });

    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Checks if the shared pref file exist to store the boolean value checkSelected. If not create it
     */
    public void checkIfSharedPrefFileExists(){
        // Checks if a sharedPrefFile exist on the android device. Creates if not already exist
        boolean checkFileExists = Boolean.parseBoolean(statusCheck());
        if (!checkFileExists) {
            editor = getSharedPreferences(CHECK_STATUS, MODE_PRIVATE).edit();
            editor.putString("checkSelected", "false").apply();
        }
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * This method checks the database to check the status of the user in the db. If the database has
     * updated where the task if set back to No after 15. This method will set the checkSelected value
     * to false to allow to task accepting again
     */
    public void checkIfUserExistInDB(){

        // Query the DB for the specific instance of userID
        Query queryRefCheck = mRef.orderByChild("userID").equalTo(getUserID());
        readData(queryRefCheck, new OnGetDataListener() {

            @Override
            public void onSucess(DataSnapshot dataSnapshot) {
                boolean checkSelected = Boolean.parseBoolean(statusCheck());
                if (dataSnapshot.exists()){}
                else {
                    if(checkSelected){
                        statusEdit("false");
                    }
                }
            }

            @Override
            public void onStart() {}

            @Override
            public void onFailure() {}
        });
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
     * Generates a spinner to help user filter through all item according to the ward selected.
     * Updates the list per filter
     */
    public void filterByWardSpinner(){

        // Spinner for filtering by ward
        firebaseFilterSpinner = (Spinner) findViewById(R.id.firebaseFilterSpinner);
        ArrayAdapter<CharSequence> SpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.firebase_filter_array, android.R.layout.simple_spinner_item);

        SpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        firebaseFilterSpinner.setAdapter(SpinnerAdapter);
        firebaseFilterSpinner.setOnItemSelectedListener(this);
    }
    // ---------------------------------------------------------------------------------------------
    /**
     * Click event listener for each item in the list view. Expands the item on click to display
     * more information per task
     */
    public void listViewEventListener(){
        // Click event for list items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view,
                                    final int position, long id) {

                subWorkListItem = (ConstraintLayout) view.findViewById(R.id.subWorkListItem);
                toggleItem(); // expands and contracts the listItem to show more or less info on click

                Button subWorkListButtonsOK = (Button) view.findViewById(R.id.subWorkListButtonsOK);
                Button subWorkListButtonsNO = (Button) view.findViewById(R.id.subWorkListButtonsNO);

                TaskInfo x = (TaskInfo) listView.getItemAtPosition(position);

                // Event listeners for button Ok and No within the list
                buttonOnClickOK(subWorkListButtonsOK, position, x);
                buttonOnClickNO(subWorkListButtonsNO, position);

            }
        });
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * populate the work list view according to the query
     * @param queryRef query for the database
     */
    public void firebasePopulateView(Query queryRef) {

        adapter = new FirebaseListAdapter<TaskInfo>(this, TaskInfo.class, R.layout.row_layout, queryRef) {
            @Override
            protected void populateView(View v, TaskInfo model, int position) {

                // call method to set specific values to TextViews
                populateItemView(model,v);
            }
        };
        listView.setAdapter(adapter);

    }

// -------------------------------------------------------------------------------------------------
    /**
     * Populate each item in the work list view
     * @param model the task object to populate the item with
     * @param v the View
     */
    public void populateItemView(TaskInfo model, View v){
        Typeface font = Typeface.createFromAsset( getAssets(), "fontawesome-webfont.ttf" );

        TextView workListOriginView = (TextView) v.findViewById(R.id.workListOriginView);
        TextView workListDestinationView = (TextView) v.findViewById(R.id.workListDestinationView);
        TextView workListTimerView = (TextView) v.findViewById(R.id.workListTimerView);
        TextView workListPatientNameView = (TextView) v.findViewById(R.id.workListPatientNameView);
        TextView workListPatientIDView = (TextView) v.findViewById(R.id.workListPatientIDView);
        TextView workListTransportModeView = (TextView) v.findViewById(R.id.workListTransportModeView);

        // icons
        String iconWheelchair = getString(R.string.icon_wheelchair);
        String iconBed = getString(R.string.icon_bed);
        String iconWalking = getString(R.string.icon_walking);
        String iconArrow = getString(R.string.icon_right_arrow);
        String iconEllipsis = getString(R.string.icon_ellipsis);

        ImageView timerIcon = (ImageView) v.findViewById(R.id.iconTimerView);

        workListOriginView.setText("From " + model.getWard() + " " + iconArrow);
        workListDestinationView.setText(iconEllipsis + " " + iconArrow+ " To " + model.getDestination());
        workListTimerView.setText(model.getMinutes()+" Mins");
        workListPatientIDView.setText("Patient: " + model.getPatientID());
        workListPatientNameView.setText("Patient Name: " + model.getPatientName());

        // Set Icon for transport moder
        if(model.getTransportMode() == 1){
            workListTransportModeView.setText(iconWheelchair);
        }else if(model.getTransportMode() == 2){
            workListTransportModeView.setText(iconBed);
        }else if(model.getTransportMode() == 3){
            workListTransportModeView.setText(iconWalking);
        }

        // Set icon colour for priority
        if(model.getPriority() == 1){
            DrawableCompat.setTint(timerIcon.getDrawable(),
                    ContextCompat.getColor(getApplicationContext(), R.color.colourTimer1));
        }else if(model.getPriority() == 2){
            DrawableCompat.setTint(timerIcon.getDrawable(),
                    ContextCompat.getColor(getApplicationContext(), R.color.colourTimer2));
        }else{
            DrawableCompat.setTint(timerIcon.getDrawable(),
                    ContextCompat.getColor(getApplicationContext(), R.color.colourTimer3));
        }

        workListOriginView.setTypeface(font);
        workListTransportModeView.setTypeface(font);
        workListDestinationView.setTypeface(font);
    }
// -------------------------------------------------------------------------------------------------

    /**
     * onStop method for tasklist Activity
     */
    protected void onStop() {
        super.onStop();
//        FirebaseAuth.getInstance().signOut();
    }

// -------------------------------------------------------------------------------------------------
    /**
     * Click event listener for Accept button
     *
     * @param subWorkListButtonsOK Button reference
     * @param position             Position of listItem
     * @param taskInfo             Object of ListItem clicked contains all relevant info for the task
     */
    public void buttonOnClickOK(Button subWorkListButtonsOK, final int position, final TaskInfo taskInfo) {

        // Get Key of parent node of the item list clicked
        final DatabaseReference itemRef = adapter.getRef(position);
        itemKey = itemRef.getKey();
        itemPosition = position;

        subWorkListButtonsOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Retrieve checkSelected flag from shared preference
                boolean checkSelected = Boolean.parseBoolean(statusCheck());

                if (!checkSelected) {

                    // Set userID field with userID and change inProgress value to yes
                    mRef.child(itemKey).child("userID").setValue(getUserID());
                    mRef.child(itemKey).child("inProgress").setValue("YES");
                    mRef.child(itemKey).child("searchValue").setValue(taskInfo.getWard()+"_YES");
                    mRef.child(itemKey).child("inProgressSince").setValue(System.currentTimeMillis());

                    // Change checkSelected flag in shared preference to true
                    statusEdit("true");

                    // Pass WorkList object to personalHomepage Activity and start Activity
                    Intent i = new Intent(Tasklist.this, PersonalScreenActivity.class).
                            putExtra("taskObject", taskInfo);
                    startActivity(i);

                } else {
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
     *
     * @param subWorkListButtonsNO
     * @param position             int value position for listItem clicked
     */
    public void buttonOnClickNO(Button subWorkListButtonsNO, final int position) {
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
    public void toggleItem() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            if (subWorkListItem.getVisibility() == View.VISIBLE) {
                subWorkListItem.setVisibility(View.GONE);
            } else {
                subWorkListItem.setVisibility(View.VISIBLE);
            }
            }
        });
    }
// -------------------------------------------------------------------------------------------------
    /**
     * Check the variable stored in a shared preference file if the User has already accepted a Task
     * @return true if User has already accepted a task, false if not, or not found if file could
     * not be found.
     */
    public String statusCheck() {
        //Find shared preference file
        SharedPreferences prefs = getSharedPreferences(CHECK_STATUS, MODE_PRIVATE);
        //Find value by key, checkSelected
        String checkSelected = prefs.getString("checkSelected", "Not found");
        return checkSelected;
    }

    /**
     * Edits a value stored in the shared preference files. The value represents true if use has
     * already accpeted a task, or false if user has not
     *
     * @param status the status true or false to change in the file
     */
    public void statusEdit(String status) {
        editor = getSharedPreferences(CHECK_STATUS, MODE_PRIVATE).edit();
        editor.putString("checkSelected", status).apply();
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
        switch (item.getItemId()) {
            case R.id.menu_tasklist:
                break;
            case R.id.menu_my_page:
                intent = new Intent(this, PersonalScreenActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_contacts:
                intent = new Intent(this, PhoneDirectory.class);
                startActivity(intent);
                break;
            case R.id.menu_settings:
                intent = new Intent(this, settingspage.class);
                startActivity(intent);
                break;
            case R.id.menu_about:
                startActivity(new Intent(this, About.class));
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * This method helps the filter spinner requests data from the database and populates the list
     * view accordingly
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String filterSelected = firebaseFilterSpinner.getSelectedItem().toString();
        Context context = getApplicationContext();

        switch (filterSelected) {
            case ("↓ View by Ward ↓"): {
                Query queryRef = mRef.orderByChild("inProgress").equalTo("NO");
                firebasePopulateView(queryRef);
                break;
            }
            case ("St. John Ward"): {
                filterBy = "St. John Ward";
                Query queryRef = mRef.orderByChild("searchValue").equalTo(filterSelected + "_NO");
                firebasePopulateView(queryRef);
                break;
            }
            case ("Helens Ward"): {
                filterBy = "Helens Ward";
                Query queryRef = mRef.orderByChild("searchValue").equalTo(filterSelected + "_NO");
                firebasePopulateView(queryRef);
                break;
            }
            case ("Herbert Ward"): {
                filterBy = "Herbert Ward";
                Query queryRef = mRef.orderByChild("searchValue").equalTo(filterSelected + "_NO");
                firebasePopulateView(queryRef);
                break;
            }
            case ("St. Kevins Ward"): {
                filterBy = "St. Kevins Ward";
                Query queryRef = mRef.orderByChild("searchValue").equalTo(filterSelected + "_NO");
                firebasePopulateView(queryRef);
                break;
            }
            case ("Rialto Ward"): {
                filterBy = "Rialto Ward";
                Query queryRef = mRef.orderByChild("searchValue").equalTo(filterSelected + "_NO");
                firebasePopulateView(queryRef);
                break;
            }
            case ("Mary Mercers Ward"): {
                filterBy = "Mary Mercers Ward";
                Query queryRef = mRef.orderByChild("searchValue").equalTo(filterSelected + "_NO");
                firebasePopulateView(queryRef);
                break;
            }
            case ("John Houstons Ward"): {
                filterBy = "John Houstons Ward";
                Query queryRef = mRef.orderByChild("searchValue").equalTo(filterSelected + "_NO");
                firebasePopulateView(queryRef);
            }
            case ("Adams Ward"): {
                filterBy = "Adams Ward";
                Query queryRef = mRef.orderByChild("searchValue").equalTo(filterSelected + "_NO");
                firebasePopulateView(queryRef);
            }
        }
    }

    @Override
    public void onNothingSelected (AdapterView < ? > parent){}
    // ---------------------------------------------------------------------------------------------


// -------------------------------------------------------------------------------------------------
    /**
     * Code to randomly generate work list tasks at random
     */
    public void populateDatabase() {

        for (int i = 0; i < 40; i++) {

            String[] names = {
                    "Jack", "Emily", "James", "Emma","Daniel","Ava", "Conor", "Sophie", "Sean","Amelia",
                    "Adam", "Ella", "Noah","Lucy", "Michael","Grace", "Charlie", "Chloe", "Luke","Mia", "Thomas",
                    "Lily", "Oisin","Hannah","Alex","Aoife","Cian", "Anna","Harry",	"Olivia","Patrick",
                    "Sarah","Dylan", "Kate", "Ryan", "Saoirse","Fionn","Lauren", "Liam","Caoimhe",
                    "Darragh","Sophia","Cillian", "Katie","David","Robyn","Jamie","Ellie", "Jake",
                    "Roisin","Aaron","Isabelle","John","Holly", "Ben","Ciara", "Finn", "Zoe", "Oliver",
                    "Nathan","Freya", "Kyle","Leah","Evan","Amy","Tom","Cara","Sam","Jessica", "Mason",
                    "Eva", "Ethan","Alice","Samuel","Sofia","Joseph","Niamh","Callum","Rachel","Max",
                    "Faye","Oscar","Erin","Alexander", "Isabella", "Eoin", "Layla","Joshua", "Eve",
                    "Jacob","Laura","Bobby"};

            String[] wards = {"St. John Ward", "Helens Ward", "Herbert Ward", "St. Kevins Ward",
                    "Rialto Ward", "Mary Mercers Ward", "John Houstons Ward", "Adams Ward"};

            Random r = new Random();
            char c = (char)(r.nextInt(26) + 'a');
            c = Character.toUpperCase(c);

            int priority = (int) (Math.random() * 3 + 1);
            int randomNum = (int) (Math.random() * 15 + 1);
            int wardNum = (int) (Math.random() * 7 + 1);
            int destNum = (int) (Math.random() * 7 + 1);
            int destTransportModeNum = (int) (Math.random() * 3 + 1);
            int nameNum = (int) (Math.random() * names.length);
            String wardID = wards[wardNum-1];
            String destID = wards[destNum-1];

            String time = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

            DatabaseReference Task = mRef.child("Task " + i); // ID for each task

            DatabaseReference TaskID = Task.child("taskID"); //Patient name for each task
            TaskID.setValue("Task " + i);

            DatabaseReference ward = Task.child("ward"); // Ward Value for each task
            ward.setValue(wardID);

            DatabaseReference PatientName = Task.child("patientName"); //Patient name for each task
            PatientName.setValue(names[nameNum] +" "+ c+".");

            DatabaseReference Destination = Task.child("destination"); //Destiantion for task
            Destination.setValue(destID);

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
            patientID.setValue(100 + i + "");

            DatabaseReference searchValue = Task.child("searchValue"); //In progress Since Time
            searchValue.setValue(wardID + "_NO");

            DatabaseReference transportMode = Task.child("transportMode"); //In progress Since Time
            transportMode.setValue(destTransportModeNum);
        }
    }
//--------------------------------------------------------------------------------------------------
}