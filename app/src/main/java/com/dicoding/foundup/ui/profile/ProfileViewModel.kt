package com.dicoding.foundup.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.foundup.data.UserRepository
import com.dicoding.foundup.data.response.UserData
import com.dicoding.foundup.data.util.CustomResult
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _userToken = MutableLiveData<String>()
    val userToken: LiveData<String> = _userToken

    private val _uploadStatus = MutableLiveData<CustomResult<String>>()
    val uploadStatus: LiveData<CustomResult<String>> = _uploadStatus

    private val _logoutStatus = MutableLiveData<Boolean>()
    val logoutStatus: LiveData<Boolean> = _logoutStatus

    private val _userProfile = MutableLiveData<UserData?>()
    val userProfile: LiveData<UserData?> = _userProfile

    fun fetchUserToken() {
        viewModelScope.launch {
            _userToken.value = userRepository.getUserToken() ?: ""
        }
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            _userProfile.value = null
            try {
                val token = userRepository.getUserToken() ?: ""
                val profileResponse = userRepository.getUserProfile(token)
                if (profileResponse.error == false) {
                    _userProfile.value = profileResponse.data
                } else {
                    throw Exception("Gagal memuat profil pengguna.")
                }
            } catch (e: Exception) {
                _userProfile.value = null // Informasi error dapat ditangani di observer
            }
        }
    }


    fun uploadCV(file: MultipartBody.Part) {
        _uploadStatus.value = CustomResult.Loading
        viewModelScope.launch {
            try {
                val token = _userToken.value ?: ""
                val response = userRepository.uploadCV(token, file)
                if (response.error == false) {
                    _uploadStatus.value = CustomResult.Success("CV berhasil diunggah.")
                } else {
                    _uploadStatus.value = CustomResult.Failure(Exception("Gagal mengunggah CV."))
                }
            } catch (e: Exception) {
                _uploadStatus.value = CustomResult.Failure(Exception("Terjadi kesalahan: ${e.message}", e))
            }
        }
    }



    fun clearUserDataAndLogout() {
        viewModelScope.launch {
            userRepository.clearUserData()
            _logoutStatus.value = true
        }
    }
}
