package com.dicoding.foundup.ui.profileowner

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.foundup.data.UserRepository
import com.dicoding.foundup.data.response.DataUser
import kotlinx.coroutines.launch

class ProfileOwnerViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _userToken = MutableLiveData<String>()
    val userToken: LiveData<String> = _userToken

    private val _logoutStatus = MutableLiveData<Boolean>()
    val logoutStatus: LiveData<Boolean> = _logoutStatus

    private val _ownerProfile = MutableLiveData<DataUser?>()
    val ownerProfile: LiveData<DataUser?> = _ownerProfile

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchUserToken() {
        viewModelScope.launch {
            _userToken.value = userRepository.getUserToken() ?: ""
        }
    }

    fun fetchOwnerProfile() {
        _isLoading.value = true // Mulai loading
        viewModelScope.launch {
            _ownerProfile.value = null
            try {
                val token = userRepository.getUserToken() ?: ""
                val profileResponse = userRepository.getOwnerProfile(token)
                if (profileResponse.error == false) {
                    _ownerProfile.value = profileResponse.data
                } else {
                    throw Exception("Gagal memuat profil owner.")
                }
            } catch (e: Exception) {
                _ownerProfile.value = null // Tangani kesalahan
            } finally {
                _isLoading.value = false // Akhiri loading
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
