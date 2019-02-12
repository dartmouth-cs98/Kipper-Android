package com.artfara.apps.kipper;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
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


public class TrackingService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = "Tracking Service ";
    private DatabaseReference mDatabase;
    private GoogleApiClient mGoogleApiClient;
    private String mID;
    private boolean mServiceAlreadyStarted = false;

    @Override
    public void onCreate(){
//        Log.d(TAG, "onCreate");

        if (mServiceAlreadyStarted) return;
        mServiceAlreadyStarted = true;

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
    }



    public LocationListener mLocationListener = new com.google.android.gms.location.LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
//            Log.d(TAG, "location updated, location = " + location);
            if (Utils.setDatabaseIfPossible(getApplicationContext())) {
                Latlng loc = new Latlng(location.getLatitude(), location.getLongitude(),
                        true, "service " + Utils.getCurrentFormattedTime());
                Map<String, Object> childUpdates = new HashMap<>();
                Globals.location = loc.toMap();
                childUpdates.put(mID, loc.toMap());
                mDatabase.child(Globals.USERS_WRITE_TABLE_NAME).updateChildren(childUpdates);
            }
        }
    };
    @Override
    //Remove notifications if activity is shut down
    public void onDestroy() {
//        Log.d(TAG, "onDestroy");
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, mLocationListener);
            mGoogleApiClient.disconnect();
        }
        catch (Exception e){
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1500000);
        mLocationRequest.setFastestInterval(200000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (Build.VERSION.SDK_INT <= 22 || ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, mLocationListener);
        }
    }





    //*******************************************************************************************
    //ALL METHODS and FIELDS BELOW THIS LINE ARE NOT USED
    //*******************************************************************************************

    //OnStartCommand gets called multiple times, so use onCreate() instead
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
    public TrackingService() {}
    @Override
    public IBinder onBind(Intent intent) {
        return (mBinder);
    }

    public class TrackingBinder extends Binder {
        TrackingService getService() {
            // Return this instance of DownloadBinder so MapDisplayActivity can call public methods
            return TrackingService.this;
        }
    }
    private final IBinder mBinder = new TrackingBinder();

    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}
