package com.artfara.apps.kipper;

import com.artfara.apps.kipper.models.College;
import com.artfara.apps.kipper.models.CustomPlace;
import com.artfara.apps.kipper.models.Place;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joe Connolly on 10/20/16.
 */

public class Globals {
    public static final String COLLEGES_TABLE_NAME = "colleges";
    public final static String REPLIES_TABLE_NAME = "replies";
    public static ArrayList<Place> globalPlaces;
    public static ArrayList<LatLng> globalUsers;
    public static Map<String, Object> location = new HashMap<>();

    //Constants to be used throughout the application
    public static String DATABASE_ROOT_NAME = null;
    public static String POSTS_VOTED_TABLE_NAME;
    public static String ACCOUNTS_INITIALIZED_TABLE_NAME;
    public static String ACCOUNTS_TABLE_NAME;
    public static String POSTS_TABLE_NAME;
    public static String CUSTOM_PLACES_TABLE_NAME;
    public static String PLACES_TABLE_NAME;
    public static String USERS_ACCOUNTS_TABLE_NAME;
    public static String USERS_READ_TABLE_NAME;
    public static String USERS_WRITE_TABLE_NAME;
    public static String POSTS_REPLIED_TABLE_NAME;
    public static College COLLEGE;
    public static ArrayList<CustomPlace> globalCustomPlaces;

    public static void initialize() {
        DATABASE_ROOT_NAME = COLLEGE.databaseRoot;
        POSTS_VOTED_TABLE_NAME = DATABASE_ROOT_NAME + "posts_voted";
        ACCOUNTS_INITIALIZED_TABLE_NAME = DATABASE_ROOT_NAME + "accounts_initialized";
        ACCOUNTS_TABLE_NAME = DATABASE_ROOT_NAME + "users_accounts";
        POSTS_TABLE_NAME = DATABASE_ROOT_NAME + "posts";
        PLACES_TABLE_NAME = DATABASE_ROOT_NAME + "places";
        CUSTOM_PLACES_TABLE_NAME = DATABASE_ROOT_NAME + "places_custom";
        USERS_ACCOUNTS_TABLE_NAME = DATABASE_ROOT_NAME + "users_accounts";
        USERS_READ_TABLE_NAME = DATABASE_ROOT_NAME + "users_read";
        USERS_WRITE_TABLE_NAME = DATABASE_ROOT_NAME + "users_write";
        POSTS_REPLIED_TABLE_NAME = DATABASE_ROOT_NAME + "posts_replied";
    }
}
