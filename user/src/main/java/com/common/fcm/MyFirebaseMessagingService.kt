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
import com.massttr.user.MyApp
import com.massttr.user.R
import com.massttr.user.chat.ChatMessage
import com.massttr.user.chat.SocketService
import com.massttr.user.ui.main.MainActivity
import com.massttr.user.utils.PushUtils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import timber.log.Timber
import java.util.*
import kotlin.random.Random

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
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

    @ObsoleteCoroutinesApi
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
//                intents.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
////                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//                val pendingIntents =
//                    PendingIntent.getActivity(this, Random.nextInt(0, 1000), intents, PendingIntent.FLAG_UPDATE_CURRENT)
//
//                val inboxStyle = NotificationCompat.InboxStyle()
//                var vLastMessage = ""
//
//                when (chatMessage.message_type) {
//                    MessageType.TEXT.type -> vLastMessage = chatMessage.message
//                    MessageType.PHOTO.type -> vLastMessage = "Image"
//                    MessageType.VIDEO.type -> vLastMessage = "Video"
//                    MessageType.CHANGE_PRICE.type -> vLastMessage = "${chatMessage.message} QD Changed Price"
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
//                val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
////                val intent = Intent(applicationContext, MainActivity::class.java)
////                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
////                val pendingIntent = PendingIntent.getActivity(
////                    this, 0 /* Request code */, intent,
////                    PendingIntent.FLAG_UPDATE_CURRENT
////                )
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
//                        .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_foreground))
//                        .setSound(defaultSoundUri)
//                        .setContentIntent(pendingIntents)
////                        .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
////                        .setNumber(chatMessage.count.toInt())
//
////                val badgeCount = chatMessage.count.toInt()
////                 ShortcutBadger.applyCount(this, 3) //for 1.1.4+
//                try{
//                    val notification: Notification = notificationBuilder.build()
//                    ShortcutBadger.applyNotification(applicationContext, notification, chatMessage.count.toInt());
//                   // IconBadgeNumManager().setIconBadgeNum(application, notification, chatMessage.count.toInt())
//                }catch (e:Exception){
//                    e.printStackTrace()
//                }
//
//                notificationManager.notify(
//                    Random.nextInt(0, 1000) /* ID of notification */,
//                    notificationBuilder.build()
//                )
//
//               // notificationManager.notify(Random.nextInt(0, 1000),notification)
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

                val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
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
                        .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_foreground))
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        //.setBadgeIconType(BADGE_ICON_SMALL)
                        //.setNumber(5)
                notificationManager.notify(
                    Random.nextInt(0, 1000) /* ID of notification */,
                    notificationBuilder.build()
                )
                // only masster app notification count
                SocketService.getInstance()?.checkNotificationCount()
            }
        }
    }
}