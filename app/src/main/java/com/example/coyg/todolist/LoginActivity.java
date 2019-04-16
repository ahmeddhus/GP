package com.example.coyg.todolist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.coyg.todolist.database.AppDatabase;
import com.example.coyg.todolist.database.TaskEntry;
import com.example.coyg.todolist.notes.GetNoteCloud;
import com.example.coyg.todolist.notes.NotesMain;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity
{
    private static final String TAG = LoginActivity.class.getSimpleName();
    private String email, password;
    private EditText email_edittext, password_edittext;

    private AppDatabase appDatabase;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_login);

        init ();
    }

    private void init()
    {
        email_edittext = findViewById (R.id.email_edittext);
        password_edittext = findViewById (R.id.password_edittext);

        appDatabase = AppDatabase.getsInstance (getApplicationContext ());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null)
            updateUI(currentUser);
    }

    public void DoLogin(View view)
    {
        email = email_edittext.getText ().toString ();
        password = password_edittext.getText ().toString ();

        if (!validation(email, password))
            return;

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            Log.d(TAG, "signInWithEmail:success");

                            progressDialog = ProgressDialog.show(LoginActivity.this,
                                    "","Please Wait...", true);

                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            saveEmail (email);
                            getDataFromCloudFirestore(email);
                            updateUI(user);
                        }
                        else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void DoSingup(View view)
    {
        email = email_edittext.getText ().toString ();
        password = password_edittext.getText ().toString ();

        if (!validation(email, password))
            return;

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult> ()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(LoginActivity.this, "Authentication success.", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);
                            saveEmail (email);
                            addToCloudFirestore (email);
                        }
                        else {

                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed. "+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser currentUser)
    {
        Intent goToMain = new Intent (LoginActivity.this, MainActivity.class);
        startActivity (goToMain);
    }

    private void saveEmail(String email)
    {
        SharedPreferences.Editor editor = getSharedPreferences(getString (R.string.myEmail), MODE_PRIVATE).edit();
        editor.putString("email", email);
        editor.apply();

    }

    private boolean validation(String email, String password)
    {
        boolean valid = true;

        if (TextUtils.isEmpty(email))
        {
            email_edittext.setError("Required.");
            valid = false;
        }
        else {
            email_edittext.setError(null);
        }

        if (TextUtils.isEmpty(password))
        {
            password_edittext.setError("Required.");
            valid = false;
        }
        else {
            password_edittext.setError(null);
        }

        return valid;
    }

    private void addToCloudFirestore(String email)
    {
        Map<String, Object> theUser = new HashMap<>();


        firebaseFirestore.collection("users").document (email).collection ("notes")
                .add(theUser)
                .addOnSuccessListener (new OnSuccessListener<DocumentReference> ()
                {
                    @Override
                    public void onSuccess(DocumentReference documentReference)
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

    private void getDataFromCloudFirestore(String email)
    {
        firebaseFirestore.collection("users")
                .document (email).collection ("notes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot> ()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            if(task.getResult () != null)
                            {
                                for (QueryDocumentSnapshot document : task.getResult())
                                {
                                    Log.d(TAG, document.getId() + " => " + document.getData());

                                    TaskEntry taskEntry = new TaskEntry
                                            (
                                                    document.getId(),
                                                    document.get ("note").toString (),
                                                    document.get ("date").toString ()
                                            );

                                    appDatabase.taskDAO ().insertTask (taskEntry);
                                }
                                progressDialog.dismiss ();
                            }
                        }
                        else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
