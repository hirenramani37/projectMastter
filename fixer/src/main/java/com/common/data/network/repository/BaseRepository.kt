package com.common.data.network.repository

import com.common.data.ApiError
import com.common.data.ApiEvent
import com.common.data.ApiSuccess
import com.common.data.network.model.ResponseCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber

abstract class BaseRepository {

    protected suspend fun <T> callApi(apiCall: suspend () -> Response<T>): ApiEvent<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall()
                //Listen APIs Response/Failure in @Dispatchers.Main thread
                if (response.code() == ResponseCode.OK.code || response.code() == ResponseCode.Crated.code)
                    withContext(Dispatchers.Main) { ApiSuccess(response.body()) }
                else
                    throw HttpException(response)
            } catch (e: Exception) {
                Timber.e("BRError: ${e.message}")
                ApiError(e)
            }
        }
    }
}