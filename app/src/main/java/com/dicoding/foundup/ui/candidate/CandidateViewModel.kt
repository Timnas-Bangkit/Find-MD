package com.dicoding.foundup.ui.candidate

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide.init
import com.dicoding.foundup.data.UserRepository
import com.dicoding.foundup.data.pref.UserPreference
import com.dicoding.foundup.data.remote.ApiConfig
import com.dicoding.foundup.data.remote.ApiService
import com.dicoding.foundup.data.response.CandidateResponse
import kotlinx.coroutines.launch

class CandidateViewModel (application: Application) : AndroidViewModel(application){

    private val apiService: ApiService = ApiConfig.getApiService()
    private val userRepository: UserRepository

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _postId = MutableLiveData<Int>()
    val postId: LiveData<Int> get() = _postId

    private val _candidates = MutableLiveData<CandidateResponse>()
    val candidates: LiveData<CandidateResponse> = _candidates

    init {
        val context = getApplication<Application>().applicationContext
        val userPreference = UserPreference.getInstance(context)
        userRepository = UserRepository(apiService, userPreference)
    }

    fun setPostId(postId: Int) {
        _postId.value = postId
    }

    fun getCandidate() {
        getUserToken { token ->
            if (!token.isNullOrEmpty()) {
                viewModelScope.launch {
                    try {
                        val response = apiService.getCandidate("Bearer $token", postId.value!!)
                        Log.d("CandidateViewModel", "Response: ${response.toString()}")
                        if (response.error == false) {
                            _candidates.value = response
                        } else {
                            _candidates.value = CandidateResponse(null, true)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        _candidates.value = CandidateResponse(null, true)
                    } finally {
                        _loading.value = false
                    }
                }
            } else {
                _candidates.value = CandidateResponse(null, true)
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