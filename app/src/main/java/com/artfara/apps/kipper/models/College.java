package com.artfara.apps.kipper.models;

/**
 * Created by joeconnolly on 9/5/17.
 */

public class College {
    public String databaseRoot;
    public String name;
    public double latitude;
    public double longitude;
    public int point;
    public int b;
    public int g;
    public int r;

    public College() {
        // Default constructor required for calls to DataSnapshot.getValue()
    }

}
