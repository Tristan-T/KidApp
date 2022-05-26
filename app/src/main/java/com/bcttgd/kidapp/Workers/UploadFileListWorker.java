package com.bcttgd.kidapp.Workers;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

public class UploadFileListWorker extends Worker {
    Context context;

    public UploadFileListWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        //Get Sharedreference device_id
        String deviceId = context.getSharedPreferences("device_id", MODE_PRIVATE).getString("device_id", "");

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://devmobilem1-default-rtdb.europe-west1.firebasedatabase.app");
        String reference = "users/" + mAuth.getUid() + "/devicesData/" + deviceId + "/storageMessage";
        DatabaseReference myRef = database.getReference(reference);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.getValue().equals("FAILURE") && !dataSnapshot.getValue().equals("SUCCESS")) {
                    Log.d(TAG, "onDataChange: " + dataSnapshot.getValue());
                    //Get file from path
                    String path = dataSnapshot.getValue().toString();
                    //Upload file to firebase
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    StorageReference fileRef = storageRef.child(deviceId + "/" + path);
                    Uri file = Uri.fromFile(new File("/storage/" + path));
                    UploadTask uploadTask = fileRef.putFile(file);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            myRef.setValue("FAILURE");
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            myRef.setValue("SUCCESS");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Files", "Failed to read value.", databaseError.toException());
            }
        });
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
