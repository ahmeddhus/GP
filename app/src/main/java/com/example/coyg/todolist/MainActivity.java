package com.example.coyg.todolist;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.coyg.todolist.database.AppDatabase;
import com.example.coyg.todolist.notes.AddNoteActivity;
import com.example.coyg.todolist.notes.NotesMain;
import com.example.coyg.todolist.remainders.RemaindersMain;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity
{
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private AppDatabase appDatabase;

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        Toolbar toolbar = findViewById (R.id.toolbar);
        setSupportActionBar (toolbar);

        appDatabase = AppDatabase.getsInstance ((MainActivity.this));

        mSectionsPagerAdapter = new SectionsPagerAdapter (getSupportFragmentManager ());

        mViewPager = findViewById (R.id.container);
        mViewPager.setAdapter (mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById (R.id.tabs);

        mViewPager.addOnPageChangeListener (new TabLayout.TabLayoutOnPageChangeListener (tabLayout));
        tabLayout.addOnTabSelectedListener (new TabLayout.ViewPagerOnTabSelectedListener (mViewPager));

        FloatingActionButton fab = findViewById (R.id.fab);
        fab.setOnClickListener (new View.OnClickListener ()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater ().inflate (R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId ();

        if(id == R.id.logout_itemmenu)
        {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();

            appDatabase.taskDAO ().deleteALL ();
            appDatabase.remainderDAO ().deleteALL ();

            Intent i = new Intent (MainActivity.this, LoginActivity.class);
            startActivity (i);
            return true;
        }

        return super.onOptionsItemSelected (item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        public SectionsPagerAdapter(FragmentManager fm)
        {
            super (fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            switch (position)
            {
                case 0:
                    NotesMain notesMain = new NotesMain ();
                    return notesMain;
                case 1:
                    RemaindersMain remaindersMain = new RemaindersMain ();
                    return remaindersMain;
                default:
                     return null;
            }
        }

        @Override
        public int getCount()
        {
            return 2;
        }
    }

    @Override
    public void onBackPressed()
    {
        if (doubleBackToExitPressedOnce)
        {
            finishAffinity ();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.clickBack, Toast.LENGTH_SHORT).show();

        new Handler ().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
