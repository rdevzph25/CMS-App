package com.rdevzph.dpwhcms.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.rdevzph.dpwhcms.R;

public class MainActivity extends AppCompatActivity {
// Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.CAMERA
    };
    
    private Button complaintBtn;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        complaintBtn = (Button) findViewById(R.id.complaintBtn);

        complaintBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    verifyStoragePermissions(MainActivity.this);

                }
            });

        disclaimer();

    }

    private void disclaimer() {
        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("Disclaimer")
            .setMessage("This application was developed by a group of BSIT Students from Occidental Mindoro State College for Capstone Project purposes. \n\n© CMS 2022 All rights reserved.")
            .setPositiveButton("I Understand", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dia, int which) {


                }
            })
            .create();
        dialog.setCancelable(false);
        dialog.show();
    }

    public void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ContextCompat.checkSelfPermission(activity, 
                                                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ContextCompat.checkSelfPermission(activity, 
                                                               android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(activity, 
                                                                 android.Manifest.permission.CAMERA);

        if (writePermission != PackageManager.PERMISSION_GRANTED || 
            readPermission != PackageManager.PERMISSION_GRANTED || cameraPermission 
            != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            );
        } else {
            startActivity(new Intent(MainActivity.this, WebViewActivity.class));
            finish();
        }
    }


}

