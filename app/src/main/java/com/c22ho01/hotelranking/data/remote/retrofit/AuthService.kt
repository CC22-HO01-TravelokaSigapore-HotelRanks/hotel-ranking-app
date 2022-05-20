package com.c22ho01.hotelranking.data.remote.retrofit

import com.c22ho01.hotelranking.data.remote.response.auth.LoginResponse
import com.c22ho01.hotelranking.data.remote.response.auth.RegisterResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthService {
    @POST("user/login")
    @FormUrlEncoded
    suspend fun login(
            @Field("userName") userName: String,
            @Field("password") password: String
    ): Response<LoginResponse>

    @POST("user/register")
    @FormUrlEncoded
    suspend fun register(
            @Field("userName") userName: String,
            @Field("email") email: String,
            @Field("password") password: String
    ): Response<RegisterResponse>
}