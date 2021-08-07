package com.example.mediguide.notification;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.mediguide.R;

import java.util.Random;

public class RefillNotification extends BroadcastReceiver {

    private String message;
    int id;
    private NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Random random = new Random();
        notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        System.out.println("Broadcast here .............................................");

        //get the refill count message
        if (intent.getExtras() != null) {
            message =  intent.getStringExtra("message");
            id = intent.getIntExtra("randomId", random.nextInt());
        }

        //To show notification
        Uri alarmSound =
                RingtoneManager. getDefaultUri (RingtoneManager. TYPE_NOTIFICATION );
        MediaPlayer mp = MediaPlayer. create (context, alarmSound);
        mp.start();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_baseline_access_alarm_24)
                .setContentTitle("Refill Notification")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)//to show content in lock screen
                .setPriority(NotificationCompat.PRIORITY_HIGH);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(
                    String.valueOf(id),
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(String.valueOf(id));
        }

        notificationManager.notify(id, builder.build());

        System.out.println("Broadcast end................................");

        Intent intent1 = new Intent(context.getApplicationContext(), RefillNotification.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), id, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);

        alarmManager.cancel(pendingIntent);


    }
}