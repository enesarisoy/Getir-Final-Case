package com.ns.getirfinalcase.data.source.remote

import com.ns.getirfinalcase.core.base.BaseResponse
import kotlinx.coroutines.channels.SendChannel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Callback<T>(
    private val responseChannel: SendChannel<BaseResponse<T>>
) : Callback<T> {

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                responseChannel.trySend(BaseResponse.Success(body))
            } else {
                responseChannel.trySend(BaseResponse.Error("Body is null"))
            }
        } else {
            val errorBody = response.errorBody().toString()
            responseChannel.trySend(BaseResponse.Error(errorBody))
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        responseChannel.trySend(BaseResponse.Error(t.localizedMessage))
    }
}