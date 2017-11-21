package com.example.lucas.porterapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private EditText mEmail;
    private EditText mPassword;
    private Button mLogin;
    public static FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private SensorManager sensorManager;
    private DatabaseHelper db;

    private TextView barcodeResult;

    // ---------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmail = (EditText) findViewById(R.id.emailField);
        mPassword = (EditText) findViewById(R.id.passwordField);
        mLogin = (Button) findViewById(R.id.login);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
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


        Button callTechnicalSupport = (Button) findViewById(R.id.techSupportButton);
        callTechnicalSupport.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                callTechnicalSupport(view);
            }
        });
        db = new DatabaseHelper(this);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);



    }

    @Override
    public void onResume(){
        super.onResume();
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        db.countStep();
//        if (activityRunning) {
//            count.setText(String.valueOf(event.values[0]));
//        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    // ---------------------------------------------------------------------------------------------

    private void startSignIn(){
        String email = mEmail.getText().toString();
        String password =mPassword.getText().toString();
        if(TextUtils.isEmpty(email)) {
            Toast.makeText(MainActivity.this, "Email required", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(password)) {
            Toast.makeText(MainActivity.this, "Password required", Toast.LENGTH_LONG).show();
        }
        else{
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Sign in problem", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    // ---------------------------------------------------------------------------------------------

    // called to initiate the barcode scanning
    public void scanBarcode(View v) {
        Intent intent = new Intent(this, BarcodeScanner.class);

        // receives the result from the BarcodeScanner activity
        startActivityForResult(intent, 0);
    }

    // ---------------------------------------------------------------------------------------------

    // get the result of the barcode back rom the BarcodeScanner
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0){
            if(resultCode == CommonStatusCodes.SUCCESS){
                if(data!=null){
                    Barcode barcode = data.getParcelableExtra("barcode");
                    barcodeResult.setText("Barcode: " + barcode.displayValue);
                }else{
                    barcodeResult.setText("No barcode detected from camera");
                }
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    // ---------------------------------------------------------------------------------------------

    public void callTechnicalSupport(View v){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:011234567" ));
        startActivity(intent);
    }

}
