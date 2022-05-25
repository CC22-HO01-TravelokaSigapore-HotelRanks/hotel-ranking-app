package com.c22ho01.hotelranking.data.remote.retrofit

import com.c22ho01.hotelranking.data.remote.response.profile.ProfileGetResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


interface ProfileService {
    @GET("user/{id}")
    suspend fun getUserById(
        @Path("id") id: Int?
    ): Response<ProfileGetResponse>
}