package com.c22ho01.hotelranking.data.remote.retrofit

import com.c22ho01.hotelranking.data.remote.response.auth.LoginResponse
import com.c22ho01.hotelranking.data.remote.response.auth.RegisterResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface AuthService {
    @POST("user/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("userName") userName: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @GET("user/login/google")
    suspend fun loginGoogle(
        @Query("code") code: String
    ): Response<LoginResponse>

    @POST("user/register")
    @FormUrlEncoded
    suspend fun register(
        @Field("userName") userName: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<RegisterResponse>

    @GET("user/login/refresh-login")
    suspend fun refreshLogin(
        @Header("Cookie") cookie: String
    ): Response<LoginResponse>

    @GET("user/login/refresh-login")
    fun refreshToken(
        @Header("Cookie") cookie: String
    ): Call<LoginResponse>
}