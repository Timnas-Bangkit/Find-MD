package com.dicoding.foundup.data.remote

import com.dicoding.foundup.data.response.AddIdeResponse
import com.dicoding.foundup.data.response.AllUserResponse
import com.dicoding.foundup.data.response.ApplicationResponse
import com.dicoding.foundup.data.response.CVProfileResponse
import com.dicoding.foundup.data.response.CandidateResponse
import com.dicoding.foundup.data.response.DataRole
import com.dicoding.foundup.data.response.DetaiIdeResponse
import com.dicoding.foundup.data.response.JoinIdeResponse
import com.dicoding.foundup.data.response.LikeResponse
import com.dicoding.foundup.data.response.LoginResponse
import com.dicoding.foundup.data.response.MyPostResponse
import com.dicoding.foundup.data.response.ProfileResponse
import com.dicoding.foundup.data.response.RecomendationResponse
import com.dicoding.foundup.data.response.RegisterResponse
import com.dicoding.foundup.data.response.RoleResponse
import com.dicoding.foundup.data.response.UnlikeResponse
import com.dicoding.foundup.data.response.UploadCVResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    //fungsi untuk mengirimkan data saat register
    @POST("api/users/register")
    @FormUrlEncoded
    suspend fun registerUser(
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    // fungsi untuk mengirimkan data saat login
    @POST("api/users/login")
    @FormUrlEncoded
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    // fungsi untuk mengambil data user saat login
    @GET("api/users/me")
    suspend fun getRole(@Header("Authorization") authHeader: String): ProfileResponse

    // fungsi untuk mengirimkan data (mengambil) role
    @POST("api/users/role")
    suspend fun roleUser(
        @Header("Authorization") token: String,
        @Body dataRole: DataRole
    ): RoleResponse

    // fungsi untuk mengambil data postingan
    @GET("api/posts")
    suspend fun getAllUser(
        @Header("Authorization") token: String
    ): AllUserResponse

    // fungsi untuk mengirimkan data postingan
    @Multipart
    @POST("api/posts")
    suspend fun uploadIde(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part ("summary") summary: RequestBody,
        @Part("detail") detail: RequestBody,
        @Part("neededRole") neededRole1: RequestBody,
        @Part("neededRole") neededRole2: RequestBody
    ): AddIdeResponse


    // fungsi untuk mendapatkan rekomendasi CV
    @GET("api/posts/recommendation")
    suspend fun getRecommendedPosts(
        @Header("Authorization") token: String
    ): RecomendationResponse


    // fungsi untuk mendapatkan detail post (berdasarkan id)
    @GET("api/posts/{id}")
    suspend fun getDetailIde(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): DetaiIdeResponse

    // fungsi untuk mengirimkan data (CV / dokumen pdf)
    @Multipart
    @POST("api/users/cv")
    suspend fun uploadCV(
        @Header("Authorization") token: String,
        @Part cv: MultipartBody.Part
    ): UploadCVResponse

    // fungsi untuk mengirimkan data (join postingan)
    @POST("api/posts/{id}/apply")
    suspend fun joinTeam(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): JoinIdeResponse

    // fungsi untuk mendapatkan data (list yang join postingan)
    @GET("api/posts/{post_id}/candidates")
    suspend fun getCandidate(
        @Header("Authorization") token: String,
        @Path("post_id") postId: Int
    ): CandidateResponse

    // fungsi untuk mendapatkan data (list yang sudah join / applied)
    @GET("api/users/me/applied-ideas")
    suspend fun getApplication(
        @Header("Authorization") token: String
    ): ApplicationResponse

    // fungsi untuk mendapatkan data user berdasarka cv
    @GET("api/users/cv/me")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): CVProfileResponse

    // fungsi untuk mendapatkan data (postigan sendiri)
    @GET("api/posts/me")
    suspend fun getPost(
        @Header("Authorization") token: String
    ): MyPostResponse

    // fungsi untuk mengirim data like
    @POST("api/posts/{id}/like")
    suspend fun likePost(
        @Header("Authorization") token: String,
        @Path("id") postId: Int
    ): LikeResponse

    // fungsi untuk menghapus data like
    @DELETE("api/posts/{id}/like")
    suspend fun unlikePost(
        @Header("Authorization") token: String,
        @Path("id") postId: Int
    ): UnlikeResponse

}