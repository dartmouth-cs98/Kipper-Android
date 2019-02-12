package com.artfara.apps.kipper;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.artfara.apps.kipper.models.College;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SelectCollegeActivity extends AppCompatActivity {
    private static final String TAG = "College Activity ";
    private DatabaseReference mDatabase;
    private ArrayList<College> mColleges;
    private ListView mListView;
    private SelectCollegeListViewAdapter mCustomBaseAdapter;
    private Handler mHandler;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_college);
//        Log.d(TAG, "onCreate");
        //Create Custom Adapter
        mCustomBaseAdapter = new SelectCollegeListViewAdapter(this);

        //Grab a handle on ListView
        mListView = (ListView) findViewById(R.id.ListViewColleges);
        mListView.setAdapter(mCustomBaseAdapter);

        FirebaseDatabase.getInstance().getReference()
                .child(Globals.COLLEGES_TABLE_NAME)
                .addListenerForSingleValueEvent(mCollegesSingleEventListener);
    }

    private ValueEventListener mCollegesSingleEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mColleges = new ArrayList<>();
            for (DataSnapshot collegeSnapshot : dataSnapshot.getChildren()) {
                College college = collegeSnapshot.getValue(College.class);
                mColleges.add(college);
//                Log.d(TAG, "college " + college.name);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, databaseError.getMessage());
        }
    };

    @Override
    public void onResume() {
        super.onResume();
//        Log.d(TAG, "onResume");
        //Update colleges as soon as they become available
        mHandler = new Handler();
        mHandler.postDelayed(mPopulateListViewRunnable, Constants.MILLISECONDS_BEFORE_POLLING);
    }

    Runnable mPopulateListViewRunnable = new Runnable() {
        public void run() {
            //If data has not yet been downloaded, try again later
            if (mColleges == null || mColleges.size() == 0) {
//                Log.d(TAG, "colleges still null");
                mHandler.postDelayed(this, Constants.MILLISECONDS_BETWEEN_POLLING);
                if (mProgressDialog == null) {
                    //Display loading spinner
                    mProgressDialog = new ProgressDialog(SelectCollegeActivity.this);
                    mProgressDialog.setMessage(getString(R.string.loading_message));
                    mProgressDialog.show();
                }
            }
            else {
                if (SelectCollegeActivity.this != null && mProgressDialog != null
                        && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
                mCustomBaseAdapter.setEntries(mColleges);
            }
        }
    };


    public void onBackPressed() {
        // Override to disable
    }
}


