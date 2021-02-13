package com.example.accesscurrentlocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    Button btnLocation;
    TextView tvLatitude, tvLongitude;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialisation
        btnLocation = findViewById(R.id.btnLocation);
        tvLatitude = findViewById(R.id.latitude);
        tvLongitude = findViewById(R.id.longitude);
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(MainActivity.this);

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check condition
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                    //When both permissions are granted then call below method
                    getLocation();
                }else{
                    //When permission not granted, request permission
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},100);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100 && grantResults.length > 0 && (grantResults[0] + grantResults[1] ==
                PackageManager.PERMISSION_GRANTED)){
            //When permisson granted call method
            getLocation();
        }else {
            Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG);
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        //Initialise the location manager
        LocationManager locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);

        //Check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
        //when location service is enabled
            // Get last location
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                //Initialise the location
                    Location location=task.getResult();
                    if (location!=null){
                        tvLatitude.setText(String.valueOf(location.getLatitude()));
                        tvLongitude.setText(String.valueOf(location.getLongitude()));
                    }else {
                        //When location is null
                        //Initialize the location request
                        LocationRequest locationRequest=new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);

                        //Initialise the location call back
                        LocationCallback locationCallback=new LocationCallback(){
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                //Initialise the location
                                Location location1=locationResult.getLastLocation();
                                //set latitude and longitude
                                tvLatitude.setText(String.valueOf(location1.getLatitude()));
                                tvLongitude.setText(String.valueOf(location1.getLongitude()));
                            }
                        };
                        //Request location updates
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
                    }
                }
            });
        }else{
            //when location service is not enabled
            //Open Location settings
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
}