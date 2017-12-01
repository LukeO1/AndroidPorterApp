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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PhoneDirectory extends AppCompatActivity {
    private ListView phoneDirectory;
    private DatabaseReference phoneDirectoryDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_directory);

        getSupportActionBar().setTitle("Phone Directory");

        // reference listview in xml
        phoneDirectory = (ListView)findViewById(R.id.listview_phone_directory);

        // connect to phone directory table in firebase
        phoneDirectoryDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://porterapp-3178d.firebaseio.com/phoneDirectory");

        // pass the object class, layout xml and database reference to firebase
        ListAdapter adapter = new FirebaseListAdapter<PhoneDirectoryInfo>(this, PhoneDirectoryInfo.class, R.layout.listview_phone_directory, phoneDirectoryDatabase) {
            @Override
            protected void populateView(View v, final PhoneDirectoryInfo model, int position) {

                // link to xml
                TextView phoneDirectoryWard = (TextView) v.findViewById(R.id.wardName);
                TextView phoneDirectoryWardFloor = (TextView) v.findViewById(R.id.floorNumber);
                ImageButton wardPhoneButton = (ImageButton) v.findViewById(R.id.callButton);


                // use the getter methods to populate the listview
                phoneDirectoryWard.setText(model.getWardName());
                phoneDirectoryWardFloor.setText("Floor: " + model.getFloorNumber());

                // launches the phone application when the call button is clicked
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
//            case R.id.menu_sign_out:
//                Toast.makeText(this, "Sign Out", Toast.LENGTH_LONG).show();
//                (MainActivity.mAuth).getInstance().signOut();
//                startActivity(new Intent(this, MainActivity.class));
//                finish();
//                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}
