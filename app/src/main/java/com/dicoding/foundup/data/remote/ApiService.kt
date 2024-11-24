package com.dicoding.foundup.data.remote

import com.dicoding.foundup.data.response.AllUserResponse
import com.dicoding.foundup.data.response.LoginResponse
import com.dicoding.foundup.data.response.RegisterResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("api/users/register")
    @FormUrlEncoded
    suspend fun registerUser(
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @POST("api/users/login")
    @FormUrlEncoded
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("api/posts/")
    suspend fun getAllUser(
        @Header("Authorization") token: String
    ): AllUserResponse
}