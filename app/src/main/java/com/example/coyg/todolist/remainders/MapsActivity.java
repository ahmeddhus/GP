package com.example.coyg.todolist.remainders;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;

import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;

import com.example.coyg.todolist.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener
{

    private GoogleMap mMap;
    private GoogleMapOptions options;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;

    private GeofencingClient geofencingClient;
    private List<Geofence> geofenceList = new ArrayList<> ();
    private PendingIntent geofencePendingIntent;

    private static final float GEOFENCE_RADIUS = 50; // 50 meters
    private static final long GEOFENCE_TIMEOUT = 24 * 60 * 60 * 1000; //24 hours

    private LatLng latLngMain;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager ()
                .findFragmentById (R.id.map);
        mapFragment.getMapAsync (this);

        locationManager = (LocationManager) getSystemService (Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission (this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission (this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            getLocationPermission();

        locationManager.requestLocationUpdates (LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE,
                this);

        geofencingClient = LocationServices.getGeofencingClient (this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission (this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            getLocationPermission();

        mMap.setMyLocationEnabled (true);
        mMap.getUiSettings ().setMyLocationButtonEnabled (true);

        mMap.setOnMapClickListener (new GoogleMap.OnMapClickListener ()
        {
            @Override
            public void onMapClick(LatLng latLng)
            {
                mMap.clear ();
                mMap.addMarker (new MarkerOptions ().position (latLng)
                        .title ("-"));
                mMap.moveCamera (CameraUpdateFactory.newLatLng (latLng));

                latLngMain = latLng;
            }
        });
    }

    @Override
    public void onLocationChanged(Location location)
    {
        LatLng latLng = new LatLng (location.getLatitude (), location.getLongitude ());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom (latLng, 13);
        mMap.animateCamera (cameraUpdate);
        locationManager.removeUpdates (this);

    }


    public void AddGeofencebtn(View view)
    {
        addGeoence (latLngMain);

        if (ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            getLocationPermission();

        geofencingClient.addGeofences (getGeofencingRequest (), getGeofencePendingIntent ())
                .addOnSuccessListener (this, new OnSuccessListener<Void> ()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        Toast.makeText (MapsActivity.this, "ADDED", Toast.LENGTH_LONG).show ();
                    }
                })
                .addOnFailureListener (this, new OnFailureListener ()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText (MapsActivity.this, "FAILED", Toast.LENGTH_LONG).show ();
                    }
                });

        finish ();
    }

    private void addGeoence(LatLng latLng)
    {
        geofenceList.add(new Geofence.Builder()
                .setRequestId("ID")

                .setCircularRegion(
                        latLng.latitude,
                        latLng.longitude,
                        GEOFENCE_RADIUS)
                .setExpirationDuration(GEOFENCE_TIMEOUT)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());
        //| Geofence.GEOFENCE_TRANSITION_EXIT
    }

    private GeofencingRequest getGeofencingRequest()
    {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        //GEOFENCE_TRANSITION_EXIT if user exit
        //INITIAL_TRIGGER_ENTER if enter
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent()
    {
        if (geofencePendingIntent != null)
            return geofencePendingIntent;

        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        geofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return geofencePendingIntent;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    @Override
    public void onProviderEnabled(String provider)
    {

    }

    @Override
    public void onProviderDisabled(String provider)
    {
    }

    private void getLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            mLocationPermissionGranted = true;
        }
        else
            {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode)
        {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }
}
