package com.example.bpfol.weighty;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;
import java.util.List;


public class SetFence extends AppCompatActivity implements OnMapReadyCallback, OnMyLocationClickListener, OnMyLocationButtonClickListener, ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE =1;
    private boolean mPermissionDenied = false;
    private GeofencingClient geofencingClient;
    private Geofence geofence;
    private Circle circle;
    private List geofencingList = new LinkedList();
    private LatLng currentLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_fence);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geofencingClient = LocationServices.getGeofencingClient(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMapClickListener(this);
        enableMyLocation();

    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
//            currentLocation = new LatLng(location.latitude, location.longitude);
        }
        //get location and set myLocation
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

//    This is where you will update the actual geofence, and draw a circle on the map to show stuff.
    @Override
    public void onMapLongClick(LatLng latLng) {
        Toast.makeText(this, "long Click:\n" + latLng, Toast.LENGTH_SHORT).show();
        if(circle != null){
            circle.remove();
        }
        circle = mMap.addCircle(new CircleOptions()
            .center(latLng)
        .radius(5)
        .strokeColor(Color.RED)
        .strokeWidth(5)
        .fillColor(0x220000FF));

        new AlertDialog.Builder(this).setTitle("Set as home?")
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // build geofence
                                setGeofence(circle.getCenter());
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onMapClick(final LatLng latLng) {
//        check if in the circle. if it is, then ask if you want to set the geofence there
        double radius = circle.getRadius();
        LatLng circleCenter = circle.getCenter();
        Toast.makeText(this, "latLng: " + latLng + " and circle latlng: " + circleCenter, Toast.LENGTH_SHORT).show();
        if(latLng.latitude >= circleCenter.latitude -radius && latLng.longitude <= circleCenter.longitude + radius){
            if(latLng.longitude >= circleCenter.longitude - radius && latLng.latitude <=circleCenter.latitude+radius){
//                Do the stuff.

            }
        }
    }

//    private PendingIntent getGeofencePendingIntent() {
//        // Reuse the PendingIntent if we already have it.
//        if (geofencePendingIntent != null) {
//            return geofencePendingIntent;
//        }
//        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
//        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
//        // calling addGeofences() and removeGeofences().
//        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.
//                FLAG_UPDATE_CURRENT);
//        return geofencePendingIntent;
//    }

    public void setGeofence(LatLng latLng){
        geofencingList.add(new Geofence.Builder()
            .setRequestId("home")
            .setCircularRegion(latLng.latitude, latLng.longitude, 5)
            .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_MILLISECONDS)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
            .build());
//        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencingPendingIntent())
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofencingList);
        return builder.build();
    }

}

