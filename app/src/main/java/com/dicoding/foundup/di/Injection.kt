package com.dicoding.foundup.di

import android.content.Context
import com.dicoding.foundup.data.UserRepository
import com.dicoding.foundup.data.pref.UserPreference
import com.dicoding.foundup.data.remote.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context)
        val token = runBlocking { pref.getUserToken.first() }
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(apiService, pref)
    }
}