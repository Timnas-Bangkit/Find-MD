package com.dicoding.foundup.ui.akun.login

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.foundup.data.UserRepository
import com.dicoding.foundup.data.remote.ApiConfig
import com.dicoding.foundup.data.remote.ApiService
import com.dicoding.foundup.data.response.DataRole
import com.dicoding.foundup.data.response.LoginResponse
import com.dicoding.foundup.data.response.RoleResponse
import kotlinx.coroutines.launch

class LoginViewModel(application: Application, private val userRepository: UserRepository) :
    AndroidViewModel(application) {

    private val _data = MutableLiveData<LoginResponse>()
    val data: LiveData<LoginResponse> = _data

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _navigateToRoleActivity = MutableLiveData<Boolean>()
    val navigateToRoleActivity: LiveData<Boolean> = _navigateToRoleActivity

    private val _navigateToMainActivity = MutableLiveData<Boolean>()
    val navigateToMainActivity: LiveData<Boolean> = _navigateToMainActivity


    val apiService = ApiConfig.getApiService()

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

    fun fetchUserRole(token: String) {
        viewModelScope.launch {
            try {
                val roleResponse = userRepository.getUserRole(token)
                val role = roleResponse?.data?.role
                Log.d("LoginViewModel", "Role fetched: $role")  // Debugging log

                if (role == "user") {
                    // Arahkan ke RoleActivity jika role kosong atau tidak valid
                    _navigateToRoleActivity.value = true
                } else {
                    // Jika role valid, arahkan ke MainActivity
                    _navigateToMainActivity.value = true
                }

            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error fetching user role: ${e.message}")
                _navigateToMainActivity.value = true
            }
        }
    }


}