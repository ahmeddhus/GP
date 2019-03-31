package com.example.coyg.todolist.remainders;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.coyg.todolist.MainActivity;
import com.example.coyg.todolist.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

public class SetRemainderActivity extends AppCompatActivity  implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{
    private static final String TAG = SetRemainderActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private static final int PLACE_PICKER_REQUEST = 1;

    private GoogleApiClient googleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_set_remainder);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();

    }

    public void onAddPlaceButtonClicked(View view)
    {

        if (ActivityCompat.checkSelfPermission (this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText (this, getString (R.string.need_location_permission_message), Toast.LENGTH_LONG).show ();
            return;
        }

        try
        {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder ();
            Intent i = intentBuilder.build(this);
            startActivityForResult(i, PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException e)
        {
            Toast.makeText (SetRemainderActivity.this, e.getMessage().toString (), Toast.LENGTH_LONG).show ();
            Log.i(TAG, String.format("GooglePlayServices Not Available [%s]", e.getMessage()));
        } catch (GooglePlayServicesNotAvailableException e)
        {
            Log.i(TAG, String.format("GooglePlayServices Not Available [%s]", e.getMessage()));
            Toast.makeText (SetRemainderActivity.this, e.getMessage().toString (), Toast.LENGTH_LONG).show ();

        } catch (Exception e)
        {
            Log.i(TAG, String.format("PlacePicker Exception: %s", e.getMessage()));
            Toast.makeText (SetRemainderActivity.this, e.getMessage().toString (), Toast.LENGTH_LONG).show ();

        }
    }

    public void onLocationPermissionClicked(View view)
    {
        ActivityCompat.requestPermissions(SetRemainderActivity.this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_FINE_LOCATION);
    }

    public void onRingerPermissionsClicked(View view)
    {
//        Intent intent = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
//        {
//            intent = new Intent (android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
//        }
//        startActivity(intent);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        CheckBox locationPermissions = findViewById(R.id.location_permission_checkbox);
        if (ActivityCompat.checkSelfPermission(SetRemainderActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            locationPermissions.setChecked(false);
        }
        else {
            locationPermissions.setChecked(true);
            locationPermissions.setEnabled(false);
        }

//        CheckBox ringerPermissions = findViewById(R.id.ringer_permissions_checkbox);
//        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        if (nm != null)
//        {
//            if (android.os.Build.VERSION.SDK_INT >= 24 && !nm.isNotificationPolicyAccessGranted())
//            {
//                ringerPermissions.setChecked(false);
//            }
//            else {
//                ringerPermissions.setChecked(true);
//                ringerPermissions.setEnabled(false);
//            }
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK)
        {
            Place place = PlacePicker.getPlace (this, data);
            if (place == null)
            {
                Log.i (TAG, "No place selected");
                return;
            }
        }
        else
        {
            Log.i (TAG, requestCode+" - "+resultCode+" - "+data);
        }
    }



    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        Log.i(TAG, "API Client Connection Successful!");
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        Log.i(TAG, "API Client Connection Suspended!");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Log.e(TAG, "API Client Connection Failed!");
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture)
    {

    }
}
