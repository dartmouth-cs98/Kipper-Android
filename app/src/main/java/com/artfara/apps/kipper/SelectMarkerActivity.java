package com.artfara.apps.kipper;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.artfara.apps.kipper.models.Account;
import com.artfara.apps.kipper.models.CustomPlace;
import com.artfara.apps.kipper.models.MarkerGlobal;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SelectMarkerActivity extends AppCompatActivity {

    private static final String TAG = " SelectMarker Activity";
    private ListView listview;
    private SelectMarkerListViewAdapter mCustomBaseAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_select_marker_list_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
            TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
            Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "Comfortaa-Bold.ttf");
            title.setTypeface(typeFaceBold);
        }

        FirebaseDatabase.getInstance().getReference().child(Constants.MARKER_GLOBALS_TABLE_NAME)
                .addListenerForSingleValueEvent(mMarkerGlobalsSingleEventListener);

        mCustomBaseAdapter = new SelectMarkerListViewAdapter(this);
        //Grab a handle on ListView
        listview = (ListView) findViewById(R.id.ListViewSelectMarker);
        listview.setAdapter(mCustomBaseAdapter);
    }

    private ValueEventListener mMarkerGlobalsSingleEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            MarkerGlobal markerGlobals = dataSnapshot.getValue(MarkerGlobal.class);
            int numMarkers = getNumberMarkers();
            Log.d(TAG, "numMarkers " + numMarkers + " limit " + markerGlobals.marker_limit);
            if (numMarkers >= markerGlobals.marker_limit) {
                Toast.makeText(getApplicationContext(), markerGlobals.limit_reached_alert,
                        Toast.LENGTH_LONG).show();
                finish();
            }
            else {
                mCustomBaseAdapter.setEntries();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, databaseError.getMessage());
        }
    };

    private int getNumberMarkers() {
        int count = 0;
        for (CustomPlace place : Globals.globalCustomPlaces) {
            if (Objects.equals(place.UDID, Utils.getAndroidID(this))) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void onResume() {
        super.onResume();
////        Log.d(TAG, "onResume");
//        mActionStartFromTop = getIntent().getBooleanExtra(
//                Constants.ACTION_START_FROM_TOP_KEY, false);
//        //Update replies as soon as they become available
//        //update posts as soon as they become available
//        customBaseAdapter.setEntries(new ArrayList<Post>());
////        Log.d(TAG, "startAtTop " + mActionStartFromTop);
//        if (mActionStartFromTop) {
//            PostDatabaseHelper.downloadReplies(mPostId);
//        }
//        mHandler = new Handler();
//        mHandler.postDelayed(mPopulateListViewRunnable, Constants.MILLISECONDS_BEFORE_POLLING);
    }

//    Runnable mPopulateListViewRunnable = new Runnable() {
//        public void run() {
//            //If data has not yet been downloaded, try again later
//            if (PostDatabaseHelper.mFinishedDownloading == false) {
////                Log.d(TAG, "posts still null");
//                mHandler.postDelayed(this, Constants.MILLISECONDS_BETWEEN_POLLING);
//                if (mProgressDialog == null) {
//                    //Display loading spinner
//                    mProgressDialog = new ProgressDialog(SelectMarkerActivity.this);
//                    mProgressDialog.setMessage(getString(R.string.loading_message));
//                    mProgressDialog.show();
//                }
//            }
//            else {
//                if (SelectMarkerActivity.this != null && mProgressDialog != null
//                        && mProgressDialog.isShowing()) {
//                    mProgressDialog.dismiss();
//                    mProgressDialog = null;
//                }
////                Log.d(TAG, "posts NOT null");
//                if (PostDatabaseHelper.contains(mPostId)) {
//                    customBaseAdapter.setEntries(PostDatabaseHelper.getReplies(mPostId));
////                    Log.d(TAG, "startAtTop in runnable " + mActionStartFromTop);
//                    if (mActionStartFromTop) {
//                        getIntent().putExtra(Constants.ACTION_START_FROM_TOP_KEY, false);
//                    }
//                    else {
//                        listview.post(new Runnable() {
//                            public void run() {
//                                listview.setSelection(listview.getCount() - 1);
//                            }
//                        });
//                    }
//                }
//            }
//        }
//    };
}
