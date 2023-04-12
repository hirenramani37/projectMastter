package com.massttr.user.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.massttr.user.MyApp
import com.massttr.user.R
import com.massttr.user.chat.ChatMessage
import com.massttr.user.ui.main.inbox.chat.ChatActivity
import com.massttr.user.ui.main.inbox.chat.MessageType
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.math.max
import kotlin.random.Random


/**
 * Created by Nikul Vaghani on 12-11-2021.
 */
class PushUtils {
    @ObsoleteCoroutinesApi
    @DelicateCoroutinesApi
    companion object {
        private const val SUMMARY_ID = 1010
        private const val vGroupKey = "Chat"

        fun generateChatPush(context: Context, newMessage: ChatMessage) {
            val notificationHelper = MyApp.getInstance().getNotificationHelper()
            val pref = MyApp.getInstance().getPref()
            try {
                val notificationId = newMessage.sender_id.hashCode()

                val mNotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

//                val pendingIntent = NavDeepLinkBuilder(context)
//                    .setComponentName(MainActivity::class.java)
//                    .setGraph(R.navigation.nav_main)
//                    .setDestination(R.id.navInboxFragment)
////                    .setArguments(bundle)
//                    .createPendingIntent()

                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra(ChatActivity.CHAT_MESSAGE, newMessage)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                val pendingIntent =
                    PendingIntent.getActivity(context, Random.nextInt(0, 1000), intent, PendingIntent.FLAG_UPDATE_CURRENT)

                val inboxStyle = NotificationCompat.InboxStyle()
                val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                val iTime = 500

                val lTime = longArrayOf(
                    iTime.toLong(),
                    iTime.toLong(),
                    iTime.toLong(),
                    iTime.toLong(),
                    iTime.toLong()
                )

                notificationHelper.createMessageNotificationChannelIfNotExist(
                    soundUri,
                    lTime
                )

                val chatMessages = pref.unreadChatMessages.filter { it.chat_id == newMessage.chat_id }
                val list = chatMessages.subList(max(chatMessages.size - 7, 0), chatMessages.size)
                Timber.d("unreadCount: ${list.size}")

                val alUnreadMessages = ArrayList(list)
                var vLastMessage = ""
                alUnreadMessages.forEach {
                    Timber.d(it.message)
                    if (it.sender_id == newMessage.sender_id) {
                        when (it.message_type) {
                            MessageType.TEXT.type -> vLastMessage = it.message
                            MessageType.PHOTO.type -> vLastMessage = "Image"
                            MessageType.VIDEO.type -> vLastMessage = "Video"
                            MessageType.CHANGE_PRICE.type -> vLastMessage = "${it.message} QD Changed Price"
                        }
                        inboxStyle.addLine(vLastMessage)
                    }
                }

                val senderName = newMessage.sender_name
                val contentTitle =
                    if (chatMessages?.size!! > 1) "$senderName (${chatMessages.size} messages)" else "$senderName (${chatMessages.size} message)"
                if (chatMessages.size > 1) "$senderName (${chatMessages.size} messages)" else "$senderName (${chatMessages.size} message)"

                val notificationBuilder =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val channelId = notificationHelper.getNotificationChannelId(context.getString(R.string.channel_id_chat))
                        NotificationCompat.Builder(context, channelId)
                    } else {
                        NotificationCompat.Builder(context)
                    }

                notificationBuilder.setContentTitle(contentTitle)
                notificationBuilder.setContentText(vLastMessage)
                notificationBuilder.setSmallIcon(R.mipmap.ic_launcher_foreground)
                notificationBuilder.setStyle(inboxStyle)
//                notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher_foreground))
                notificationBuilder.setContentIntent(pendingIntent)
                notificationBuilder.setAutoCancel(true)
                notificationBuilder.color = ContextCompat.getColor(context, R.color.colorPrimary)
                notificationBuilder.setGroup(vGroupKey)
                notificationBuilder.setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                notificationBuilder.setWhen(System.currentTimeMillis())
                notificationBuilder.setSound(soundUri)
                notificationBuilder.setVibrate(lTime)

                CoroutineScope(Dispatchers.Default).launch {
                    val loader = ImageLoader(context)
                    val request = ImageRequest.Builder(context)
                        .data(newMessage.sender_avatar)
                        .allowHardware(false) // Disable hardware bitmaps.
                        .build()

                    val result = (loader.execute(request) as SuccessResult).drawable
                    val bitmap = (result as BitmapDrawable).bitmap
//                    notificationBuilder.setLargeIcon(bitmap)
                    val circularBitmap = getCircleBitmap(bitmap)
                    notificationBuilder.setLargeIcon(circularBitmap)
                    mNotificationManager.notify(notificationId, notificationBuilder.build())
                    bundleNotification(mNotificationManager, context, soundUri)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun bundleNotification(mNotificationManager: NotificationManager, context: Context, soundUri: Uri) {
            val pref = MyApp.getInstance().getPref()
            val notificationHelper = MyApp.getInstance().getNotificationHelper()
            val totalUnreadChats = pref.unreadChatMessages.groupBy { it.chat_id }.size
            val totalUnreadMessages = pref.unreadChatMessages.size

            val summaryNotification =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channelId = notificationHelper.getNotificationChannelId(context.getString(R.string.channel_id_chat))
                    NotificationCompat.Builder(context, channelId)
                } else {
                    NotificationCompat.Builder(context)
                }

            summaryNotification.setContentTitle("")
            //set content text to support devices running API level < 24
            summaryNotification.setContentText("$totalUnreadMessages messages from $totalUnreadChats chats")
            summaryNotification.setSmallIcon(R.mipmap.ic_launcher_foreground)
            //build summary info into InboxStyle template
            summaryNotification.setStyle(
                NotificationCompat.InboxStyle()
                    .setBigContentTitle("$totalUnreadMessages messages from $totalUnreadChats chats")
                    .setSummaryText("$totalUnreadMessages messages from $totalUnreadChats chats")
            )
            //specify which group this notification belongs to
            summaryNotification.setGroup(vGroupKey)
            //set this notification as the summary for the group
            summaryNotification.setGroupSummary(true)
            summaryNotification.setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
            summaryNotification.color = ContextCompat.getColor(context, R.color.colorPrimary)
//                    .setSound(soundUri)
            summaryNotification.setSound(soundUri)

            mNotificationManager.notify(SUMMARY_ID, summaryNotification.build())
        }

        private fun getCircleBitmap(bitmap: Bitmap): Bitmap? {
            val output = Bitmap.createBitmap(
                bitmap.width,
                bitmap.height, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(output)
            val color: Int = Color.RED
            val paint = Paint()
            val rect = Rect(0, 0, bitmap.width, bitmap.height)
            val rectF = RectF(rect)
            paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = color
            canvas.drawOval(rectF, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, rect, rect, paint)
            bitmap.recycle()
            return output
        }
    }
}

