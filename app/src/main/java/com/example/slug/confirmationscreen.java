package com.example.slug;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class confirmationscreen extends AppCompatActivity {
    //location init
    private boolean locationPermissionGranted = true;
    private FusedLocationProviderClient getLocation;
    final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Location lastKnownLocation;

    //Firebase init
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference coordsRef = db.collection("coords");
    public static final String TAG = "DatabaseUpload";
    DocumentReference docWithCoords = coordsRef.document();

    public String textInputConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmationscreen);
        Toolbar toolbar = findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);

        //These lines retrieve the variables from the last activity
        Intent intent = getIntent();
        Bitmap imageBitmapTransferred = (Bitmap) intent.getParcelableExtra("thumbnail");
        ImageView imageView = findViewById(R.id.confirmationImage);
        imageView.setImageBitmap(imageBitmapTransferred);

        textInputConfirm = intent.getStringExtra("textInput");
        TextView textConf = (TextView) findViewById(R.id.textConf);
        textConf.setText(textInputConfirm);

        getLocation = LocationServices.getFusedLocationProviderClient(this);

        Button confirmButton = findViewById(R.id.greenButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //in here, before these two lines, is when I think we need to store the data onto a server and
                //communicate with the back end. Store the latitude, longitude, the image (which is going to be "imageBitmapTransferred"
                //, and the description (which will be stored in textInputConfirm)
                getLocationPermissionForDataUpload();
                getDeviceLocationForDataUpload();


                //the marker will be placed in the firstscreen_activity.java file

                //bottom two lines go back to the main page
                Intent myIntent = new Intent(confirmationscreen.this, firstscreen_activity.class);
                confirmationscreen.this.startActivity(myIntent);
            }
        });

        Button denyButton = findViewById(R.id.redButton);
        denyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(confirmationscreen.this, activity_uio.class);
                confirmationscreen.this.startActivity(myIntent);
            }
        });



    }


    public void getLocationPermissionForDataUpload() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    public void getDeviceLocationForDataUpload() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = getLocation.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                LatLng preciseLocation = new LatLng (lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

                                //Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                //       R.drawable.slugger);
                                //drawMarker(preciseLocation, getImageUri(getApplicationContext(), icon));

                                //Storing Latitude and Longitude to Firestore database
                                Map<String, Object> general = new HashMap<>();
                                general.put("longitude", lastKnownLocation.getLongitude());
                                general.put("latitude", lastKnownLocation.getLatitude());
                                general.put("description", textInputConfirm);

                                docWithCoords
                                        .set(general)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "Doc successfully Written");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error uploading to database");
                                            }
                                        });

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }



}