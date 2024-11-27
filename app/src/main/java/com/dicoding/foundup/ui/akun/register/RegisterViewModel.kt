package com.dicoding.foundup.ui.akun.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.foundup.data.UserRepository
import com.dicoding.foundup.data.pref.UserPreference
import com.dicoding.foundup.data.remote.ApiConfig
import com.dicoding.foundup.data.response.RegisterResponse
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository: UserRepository = UserRepository.getInstance(
        ApiConfig.getApiService(), UserPreference.getInstance(application)
    )

    fun registerUser(
        name: String,
        email: String,
        password: String,
        onResult: (RegisterResponse) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = userRepository.registerUser(name, email, password)
                onResult(response)
            } catch (e: Exception) {
                e.printStackTrace()

            }
        }
    }
}