package com.example.slug;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class firstscreen_activity extends AppCompatActivity implements OnMapReadyCallback {
    private boolean locationPermissionGranted = false;
    private GoogleMap mapper;
    private Location lastKnownLocation;
    private LatLng defaultLocation;
    private FusedLocationProviderClient getLocation;
    final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    final int DEFAULT_ZOOM = 20;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private int markerclicked;
    private Bitmap bmp = null;
    private Marker currentLocationMarker;
    static Date currentTime;

    //Firebase shiz
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference dataRef = db.collection("coords");
    DocumentReference docWithCoords = dataRef.document();
    public static final String TAG = "DatabaseUpload";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstscreen_activity);
        Toolbar toolbar2 = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar2);

        FloatingActionButton markerSetter = findViewById(R.id.setMarker);
        markerSetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //the line below this is responsible for moving to another activity, aka new screen
                Intent myIntent = new Intent(firstscreen_activity.this, activity_uio.class);
                firstscreen_activity.this.startActivity(myIntent);

            }
        });

        FloatingActionButton home = findViewById(R.id.backhome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();


            }
        });

        //the map fragment is the google map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

        // Initialize the SDK
        //obtains the api key and initializes the place
        Places.initialize(getApplicationContext(), getString(R.string.places_api));
        //Places.initialize(getApplicationContext(), "asdfdsafasdgadsgasdg");
        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

        getLocation = LocationServices.getFusedLocationProviderClient(this);

        defaultLocation = new LatLng(0, 0);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng latLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                mapper.addMarker(new MarkerOptions().position(latLng)
                        .title("Current Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_slug)));
                mapper.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }


            @Override
            public void onError(Status status) {
            }
        });



    }

    //All the below functions deal with the map, getting the location, etc.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapper = googleMap;

        updateLocationUI();

        getDeviceLocation();

        plotExistingMarkers();

        //I think this is where we could communicate with the database. We could go through
        //the database and put down the pins
    }

    //38.2295, -122.8051

    public void plotExistingMarkers() {
        //iterates over collection and adds a marker using the document's long/lat values
        dataRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                mapper.addMarker(new MarkerOptions()
                                        .position(new LatLng(document.getDouble("latitude"), document.getDouble("longitude")))
                                        .title("Marker")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_slug)));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

    private void getLocationPermission() {
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mapper == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mapper.setMyLocationEnabled(true);
                mapper.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mapper.setMyLocationEnabled(false);
                mapper.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
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
                                mapper.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                LatLng preciseLocation = new LatLng (lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                                mapper.addMarker(new MarkerOptions()
                                        .position(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()))
                                        .title("Current Location")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_slug)));
                                //Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                 //       R.drawable.slugger);
                                //drawMarker(preciseLocation, getImageUri(getApplicationContext(), icon));

                                //Storing Latitude and Longitude to Firestore database
                                Map<String, Object> general = new HashMap<>();
                                general.put("longitude", lastKnownLocation.getLongitude());
                                general.put("latitude", lastKnownLocation.getLatitude());

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
                            mapper.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            mapper.getUiSettings().setMyLocationButtonEnabled(false);
                            mapper.addMarker(new MarkerOptions()
                                    .position(new LatLng(defaultLocation.latitude, defaultLocation.longitude)) //edit later
                                    .title("Marker")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_slug)));
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    //this function is responsible for drawing the markers with images and putting
    //the circluar borders. We should use this every time we place a new pin down
    //from the database. HOWEVER, we might abandon this function and just stick
    //with the default slug pins since it is difficult to store images in a database.
    //For now, I would ignore this function.
    private void drawMarker(LatLng location, Uri imageurl) {
        Target mTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Marker driver_marker = mapper.addMarker(new MarkerOptions()
                        .position(location)
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                        .title("Current Location.")
                        .snippet("This is where you are. Hopefully there's some banana slugs around!")
                );
            }

            @Override
            public void onBitmapFailed(Exception ex, Drawable errorDrawable) {
                Log.d("picasso", "onBitmapFailed");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        Picasso.get()
                .load(imageurl)
                .resize(200,200)
                .centerCrop()
                .transform(new CircleBubbleTransformation())
                .into(mTarget);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        //String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "ThisIsImageTitleString" + " - " + (currentTime = Calendar.getInstance().getTime()), null);

        return Uri.parse(path);
    }





}