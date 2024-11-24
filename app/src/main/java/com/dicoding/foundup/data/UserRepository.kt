package com.dicoding.foundup.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicoding.foundup.data.pref.UserPreference
import com.dicoding.foundup.data.remote.ApiService
import com.dicoding.foundup.data.response.DataItem
import com.dicoding.foundup.data.response.LoginResponse
import com.dicoding.foundup.data.response.RegisterResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class UserRepository(
    private val apiService: ApiService, private val userPreference: UserPreference
) {

    private val _user = MutableLiveData<List<DataItem>>()
    val user: LiveData<List<DataItem>> = _user

    suspend fun registerUser(username: String, email: String, password: String): RegisterResponse? {
        return try {
            val response = apiService.registerUser(username, email, password)
            response
        } catch (e: Exception) {
            RegisterResponse(
                password = null,
                email = null,
                username = null
            )
        }
    }

    suspend fun loginUser(email: String, password: String): LoginResponse? {
        return try {
            val response = apiService.loginUser(email, password)
            response
        } catch (e: Exception) {
            LoginResponse(
                password = null,
                email = null
            )
        }
    }


    suspend fun fetchUser(): List<DataItem> {
        return try {
            val token = userPreference.getUserToken.first()
            println("Fetched token: $token") // Debug token
            if (token.isNullOrEmpty()) {
                throw IllegalStateException("Token is null or empty")
            }
            val response = apiService.getAllUser("Bearer $token")
            response.data
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }


    suspend fun getUserToken(): String? {
        return userPreference.getUserToken.first()
    }

    suspend fun clearUserData() {
        userPreference.clearUserToken()
        userPreference.clearUserLogin()
        userPreference.setStatusLogin(false)
    }


    suspend fun isUserLoggedIn(): Boolean {
        return userPreference.getStatusLogin.first()
    }

    suspend fun saveUserToken(token: String) {
        userPreference.saveUserToken(token)
    }

    suspend fun setStatusLogin(isLoggedIn: Boolean) {
        userPreference.setStatusLogin(isLoggedIn)
    }


    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(apiService: ApiService, userPreference: UserPreference): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPreference).also { instance = it }
            }
    }
}