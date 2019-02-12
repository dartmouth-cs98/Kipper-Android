package com.artfara.apps.kipper;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.artfara.apps.kipper.models.CustomPlace;
import com.artfara.apps.kipper.models.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;

/**
 * Maps Fragment -- that is, THE Maps Fragment
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback {

    MapView mMapView;
    private GoogleMap mMap;
    private static final String TAG = "Maps Fragment";
    private Handler mHandler;
    private TileOverlay mOverlay;
    private HeatmapTileProvider mHeatMapProvider;
    private ProgressDialog mProgressDialog;
    private SharedPreferences mPrefs;
    private boolean mShowMarkers;
    private ImageView mShowMarkersImg;
    private ImageView mAddMarkersImg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        Log.d(TAG, "onCreate");
        // inflate the layout
        View v = inflater.inflate(R.layout.fragment_maps, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        mShowMarkers = mPrefs.getBoolean(Constants.SHOW_MARKERS_KEY, true);

        mShowMarkersImg = (ImageView) v.findViewById(R.id.showMarkersBtn);
        mShowMarkersImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShowMarkers = !mShowMarkers;
                mPrefs.edit().putBoolean(Constants.SHOW_MARKERS_KEY, mShowMarkers).apply();
                renderMap();
            }
        });
        mAddMarkersImg = (ImageView) v.findViewById(R.id.addMarkerBtn);
        mAddMarkersImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SelectMarkerActivity.class));
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
//        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        //update markers as soon as they become available
        mHandler = new Handler();
        mHandler.postDelayed(mPopulateMapRunnable, Constants.MILLISECONDS_BEFORE_POLLING);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView!=null) {
            mMapView.onDestroy();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMap != null) {
            mMap.clear();
            mMap = null;
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        //If we have permission, enable location
        if (Build.VERSION.SDK_INT <= 22 || ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        //Center map at Hanover
        LatLng location = new LatLng(Globals.COLLEGE.latitude,
                Globals.COLLEGE.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f));

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        mMap.setInfoWindowAdapter(new PopUpAdapter(layoutInflater));
    }

    Runnable mPopulateMapRunnable = new Runnable() {
        public void run() {
            //If data has not yet been downloaded, try again later
            if (Globals.globalPlaces == null || Globals.globalUsers == null ||
                    Globals.globalCustomPlaces == null || mMap == null) {
                if (mProgressDialog == null && getContext() != null) {
                    //Display loading spinner
                    mProgressDialog = new ProgressDialog(getContext());
                    mProgressDialog.setMessage(getString(R.string.loading_message));
                    mProgressDialog.show();
                }
//                Log.d(TAG, "places or users or map still null");
                mHandler.postDelayed(this, Constants.MILLISECONDS_BETWEEN_POLLING);
            } else {
                renderMap();
                if (getActivity() != null && mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        }
    };

    private void renderMap() {
        mMap.clear();
        if (mShowMarkers) {
            renderMarkers();
        }
        renderCustomMarkers();
        renderHeatMap();
        int showMarkersImgID = (mShowMarkers ? R.drawable.hide_markers : R.drawable.show_markers);
        mShowMarkersImg.setImageDrawable(getResources()
                .getDrawable(showMarkersImgID, getContext().getTheme()));
    }

    private void renderHeatMap() {
        if (Globals.globalUsers != null && Globals.globalUsers.size() > 0) {
            for (LatLng user: Globals.globalUsers) {
                mMap.addCircle(new CircleOptions()
                        .center(new LatLng(user.latitude, user.longitude))
                        .strokeWidth(Constants.USERS_CIRCLE_STROKE_WIDTH)
                        .radius(Constants.USERS_CIRCLE_RADIUS)
                        .strokeColor(Color.argb(61, 255, 128, 0))
                        .fillColor(Color.argb(74, 255, 0, 0)));
            }
        }
    }

    private void renderMarkers() {
        ArrayList<Place> places = Globals.globalPlaces;
        for (Place place : places) {
            //Plot marker
            mMap.addMarker((new MarkerOptions()
                    .position(new LatLng(place.latitude, place.longitude))
                    .icon(BitmapDescriptorFactory.fromResource(Constants.PLACES.get(place.type)))
                    .title(place.location)
                    .snippet(place.string)));
        }
    }

    private void renderCustomMarkers() {
        ArrayList<CustomPlace> customPlaces = Globals.globalCustomPlaces;
        for (CustomPlace place : customPlaces) {
            //Plot marker
            mMap.addMarker((new MarkerOptions()
                    .position(new LatLng(place.latitude, place.longitude))
                    .icon(BitmapDescriptorFactory.fromResource(Constants.CUSTOM_PLACES.get(place.type)))
                    .title(place.time)
                    .snippet(place.customText)));
        }
    }

}