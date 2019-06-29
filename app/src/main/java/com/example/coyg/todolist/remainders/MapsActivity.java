package com.example.coyg.todolist.remainders;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.coyg.todolist.R;

import com.example.coyg.todolist.database.AppDatabase;
import com.example.coyg.todolist.database.AppExecutors;
import com.example.coyg.todolist.database.RemainderEntry;
import com.example.coyg.todolist.database.TaskEntry;
import com.google.android.gms.common.api.Status;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener
{

    private GoogleMap mMap;
    private GoogleMapOptions options;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    int AUTOCOMPLETE_REQUEST_CODE = 1;
    private boolean mLocationPermissionGranted;
    public static final String LAT_SAVED = "lat";
    public static final String LNG_SAVED = "lng";
    public static final String TYPE_SAVED = "lng";
    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;

    private GeofencingClient geofencingClient;
    private List<Geofence> geofenceList = new ArrayList<> ();
    private PendingIntent geofencePendingIntent;

    private static final float GEOFENCE_RADIUS = 50; // 50 meters
    private static final long GEOFENCE_TIMEOUT = 24 * 60 * 60 * 1000; //24 hours

    private LatLng latLngMain;
    private  Intent intent;
    private String typeShared="enter";
    private AppDatabase mDb;
    private RemainderEntry remainderEntry;

    private Button addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager ()
                .findFragmentById (R.id.map);

        mapFragment.getMapAsync (this);

        intent = getIntent();
        typeShared = intent.getStringExtra("type");
        mDb = AppDatabase.getsInstance (getApplicationContext ());
        addBtn = findViewById (R.id.addBtn);


        locationManager = (LocationManager) getSystemService (Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission (this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission (this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            getLocationPermission();

        locationManager.requestLocationUpdates (LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE,
                this);

        geofencingClient = LocationServices.getGeofencingClient (this);


        if (!Places.isInitialized())
        {
            Places.initialize(getApplicationContext (), getApplicationContext ().getString(R.string.google_maps_key));
        }

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        if (autocompleteFragment != null)
        {
            autocompleteFragment.setPlaceFields(Collections.singletonList (Place.Field.LAT_LNG));

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener ()
            {
                @Override
                public void onPlaceSelected(@NonNull Place place)
                {
                    latLngMain = place.getLatLng ();
                    mMap.clear ();
                    mMap.addMarker (new MarkerOptions ().position (latLngMain).title ("-"));
                    mMap.moveCamera (CameraUpdateFactory.newLatLng (latLngMain));
                }

                @Override
                public void onError(@NonNull Status status)
                {
                    Toast.makeText (MapsActivity.this, "An error occurred: "+status, Toast.LENGTH_LONG).show ();
                }
            });
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission (this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            getLocationPermission();

        if(intent != null && intent.hasExtra (LAT_SAVED) && intent.hasExtra (LNG_SAVED))
        {
            double lat = intent.getDoubleExtra (LAT_SAVED, 0);
            double lng = intent.getDoubleExtra (LNG_SAVED, 0);

            LatLng lngSaved = new LatLng (lat, lng);

            mMap.addMarker (new MarkerOptions ().position (lngSaved).title ("-"));
            mMap.moveCamera (CameraUpdateFactory.newLatLng (lngSaved));

            latLngMain = lngSaved;
            //addBtn.setVisibility (View.GONE);
        }
        else
        {
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
    }

    @Override
    public void onLocationChanged(Location location)
    {
        LatLng latLng = new LatLng (location.getLatitude (), location.getLongitude ());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom (latLng, 20);
        mMap.animateCamera (cameraUpdate);
        locationManager.removeUpdates (this);
    }


    public void AddGeofencebtn(View view)
    {
        if(latLngMain == null)
        {
            Toast.makeText(MapsActivity.this, "CHOOSE PLACE", Toast.LENGTH_SHORT).show();
            return;
        }

        theDialog(latLngMain, typeShared);
    }

    private void addGeoence(LatLng latLng, String type)
    {
        if(type.equals("enter"))
            geofenceList.add(new Geofence.Builder()
                .setRequestId("ID")

                .setCircularRegion(
                        latLng.latitude,
                        latLng.longitude,
                        GEOFENCE_RADIUS)
                .setExpirationDuration(GEOFENCE_TIMEOUT)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build());

        else if(type.equals("exit"))
            geofenceList.add(new Geofence.Builder()
                    .setRequestId("ID")

                    .setCircularRegion(
                            latLng.latitude,
                            latLng.longitude,
                            GEOFENCE_RADIUS)
                    .setExpirationDuration(GEOFENCE_TIMEOUT)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());

    }

    private GeofencingRequest getGeofencingRequest(String type)
    {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        //GEOFENCE_TRANSITION_EXIT if user exit
        //INITIAL_TRIGGER_ENTER if enter
        if(type.equals("enter"))
            builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        else if(type.equals("exit"))
            builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT);


        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent()
    {
        if (geofencePendingIntent != null)
            return geofencePendingIntent;

//        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);

        Intent intent = new Intent(this, GeofenceTransService.class);

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

    public void onSaveButtonClicked(String name, double lat, double lng, String type)
    {
        remainderEntry = new RemainderEntry (name, lat, lng, type);
        AppExecutors.getInstance ().getDiskIO ().execute (new Runnable ()
        {
            @Override
            public void run()
            {
                mDb.remainderDAO().insertRemainder (remainderEntry);
                finish ();
            }
        });
    }

    private void theDialog(final LatLng latLng, final String type)
    {

        if(intent != null && intent.hasExtra (LAT_SAVED) && intent.hasExtra (LNG_SAVED))
        {
            String typeSaved = intent.getStringExtra (TYPE_SAVED);

            addGeoence (latLng, typeSaved);

            if (ActivityCompat.checkSelfPermission
                    (MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                getLocationPermission();

            geofencingClient.addGeofences (getGeofencingRequest (typeSaved), getGeofencePendingIntent ())
                    .addOnSuccessListener (MapsActivity.this, new OnSuccessListener<Void> ()
                    {
                        @Override
                        public void onSuccess(Void aVoid)
                        {

                            Toast.makeText (MapsActivity.this, "ADDED", Toast.LENGTH_LONG).show ();
                        }
                    })
                    .addOnFailureListener (MapsActivity.this, new OnFailureListener ()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast.makeText (MapsActivity.this, "FAILED"+e.toString (), Toast.LENGTH_LONG).show ();
                        }
                    });

            finish ();
        }
        else
        {
            final Dialog dialog = new Dialog (MapsActivity.this);
            dialog.setContentView (R.layout.the_dialog);
            dialog.setTitle ("Reminder Name");
            dialog.setCancelable (false);

            final EditText name_dialog = dialog.findViewById (R.id.name_dialog);

            Button dialog_btn = dialog.findViewById (R.id.dialog_btn);

            dialog.show ();

            dialog_btn.setOnClickListener (new View.OnClickListener ()
            {
                @Override
                public void onClick(View v)
                {
                    if( name_dialog.getText ().toString ().isEmpty ())
                    {
                        Toast.makeText(MapsActivity.this, "ENTER THE NAME", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    addGeoence (latLng, type);
                    if (ActivityCompat.checkSelfPermission
                            (MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                        getLocationPermission();
                    geofencingClient.addGeofences (getGeofencingRequest (type), getGeofencePendingIntent ())
                            .addOnSuccessListener (MapsActivity.this, new OnSuccessListener<Void> ()
                            {
                                @Override
                                public void onSuccess(Void aVoid)
                                {

                                    Toast.makeText (MapsActivity.this, "ADDED", Toast.LENGTH_LONG).show ();
                                    onSaveButtonClicked(
                                            name_dialog.getText ().toString (),
                                            latLng.latitude,
                                            latLng.longitude,
                                            type);
                                }
                            })
                            .addOnFailureListener (MapsActivity.this, new OnFailureListener ()
                            {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    Toast.makeText (MapsActivity.this, "FAILED", Toast.LENGTH_LONG).show ();
                                }
                            });

                    dialog.dismiss ();
                    finish ();
                }
            });
        }
    }
}
