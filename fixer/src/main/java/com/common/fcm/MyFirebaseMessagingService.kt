package com.common.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.common.data.network.model.PushData
import com.common.data.prefs.SharedPref
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.massttr.provider.MyApp
import com.massttr.provider.R
import com.massttr.provider.chat.ChatMessage
import com.massttr.provider.chat.FixerSocketService
import com.massttr.provider.ui.main.MainActivity
import com.massttr.provider.ui.main.availableTasks.viewTask.ViewTaskAcceptActivity
import com.massttr.provider.ui.main.myprofiles.manages.documents.DocumentsActivity
import com.massttr.user.utils.AVAILABLE_FIXDETAIL
import com.massttr.user.utils.PushUtils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import timber.log.Timber
import java.util.*
import kotlin.random.Random

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val pref: SharedPref by lazy { MyApp.getInstance().getPref() }
    private val gson = Gson()

    override fun onNewToken(vToken: String) {
        super.onNewToken(vToken)
        pref.fcmToken = vToken
        Timber.e("Token:=$vToken")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.i("${remoteMessage.data}")

        val vJsonStr = Gson().toJson(remoteMessage.data)
        Timber.d("Push Data: $vJsonStr")
        val pushData = Gson().fromJson(vJsonStr, PushData::class.java)

        pushData?.let {
            Timber.d("pushNotification: ${it.message}")
            generatePush(pushData)
        }
    }

    private fun generatePush(pushData: PushData) {
        when (pushData.type) {
            "CHAT" -> {
                val chatMessage = gson.fromJson(pushData.data, ChatMessage::class.java)
                val list = pref.unreadChatMessages
                list.add(chatMessage)
                pref.unreadChatMessages = list
                PushUtils.generateChatPush(this, chatMessage)

//                val intents = Intent(this, ChatActivity::class.java)
//                intents.putExtra(ChatActivity.CHAT_MESSAGE, chatMessage)
////                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//                val pendingIntents = PendingIntent.getActivity(
//                    this,
//                    Random.nextInt(0, 1000),
//                    intents,
//                    PendingIntent.FLAG_UPDATE_CURRENT
//                )
//
//                val inboxStyle = NotificationCompat.InboxStyle()
//                var vLastMessage = ""
//
//                when (chatMessage.message_type) {
//                    MessageType.TEXT.type -> vLastMessage = chatMessage.message
//                    MessageType.PHOTO.type -> vLastMessage = "Image"
//                    MessageType.VIDEO.type -> vLastMessage = "Video"
//                    MessageType.CHANGE_PRICE.type -> vLastMessage =
//                        "${chatMessage.message} QD Changed Price"
//                }
//                inboxStyle.addLine(vLastMessage)
//
//                val notificationManager =
//                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    val notificationChannel =
//                        NotificationChannel(
//                            getString(R.string.app_name).lowercase(Locale.ROOT),
//                            getString(R.string.app_name).lowercase(Locale.ROOT),
//                            NotificationManager.IMPORTANCE_HIGH
//                        )
//                    notificationChannel.enableLights(true)
//                    notificationChannel.enableVibration(true)
//                    notificationChannel.setShowBadge(true)
//                    notificationManager.createNotificationChannel(notificationChannel)
//                }
//
//                val defaultSoundUri =
//                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//                val intent = Intent(applicationContext, MainActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                val pendingIntent = PendingIntent.getActivity(
//                    this, 0 /* Request code */, intent,
//                    PendingIntent.FLAG_UPDATE_CURRENT
//                )
//
//                val notificationBuilder =
//                    NotificationCompat.Builder(
//                        this,
//                        getString(R.string.app_name).lowercase(Locale.ROOT)
//                    )
//                        .setContentTitle(chatMessage.sender_name)
//                        .setContentText(vLastMessage)
//                        .setAutoCancel(true)
//                        .setSmallIcon(R.mipmap.ic_launcher_foreground)
//                        .setLargeIcon(
//                            BitmapFactory.decodeResource(
//                                resources,
//                                R.mipmap.ic_launcher_foreground
//                            )
//                        )
//                        .setSound(defaultSoundUri)
//                        .setContentIntent(pendingIntents)
//                        .setPriority(NotificationCompat.PRIORITY_HIGH)
//                // .setBadgeIconType(Notification.BADGE_ICON_SMALL)
//                //.setNumber(chatMessage.count.toInt())
//                notificationManager.notify(
//                    Random.nextInt(0, 1000) /* ID of notification */,
//                    notificationBuilder.build()
//                )
//                val notification: Notification = notificationBuilder.build()
//                // val badgeCount = chatMessage.count
////                ShortcutBadger.applyCount(this, badgeCount.toInt()) //for 1.1.4+
////                ShortcutBadger.applyNotification(this, notification, badgeCount.toInt());
//                //    notificationManager.notify(Random.nextInt(0, 1000),notification)
//
//                //this.startSocketService()
            }
            else -> {
                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val notificationChannel =
                        NotificationChannel(
                            getString(R.string.app_name).lowercase(Locale.ROOT),
                            getString(R.string.app_name).lowercase(Locale.ROOT),
                            NotificationManager.IMPORTANCE_HIGH
                        )
                    notificationChannel.enableLights(true)
                    notificationChannel.enableVibration(true)
                    notificationChannel.setShowBadge(true)
                    notificationManager.createNotificationChannel(notificationChannel)
                }
                var intent = Intent()
                val defaultSoundUri =
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val chatMessage = gson.fromJson(pushData.data, ChatMessage::class.java)
                if (pushData.type == "DOCUMENTSTATUS") {
                    intent = Intent(applicationContext, MainActivity::class.java)
                    intent.putExtra("DOCUMENTSTATUS", true)
//                    intent = Intent(applicationContext, DocumentsActivity::class.java)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                } else if (pushData.type == "NEWJOB") {
                    intent = Intent(applicationContext, ViewTaskAcceptActivity::class.java)
                    intent.putExtra(AVAILABLE_FIXDETAIL, chatMessage.job_id)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                } else if (pushData.type == "PRICECHANGE" || pushData.type == "JOBSTATUS") {
                    intent = Intent(applicationContext, ViewTaskAcceptActivity::class.java)
                    intent.putExtra(AVAILABLE_FIXDETAIL, chatMessage.job_id)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }

                val pendingIntent = PendingIntent.getActivity(
                    this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                val notificationBuilder =
                    NotificationCompat.Builder(
                        this,
                        getString(R.string.app_name).lowercase(Locale.ROOT)
                    )
                        .setContentTitle(getString(R.string.app_name).lowercase(Locale.ROOT))
                        .setContentText(pushData.message)
                        .setAutoCancel(true)
                        .setSmallIcon(R.mipmap.ic_launcher_foreground)
                        .setLargeIcon(
                            BitmapFactory.decodeResource(
                                resources,
                                R.mipmap.ic_launcher_foreground
                            )
                        )
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)

                notificationManager.notify(
                    Random.nextInt(0, 1000) /* ID of notification */,
                    notificationBuilder.build()
                )
                // only masster app notification count
                FixerSocketService.getInstance()?.checkNotificationCount()
            }
        }
    }
}
