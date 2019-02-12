package com.artfara.apps.kipper;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.artfara.apps.kipper.models.Latlng;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joe Connolly on 10/23/16.
 */

public class TrackingJobService extends JobService implements GoogleApiClient.ConnectionCallbacks,  GoogleApiClient.OnConnectionFailedListener {
    private JobParameters mParams;
    private DatabaseReference mDatabase;
    private static final String TAG = "Job Service ";
    private GoogleApiClient mGoogleApiClient;
    private String mID;
    private boolean mServiceAlreadyStarted = false;
    @Override
    public boolean onStartJob(JobParameters params) {
//        Log.d(TAG, "onStartJob");
        if (mServiceAlreadyStarted) return true;
        mServiceAlreadyStarted = true;

        mParams = params;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mID = Utils.getUserID(getApplicationContext());
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(50000);
        mLocationRequest.setFastestInterval(30000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //Only start app if we have the permissions we need to access location
        if (Build.VERSION.SDK_INT <= 22 || ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, mLocationListener);
        }

    }

    public LocationListener mLocationListener = new com.google.android.gms.location.LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
//            Log.d(TAG, "location updated, location = " + location + "");
            if (Utils.setDatabaseIfPossible(getApplicationContext())) {
                Latlng loc = new Latlng(location.getLatitude(), location.getLongitude(), true,
                        "job " + Utils.getCurrentFormattedTime());
                Globals.location = loc.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(mID, loc.toMap());
                mDatabase.child(Globals.USERS_WRITE_TABLE_NAME).updateChildren(childUpdates);
            }

            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, mLocationListener);
                mGoogleApiClient.disconnect();

                jobFinished(mParams, true);
            }
            catch (Exception e){

            }
        }
    };






    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
