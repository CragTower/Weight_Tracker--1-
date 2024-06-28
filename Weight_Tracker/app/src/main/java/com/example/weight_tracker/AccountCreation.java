package com.example.weight_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AccountCreation extends AppCompatActivity {

    // Variable declaration
    EditText username;
    EditText password;
    EditText repassword;
    TextView usernameDup;
    Button createAccount;
    AppDatabase DB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_creation);

        // Variable initialization
        username = (EditText) findViewById(R.id.usernameCreate);
        password = (EditText) findViewById(R.id.passwordCreate);
        repassword = (EditText) findViewById(R.id.repasswordCreate);
        usernameDup = (TextView) findViewById(R.id.duplicateUsernameAlert);
        createAccount = (Button) findViewById(R.id.createAccountButton);
        DB = new AppDatabase(this);

        // Create account button listener
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Gets information from edit text boxes
                String user = username.getText().toString();
                String pass = password.getText().toString();
                String repass = repassword.getText().toString();

                // If any of the edit text boxes are empty, prompt the user
                if (user.equals("") || pass.equals("") || repass.equals(""))
                    Toast.makeText(AccountCreation.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                else {
                    // Checks if username already exists
                    boolean checkUsername = DB.findUsername(user);
                    // If username already exists, prompt the user
                    if (checkUsername)
                        Toast.makeText(AccountCreation.this, "Username already exists", Toast.LENGTH_SHORT).show();
                    else {
                        // If both password boxes don't match, prompt user
                        if (!pass.equals(repass))
                            Toast.makeText(AccountCreation.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                        else {
                            // Add new username and password to database, then start MainPage activity
                            DB.addUsernamePassword(user, pass);
                            Intent intent = new Intent(getApplicationContext(), MainPage.class);
                            intent.putExtra("username", user);
                            startActivity(intent);
                        }
                    }
                }
            }
        });
    }
}
