package com.artfara.apps.kipper.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Van on 8/31/16.
 * This class is structured so that it can be inserted or queried from
 * the Firebase database
 * For a particular user, this class holds their latitude and longitude
 */
@IgnoreExtraProperties
public class Latlng {

    public Double latitude;
    public Double longitude;
    public boolean isDeletable;
    public String lastUpdated;


    public Latlng() {
        // Default constructor required for calls to DataSnapshot.getValue(Latlng.class)
    }

    public Latlng(double latitude, double longitude,  boolean isDeletable, String lastUpdated) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.isDeletable = isDeletable;
        this.lastUpdated = lastUpdated;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> latLngMap = new HashMap<>();
        latLngMap.put("latitude", latitude);
        latLngMap.put("longitude", longitude);
        latLngMap.put("isDeletable", isDeletable);
        latLngMap.put("lastUpdated", lastUpdated);
        return latLngMap;
    }
}