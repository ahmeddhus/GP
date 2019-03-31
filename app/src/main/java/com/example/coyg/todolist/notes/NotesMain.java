package com.example.coyg.todolist.notes;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.coyg.todolist.database.MainViewModel;
import com.example.coyg.todolist.R;
import com.example.coyg.todolist.database.AppDatabase;
import com.example.coyg.todolist.database.AppExecutors;
import com.example.coyg.todolist.database.TaskEntry;
import com.example.coyg.todolist.remainders.SetRemainderActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotesMain extends Fragment implements Adapter.ItemClickListener
{
    private static final String TAG = NotesMain.class.getSimpleName();
    RecyclerView recyclerView;
    Adapter adapter;

    private AppDatabase appDatabase;

    private FirebaseFirestore firebaseFirestore;
    FirebaseFirestoreSettings firebaseFirestoreSettings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate (R.layout.fragment_notes, container, false);

        initViews(view);

        new ItemTouchHelper (new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)
        {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target)
            {
                return false;
            }
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir)
            {
                AppExecutors.getInstance ().getDiskIO ().execute (new Runnable ()
                {
                    @Override
                    public void run()
                    {
                        int position = viewHolder.getAdapterPosition ();
                        List<TaskEntry> taskEntries = adapter.getmTaskEntries ();
                        appDatabase.taskDAO ().delete (taskEntries.get (position));

                        deleteFromCloudFirestore(taskEntries.get(position).getId());
                    }
                });
            }
        }).attachToRecyclerView(recyclerView);


        setupViewModel ();

        return view;
    }

    private void initViews(View view)
    {
        recyclerView = view.findViewById (R.id.recyclerview_notes);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager (2, StaggeredGridLayoutManager.VERTICAL));
        adapter = new Adapter( getActivity (), this);

        appDatabase = AppDatabase.getsInstance ((getActivity ()));

        recyclerView.setAdapter(adapter);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestoreSettings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();

        firebaseFirestore.setFirestoreSettings(firebaseFirestoreSettings);
    }

    private void setupViewModel()
    {
        MainViewModel mainViewModel = ViewModelProviders.of (this).get(MainViewModel.class);
        mainViewModel.getTask ().observe (this, new Observer<List<TaskEntry>> ()
        {
            @Override
            public void onChanged(@Nullable List<TaskEntry> taskEntries)
            {
                adapter.setTasks(taskEntries);
            }
        });
    }

    private void deleteFromCloudFirestore(String ID)
    {
        DocumentReference documentReference = firebaseFirestore.collection("users")
                .document (getEmail ()).collection ("notes").document (ID);

        documentReference.delete ()
                .addOnSuccessListener (new OnSuccessListener<Void> ()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener (new OnFailureListener ()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    private String getEmail()
    {
        SharedPreferences prefs = getActivity ().getSharedPreferences(getString (R.string.myEmail), Context.MODE_PRIVATE);
        return prefs.getString("email", "No name defined");
    }

    @Override
    public void onItemClickActionListener(String position)
    {
        Intent intent = new Intent (getActivity (), AddNoteActivity.class);
        intent.putExtra (AddNoteActivity.EXTRA_TASK_ID, position);
        startActivity (intent);
    }
}
