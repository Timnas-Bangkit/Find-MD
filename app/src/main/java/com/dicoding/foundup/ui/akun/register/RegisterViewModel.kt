package com.dicoding.foundup.ui.akun.register

import androidx.lifecycle.ViewModel
import com.dicoding.foundup.data.UserRepository
import com.dicoding.foundup.data.response.RegisterResponse

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {

    suspend fun registerUser(name: String, email: String, password: String): RegisterResponse? {
        return try {
            userRepository.registerUser(name, email, password)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
