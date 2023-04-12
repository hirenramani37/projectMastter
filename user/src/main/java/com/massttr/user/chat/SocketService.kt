package com.massttr.user.chat

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import com.massttr.user.utils.*
import com.google.gson.Gson
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.engineio.client.transports.WebSocket
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import com.google.gson.reflect.TypeToken
import com.massttr.user.MyApp
import com.massttr.user.ui.main.MainActivity
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Nikul Vaghani on 26-10-2021.
 */

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class SocketService : Service() {

    val app by lazy { MyApp.getInstance() }
    val pref by lazy { app.getPref() }
    val gson = Gson()
    var mainActivity = MainActivity()

    companion object {
        private var TIMEOUT = 60 * 1000
        private var socket: Socket? = null
        private var service: SocketService? = null
        fun getSocketInstance(): Socket? = socket
        fun getInstance(): SocketService? = service
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("onCreate")
        service = this
        mainActivity = MainActivity()
    }

    override fun onBind(p0: Intent?): IBinder = Binder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        connectSocket()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        socket?.disconnect()
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        socket?.disconnect()
        super.onTaskRemoved(rootIntent)
    }

    private fun connectSocket() {
        Timber.i("connectSocket")
        val options = IO.Options()
        options.forceNew = true
        options.timeout = TIMEOUT.toLong() //set -1 to  disable it
        options.reconnection = true
        options.reconnectionDelay = 3000
        options.reconnectionDelayMax = 60000
        options.reconnectionAttempts = 99999
        options.transports = arrayOf(WebSocket.NAME)
        options.query = "user_id=${pref.userInfo?.id}&type=${2}&device_type=A"
//        socket = IO.socket("http://192.168.0.87:8000", options)
//        socket = IO.socket("http://51.158.47.247:8000", options)
        socket = IO.socket("http://134.209.237.135:8000", options)

        socket?.on(Socket.EVENT_CONNECT_ERROR, onError)
        socket?.on(Socket.EVENT_CONNECT, onConnect)
        socket?.on(Socket.EVENT_DISCONNECT, onDisconnect)
        socket?.on("listChats", onListChat)
        socket?.on("online", onOnline)
        socket?.on("typing", onTyping)
        socket?.on("getMessages", onGetMessages)
        socket?.on("chatMessage", onNewMessage)
        socket?.on("messageDelivered", onDeliveredMessage)
        socket?.on("deliveredAll", onDeliveredAllMessages)
        socket?.on("messageRead", onReadMessage)
        socket?.on("readAll", onReadAllMessages)
        socket?.on("unreadCountNotification", onUnReadCountNotification)
        socket?.on("unreadCountChat", onUnReadCountChat)

        socket?.connect()
    }

    private val onError = Emitter.Listener { args ->
        Timber.e(args.contentToString())
    }
    private val onConnect = Emitter.Listener { args ->
        Timber.i(args.contentToString())
    }
    private val onDisconnect = Emitter.Listener { args ->
        Timber.e(args.contentToString())
    }

    private val onUnReadCountChat =
        Emitter.Listener { args ->
            //Timber.i(args.contentToString())
            val data = args[0] as Int
            Timber.e("onUnReadCountChat $data")
            pref.chatCount = data
            val bundle = Bundle()
            bundle.putInt(BUS_EVENT_CHAT_COUNT,data)
            EventBus.publish(bundle)
           // (mainActivity).inboxCount(data)

        }

    private val onUnReadCountNotification =
        Emitter.Listener { args ->
            Timber.i(args.contentToString())
            val data = args[0] as Int
            Timber.e("onUnReadCountNotification $data")
            pref.notificationCount = data
            val bundle = Bundle()
            bundle.putInt(BUS_EVENT_NOTIFICATION_COUNT,data)
            EventBus.publish(bundle)
        }

    private val onListChat =
        Emitter.Listener { args ->
            Timber.i(args.contentToString())
            val data = args[0] as JSONArray

            val listType: Type = object : TypeToken<ArrayList<Inbox?>?>() {}.type
            val inboxList: ArrayList<Inbox> = gson.fromJson(data.toString(), listType)

            val bundle = Bundle()
            bundle.putParcelableArrayList(BUS_EVENT_INBOX_LIST, inboxList)
            EventBus.publish(bundle)
        }

    private val onOnline =
        Emitter.Listener { args ->
            Timber.i(args.contentToString())
            val data = args[0] as JSONObject

            val online = gson.fromJson(data.toString(), Online::class.java)

            val bundle = Bundle()
            bundle.putParcelable(BUS_EVENT_ONLINE, online)
            EventBus.publish(bundle)
        }

    private val onTyping =
        Emitter.Listener { args ->
            Timber.i(args.contentToString())
            val data = args[0] as JSONObject

            val typing = gson.fromJson(data.toString(), Typing::class.java)

            val bundle = Bundle()
            bundle.putParcelable(BUS_EVENT_TYPING, typing)
            EventBus.publish(bundle)
        }

    private val onGetMessages =
        Emitter.Listener { args ->
            Timber.i(args.contentToString())
            val data = args[0] as JSONArray

            val listType: Type = object : TypeToken<ArrayList<ChatMessage?>?>() {}.type
            val chatMessageList: ArrayList<Inbox> = gson.fromJson(data.toString(), listType)

            val bundle = Bundle()
            bundle.putParcelableArrayList(BUS_EVENT_MESSAGES_LIST, chatMessageList)
            EventBus.publish(bundle)
        }

    private val onNewMessage =
        Emitter.Listener { args ->
            Timber.i(args.contentToString())
            val data = args[0] as JSONObject

            val chatMessage = gson.fromJson(data.toString(), ChatMessage::class.java)

            if (chatMessage.sender_id == pref.userInfo?.id) {
                val bundle = Bundle()
                bundle.putParcelable(BUS_EVENT_MESSAGE_RECEIVED, chatMessage)
                EventBus.publish(bundle)
            } else {
                sendDeliveredMessage(chatMessage.chat_id, chatMessage.m_id)
                val list = pref.unreadChatMessages
                list.add(chatMessage)
                pref.unreadChatMessages = list

                if (CURRENT_OTHER_USER_ID == chatMessage.sender_id) {
                    Timber.d("$CURRENT_OTHER_USER_ID")
                    val bundle = Bundle()
                    bundle.putParcelable(BUS_EVENT_MESSAGE_RECEIVED, chatMessage)
                    EventBus.publish(bundle)
                } else {
                    PushUtils.generateChatPush(MyApp.getInstance(), chatMessage)
                }
            }

            val bundle = Bundle()
            bundle.putParcelable(BUS_EVENT_MESSAGE_RECEIVED_INBOX, chatMessage)
            EventBus.publish(bundle)
        }

    private val onDeliveredMessage =
        Emitter.Listener { args ->
            Timber.i(args.contentToString())
            val data = args[0] as JSONObject
            val deliveredMessage = gson.fromJson(data.toString(), Message::class.java)

            val bundle = Bundle()
            bundle.putParcelable(BUS_EVENT_DELIVERED_MESSAGE, deliveredMessage)
            EventBus.publish(bundle)
        }

    private val onDeliveredAllMessages =
        Emitter.Listener { args ->
            Timber.i(args.contentToString())
            val data = args[0] as JSONObject
            val deliveredAllMessage = gson.fromJson(data.toString(), InboxItem::class.java)

            val bundle = Bundle()
            bundle.putParcelable(BUS_EVENT_DELIVERED_ALL_MESSAGES, deliveredAllMessage)
            EventBus.publish(bundle)
        }

    private val onReadMessage =
        Emitter.Listener { args ->
            Timber.i(args.contentToString())
            val data = args[0] as JSONObject
            val readMessage = gson.fromJson(data.toString(), Message::class.java)

            val bundle = Bundle()
            bundle.putParcelable(BUS_EVENT_READ_MESSAGE, readMessage)
            EventBus.publish(bundle)
        }

    private val onReadAllMessages =
        Emitter.Listener { args ->
            Timber.i(args.contentToString())
            val data = args[0] as JSONObject
            val readAllMessage = gson.fromJson(data.toString(), InboxItem::class.java)

            val bundle = Bundle()
            bundle.putParcelable(BUS_EVENT_READ_ALL_MESSAGES, readAllMessage)
            EventBus.publish(bundle)
        }


    fun sendTypingStatus(isTyping: Boolean, chatId: String) {
        val obj = JSONObject()
        obj.put("chat_id", chatId)
        obj.put("userId", pref.userInfo?.id)
        obj.put("type", "2")
        obj.put("typing", if (isTyping) 1 else 0)

        Timber.i("$obj")
        socket?.emit("typing", obj)
    }

    fun sendCreateChat(to: Int, jobId: Int) {
        Timber.d("$to : $jobId: ${socket?.connected()}")
        val obj = JSONObject()
        obj.put("from", pref.userInfo?.id)
        obj.put("to", to)
        obj.put("job_id", jobId)
        obj.put("type", "2")

        Timber.i("$obj")
        socket?.emit("createChat", obj, object : Ack {
            override fun call(vararg args: Any?) {
                /**
                 * we can rerun it like listeners but we want as bus event as of chat might be created from different screens
                 * * code commented due to inbox list will be getting affected by another event called "listChats"
                 * */

                val data = args[0] as JSONObject
                val inbox = gson.fromJson(data.toString(), Inbox::class.java)

                val bundle = Bundle()
                bundle.putParcelable(BUS_EVENT_CREATE_CHAT, inbox)
                EventBus.publish(bundle)
            }
        })
    }

    fun sendListChats(offset: Int) {
        Timber.d("$offset : ${socket?.connected()}")
        val obj = JSONObject()
        obj.put("offset", offset)
        obj.put("type", "2")

        Timber.i("$obj")
        socket?.emit("listChats", obj)
    }

    fun sendChatMessage(
        to: Int,
        message: String,
        messageType: Int,
        chatId: String,
        senderAvatar: String/*need to send whole image with baseUrl*/,
        senderName: String, jobId: Int
    ) {
        Timber.d("$to : $message : $chatId : ${socket?.connected()}")
        val obj = JSONObject()
        obj.put("from", pref.userInfo?.id)
        obj.put("to", to)
        obj.put("type", messageType)
        obj.put("message", message)
        obj.put("chat_id", chatId)
        obj.put("m_id", getUniqueId())
        obj.put("sender_avatar", senderAvatar)
        obj.put("sender_name", senderName)
        obj.put("job_id", jobId)

        Timber.i("$obj")
        socket?.emit("chatMessage", obj)
    }

    fun sendGetMessage(chatId: String, offset: Int) {
        Timber.d("$chatId : $offset : ${socket?.connected()}")
        val obj = JSONObject()
        obj.put("chat_id", chatId)
        obj.put("offset", offset)

        Timber.i("$obj")
        socket?.emit("getMessages", obj)
    }

    private fun sendDeliveredMessage(chatId: String, mId: String) {
        Timber.d("$chatId : ${socket?.connected()}")
        val obj = JSONObject()
        obj.put("chat_id", chatId)
        obj.put("m_id", mId)
        obj.put("user_id", pref.userInfo?.id)

        Timber.i("$obj")
        socket?.emit("deliverMessage", obj)
    }

    fun sendReadMessage(chatId: String, mId: String) {
        Timber.d("$chatId : ${socket?.connected()}")
        val obj = JSONObject()
        obj.put("chat_id", chatId)
        obj.put("m_id", mId)
        obj.put("user_id", pref.userInfo?.id)

        Timber.i("$obj")
        socket?.emit("readMessage", obj)
    }

    fun sendReadAllMessages(chatId: String) {
        Timber.d("$chatId : ${socket?.connected()}")
        val obj = JSONObject()
        obj.put("chat_id", chatId)
        obj.put("user_id", pref.userInfo?.id)

        Timber.i("$obj")
        socket?.emit("readAllMessages", obj)
    }

    fun checkNotificationCount(){
        // fixer - 1 , user - 2
        val obj = JSONObject()
        obj.put("user_id",pref.userInfo?.id.toString())
        obj.put("type",2)
        socket?.emit("checkNotificationCount",obj)
    }

    fun readNotification(){
        val obj = JSONObject()
        obj.put("user_id",pref.userInfo?.id.toString())
        obj.put("type",2)
        socket?.emit("readNotification",obj)
    }

    private fun getUniqueId() = UUID.randomUUID().toString().replace("-", "")
}