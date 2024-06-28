package com.example.weight_tracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainPage extends AppCompatActivity {

    // Variable declaration
    String username;
    EditText dailyWeight;
    EditText goalWeight;
    Button dailyWeightSubmit;
    Button goalWeightSubmit;
    ListView weightHistory;
    TextView goalWeightDisplay;
    Button delete;
    AppDatabase DB;
    ListAdapter adapter;

    private static final int SMS_PERMISSION_CODE = 193;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        // Collects username from login screen
        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        // Variable initialization
        dailyWeight = (EditText) findViewById(R.id.dailyWeightText);
        goalWeight = (EditText) findViewById(R.id.goalWeightText);
        dailyWeightSubmit = (Button) findViewById(R.id.dailyWeightButton);
        goalWeightSubmit = (Button) findViewById(R.id.goalWeightButton);
        delete = (Button) findViewById(R.id.deleteEntry);
        weightHistory = (ListView) findViewById(R.id.weightHistory);
        goalWeightDisplay = (TextView) findViewById(R.id.goalWeightDisplay);
        DB = new AppDatabase(this);

        // On click listener assignment
        dailyWeightSubmit.setOnClickListener(dailyWeightSubmitListener);
        goalWeightSubmit.setOnClickListener(goalWeightSubmitListener);

        // Sets up List View adapter for daily weight list display
        adapter = getAdapter();
        weightHistory.setAdapter(adapter);
        goalWeightDisplay.setText(DB.getGoalWeight(username));
    }

    // On click listener setup for daily weight submit button
    private View.OnClickListener dailyWeightSubmitListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dlyWeight = dailyWeight.getText().toString();
                // If daily weight edit text is empty, prompt user
                if (dlyWeight.equals(""))
                    Toast.makeText(MainPage.this, "Please enter a weight", Toast.LENGTH_SHORT).show();
                else {
                    // Checks if daily weight was entered properly
                    boolean success = DB.addDailyWeight(username, dlyWeight);
                    if (success) {
                        Toast.makeText(MainPage.this, "Successfully added daily weight", Toast.LENGTH_SHORT).show();
                        /*if (Float.parseFloat(dlyWeight) <= Float.parseFloat(goalWeightDisplay.getText().toString())) {
                            String permission = Manifest.permission.SEND_SMS;
                            if (ContextCompat.checkSelfPermission(MainPage.this, permission) == PackageManager.PERMISSION_GRANTED) {
                                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                                sendIntent.putExtra("sms_body", "default content");
                                sendIntent.setType("vnd.android-dir/mms-sms");
                                startActivity(sendIntent);
                            }
                        }*/
                    } else
                        Toast.makeText(MainPage.this, "Failed to add daily weight", Toast.LENGTH_SHORT).show();
                }
                // Clears any text in daily weight edit text
                dailyWeight.setText("");

                // Updates list view display
                adapter = getAdapter();
                weightHistory.setAdapter(adapter);
            }
    };

    // On click listener setup for goal weight submit button
    private View.OnClickListener goalWeightSubmitListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String golWeight = goalWeight.getText().toString();
            // If daily weight edit text is empty, prompt user
            if (golWeight.equals(""))
                Toast.makeText(MainPage.this, "Please enter a weight", Toast.LENGTH_SHORT).show();
            else {
                // Clears any text in goal weight edit text
                goalWeight.setText("");
                // Checks if goal weight was entered properly
                boolean success = DB.addGoalWeight(username, golWeight);
                if (success) {
                    Toast.makeText(MainPage.this, "Successfully added goal weight", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(MainPage.this, "Failed to add goal weight", Toast.LENGTH_SHORT).show();
            }
            // Sets the goal weight text box with entry in goal weight table
            goalWeightDisplay.setText(DB.getGoalWeight(username));
        }
    };

    // Adapter setup for list view
    public ListAdapter getAdapter() {
        // Creates new array list with daily weight table
        ArrayList<HashMap<String, String>> weightList = DB.getDailyWeight(username);

        // Loops through array list to setup adapter
        ListAdapter adptr = new SimpleAdapter(MainPage.this, weightList, R.layout.list_view,
                new String[]{"weight", "timestamp"}, new int[]{R.id.dailyWeightGrid, R.id.timestampGrid})
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                // Sets up on click listener for each delete button to each view in List View
                Button b = (Button) v.findViewById(R.id.deleteEntry);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Collects the timestamp from the view that the delete button is attached to
                        String d = weightList.get(position).get("timestamp");

                        // Deletes entry and checks for it's success
                        boolean success = DB.deleteEntry(d);
                        if (success) {
                            Toast.makeText(MainPage.this, "Delete successful", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(MainPage.this, "Delete unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                        // Updates List View
                        adapter = getAdapter();
                        weightHistory.setAdapter(adapter);
                    }
                });
                return v;
            }
        };
        return adptr;
    }
}
