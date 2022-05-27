package com.c22ho01.hotelranking.data.remote.retrofit

import com.c22ho01.hotelranking.data.remote.response.profile.ProfileGetResponse
import com.c22ho01.hotelranking.data.remote.response.profile.ProfilePutResponse
import retrofit2.Response
import retrofit2.http.*


interface ProfileService {
    @GET("user/{id}")
    suspend fun getUserById(
        @Header("Authorization") token: String,
        @Path("id") id: Int?
    ): Response<ProfileGetResponse>

    @PUT("user/{id}")
    @FormUrlEncoded
    suspend fun updateUserById(
        @Header("Authorization") token: String,
        @Path("id") id: Int?,
        @Field("name") name: String?,
        @Field("birth_date") birthDate: String?,
        @Field("nid") nid: Int?,
        @Field("family") family: Boolean?,
        @Field("hobby") hobby: String?,
        @Field("search_history") searchHistory: String?,
        @Field("stay_history") stayHistory: String?,
        @Field("special_needs") specialNeeds: String?
    ): Response<ProfilePutResponse>
}