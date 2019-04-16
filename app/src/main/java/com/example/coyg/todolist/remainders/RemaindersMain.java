package com.example.coyg.todolist.remainders;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.coyg.todolist.R;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;


public class RemaindersMain extends Fragment
{
    private static final String TAG = RemaindersMain.class.getSimpleName();

    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private static final int PLACE_PICKER_REQUEST = 1;

    private boolean mIsEnabled;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate (R.layout.fragment_remainders, container, false);

        onAddPlaceButtonClicked(view);
        onLocationPermissionClicked(view);
        onRingerPermissionsClicked(view);



        Switch onOffSwitch = view.findViewById(R.id.enable_switch);
        mIsEnabled = getActivity ().getPreferences(MODE_PRIVATE).getBoolean(getString(R.string.setting_enabled), false);
        onOffSwitch.setChecked(mIsEnabled);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
//                SharedPreferences.Editor editor = getActivity ().getPreferences(MODE_PRIVATE).edit();
//                editor.putBoolean(getString(R.string.setting_enabled), isChecked);
//                mIsEnabled = isChecked;
//                editor.apply();

//                if (isChecked) geofencing.registerAllGeofences();
//                else geofencing.unRegisterAllGeofences();
            }

        });

        return view;
    }


    public void onAddPlaceButtonClicked(View view)
    {
        view.findViewById (R.id.add_new_loc).setOnClickListener (new View.OnClickListener ()
       {
           @Override
           public void onClick(View v)
           {
               Intent intent = new Intent (getActivity (), MapsActivity.class);
               startActivity (intent);
           }

       });
    }

    public void onLocationPermissionClicked(View view)
    {
        view.findViewById (R.id.location_permission_checkbox)
                .setOnClickListener (new View.OnClickListener ()
                {
                    @Override
                    public void onClick(View v)
                    {
                        ActivityCompat.requestPermissions(getActivity (),
                                new String[]{ACCESS_FINE_LOCATION},
                                PERMISSIONS_REQUEST_FINE_LOCATION);
                    }
                });
    }


    public void onRingerPermissionsClicked(View view)
    {
        view.findViewById (R.id.ringer_permissions_checkbox)
                .setOnClickListener (new View.OnClickListener ()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                        startActivity(intent);
                    }
                });
    }


    @Override
    public void onResume()
    {
        super.onResume();

        CheckBox locationPermissions = getView ().findViewById(R.id.location_permission_checkbox);
        if (ActivityCompat.checkSelfPermission(getActivity (),
                ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            locationPermissions.setChecked(false);
        }
        else {
            locationPermissions.setChecked(true);
            locationPermissions.setEnabled(false);
        }

        CheckBox ringerPermissions = getActivity ().findViewById(R.id.ringer_permissions_checkbox);
        NotificationManager nm = (NotificationManager) getActivity ().getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= 24 && !nm.isNotificationPolicyAccessGranted())
        {
            ringerPermissions.setChecked(false);
        }
        else
            {
            ringerPermissions.setChecked(true);
            ringerPermissions.setEnabled(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

//        if (requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK)
//        {
//            Place place = PlacePicker.getPlace (getActivity (), data);
//            if (place == null)
//            {
//                Log.i (TAG, "No place selected");
//                return;
//            }
//        }
//        else
//        {
//            Log.i (TAG, requestCode+" - "+resultCode+" - "+data);
//        }
    }

}
