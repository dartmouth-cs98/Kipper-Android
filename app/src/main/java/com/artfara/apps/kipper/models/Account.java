package com.artfara.apps.kipper.models;

/**
 * Created by joeconnolly on 9/6/17.
 */

public class Account {

    public int points;
    public String rankString;
    public String relativeRankString;
    public String userID;
    public String fcmToken;


    public Account() {
        // Default constructor required for calls to DataSnapshot.getValue()
    }
}
