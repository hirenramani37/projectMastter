package com.massttr.user.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import com.massttr.user.R
import timber.log.Timber

/**
 * Created by Nikul Vaghani on 11-11-2021.
 */

class NotificationChannelHelper private constructor(val context: Context) {

    companion object {
        private lateinit var INSTANCE: NotificationChannelHelper

        @Synchronized
        fun getInstance(context: Context): NotificationChannelHelper {
            if (!this::INSTANCE.isInitialized) INSTANCE = NotificationChannelHelper(context.applicationContext)
            return INSTANCE
        }
    }

    fun createMessageNotificationChannelIfNotExist(soundUri: Uri, lTime: LongArray) {
        val channelId = context.resources.getString(R.string.channel_id_chat)
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            var existingChannel: NotificationChannel? = null
            mNotificationManager.notificationChannels.forEach {
                if (it.id.startsWith(channelId)) {
                    existingChannel = it
                }
            }

            if (existingChannel != null) {
                Timber.e("Notification Channel Already created")
                return
            }

            // Creating an Audio Attribute
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(channelId + System.currentTimeMillis(), channelId, importance)
            mChannel.enableLights(true)
            mChannel.lightColor = Color.GREEN
            mChannel.setShowBadge(true)
//        mChannel.setSound(RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION), audioAttributes)
            mChannel.setSound(soundUri, audioAttributes)
            mChannel.vibrationPattern = lTime
            mChannel.enableVibration(true)

            mNotificationManager.createNotificationChannel(mChannel)
        }
    }

    fun getNotificationChannelId(prefixChannelId: String): String {
        var existingChannel: NotificationChannel? = null
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mNotificationManager.notificationChannels.forEach {
                if (it.id.startsWith(prefixChannelId)) {
                    existingChannel = it
                }
            }
            return existingChannel?.id ?: prefixChannelId
        }
        return ""
    }
}