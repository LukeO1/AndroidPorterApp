package com.example.lucas.porterapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Settings");
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
                Toast.makeText(this, "Tasklist", Toast.LENGTH_LONG).show();
                intent = new Intent(this, Tasklist.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
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
}
