
/**
 * Created by karlroe on 26/11/2017.
 */



package com.example.lucas.porterapp;

        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.support.annotation.NonNull;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ProgressBar;
        import android.widget.SeekBar;
        import android.widget.Switch;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;

public class settingspage  extends AppCompatActivity {

    private Button btnChangePassword, changePassword, logoutofapp;
    private EditText password, newPassword;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingslayout);
        auth = FirebaseAuth.getInstance();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    finish();
                }
            }
        };

        btnChangePassword = (Button) findViewById(R.id.bChangePassword);

        password = (EditText) findViewById(R.id.etPassword);
        newPassword = (EditText) findViewById(R.id.etnewPassword);






        password.setVisibility(View.GONE);
        newPassword.setVisibility(View.GONE);
        changePassword = (Button) findViewById(R.id.button5);
        logoutofapp = (Button) findViewById(R.id.bLogout);
        changePassword.setVisibility(View.GONE);




        logoutofapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (MainActivity.mAuth).getInstance().signOut();
                startActivity(new Intent(settingspage.this, MainActivity.class));
                finish();
            }
        });


        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password.setVisibility(View.VISIBLE);
                newPassword.setVisibility(View.VISIBLE);
                changePassword.setVisibility(View.VISIBLE);


            }
        });
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null && !newPassword.getText().toString().trim().equals("")) {
                    if (newPassword.getText().toString().trim().length() < 6) {
                        newPassword.setError("Password too short, enter minimum 6 characters");
                    } else {
                        user.updatePassword(newPassword.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(settingspage.this, "Password is updated!", Toast.LENGTH_SHORT).show();

                                        } else {
                                            Toast.makeText(settingspage.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                } else if (newPassword.getText().toString().trim().equals("")) {
                    newPassword.setError("Enter password");
                }
            }
        });




    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
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
                Toast.makeText(this, "Dropdown", Toast.LENGTH_LONG).show();
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

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}






