package com.artfara.apps.kipper;

import com.artfara.apps.kipper.models.Place;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Van on 9/1/16.
 */
public class Constants {
    public static final String COLLEGE_KEY = "database root21";
    public static final String MARKER_GLOBALS_TABLE_NAME = "production/production_globals/Notice";
    public final static String VOTE_FIELD_NAME = "voteCount";
    public static final double HANOVER_LATITUDE = 43.703272;
    public static final double HANOVER_LONGITUDE = -72.288633;
    public static final Place[] PLACE_TOTALS_TEMPLATES = new Place[] {new Place("Greek Life", "frat"), new Place("Food Courts", "food"),
            new Place("Libraries", "library"), new Place("Gyms", "gym"), new Place("Events", "event")};
    public static final String PLACE_TYPE_KEY = "place type";
    public static final String POST_ID_KEY = "postID";
    public static final String ANDROID_ID_KEY = "androidID";
    public static final String[] LETTERS = {"OP","A","B","C","D","E","F","G","H","I","J","K",
            "L","M","N","O","P","Q","R","S","T","U","V","W","X", "Y","Z","a","b","c",
            "d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v",
            "w","x","y","z","1","2","3","4","5","6","7","8","9"};
    ;
    public static final String POSTS_TYPE_HOT = "HOT";
    public static final String POSTS_TYPE_NEW = "NEW";
    public static final String POSTS_TYPE_YOURS = "YOURS";
    public static final long REFRESH_RATE = 600000;
    public static final int POST_MAXLENGTH = 200;
    public static final String ACTION_START_FROM_TOP_KEY = "start from front key";
    public static final String ACTION_RELOAD_REPLIES_KEY = "reload replies";
    public static final String ACTION_LAUNCH_REPLIES = "launchReplies";
    public static final String LAST_TAB_SELECTED_KEY = "Last tab selected key" ;
    public static final int ALARM_ID = 0;
    public static final String USER_ID_KEY = "userID";
    public static final int MILLISECONDS_BEFORE_POLLING = 1;
    public static final int MILLISECONDS_BETWEEN_POLLING = 500;
    public static final int USERS_CIRCLE_RADIUS = 11;
    public static final float USERS_CIRCLE_STROKE_WIDTH = 7.0f;
    public static final String ACCOUNT_FCM_TOKEN_KEY = "fcmToken";
    public static final String ACCOUNT_INIATILIZED_KEY = "account initialized3";
    public static final String SHOW_MARKERS_KEY = "show markers";
    public static final String MARKER_ID_KEY = "marker id";
    public static final ArrayList<String> MARKER_VALUES = new ArrayList<>(
            Arrays.asList("0", "1", "2", "3", "4", "5", "6" ,"7", "8", "9",
                    "10"));


    public static Map<String, Integer> PLACES = new HashMap<>();
    public static Map<String, Integer> CUSTOM_PLACES = new HashMap<>();

    public static void prepare() {
        PLACES.put("frat", R.drawable.frat_marker);
        PLACES.put("gym", R.drawable.gym_marker);
        PLACES.put("library", R.drawable.library_marker);
        PLACES.put("food", R.drawable.food_marker);
        PLACES.put("building", R.drawable.building_marker);
        PLACES.put("event", R.drawable.event_marker);
        PLACES.put("frat", R.drawable.frat_marker);
        CUSTOM_PLACES.put("0", R.drawable.marker0);
        CUSTOM_PLACES.put("1", R.drawable.marker1);
        CUSTOM_PLACES.put("2", R.drawable.marker2);
        CUSTOM_PLACES.put("3", R.drawable.marker3);
        CUSTOM_PLACES.put("4", R.drawable.marker4);
        CUSTOM_PLACES.put("5", R.drawable.marker5);
        CUSTOM_PLACES.put("6", R.drawable.marker6);
        CUSTOM_PLACES.put("7", R.drawable.marker7);
        CUSTOM_PLACES.put("8", R.drawable.marker8);
        CUSTOM_PLACES.put("9", R.drawable.marker9);
        CUSTOM_PLACES.put("10", R.drawable.marker10);
        CUSTOM_PLACES.put("11", R.drawable.marker11);
    }

}

