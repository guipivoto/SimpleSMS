package com.pivoto.simplesms;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class SmsReceiver extends BroadcastReceiver {

    @SuppressLint("ApplySharedPref")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action != null) {
            Log.d(MainActivity.TAG, "action: " + action);
            switch (action) {
                case "android.provider.Telephony.SMS_DELIVER":
                case Telephony.Sms.Intents.SMS_RECEIVED_ACTION:

                    Log.d(MainActivity.TAG, "onReceive() incoming SMS");

                    final Bundle bundle = intent.getExtras();
                    try {
                        if (bundle != null) {

                            final Object[] pdusObj = (Object[]) bundle.get("pdus");
                            final String format = intent.getStringExtra("format");

                            if (pdusObj != null) {
                                for (int i = 0; i < pdusObj.length; i++) {
                                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i], format);
                                    processNewSMS(context, currentMessage);
                                }
                            } else {
                                Log.w(MainActivity.TAG, "Null PDUs");
                            }
                        } else {
                            Log.w(MainActivity.TAG, "Null Bundle");
                        }
                    } catch (Exception e) {
                        StackTraceElement trace[] = e.getStackTrace();
                        Log.e(MainActivity.TAG, e.getMessage());
                        for (StackTraceElement aTrace : trace) {
                            Log.e(MainActivity.TAG, aTrace.toString());
                        }
                    }
                    break;

                case "com.pivoto.simplesms.DELETE_MESSAGE":
                case "com.pivoto.simplesms.BLOCK_CONTACT_MESSAGE": {

                    int notificationId = intent.getIntExtra("notiID", -1);
                    String address = intent.getStringExtra("address");
                    long date = intent.getLongExtra("date", -1);

                    Log.d(MainActivity.TAG, "onReceive() delete message: " + notificationId + " address: " + address + " date: " + date);

                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(notificationId);

                    deleteSMS(context, address, date);

                    if (action.equals("com.pivoto.simplesms.BLOCK_CONTACT_MESSAGE")) {
                        if (!(address == null && date == -1 && notificationId == -1)) {
                            SharedPreferences prefs = context.getSharedPreferences(MainActivity.BLACKLIST_SHARED_PREFS_FILE, Context.MODE_PRIVATE);
                            Set<String> stringSet = new HashSet<>(prefs.getStringSet(MainActivity.BLACKLIST_KEY, new HashSet<String>()));

                            stringSet.add(address);

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putStringSet(MainActivity.BLACKLIST_KEY, stringSet);
                            editor.commit();

                            Log.d(MainActivity.TAG, "onReceive() number added to blacklist");
                        } else {
                            Log.w(MainActivity.TAG, "onReceive() invalid message.. not able to block this number");
                        }
                    }

                    break;
                }
                case "com.pivoto.simplesms.TEST_INTENT": {
                    /*
                    String address = intent.getStringExtra("address");
                    String body = intent.getStringExtra("body");
                    long date = intent.getLongExtra("date", -1);

                    processNewSMS(context, address, date, body);
                    */
                    break;
                }
            }
        } else {
            Log.w(MainActivity.TAG, "action is null");
        }
    }

    private void processNewSMS(Context context, SmsMessage sms) {
        String address = sms.getDisplayOriginatingAddress();
        long date = sms.getTimestampMillis();
        String body = sms.getDisplayMessageBody();

        Log.d(MainActivity.TAG, "processNewSMS() address: " + address + " date: " + date + " body: " + body);

        SharedPreferences prefs = context.getSharedPreferences(MainActivity.BLACKLIST_SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        Set<String> stringSet = prefs.getStringSet(MainActivity.BLACKLIST_KEY, null);

        if (stringSet != null) {
            for (String aStringSet : stringSet)
                Log.d(MainActivity.TAG, "Number Blocked: " + aStringSet);

            if (!stringSet.contains(address)) {
                storeSMS(context, sms);
                displayNotification(context, address, date, body);
            } else {
                Log.d(MainActivity.TAG, "onReceive() Number on blacklist. Ignore message");
                abortBroadcast();
            }
        } else {
            displayNotification(context, address, date, body);
        }
    }

    private void storeSMS(Context context, SmsMessage sms) {
        ContentValues values = new ContentValues();
        values.put(Telephony.Sms.Inbox.ADDRESS, sms.getDisplayOriginatingAddress());
        values.put(Telephony.Sms.DATE, sms.getTimestampMillis());
        values.put(Telephony.Sms.Inbox.BODY, sms.getDisplayMessageBody());
        values.put(Telephony.Sms.Inbox.DATE_SENT, sms.getTimestampMillis());
        values.put(Telephony.Sms.Inbox.PROTOCOL, sms.getProtocolIdentifier());
        values.put(Telephony.Sms.Inbox.READ, 0);
        values.put(Telephony.Sms.Inbox.SEEN, 0);
        values.put(Telephony.Sms.Inbox.SUBJECT, sms.getPseudoSubject());
        values.put(Telephony.Sms.Inbox.REPLY_PATH_PRESENT, sms.isReplyPathPresent() ? 1 : 0);
        values.put(Telephony.Sms.Inbox.SERVICE_CENTER, sms.getServiceCenterAddress());
        context.getContentResolver().insert(Telephony.Sms.Inbox.CONTENT_URI, values);
    }


    public void deleteSMS(Context context, String address, long date) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                    Telephony.Sms.CONTENT_URI,
                    new String[] { "_id", Telephony.Sms.ADDRESS, Telephony.Sms.DATE, Telephony.Sms.BODY },
                    Telephony.Sms.DATE + " = ? AND " + Telephony.Sms.ADDRESS + " = ? " ,
                    new String[] {Long.toString(date), address}, null);

            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                String foundAddress = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
                String foundABody = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));
                long foundDate = cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE));
                Log.d(MainActivity.TAG, "deleteSMS() Result: " + cursor.getCount() + " Message Deleted -  id: " + id + " address: " + foundAddress + " date: " + foundDate + " body: " + foundABody);

                int result = context.getContentResolver().delete(Uri.parse("content://sms/" + id), null, null);

                Log.d(MainActivity.TAG, "Messages deleted: " + result);
            } else {
                if(cursor == null)
                    Log.w(MainActivity.TAG, "cursor is null");
                else
                    Log.w(MainActivity.TAG, "cursor is not null.. count(): " + cursor.getCount());
            }
        } catch (Exception e) {
            StackTraceElement trace[] = e.getStackTrace();
            Log.e(MainActivity.TAG, e.getMessage());
            for (StackTraceElement aTrace : trace) {
                Log.e(MainActivity.TAG, aTrace.toString());
            }
        } finally {
            if(cursor != null)
                cursor.close();
        }
    }


    private void displayNotification(Context context, String address, long date, String body) {

        Log.d(MainActivity.TAG, "displayNotification()");

        String notificationChannel = "channel0";
        String notificationChannelDescription = "Notificação de SMS";

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            final NotificationChannel mChannel = new NotificationChannel(notificationChannel, notificationChannelDescription, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        int id = (int) date;

        Intent clickIntent = new Intent(context, MainActivity.class);

        Intent deleteIntent = new Intent(context, SmsReceiver.class);
        deleteIntent.setAction("com.pivoto.simplesms.DELETE_MESSAGE");
        deleteIntent.putExtra("notiID", id);
        deleteIntent.putExtra("address", address);
        deleteIntent.putExtra("date", date);

        Intent blockIntent = new Intent(context, SmsReceiver.class);
        blockIntent.setAction("com.pivoto.simplesms.BLOCK_CONTACT_MESSAGE");
        blockIntent.putExtra("notiID", id);
        blockIntent.putExtra("address", address);
        blockIntent.putExtra("date", date);

        PendingIntent clickPendingIntent = PendingIntent.getActivity(
                context,
                id+1,
                clickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(
                context,
                id+2,
                deleteIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent blockPendingIntent = PendingIntent.getBroadcast(
                context,
                id+3,
                blockIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender();
        wearableExtender.addAction(new NotificationCompat.Action(R.drawable.ic_delete_white_32dp, context.getString(R.string.delete), deletePendingIntent));
        wearableExtender.addAction(new NotificationCompat.Action(R.drawable.ic_block_white_32dp, context.getString(R.string.block), blockPendingIntent));

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, notificationChannel);
        notificationBuilder.setSmallIcon(R.drawable.ic_message_white_32dp)
                .setContentTitle(address)
                .setContentText(body)
                .setContentIntent(clickPendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setBigContentTitle(address)
                        .bigText(body))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .addAction(R.drawable.ic_delete_white_32dp, context.getString(R.string.delete), deletePendingIntent)
                .addAction(R.drawable.ic_block_white_32dp, context.getString(R.string.block), blockPendingIntent)
                .extend(wearableExtender)
                .setLights(Color.BLUE, 3000, 3000);

        notificationBuilder.setColor(ContextCompat.getColor(context, android.R.color.holo_blue_bright));
        notificationBuilder.setVibrate(new long[]{0, 500, 500});
        notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        Notification mNotificationBar = notificationBuilder.build();
        mNotificationBar.flags |= Notification.DEFAULT_SOUND;
        mNotificationBar.flags |= Notification.FLAG_SHOW_LIGHTS;
        mNotificationBar.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(id, mNotificationBar);
    }
}
