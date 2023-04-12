package com.common.data.prefs

import android.content.Context
import android.content.Intent
import com.common.data.network.model.UserInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.massttr.provider.chat.ChatMessage
import com.massttr.provider.chat.FixerSocketService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import java.util.ArrayList

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
class SharedPref(context: Context, private val gson: Gson) : EncryptedPreferences(context) {

    val type = object : TypeToken<ArrayList<ChatMessage>>() {}.type

    private inline fun <reified T> toJson(value: T?) =
        if (value == null) null else gson.toJson(value)

    private inline fun <reified T> fromJson(value: String?) =
        if (value.isNullOrEmpty()) null else gson.fromJson(value, T::class.java)

    fun clearAppUserData() {
        val intent = Intent(context, FixerSocketService::class.java)
        context.stopService(intent)
        clearPreferences()
    }

    var authToken: String?
        get() = getString(this::authToken.name)
        set(value) = setString(this::authToken.name, value)

    var accountCreatedDate: String?
        get() = getString(this::accountCreatedDate.name)
        set(value) = setString(this::accountCreatedDate.name, value)

    var fcmToken: String?
        get() = getString(this::fcmToken.name)
        set(value) = setString(this::fcmToken.name, value)

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

    var selectLanguage: String?
        get() = getString(this::selectLanguage.name)
        set(value) = setString(this::selectLanguage.name, value)

    var isOnline: Boolean?
        get() = getBoolean(this::isOnline.name, false)
        set(value) = setBoolean(this::isOnline.name, value)

    var isLogin: Boolean?
        get() = getBoolean(this::isLogin.name, false)
        set(value) = setBoolean(this::isLogin.name, value)

    var isLanguage: Boolean?
        get() = getBoolean(this::isLanguage.name, false)
        set(value) = setBoolean(this::isLanguage.name, value)

    var isArabic: Boolean?
        get() = getBoolean(this::isArabic.name, false)
        set(value) = setBoolean(this::isArabic.name, value)

    var kmProgress: Int?
        get() = getInt(this::kmProgress.name)
        set(value) = setInt(this::kmProgress.name, value ?: 5)

    var chatCount:Int?
    get() = getInt(this::chatCount.name)
    set(value) = setInt(this::chatCount.name,value?:0)

    var notificationCount:Int?
        get() = getInt(this::notificationCount.name)
        set(value) = setInt(this::notificationCount.name,value?:0)

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
}