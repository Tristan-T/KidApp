package com.bcttgd.kidapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SelectDevice extends AppCompatActivity {
    private FirebaseAuth mAuth;
    ArrayList<String> devices = new ArrayList<>();
    ArrayList<String> devices_id = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_device);
        //Check if device_id preference has been set
        SharedPreferences sharedPref = getSharedPreferences("device_id", Context.MODE_PRIVATE);
        if (sharedPref.getString("device_id", "") != "") {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();

        //Get all devices registered for the user
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users/" + mAuth.getUid() + "/devices");
        //Get the list of devices
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                devices = new ArrayList<>();
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    devices.add(ds.getValue(String.class));
                    devices_id.add(ds.getKey());
                }

                Log.d("Devices", devices.toString());

                displayDevices();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SelectDevice.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayDevices() {
        //Get the ListView view
        ListView listView = (ListView) findViewById(R.id.list_view_device);

        //Create the adapter to convert the array to views
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, devices);

        //Attach the adapter to the list view
        listView.setAdapter(adapter);

        //Set the onClickListener
        listView.setOnItemClickListener((parent, view, position, id) -> {
            //Get the selected device
            String selectedDevice = devices.get(position);
            String selectedDeviceId = devices_id.get(position);

            //Open a dialog to confirm the selection
            AlertDialog.Builder builder = new AlertDialog.Builder(SelectDevice.this);
            builder.setTitle("Confirm device");
            builder.setMessage("Are you sure you want to select " + selectedDevice + "?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                //Save the device_id to preferences
                SharedPreferences sharedPref = getSharedPreferences("device_id", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("device_id", selectedDeviceId);
                editor.apply();

                //Also save the device name
                SharedPreferences sharedPref2 = getSharedPreferences("device_name", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor2 = sharedPref2.edit();
                editor2.putString("device_name", selectedDevice);
                editor2.apply();

                //Go to the main activity
                Intent intent = new Intent(SelectDevice.this, MainActivity.class);
                startActivity(intent);
                finish();
            });
            builder.setNegativeButton("No", null);
            builder.show();
        });

        //Set the onLongClickListener
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            //Get the selected device
            String selectedDevice = devices.get(position);
            String selectedDeviceId = devices_id.get(position);

            //Open a dialog to confirm the selection
            AlertDialog.Builder builder = new AlertDialog.Builder(SelectDevice.this);
            builder.setTitle("Confirm device");
            builder.setMessage("Are you sure you want to delete " + selectedDevice + "?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                //Delete the device
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + mAuth.getUid() + "/devices");
                ref.child(selectedDeviceId).removeValue();
            });
            builder.setNegativeButton("No", null);
            builder.show();
            displayDevices();
            return true;
        });
    }

    public void createDevice(View view) {
        //Generate unique ID for the device
        long deviceID = System.currentTimeMillis();

        //Check that deviceID is not part of the list of devices
        while(devices.contains(String.valueOf(deviceID))) {
            deviceID = System.currentTimeMillis();
        }

        //Create a material dialog asking the user for a name for the device
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Device Name");
        builder.setMessage("Enter a name for the device");
        builder.setCancelable(false);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        long finalDeviceID = deviceID;
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Get the name of the device
                String deviceName = input.getText().toString();

                //Create the device in the database
                DatabaseReference database = FirebaseDatabase.getInstance().getReference("users/" + mAuth.getUid() + "/devices");
                database.child(String.valueOf(finalDeviceID)).setValue(deviceName);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
