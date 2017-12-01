package com.example.lucas.porterapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * PhoneDirectory class that displays a list of contacts that are stored in the firebase database.
 * Displays the Ward Name and a button which when clicked on, passes the telephone number of
 * the ward to the dialler app of the Android device.
 */
public class PhoneDirectory extends AppCompatActivity {
    // listview and database reference variables
    private ListView phoneDirectory;
    private DatabaseReference phoneDirectoryDatabase;


    // ---------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // onCreate method called when the activity created
        setContentView(R.layout.activity_phone_directory);

        // set the activity title
        getSupportActionBar().setTitle("Phone Directory");

        // reference listview in xml
        phoneDirectory = (ListView)findViewById(R.id.listview_phone_directory);

        // connect to phone directory table in firebase
        // reference: https://firebase.google.com/docs/reference/android/com/google/firebase/database/FirebaseDatabase
        phoneDirectoryDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://porterapp-3178d.firebaseio.com/phoneDirectory");

        // pass the object class, layout xml and database reference to firebase
        // reference: https://firebase.google.com/docs/reference/android/com/google/firebase/database/FirebaseDatabase
        ListAdapter adapter = new FirebaseListAdapter<PhoneDirectoryInfo>(this, PhoneDirectoryInfo.class, R.layout.listview_phone_directory, phoneDirectoryDatabase) {
            @Override
            protected void populateView(View v, final PhoneDirectoryInfo model, int position) {
                // populate the phone directory listview using the data from the firebase adapter

                // link to buttons and textview to xml
                TextView phoneDirectoryWard = (TextView) v.findViewById(R.id.wardName);
                TextView phoneDirectoryWardFloor = (TextView) v.findViewById(R.id.floorNumber);
                ImageButton wardPhoneButton = (ImageButton) v.findViewById(R.id.callButton);


                // use the getter methods to populate the listview
                phoneDirectoryWard.setText(model.getWardName());
                phoneDirectoryWardFloor.setText("Floor: " + model.getFloorNumber());

                // launches the phone application when the call button is clicked
                // reference: https://stackoverflow.com/questions/11699819/how-do-i-get-the-dialer-to-open-with-phone-number-displayed
                wardPhoneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + model.getPhoneNumber()));
                        startActivity(intent);
                    }
                });

                };

        };
        phoneDirectory.setAdapter(adapter);

    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // passes the user to the selected activity using the intents below and switch case statements
        Intent intent;
        switch(item.getItemId()) {
            case R.id.menu_tasklist:
                intent = new Intent(this, Tasklist.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.menu_my_page:
                intent = new Intent(this, PersonalScreenActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_contacts:
                break;
            case R.id.menu_settings:
                intent = new Intent(this, settingspage.class);
                startActivity(intent);
                break;
            case R.id.menu_about:
                startActivity(new Intent(this, About.class));
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    // ---------------------------------------------------------------------------------------------
}
