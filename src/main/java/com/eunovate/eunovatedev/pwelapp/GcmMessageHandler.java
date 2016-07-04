package com.eunovate.eunovatedev.pwelapp;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by EunovateDev on 5/25/2016.
 */
public class GcmMessageHandler extends GcmListenerService {
    public static final int MESSAGE_NOTIFICATION_ID = 435345;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.i("LOG TAG","RECEIVED MSG"+data);
        String msg_body = data.getString("body");
        String msg_tilte = data.getString("title");
        createNotification(msg_tilte, msg_body);
    }

    // Creates notification based on title and body received
    private void createNotification(String title, String body) {
        Log.i("LOG TAG","MESSAGE TITLE "+title);
        Log.i("LOG TAG","MESSAGE BODY"+body);

        Context context = getBaseContext();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title)
                .setContentText(body);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }
}
