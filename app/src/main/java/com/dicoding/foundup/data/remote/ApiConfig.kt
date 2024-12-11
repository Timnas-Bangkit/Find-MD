package com.dicoding.foundup.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {
    fun getApiService(): ApiService {
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // Tambahkan timeout koneksi
            .readTimeout(30, TimeUnit.SECONDS)    // Tambahkan timeout membaca data
            .writeTimeout(30, TimeUnit.SECONDS)   // Tambahkan timeout menulis data
            .addInterceptor(loggingInterceptor)   // Tambahkan logging interceptor
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://run-findup-dev-501665505486.asia-southeast2.run.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client) // Gunakan OkHttpClient dengan konfigurasi baru
            .build()

        return retrofit.create(ApiService::class.java)
    }
}