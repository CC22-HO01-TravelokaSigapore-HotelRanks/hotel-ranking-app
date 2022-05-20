package com.c22ho01.hotelranking.data.remote.retrofit

import com.c22ho01.hotelranking.data.remote.response.ExampleResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ExampleService {

    @POST("example")
    @FormUrlEncoded
    suspend fun register(
            @Field("name") name: String,
            @Field("email") email: String,
            @Field("password") password: String
    ): ExampleResponse
}