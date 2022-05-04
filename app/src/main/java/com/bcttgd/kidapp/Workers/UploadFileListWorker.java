package com.bcttgd.kidapp.Workers;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;

public class UploadFileListWorker extends Worker {
    Context context;

    public UploadFileListWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        getData();

        // Indicate whether the work finished successfully with the Result
        return Result.success();
    }

    private void getData() {
        //For all external storage that are accessible
        getData("/storage/");
        //For internal storage
        getData(Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    private void getData(String folderName) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        //Get Sharedreference device_id
        String deviceId = context.getSharedPreferences("device_id", MODE_PRIVATE).getString("device_id", "");

        //Check if folderName contains '.', '#', '$', '[', or ']'
        if (folderName.contains(".") || folderName.contains("#") || folderName.contains("$") || folderName.contains("[") || folderName.contains("]")) {
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://devmobilem1-default-rtdb.europe-west1.firebasedatabase.app");
        String reference = "users/" + mAuth.getUid() + "/devicesData/" + deviceId + folderName;
        DatabaseReference myRef = database.getReference(reference);

        File home = new File(folderName);

        File[] files = home.listFiles();
        ArrayList<String> fileNames = new ArrayList<>();
        if(files != null) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    fileNames.add(file.getName());
                }
            }
            myRef.setValue(fileNames);
            for(File file : files) {
                if(file.isDirectory()) {
                    getData(file.getAbsolutePath());
                }
            }
        }
    }
}
