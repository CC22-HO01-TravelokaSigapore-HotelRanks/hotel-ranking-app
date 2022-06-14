package com.c22ho01.hotelranking.viewmodel.utils

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.c22ho01.hotelranking.data.remote.response.auth.LoginResponse
import com.c22ho01.hotelranking.data.remote.retrofit.APIConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TokenWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val refreshToken = inputData.getString(REFRESH_TOKEN)
        var data = workDataOf()
        return try {
            APIConfig.getAuthAPIService().refreshToken("refreshToken=$refreshToken")
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (response.isSuccessful) {
                            val token = response.body()?.loginData?.accessToken.toString()
                            data = workDataOf(NEW_TOKEN to token)
                            Log.e(TAG, data.getString(NEW_TOKEN) ?: "")
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Log.e(TAG, "onFailure: ${t.message}")
                    }
                })
            Result.success(data)
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            Result.failure()
        }
    }

    companion object {
        private val TAG = TokenWorker::class.java.simpleName
        const val REFRESH_TOKEN = "refresh_token"
        const val NEW_TOKEN = "new_token"
    }
}
