package com.dicoding.foundup.ui.ideDetail

import android.app.Application
import androidx.lifecycle.*
import com.dicoding.foundup.data.UserRepository
import com.dicoding.foundup.data.pref.UserPreference
import com.dicoding.foundup.data.remote.ApiConfig
import com.dicoding.foundup.data.remote.ApiService
import com.dicoding.foundup.data.response.DetaiIdeData
import com.dicoding.foundup.data.response.JoinIdeResponse
import kotlinx.coroutines.launch

class IdeDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository: UserRepository
    private val apiService: ApiService = ApiConfig.getApiService()

    private val _resultJoin = MutableLiveData<JoinIdeResponse>()
    val resultJoin: LiveData<JoinIdeResponse> get() = _resultJoin

    private val _postId = MutableLiveData<Int>()
    val postId: LiveData<Int> get() = _postId

    private val _ideDetail = MutableLiveData<DetaiIdeData?>()
    val ideDetail: LiveData<DetaiIdeData?> get() = _ideDetail

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _userRole = MutableLiveData<String?>()
    val userRole: LiveData<String?> get() = _userRole

    private val _userHasJoined = MutableLiveData<Boolean>()
    val userHasJoined: LiveData<Boolean> get() = _userHasJoined

    init {
        val context = getApplication<Application>().applicationContext
        val userPreference = UserPreference.getInstance(context)
        userRepository = UserRepository(apiService, userPreference)
    }

    fun setPostId(postId: Int) {
        _postId.value = postId
    }

    fun joinTeam(token: String) {
        viewModelScope.launch {
            try {
                val response = apiService.joinTeam("Bearer $token", postId.value!!)
                if (response.error == false) {
                    _resultJoin.value = response
                } else {
                    _resultJoin.value = JoinIdeResponse(true, "Failed to join team")
                }
            } catch (e: Exception) {
                _error.value = "Error joining team: ${e.localizedMessage}"
            }
        }
    }

    fun fetchDetailIde() {
        _loading.value = true

        getUserToken { token ->
            if (!token.isNullOrEmpty()) {
                viewModelScope.launch {
                    try {
                        val response = apiService.getDetailIde("Bearer $token", postId.value!!)
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

    fun checkIfUserJoined(token: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getApplication("Bearer $token")
                val joined = response.data?.applications?.any { it.post?.id == postId.value } == true
                _userHasJoined.value = joined
            } catch (e: Exception) {
                _error.value = "Failed to check join status: ${e.localizedMessage}"
            }
        }
    }

    fun fetchUserRole(token: String) {
        viewModelScope.launch {
            try {
                val role = getUserRole(token)
                _userRole.value = role
            } catch (e: Exception) {
                _error.value = "Failed to fetch user role: ${e.localizedMessage}"
            }
        }
    }

    suspend fun getUserRole(token: String): String? {
        return try {
            val roleResponse = userRepository.getUserRole(token)
            roleResponse?.data?.role
        } catch (e: Exception) {
            null
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
