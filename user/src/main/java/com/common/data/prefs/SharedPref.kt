package com.common.data.prefs

import android.content.Context
import android.content.Intent
import com.common.data.network.model.UserInfo

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.massttr.user.chat.ChatMessage
import com.massttr.user.chat.SocketService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import java.lang.reflect.Type
import java.util.ArrayList

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class SharedPref(context: Context, private val gson: Gson) : EncryptedPreferences(context) {

    private inline fun <reified T> toJson(value: T?) =
        if (value == null) null else gson.toJson(value)

    private inline fun <reified T> fromJson(value: String?) =
        if (value.isNullOrEmpty()) null else gson.fromJson(value, T::class.java)

    var fcmToken: String?
        get() = getString(this::fcmToken.name)
        set(value) = setString(this::fcmToken.name, value)

    var authToken: String?
        get() = getString(this::authToken.name)
        set(value) = setString(this::authToken.name, value)

    var userInfo: UserInfo?
        set(value) = setString(this::userInfo.name, toJson(value))
        get() = try {
            fromJson(getString(this::userInfo.name))
        } catch (e: Exception) {
            null
        }

    var mtUserId: String?
        get() = getString(this::mtUserId.name)
        set(value) = setString(this::mtUserId.name, value)

    var isOnline: Boolean?
        get() = getBoolean(this::isOnline.name, false)
        set(value) = setBoolean(this::isOnline.name, value)

    var isLogin: Boolean?
        get() = getBoolean(this::isLogin.name, false)
        set(value) = setBoolean(this::isLogin.name, value)

    var isLanguage: Boolean?
        get() = getBoolean(this::isLanguage.name, false)
        set(value) = setBoolean(this::isLanguage.name, value)

    var ordersIds: String?
        get() = getString(this::ordersIds.name)
        set(value) = setString(this::ordersIds.name, value)


    var unreadChatMessages: ArrayList<ChatMessage>
        // get() = fromJson(getString(this::unreadChatMessages.name),type)
        get() {
            val type = object : TypeToken<ArrayList<ChatMessage>>() {}.type
            val list = (gson.fromJson(getString(this::unreadChatMessages.name), type) as ArrayList<ChatMessage>?)
            return ArrayList(list.orEmpty())
        }
        set(value) = setString(this::unreadChatMessages.name, toJson(value))

    var chatCount:Int?
        get() = getInt(this::chatCount.name)
        set(value) = setInt(this::chatCount.name,value?:0)

    var notificationCount:Int?
        get() = getInt(this::notificationCount.name)
        set(value) = setInt(this::notificationCount.name,value?:0)




//    fun getChat(chatId: String): List<ChatMessage>? {
//        return try {
//            val type: Type = object : TypeToken<ArrayList<ChatMessage?>?>() {}.type
//            gson.fromJson(getString(chatId), type)
//        } catch (e: Exception) {
//            null
//        }
//    }
//
//    fun setChat(chatId: String, list: List<ChatMessage>) {
//        setString(chatId, toJson(list))
//    }


    fun clearAppUserData() {
        val intent = Intent(context, SocketService::class.java)
        context.stopService(intent)
        clearPreferences()
    }
}