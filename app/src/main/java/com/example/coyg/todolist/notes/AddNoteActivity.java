package com.example.coyg.todolist.notes;

import android.app.ActionBar;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.example.coyg.todolist.R;
import com.example.coyg.todolist.database.AppDatabase;
import com.example.coyg.todolist.database.AppExecutors;
import com.example.coyg.todolist.database.TaskEntry;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddNoteActivity extends AppCompatActivity
{
    public static final String TAG = AddNoteActivity.class.getSimpleName ();

    private static final String DATE_FORMAT = "dd/MM/yyy";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    public static final String EXTRA_TASK_ID = "extrataskid";
    public static final String INSTANCE_TASK_ID = "instancetaskid";

    private EditText editText;

    public static final String DEFAULT_TASK_ID = "-1";
    private String mTaskid = DEFAULT_TASK_ID;
    private AppDatabase mDb;
    private TaskEntry  taskEntry;

    private FirebaseFirestore firebaseFirestore;
    FirebaseFirestoreSettings firebaseFirestoreSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_add_task);
        
        initViews();


        Intent intent = getIntent ();
        if(intent != null && intent.hasExtra (EXTRA_TASK_ID))
        {
            mTaskid = intent.getStringExtra (EXTRA_TASK_ID);
            final LiveData<TaskEntry> taskEntry = mDb.taskDAO ().loadTaskById (mTaskid);
            taskEntry.observe (this, new Observer<TaskEntry> ()
            {
                @Override
                public void onChanged(@Nullable TaskEntry task)
                {
                    taskEntry.removeObserver (this);
                    populateUI (task);
                }
            });
        }
    }

    private void initViews()
    {
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editText = findViewById (R.id.edittext);
        mDb = AppDatabase.getsInstance (getApplicationContext ());

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestoreSettings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();

        firebaseFirestore.setFirestoreSettings(firebaseFirestoreSettings);
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }

    private void populateUI(TaskEntry taskEntry)
    {
        if (taskEntry == null)
            return;

        editText.setText (taskEntry.getDescription ());
    }

    public void onSaveButtonClicked()
    {
        final String description = editText.getText ().toString ();
        final Date date = new Date();

        if(description.isEmpty ())
        {
            finish ();
            return;
        }

        taskEntry = new TaskEntry (date.toString (), description, dateFormat.format(date));
        AppExecutors.getInstance ().getDiskIO ().execute (new Runnable ()
        {
            @Override
            public void run()
            {
                if (mTaskid.equals(DEFAULT_TASK_ID))
                {
                    mDb.taskDAO ().insertTask (taskEntry);
                    addToCloudFirestore(date.toString (), getEmail (), description,  dateFormat.format(date));
                }
                else
                {
                    taskEntry.setId (mTaskid);
                    mDb.taskDAO ().updateTask (taskEntry);
                    updateInCloudFirestore(mTaskid, getEmail (), description,  dateFormat.format(date));
                }
                finish ();
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed ();
        onSaveButtonClicked ();
    }

    private void addToCloudFirestore(String ID, final String email, String note, String date)
    {
        Map<String, Object> noteDB = new HashMap<> ();
        noteDB.put("note", note);
        noteDB.put("date", date);

        firebaseFirestore.collection("users").document (email).collection ("notes")
                .document (ID).set (noteDB)
                .addOnSuccessListener (new OnSuccessListener<Void> ()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener (new OnFailureListener ()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void updateInCloudFirestore(String ID, final String email, String note, String date)
    {
        DocumentReference documentReference = firebaseFirestore.collection("users")
                .document (email).collection ("notes").document (ID);

        documentReference.update ("note", note)
                .addOnSuccessListener (new OnSuccessListener<Void> ()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener (new OnFailureListener ()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

        documentReference.update ("date", date)
                .addOnSuccessListener (new OnSuccessListener<Void> ()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener (new OnFailureListener ()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

    }

    private String getEmail()
    {
        SharedPreferences prefs = getSharedPreferences(getString (R.string.myEmail), MODE_PRIVATE);
        return prefs.getString("email", "No name defined");
    }
}