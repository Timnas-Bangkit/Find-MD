package com.dicoding.foundup.ui.akun.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.foundup.data.UserRepository
import com.dicoding.foundup.data.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResponse?>()
    val loginResult: LiveData<LoginResponse?> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = userRepository.loginUser(email, password)
                _loginResult.value = response
            } catch (e: Exception) {
                _loginResult.value = null
                _errorMessage.value = e.message ?: "Terjadi kesalahan"
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun isUserLoggedIn(): Boolean {
        return userRepository.isUserLoggedIn()
    }

    suspend fun saveUserToken(token: String) {
        userRepository.saveUserToken(token)
    }

    suspend fun setStatusLogin(isLoggedIn: Boolean) {
        userRepository.setStatusLogin(isLoggedIn)
    }
}
