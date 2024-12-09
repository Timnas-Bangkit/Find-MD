package com.dicoding.foundup.ui.ideDetail

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.dicoding.foundup.data.UserRepository
import com.dicoding.foundup.data.pref.UserPreference
import com.dicoding.foundup.data.remote.ApiConfig
import com.dicoding.foundup.data.remote.ApiService
import com.dicoding.foundup.data.response.DetaiIdeData
import kotlinx.coroutines.launch

class IdeDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository: UserRepository
    private val apiService: ApiService = ApiConfig.getApiService()

    private val _ideDetail = MutableLiveData<DetaiIdeData?>()
    val ideDetail: LiveData<DetaiIdeData?> get() = _ideDetail

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    init {
        val context = getApplication<Application>().applicationContext
        val userPreference = UserPreference.getInstance(context)
        userRepository = UserRepository(apiService, userPreference)
    }

    fun fetchDetailIde(postId: Int) {
        _loading.value = true

        // Menggunakan metode getUserToken untuk mendapatkan token
        getUserToken { token ->
            if (!token.isNullOrEmpty()) {
                viewModelScope.launch {
                    try {
                        Log.d("IdeDetailViewModel", "Using token: Bearer $token")
                        val response = apiService.getDetailIde("Bearer $token", postId)
                        if (response.error == false) {
                            _ideDetail.value = response.data
                        } else {
                            _error.value = response.message
                        }
                    } catch (e: Exception) {
                        _error.value = "An error occurred: ${e.localizedMessage}"
                    } finally {
                        _loading.value = false
                    }
                }
            } else {
                _error.value = "Token is null or invalid"
                _loading.value = false
            }
        }
    }

    fun getUserToken(onTokenRetrieved: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val token = userRepository.getUserToken()
                onTokenRetrieved(token)
            } catch (e: Exception) {
                Log.e("IdeDetailViewModel", "Error retrieving token: ${e.localizedMessage}")
                onTokenRetrieved(null)
            }
        }
    }
}