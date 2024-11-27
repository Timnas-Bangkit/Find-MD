package com.dicoding.foundup.ui.akun.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.foundup.data.UserRepository
import com.dicoding.foundup.data.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(application: Application, private val userRepository: UserRepository) :
    AndroidViewModel(application) {

    private val _data = MutableLiveData<LoginResponse>()
    val data: LiveData<LoginResponse> = _data

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = userRepository.loginUser(email, password)
                _data.value = response
            } catch (e: Exception) {
                _data.value = LoginResponse(error = true, message = e.message)
            } finally {
                _isLoading.value = false
            }
        }
    }
}