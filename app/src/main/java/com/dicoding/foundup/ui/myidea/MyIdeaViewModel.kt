package com.dicoding.foundup.ui.myidea

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
import com.dicoding.foundup.data.response.MyPostResponse
import kotlinx.coroutines.launch

class MyIdeaViewModel (application: Application) : AndroidViewModel(application){

    private val apiService: ApiService = ApiConfig.getApiService()
    private val userRepository: UserRepository

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _postId = MutableLiveData<Int>()
    val postId: LiveData<Int> get() = _postId

    private val _myide = MutableLiveData<MyPostResponse>()
    val myide: LiveData<MyPostResponse> = _myide

    init {
        val context = getApplication<Application>().applicationContext
        val userPreference = UserPreference.getInstance(context)
        userRepository = UserRepository(apiService, userPreference)
    }

    fun setPostId(postId: Int) {
        _postId.value = postId
    }

    fun getPost() {
        getUserToken { token ->
            if (!token.isNullOrEmpty()) {
                viewModelScope.launch {
                    try {
                        val response = apiService.getPost("Bearer $token")
                        Log.d("MyIdeaViewModel", "Response: ${response.toString()}")
                        if (response.error == false) {
                            _myide.value = response
                        } else {
                            _myide.value = MyPostResponse(null, true)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        _myide.value = MyPostResponse(null, true)
                    } finally {
                        _loading.value = false
                    }
                }
            } else {
                _myide.value = MyPostResponse(null, true)
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