package com.example.coyg.todolist.remainders;

import android.app.NotificationManager;
import android.content.Intent;
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
import android.widget.RadioGroup;
import com.example.coyg.todolist.R;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.NOTIFICATION_SERVICE;

public class RemaindersMain extends Fragment
{
    private static final String TAG = RemaindersMain.class.getSimpleName();

    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;

    RadioGroup radioGroup;
    private Intent intent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate (R.layout.fragment_remainders, container, false);

        intent = new Intent (getActivity (), MapsActivity.class);

        RadiobtnsAction(view);
        onAddPlaceButtonClicked(view);
        onLocationPermissionClicked(view);

        return view;
    }


    public void onAddPlaceButtonClicked(View view)
    {
        view.findViewById (R.id.add_new_loc).setOnClickListener (new View.OnClickListener ()
       {
           @Override
           public void onClick(View v)
           {

               if (ActivityCompat.checkSelfPermission(getActivity (),
                       ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
               {
                   ActivityCompat.requestPermissions(getActivity (),
                           new String[]{ACCESS_FINE_LOCATION},
                           PERMISSIONS_REQUEST_FINE_LOCATION);
               }
               else {

                   startActivity (intent);
               }
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


    public void RadiobtnsAction(View view)
    {
        radioGroup = view.findViewById(R.id.radioGroup);
        intent.putExtra("type", "enter");
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                if(checkedId == R.id.enter)
                {
                    intent.putExtra("type", "enter");
                }

                else if(checkedId == R.id.exit)
            {
                intent.putExtra("type", "exit");
            }

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
    }
}
