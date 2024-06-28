// Justin Holmes
// CS-360 Mobile App Development
// Weight Tracker App

package com.example.weight_tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Variable declaration
    EditText username;
    EditText password;
    Button loginBtn;
    Button acctCreateBtn;
    AppDatabase DB;

    private static final int SMS_PERMISSION_CODE = 193;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Variable initialization
        username = (EditText) findViewById(R.id.loginUsername);
        password = (EditText) findViewById(R.id.loginPassword);
        loginBtn = (Button) findViewById(R.id.loginButton);
        acctCreateBtn = (Button) findViewById(R.id.createNewAccountBtn);
        DB = new AppDatabase(this);

        checkPermission(Manifest.permission.SEND_SMS, SMS_PERMISSION_CODE);

        // On click listener for log in button
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = password.getText().toString();

                // If username or password edit text are empty prompt user
                if (user.equals("") || pass.equals(""))
                    Toast.makeText(MainActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                else {
                    boolean usernameExists = DB.findUsername(user);
                    // If username doesn't exist in table prompt user
                    if (!usernameExists)
                        Toast.makeText(MainActivity.this, "Username does not exist", Toast.LENGTH_SHORT).show();
                    else {
                        boolean checkUsernamePassword = DB.findUsernamePassword(user, pass);
                        // If username and password don't match in table prompt user
                        if (!checkUsernamePassword)
                            Toast.makeText(MainActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                        else {
                            // Switch to main page activity if username and password match
                            Intent intent = new Intent(getApplicationContext(), MainPage.class);
                            intent.putExtra("username", user);
                            startActivity(intent);
                        }
                    }
                }
            }
        });

        // On click listener for account creation button
        acctCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Switch to account creation activity
                Intent intent = new Intent(getApplicationContext(), AccountCreation.class);
                startActivity(intent);
            }
        });
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {permission}, requestCode);
        }
        else {
            Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT) .show();
            }
            else {
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }
    }
}