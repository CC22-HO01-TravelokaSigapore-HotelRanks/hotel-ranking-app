package com.c22ho01.hotelranking.viewmodel.utils

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.c22ho01.hotelranking.data.remote.response.auth.LoginResponse
import com.c22ho01.hotelranking.data.remote.retrofit.APIConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TokenWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    private var resultStatus: Result = Result.success()

    override fun doWork(): Result {
        val refreshToken = inputData.getString(REFRESH_TOKEN)
        return refreshToken.getCurrentToken()
    }

    private fun String?.getCurrentToken(): Result {
        APIConfig.getAuthAPIService().refreshToken("refreshToken=$this")
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    resultStatus = if (response.isSuccessful) {
                        NEW_ACCESS_TOKEN = response.body()?.loginData?.accessToken.toString()
                        Log.e("NEW_ACCESS_TOKEN: ", NEW_ACCESS_TOKEN.toString())
                        Result.success()
                    } else {
                        Log.e(TAG, "Not Success: ${response.body()?.message}")
                        Result.failure()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e(TAG, "onFailure: ${t.message}")
                    resultStatus = Result.failure()
                }

            })
        return resultStatus
    }

    companion object {
        private val TAG = TokenWorker::class.java.simpleName
        const val REFRESH_TOKEN = "refresh_token"
        var NEW_ACCESS_TOKEN: String? = null
    }
}
