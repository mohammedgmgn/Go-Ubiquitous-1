/*
 *    Copyright 2016 Gurupad Mamadapur
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.protino.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import io.github.protino.R;
import io.github.protino.activity.MainActivity;

/**
 * Created by Gurupad Mamadapur on 11/13/2016.
 */

public class MyFcmListenerService extends FirebaseMessagingService {

    public static final int NOTIFICATION_ID = 1;
    private static final String TAG = MyFcmListenerService.class.getSimpleName();
    private static final String EXTRA_WEATHER = "weather";
    private static final String EXTRA_LOCATION = "location";

    @Override
    public void onMessageReceived(RemoteMessage message) {
        String from = message.getFrom();
        Map data = message.getData();
        if (!data.isEmpty()) {
            String senderId = getString(R.string.gcm_senderId);
            if (senderId.length() == 0) {
                Log.e(TAG, "SenderID string needs to be set");
            }
            if ((senderId).equals(from)) {
                String weather = (String) data.get(EXTRA_WEATHER);
                String location = (String) data.get(EXTRA_LOCATION);
                String alert = String.format(getString(R.string.gcm_weather_alert), weather, location);
                sendNotification(alert);
            }
            Log.i(TAG, "onMessageReceived: " + data.toString());
        }
    }

    /**
     * Put the message into a notification and post it.
     * This is just one simple example of what you might choose to do with a GCM message.
     *
     * @param message The alert message to be posted.
     */
    private void sendNotification(String message) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent =
                PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        // Notifications using both a large and a small icon (which yours should!) need the large
        // icon as a bitmap. So we need to create that here from the resource ID, and pass the
        // object along in our notification builder. Generally, you want to use the app icon as the
        // small icon, so that users understand what app is triggering this notification.
        Bitmap largeIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.art_storm);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.art_clear)
                        .setLargeIcon(largeIcon)
                        .setContentTitle("Weather Alert!")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
