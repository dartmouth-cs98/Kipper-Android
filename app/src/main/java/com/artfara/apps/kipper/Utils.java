package com.artfara.apps.kipper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import com.artfara.apps.kipper.models.College;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Van on 10/6/16.
 */

public class Utils {
    private static final String TAG = " Utils";

    public static boolean setDatabaseIfPossible(Context context) {
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        Gson gson = new Gson();
        String collegeJSON = prefs.getString(Constants.COLLEGE_KEY, null);
//        Log.d(TAG, "collegeJSON " + collegeJSON);
        if (collegeJSON == null) {
            return false;
        }
        Globals.COLLEGE = gson.fromJson(collegeJSON, College.class);
//        Log.d(TAG, "collegeName" + Globals.COLLEGE.name);
        Globals.initialize();
        return true;
    }
    public static void startAlarmTrackingService(Context context) {
        //Start Alarm Manager Tracking Service
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        boolean alarmUp = (PendingIntent.getBroadcast(context, Constants.ALARM_ID, alarmIntent, PendingIntent.FLAG_NO_CREATE) != null);
//        Log.d(TAG, "startAlarmTrackingServiceCalled");
        if (!alarmUp) {
            PendingIntent recurringAlarm = PendingIntent.getBroadcast(context, Constants.ALARM_ID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Calendar updateTime = Calendar.getInstance();
            alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), 1000000, recurringAlarm); //you can modify the interval of course
        }
    }
    //phone unique ID for recording each post or reply
    public static String getAndroidID(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String androidID = prefs.getString(Constants.ANDROID_ID_KEY, null);
        if (androidID == null){
            androidID = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            if (androidID == null) {
                androidID = FirebaseDatabase.getInstance().getReference().push().getKey();
            }
            prefs.edit().putString(Constants.ANDROID_ID_KEY, androidID).apply();
        }
        return androidID;
    }
    //Non-phone unqique ID for recording user's location
    public static String getUserID(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String userID = prefs.getString(Constants.USER_ID_KEY, null);
//        Log.d(TAG, "getting user id " + userID);
        if (userID == null) {
//            Log.d(TAG, "user id null");
            userID = FirebaseDatabase.getInstance().getReference()
                    .child(Globals.USERS_WRITE_TABLE_NAME).push().getKey();
            prefs.edit().putString(Constants.USER_ID_KEY, userID).apply();
        }
        return userID;
    }

    public static String getCurrentFormattedTime(){
        //For testing
        Date today = new Date();
        final DateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd hhmmss");
        dateFormatter.setLenient(false);
        return dateFormatter.format(today);
    }
    public static void setCurrentLocation() {}

    public static void sendFCMTokenToServer(Context context) {
        String token = "";
        try {
            token = FirebaseInstanceId.getInstance().getToken();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (token != null && !token.equals("")) {
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(Constants.ACCOUNT_FCM_TOKEN_KEY, token);
            FirebaseDatabase.getInstance().getReference()
                    .child(Globals.ACCOUNTS_TABLE_NAME).child(getAndroidID(context))
                    .updateChildren(childUpdates);
        }
    }

    public static String getFormattedTime() {
        DateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        return outputFormat.format(new Date());
    }
}
