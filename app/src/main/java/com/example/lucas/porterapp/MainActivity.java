package com.example.lucas.porterapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private EditText mEmail;
    private EditText mPassword;
    private Button mLogin;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmail = (EditText)findViewById(R.id.emailField);
        mPassword = (EditText)findViewById(R.id.passwordField);
        mLogin = (Button)findViewById(R.id.login);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
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
    }
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
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
}
