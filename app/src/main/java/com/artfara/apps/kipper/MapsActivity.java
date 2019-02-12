package com.artfara.apps.kipper;

import android.Manifest;
import android.app.AlertDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.*;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.artfara.apps.kipper.models.College;
import com.artfara.apps.kipper.models.CustomPlace;
import com.artfara.apps.kipper.models.Latlng;
import com.artfara.apps.kipper.models.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends AppCompatActivity {

    private static final String TAG = "Maps Activity ";
    private DatabaseReference mDatabase;
    private TabLayout mTabLayout;
    private SharedPreferences mPrefs;
    private boolean mLocationDialogVisible;
    private Bundle mSavedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSavedInstanceState = savedInstanceState;
        Log.d(TAG, "onCreate");

        Constants.prepare();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        boolean databaseSet = Utils.setDatabaseIfPossible(this);
//        Log.d(TAG, "databaseSet " + databaseSet);
        if (!databaseSet) {
//            Log.d(TAG, "launching select College ");
            Intent intent = new Intent(this, SelectCollegeActivity.class);
            startActivity(intent);
            finish();
            return;
        }
//        Log.d(TAG, "database " + Globals.DATABASE_ROOT_NAME + " " + Globals.ACCOUNTS_TABLE_NAME);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        PostDatabaseHelper.initialize(Utils.getAndroidID(this));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
            TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
            Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "Comfortaa-Bold.ttf");
            title.setTypeface(typeFaceBold);
        }
        if (Build.VERSION.SDK_INT < 22) requestLocationAndStartApp();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            //permission was granted, yay!
            //Start tracking users location
            initializeApplication();

        } else {
            // permission denied, boo!
            // Tell the user that they are a jackass for disabling the permission
            // must request the permission.
            showDialog();
//            Log.d(TAG, "permission denied");
            //When user presses OK in dialog, onStop() is executed and MapsActivity is restarted, calling onCreate()
        }
        return;
    }

    private void showDialog() {
        mLocationDialogVisible = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.location_explanation_message)
               .setCancelable(false)
                .setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mLocationDialogVisible = false;
                            dialog.dismiss();
                            requestLocationAndStartApp();
                        }
                    })
                .create()
                .show();
    }

    public void requestLocationAndStartApp() {
        //Only start app if we have the permissions we need to access location
        if (Build.VERSION.SDK_INT >= 22 && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //We need to request permission
            if (!mLocationDialogVisible) {
//                Log.d(TAG, "requesting permission");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        0);
            }
        } else {
            //No need to ask for permission
            initializeApplication();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        Log.d(TAG, "onResume");
        if (Build.VERSION.SDK_INT >= 22) requestLocationAndStartApp();
    }

    private void initializeApplication() {
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new CustomFragmentPageAdapter(getSupportFragmentManager()));
        mTabLayout.setupWithViewPager(viewPager);
        mTabLayout.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                        int currentTabIndex = tab.getPosition();
                        mPrefs.edit().putInt(Constants.LAST_TAB_SELECTED_KEY, currentTabIndex).apply();
                    }
                });
        int lastTabSelectedIndex = mPrefs.getInt(Constants.LAST_TAB_SELECTED_KEY, -1);
        if (lastTabSelectedIndex != -1){
            TabLayout.Tab selectedTab = mTabLayout.getTabAt(lastTabSelectedIndex);
            selectedTab.select();
        }
        else {
            TabLayout.Tab feedTab = mTabLayout.getTabAt(1);
            feedTab.select();
        }

        Intent intent = getIntent();
//        && mSavedInstanceState == null
        String postIDIntentExtra = intent.getStringExtra(Constants.POST_ID_KEY);
        if (postIDIntentExtra != null) {
            PostDatabaseHelper.downloadPosts();
            Intent chatReplyIntent = new Intent(this, ChatReplyListViewActivity.class);
            chatReplyIntent.putExtra(Constants.POST_ID_KEY, postIDIntentExtra);
            intent.removeExtra(Constants.POST_ID_KEY);
            startActivity(chatReplyIntent);
        }

        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        mDatabase.child(Globals.USERS_READ_TABLE_NAME).addListenerForSingleValueEvent(mUsersSingleEventListener);
                    }
                }, 100, 30000, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
            mDatabase.child(Globals.PLACES_TABLE_NAME).addListenerForSingleValueEvent(mPlacesSingleEventListener);
                    }
                }, 100, 30000, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        mDatabase.child(Globals.CUSTOM_PLACES_TABLE_NAME).addListenerForSingleValueEvent(mCustomPlacesSingleEventListener);
                    }
                }, 100, 30000, TimeUnit.MILLISECONDS);

        scheduleLocationTracking();
        Utils.sendFCMTokenToServer(getApplicationContext()); //Did these two commands cause the
        //not loading problem?
        initializeAccountIfNessicary();
    }

    private void initializeAccountIfNessicary() {
        //If account has not been initialized
        if (!mPrefs.getBoolean(Constants.ACCOUNT_INIATILIZED_KEY, false)) {
            mPrefs.edit().putBoolean(Constants.ACCOUNT_INIATILIZED_KEY, true).apply();
            Log.d(TAG, "initializing account " + Globals.ACCOUNTS_INITIALIZED_TABLE_NAME);
            String androidID =  Utils.getAndroidID(this);
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(androidID, androidID);
            mDatabase.child(Globals.ACCOUNTS_INITIALIZED_TABLE_NAME).updateChildren(childUpdates);
        }
    }

    private void scheduleLocationTracking() {
        Intent intent = new Intent(this, TrackingService.class);
        startService(intent);

        //Start JobScheduler Tracking Service
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancelAll();
        ComponentName serviceName = new ComponentName(this, TrackingJobService.class);
        int jobId = 1;
        JobInfo.Builder builder;
        builder = new JobInfo.Builder(jobId, serviceName)
                .setPeriodic(1000000)
                .setRequiresDeviceIdle(false) // does not matter if device is idle
                .setRequiresCharging(false) // we don't care if the device is charging or not
                .setPersisted(true); // start on boot
        int result = jobScheduler.schedule(builder.build());
        if (result == JobScheduler.RESULT_SUCCESS) Log.d(TAG, "Job scheduled successfully!");

        //Start Alarm Manager Tracking Service
        Utils.startAlarmTrackingService(this);
    }


    private ValueEventListener mUsersSingleEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            ArrayList<LatLng> users = new ArrayList<>();
            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                //GMAPS heatmap API can only take 1000 locations
                if (users.size() > 997) break;
                Latlng location = userSnapshot.getValue(Latlng.class);
                users.add(new LatLng(location.latitude, location.longitude));
            }
            Globals.globalUsers = users;
        }

        @Override //autogenerated
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            // ...
        }
    };


    private ValueEventListener mPlacesSingleEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            ArrayList<Place> places = new ArrayList<>();
            for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                Place place = placeSnapshot.getValue(Place.class);
                places.add(place);
            }
            Globals.globalPlaces = places;
        }
        @Override //autogenerated
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            // ...
        }
    };

    private ValueEventListener mCustomPlacesSingleEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            ArrayList<CustomPlace> places = new ArrayList<>();
            for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                CustomPlace place = placeSnapshot.getValue(CustomPlace.class);
                places.add(place);
            }
            Globals.globalCustomPlaces = places;
        }
        @Override //autogenerated
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            // ...
        }
    };


    public void onPause() {
        super.onPause();
//        Log.d(TAG, "onPause");
    }
    public void onStop(){
        super.onStop();
//        Log.d(TAG, "onStop");
    }

    public void hideTabs() {
        if (mTabLayout != null) mTabLayout.setVisibility(TabLayout.GONE);
    }

    public void showTabs() {
        if (mTabLayout != null) mTabLayout.setVisibility(TabLayout.VISIBLE);
    }

    private class CustomFragmentPageAdapter extends FragmentPagerAdapter {
        public CustomFragmentPageAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ChatFragment();
                case 1:
                    return new RankFragment();
                case 2:
                    return new MapsFragment();
                default:
                    return new RankFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "CHAT";
                case 1:
                    return "RANK";
                case 2:
                    return "MAP";
                default:
                    return "FEED";
            }
        }
    }

}
