package com.dicoding.foundup.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.foundup.data.UserRepository
import com.dicoding.foundup.data.response.DataItem
import kotlinx.coroutines.launch

class HomeViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _userData = MutableLiveData<List<DataItem>>(emptyList())
    val userData: LiveData<List<DataItem>> = _userData

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    suspend fun getUserToken(): String? {
        return userRepository.getUserToken()
    }

    fun fetchUserData() {
        _loading.value = true
        viewModelScope.launch {
            try {
                val data = userRepository.fetchUser()
                _userData.value = data
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
                _error.value = "Gagal memuat data user: ${e.message}"
            }
        }
    }
}
