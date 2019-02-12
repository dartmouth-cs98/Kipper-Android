package com.artfara.apps.kipper;

/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.firebase.messaging.FirebaseMessagingService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            sendNotification(remoteMessage);

        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     * @param remoteMessage FCM message body received.
     */
    private void sendNotification(RemoteMessage remoteMessage) {
        String postID = remoteMessage.getData().get("postID");
        String title = remoteMessage.getNotification().getTitle();
        String messageBody = remoteMessage.getNotification().getBody();

        Intent intent = new Intent(getApplicationContext(), MapsActivity.class); // set notification activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constants.POST_ID_KEY, postID);
        PendingIntent pIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notif = new Notification.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(messageBody)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentIntent(pIntent)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //firstParam allows you to update the notification later on.
        notificationManager.notify(1, notif);
    }
}