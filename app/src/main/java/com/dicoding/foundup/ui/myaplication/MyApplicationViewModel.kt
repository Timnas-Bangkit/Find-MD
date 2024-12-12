package com.dicoding.foundup.ui.myaplication

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.foundup.data.UserRepository
import com.dicoding.foundup.data.pref.UserPreference
import com.dicoding.foundup.data.remote.ApiConfig
import com.dicoding.foundup.data.remote.ApiService
import com.dicoding.foundup.data.response.ApplicationResponse
import com.dicoding.foundup.data.response.CandidateResponse
import kotlinx.coroutines.launch

class MyApplicationViewModel (application: Application) : AndroidViewModel(application){

    private val apiService: ApiService = ApiConfig.getApiService()
    private val userRepository: UserRepository

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _postId = MutableLiveData<Int>()
    val postId: LiveData<Int> get() = _postId

    private val _application = MutableLiveData<ApplicationResponse>()
    val application: LiveData<ApplicationResponse> = _application

    init {
        val context = getApplication<Application>().applicationContext
        val userPreference = UserPreference.getInstance(context)
        userRepository = UserRepository(apiService, userPreference)
    }

    fun setPostId(postId: Int) {
        _postId.value = postId
    }

    fun fetchApplication() {
        getUserToken { token ->
            if (!token.isNullOrEmpty()) {
                viewModelScope.launch {
                    try {
                        val response = apiService.getApplication("Bearer $token")
                        if (response.error == false) {
                            _application.value = response
                        } else {
                            _application.value = ApplicationResponse(null, true)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        _application.value = ApplicationResponse(null, true)
                    } finally {
                        _loading.value = false
                    }
                }
            } else {
                _application.value = ApplicationResponse(null, true)
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
                onTokenRetrieved(null)
            }
        }
    }
}