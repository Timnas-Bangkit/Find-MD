package com.dicoding.foundup.ui.role

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.foundup.data.UserRepository
import kotlinx.coroutines.launch

class RoleViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun setUserRole(token: String, role: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = userRepository.setUserRole(token, role)
                if (response != null && response.error == false) {
                    onSuccess() // Panggil callback jika berhasil
                } else {
                    onError("Failed to update role")
                }
            } catch (e: Exception) {
                onError(e.message ?: "An error occurred")
            }
        }
    }
}
