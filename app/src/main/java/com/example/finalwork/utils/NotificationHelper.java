package com.example.finalwork.utils;

import android.app.*;import android.content.*;import android.os.Build;import androidx.core.app.NotificationCompat;import com.example.finalwork.R;

public class NotificationHelper{private static final String CHANNEL_ID="exam_channel";public static void notifyExam(Context c,String title,String text,int id){NotificationManager nm=(NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);if(Build.VERSION.SDK_INT>=26){NotificationChannel ch=new NotificationChannel(CHANNEL_ID,"考试提醒",NotificationManager.IMPORTANCE_DEFAULT);nm.createNotificationChannel(ch);}Notification n=new NotificationCompat.Builder(c,CHANNEL_ID).setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title).setContentText(text).setPriority(NotificationCompat.PRIORITY_DEFAULT).setAutoCancel(true).build();nm.notify(id,n);}}
