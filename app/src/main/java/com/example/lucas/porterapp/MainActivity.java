package com.example.lucas.porterapp;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Activity that is displayed when the app is launched.
 */

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    //public class MainActivity extends AppCompatActivity{
    private EditText mEmail;
    private EditText mPassword;
    private Button mLogin;
    public static FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private SensorManager sensorManager;
    private DatabaseHelper myDB;

    ImageView imgClick;

    // ---------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // intialize views for login feature
        mEmail = (EditText) findViewById(R.id.emailField);
        mPassword = (EditText) findViewById(R.id.passwordField);
        mLogin = (Button) findViewById(R.id.login);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // check and authenticate login details
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(MainActivity.this, Tasklist.class));
                    finish();
                }
            }
        };

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignIn();
            }
        });

        // creates a database manager
        myDB = new DatabaseHelper(this);
        //creates a sensor manager to access step detector
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void onResume() {
        super.onResume();
        // creates Sensor to monitor steps for pedometer feature
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    /**
     * Listens for a step and calls DB method to add to pedometer count
     */
    public void onSensorChanged(SensorEvent event) {
        //        System.out.println("!!!!!STEP!!!!!");
        myDB.countStep();
        //        if (activityRunning) {
        //            count.setText(String.valueOf(event.values[0]));
        //        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    // ---------------------------------------------------------------------------------------------

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        imgClick = (ImageView) findViewById(R.id.imageView);
        imgClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:011234567"));
                startActivity(intent);
            }
        });
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Assist user through a sign in process. displays toast if required information are not provided
     */
    private void startSignIn() {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(MainActivity.this, "Email required", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(MainActivity.this, "Password required", Toast.LENGTH_LONG).show();
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener < AuthResult > () {
                @Override
                public void onComplete(@NonNull Task < AuthResult > task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Sign in problem", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    // ---------------------------------------------------------------------------------------------


}