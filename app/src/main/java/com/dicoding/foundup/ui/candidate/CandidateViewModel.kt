package com.dicoding.foundup.ui.candidate

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
import com.dicoding.foundup.data.response.CandidateResponse
import kotlinx.coroutines.launch

class CandidateViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService: ApiService = ApiConfig.getApiService()
    private val userRepository: UserRepository

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _postIds = MutableLiveData<IntArray>()  // Mengubah menjadi IntArray untuk menampung lebih dari satu ID
    val postIds: LiveData<IntArray> get() = _postIds

    private val _candidates = MutableLiveData<CandidateResponse>()
    val candidates: LiveData<CandidateResponse> = _candidates

    init {
        val context = getApplication<Application>().applicationContext
        val userPreference = UserPreference.getInstance(context)
        userRepository = UserRepository(apiService, userPreference)
    }

    fun setPostIds(ids: IntArray) {
        _postIds.value = ids
    }

    fun getCandidate() {
        getUserToken { token ->
            if (!token.isNullOrEmpty() && _postIds.value != null) {
                viewModelScope.launch {
                    try {
                        // Memproses semua ID yang ada dalam _postIds
                        val postIdList = _postIds.value!!
                        for (postId in postIdList) {
                            val response = apiService.getCandidate("Bearer $token", postId)
                            Log.d("CandidateViewModel", "Response for POST_ID $postId: ${response.toString()}")
                            if (response.error == false) {
                                // Update daftar kandidat dengan data yang diterima
                                // Anda bisa menggabungkan hasil atau menyimpan dalam daftar jika perlu
                                _candidates.value = response
                            } else {
                                _candidates.value = CandidateResponse(null, true)
                            }
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
