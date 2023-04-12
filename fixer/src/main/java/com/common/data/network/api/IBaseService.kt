package com.common.data.network.api

import com.common.data.network.repository.DecryptionInterceptor
import com.common.data.network.repository.EncryptionInterceptor
import com.common.multilanguage.LocaleManager
import com.massttr.provider.MyApp
import com.massttr.provider.BuildConfig
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

interface IBaseService {
    companion object {

        const val Authorization = "Authorization"
        const val IS_REFRESH_TOKEN = "isRefreshToken"
        const val Accept = "Accept"
        const val DeviceType = "device-type"
        private const val AcceptLanguage = "Accept-Language"
        private const val ContentType = "Content-Type"
//        private const val TIME_OUT = 120L
        private const val TIME_OUT = 20L

        @ObsoleteCoroutinesApi
        @DelicateCoroutinesApi
        fun getOkHttpClient(needEncrypt: Boolean): OkHttpClient {
            val pref = MyApp.getInstance().getPref()
            val httpClient = OkHttpClient.Builder()
            httpClient.connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            httpClient.readTimeout(TIME_OUT, TimeUnit.SECONDS)
            httpClient.writeTimeout(TIME_OUT, TimeUnit.SECONDS)

            if (needEncrypt) {
                httpClient.addInterceptor(EncryptionInterceptor())
                httpClient.addInterceptor(DecryptionInterceptor())
            }

            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .method(original.method, original.body)
                    .header(Accept, "application/json")
                    .header(ContentType, "multipart/form-data")
                    .header(AcceptLanguage, pref.selectLanguage?:"ar")

                if (!pref.authToken.isNullOrBlank())
                    requestBuilder.header(Authorization, "${pref.authToken}")

                return@addInterceptor chain.proceed(requestBuilder.build())
            }

            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor()
                logging.apply { logging.level = HttpLoggingInterceptor.Level.BODY }
                httpClient.addInterceptor(logging)
            }
            return httpClient.build()
        }
    }
}