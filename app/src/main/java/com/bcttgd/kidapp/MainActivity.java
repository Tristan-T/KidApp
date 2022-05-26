package com.bcttgd.kidapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.widget.TextView;

import com.bcttgd.kidapp.Workers.UploadFileListWorker;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView deviceName = findViewById(R.id.device_name);

        //Get shared preferences for device name
        String deviceNameString = getSharedPreferences("device_name", MODE_PRIVATE).getString("device_name", "No device name");
        deviceName.setText(deviceNameString);

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest uploadFileListRequest = new PeriodicWorkRequest.Builder(UploadFileListWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(this).enqueue(uploadFileListRequest);

    }
}