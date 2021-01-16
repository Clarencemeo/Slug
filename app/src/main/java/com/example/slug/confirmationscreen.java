package com.example.slug;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class confirmationscreen extends AppCompatActivity {
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

        String textInputConfirm = intent.getStringExtra("textInput");
        TextView textConf = (TextView) findViewById(R.id.textConf);
        textConf.setText(textInputConfirm);

        Button confirmButton = findViewById(R.id.greenButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //in here, before these two lines, is when I think we need to store the data onto a server and
                //communicate with the back end. Store the latitude, longitude, the image (which is going to be "imageBitmapTransferred"
                //, and the description (which will be stored in textInputConfirm)


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



}