package com.example.coyg.todolist.remainders;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.coyg.todolist.R;
import com.example.coyg.todolist.database.AppDatabase;
import com.example.coyg.todolist.database.MainViewModel;
import com.example.coyg.todolist.database.RemainderEntry;
import com.example.coyg.todolist.notes.AddNoteActivity;

import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class RemaindersMain extends Fragment
{
    private static final String TAG = RemaindersMain.class.getSimpleName();

    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;

    private AppDatabase appDatabase;

    RadioGroup radioGroup;
    private Intent intent;

    RemainderAdapter remainderAdapter;
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate (R.layout.fragment_remainders, container, false);

        intent = new Intent (getActivity (), MapsActivity.class);

        initViews(view);

        RadiobtnsAction(view);
        onAddPlaceButtonClicked(view);
        onLocationPermissionClicked(view);
        setupViewModel();

        return view;
    }

    private void initViews(View view)
    {
        appDatabase = AppDatabase.getsInstance ((getActivity ()));

        recyclerView = view.findViewById (R.id.remainders_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager (getActivity (), LinearLayoutManager.VERTICAL, false));
        remainderAdapter = new RemainderAdapter (getActivity ());
        //recyclerView.addItemDecoration (new DividerItemDecoration (recyclerView.getContext (), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(remainderAdapter);
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

    private void setupViewModel()
    {
        MainViewModel mainViewModel = ViewModelProviders.of (this).get(MainViewModel.class);
        mainViewModel.getRemainder ().observe (this, new Observer<List<RemainderEntry>> ()
        {
            @Override
            public void onChanged(@Nullable List<RemainderEntry> remainderEntries)
            {
                remainderAdapter.setRemainders (remainderEntries);
            }
        });
    }
}
