package com.dicoding.foundup.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicoding.foundup.data.pref.UserPreference
import com.dicoding.foundup.data.remote.ApiService
import com.dicoding.foundup.data.response.CVProfileResponse
import com.dicoding.foundup.data.response.DataItem
import com.dicoding.foundup.data.response.DataRole
import com.dicoding.foundup.data.response.LoginResponse
import com.dicoding.foundup.data.response.ProfileResponse
import com.dicoding.foundup.data.response.RegisterResponse
import com.dicoding.foundup.data.response.RoleResponse
import com.dicoding.foundup.data.response.UploadCVResponse
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import retrofit2.HttpException

class UserRepository(
    private val apiService: ApiService, private val userPreference: UserPreference
) {

    private val _user = MutableLiveData<List<DataItem>>()
    val user: LiveData<List<DataItem>> = _user

    suspend fun registerUser(name: String, email: String, password: String): RegisterResponse {
        return try {
            val response = apiService.registerUser(name, email, password)
            response
        } catch (e: retrofit2.HttpException) {
            // Tangani error berdasarkan status kode HTTP
            when (e.code()) {
                400 -> RegisterResponse(error = true, message = "Invalid input. Please check your data.")
                409 -> RegisterResponse(error = true, message = "Email already exists.")
                500 -> RegisterResponse(error = true, message = "Server error. Please try again later.")
                else -> RegisterResponse(error = true, message = "Unexpected error: ${e.message()}")
            }
        } catch (e: Exception) {
            // Tangani error umum
            RegisterResponse(error = true, message = "An unexpected error occurred: ${e.message}")
        }
    }


    suspend fun loginUser(email: String, password: String): LoginResponse {
        return try {
            val response = apiService.loginUser(email, password)
            response
        } catch (e: Exception) {
            LoginResponse(error = true, message = e.message)
        }
    }


    suspend fun fetchUser(): List<DataItem> {
        return try {
            val token = userPreference.getUserToken.first() ?: throw IllegalStateException("Token is null")
            val response = apiService.getAllUser("Bearer $token")
            response.data
        } catch (e: HttpException) {
            if (e.code() == 401) { // Unauthorized
                Log.e("UserRepository", "Invalid token, clearing user data")
                userPreference.clearUserToken()
                userPreference.clearUserLogin()
            }
            emptyList()
        } catch (e: Exception) {
            Log.e("UserRepository", "Unexpected error: ${e.message}", e)
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

    suspend fun getUserRole(token: String): ProfileResponse? {
        return try {
            val response = apiService.getRole("Bearer $token")
            if (response != null && response.error == false) {
                response
            } else {
                Log.e("UserRepository", "Error: Role response contains error or is null")
                null
            }
        } catch (e: retrofit2.HttpException) {
            val errorMessage = "HTTP Error: ${e.code()} ${e.message()}"
            Log.e("UserRepository", errorMessage)
            null
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching role: ${e.message}", e)
            null
        }
    }


    suspend fun setUserRole(token: String, role: String): RoleResponse? {
        return try {
            // Map role menjadi nilai yang sesuai dengan API (2 untuk Owner, 3 untuk Tech Worker)
            val roleValue = when (role) {
                "Owner" -> "2"
                "Tech Worker" -> "3"
                else -> throw IllegalArgumentException("Invalid role")
            }

            val dataRole = DataRole(role = roleValue)  // Pastikan role sesuai dengan nilai yang diterima API
            apiService.roleUser("Bearer $token", dataRole)
        } catch (e: Exception) {
            null // Kembalikan null jika terjadi error
        }
    }

    suspend fun getOwnerProfile(token: String): ProfileResponse {
        return try {
            apiService.getRole("Bearer $token")
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            throw Exception("HTTP Error: ${e.code()}, ${errorBody ?: e.message}")
        } catch (e: Exception) {
            throw Exception("Unexpected error: ${e.message}", e)
        }
    }


    suspend fun getUserProfile(token: String): CVProfileResponse {
        return try {
            apiService.getUserProfile("Bearer $token")
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            throw Exception("HTTP Error: ${e.code()}, ${errorBody ?: e.message}")
        } catch (e: Exception) {
            throw Exception("Unexpected error: ${e.message}", e)
        }
    }


    suspend fun uploadCV(token: String, cv: MultipartBody.Part): UploadCVResponse {
        return try {
            apiService.uploadCV("Bearer $token", cv)
        } catch (e: HttpException) {
            // Tangani kesalahan HTTP
            throw Exception("Upload failed: ${e.message()}")
        } catch (e: Exception) {
            // Tangani kesalahan lainnya
            throw Exception("Unexpected error: ${e.message}")
        }
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