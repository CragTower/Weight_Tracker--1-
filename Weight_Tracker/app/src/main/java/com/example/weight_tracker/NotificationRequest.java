package com.example.weight_tracker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class NotificationRequest extends AppCompatActivity {
    Button allow;
    Button deny;

    private static final int SMS_PERMISSION_CODE = 193;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.notification_request);

        allow = (Button) findViewById(R.id.allowButton);
        deny = (Button) findViewById(R.id.denyButton);

        allow.setOnClickListener(allowBtnListener);
        deny.setOnClickListener(denyBtnListener);

        checkPermission(Manifest.permission.SEND_SMS, SMS_PERMISSION_CODE);
    }

    private View.OnClickListener allowBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener denyBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    };

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(NotificationRequest.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(NotificationRequest.this, new String[] {permission}, requestCode);
        }
        else {
            Toast.makeText(NotificationRequest.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                                        permissions,
                                        grantResults);

        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(NotificationRequest.this, "Permission Granted", Toast.LENGTH_SHORT) .show();
            }
            else {
                Toast.makeText(NotificationRequest.this, "Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }
    }

}
