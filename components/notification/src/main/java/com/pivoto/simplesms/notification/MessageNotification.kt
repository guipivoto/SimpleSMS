package com.pivoto.simplesms.notification

import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.pivoto.simplesms.message.Message
import com.pivoto.simplesms.notification.util.Tags
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

class MessageNotification {

    lateinit var context: Context

    fun displayNotification(smsMessage: Message) {

        val notificationManager = NotificationManagerCompat.from(context)
        val channel = NotificationChannel(
            NEW_SMS_CHANNEL,
            context.getString(NEW_SMS_CHANNEL_DESCRIPTION_RES),
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val notificationId = smsMessage.date.toInt()

        val clickIntent = context.packageManager.getLaunchIntentForPackage("com.pivoto.simplesms");

        val deleteIntent = Intent("com.pivoto.simplesms.DELETE_MESSAGE")
        deleteIntent.component = ComponentName(context, "com.pivoto.simplesms.receiver.SmsReceiver")
        deleteIntent.putExtra(NOTIFICATION_ID_KEY, notificationId)
        deleteIntent.putExtra(ADDRESS_KEY, smsMessage.address)
        deleteIntent.putExtra(DATE_KEY, smsMessage.date)

        val blockIntent = Intent("com.pivoto.simplesms.BLOCK_CONTACT_MESSAGE")
        blockIntent.component = ComponentName(context, "com.pivoto.simplesms.receiver.SmsReceiver")
        blockIntent.putExtra(NOTIFICATION_ID_KEY, notificationId)
        blockIntent.putExtra(ADDRESS_KEY, smsMessage.address)
        blockIntent.putExtra(DATE_KEY, smsMessage.date)

        val intentFlags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        val clickPendingIntent = PendingIntent.getActivity(
            context,
            notificationId + 1,
            clickIntent,
            intentFlags
        )

        val deletePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 2,
            deleteIntent,
            intentFlags
        )

        val blockPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 3,
            blockIntent,
            intentFlags
        )

        val wearableExtender = NotificationCompat.WearableExtender()
        wearableExtender.addAction(
            NotificationCompat.Action(
                R.drawable.ic_delete_white_32dp,
                context.getString(R.string.delete),
                deletePendingIntent
            )
        )
        wearableExtender.addAction(
            NotificationCompat.Action(
                R.drawable.ic_block_white_32dp,
                context.getString(R.string.block),
                blockPendingIntent
            )
        )

        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, NEW_SMS_CHANNEL).apply {
                setSmallIcon(R.drawable.ic_message_white_32dp)
                setContentTitle(smsMessage.address)
                setContentText(smsMessage.body)
                setContentIntent(clickPendingIntent)
                setStyle(
                    NotificationCompat.BigTextStyle()
                        .setBigContentTitle(smsMessage.address)
                        .bigText(smsMessage.body)
                )
                setLargeIcon(
                    BitmapFactory.decodeResource(
                        context.resources,
                        R.mipmap.ic_launcher
                    )
                )
                addAction(
                    R.drawable.ic_delete_white_32dp,
                    context.getString(R.string.delete),
                    deletePendingIntent
                )
                addAction(
                    R.drawable.ic_block_white_32dp,
                    context.getString(R.string.block),
                    blockPendingIntent
                )
                extend(wearableExtender)
                setLights(Color.BLUE, 3000, 3000)
                color = ContextCompat.getColor(context, android.R.color.holo_blue_bright)
                setVibrate(longArrayOf(0, 500, 500))
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            }

        val notification = notificationBuilder.build().apply {
            flags =
                flags or Notification.DEFAULT_SOUND or Notification.FLAG_SHOW_LIGHTS or Notification.FLAG_AUTO_CANCEL
        }

        notificationManager.notify(notificationId, notification)
    }

    fun clearNotification(extras: Bundle?) {
        if(extras != null) {
            val notiId = extras.getInt(NOTIFICATION_ID_KEY, -1)
            Log.v(Tags.NOTIFICATION, "Clearing notification with ID: $notiId")
            if(notiId > 0) {
                NotificationManagerCompat.from(context).cancel(notiId)
            }
        } else {
            Log.w(Tags.NOTIFICATION, "Can't clear notification. Extras is null")
        }
    }

    companion object {
        private const val NEW_SMS_CHANNEL = "new_sms_channel"
        private val NEW_SMS_CHANNEL_DESCRIPTION_RES = R.string.new_message_notification_channel
        private const val NOTIFICATION_ID_KEY = "notification_id"
        private const val ADDRESS_KEY = "address"
        private const val DATE_KEY = "date"
    }
}

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Singleton
    @Provides
    fun providesNotificationModule(app: Application) = MessageNotification().apply {
        this.context = app.baseContext
    }
}

