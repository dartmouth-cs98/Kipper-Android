package com.artfara.apps.kipper.models;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Van on 9/5/16.
 * This class is structured so that it can be inserted or queried from
 * the Firebase database
 * For a particular place, this class holds its latitude and longitude
 * as well as name (location), etc.
 */
@IgnoreExtraProperties
public class Place {

    public Double latitude;
    public Double longitude;
    public Double radius;
    public String location;
    public String string;
    public String type;
    public int state;


    public Place() {
        // Default constructor required for calls to DataSnapshot.getValue(Latlng.class)
    }

    public Place(double latitude, double longitude, double radius, String location, String userCount, String type, int state) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.location = location;
        this.string = userCount;
        this.type = type;
        this.state = state;
    }

    //For use in Listview (lat, long, and rad are not important there)
    public Place(String location, String type) {
        this.location = location;
        this.type = type;
        this.string = "0";
    }

    public String toString(){
        return location + " rad = " + radius + " string = " + string;
    }

}