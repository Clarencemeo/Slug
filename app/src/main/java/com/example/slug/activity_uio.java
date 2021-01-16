package com.example.slug;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class activity_uio extends AppCompatActivity   {
    final int DEFAULT_ZOOM = 20;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uio);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //The back button and imgbutton below manage the button listeners

        FloatingActionButton back = findViewById(R.id.backScreen);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(activity_uio.this, firstscreen_activity.class);
                //myIntent.putExtra("key", value); to store stuff
                activity_uio.this.startActivity(myIntent);
            }
        });

        Button imgbutton = findViewById(R.id.buttonimage);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if a picture was taken successfully
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //the below line will find the image with id "confirmation image"

            Bundle extras = data.getExtras();
            //data is what stores the picture that the user JUST took
            Bitmap imageBitmap = (Bitmap) extras.get("data");


            EditText textboxes   = (EditText)findViewById(R.id.textInputEditText);
            String textboxResult      =  textboxes.getText().toString();

            Intent myIntent = new Intent(activity_uio.this, confirmationscreen.class);
            //the line below this is what is responsible for transferring variables between activities
            //so, inside "text input", it will store the value from the textboxes variable
            //this text input variable can be transferred between activties through the myIntent thingy
            //Log.v("EditText", textboxes.getText().toString());
            if (textboxResult == null) {
                myIntent.putExtra("textInput", textboxResult);
            }
            else {
                myIntent.putExtra("textInput", "You didn't enter a description!");
            }
            //we are also going to store the picture that was taken and transfer it over onto the next activity, as seen below
            myIntent.putExtra("thumbnail", imageBitmap);

            //myIntent.putExtra("key", value); to store stuff
            activity_uio.this.startActivity(myIntent);
        }
    }



}