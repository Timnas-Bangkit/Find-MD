package com.dicoding.foundup.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.dicoding.foundup.R
import com.dicoding.foundup.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.dicoding.foundup.data.UserRepository
import com.dicoding.foundup.ui.ViewModelFactory
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider
import com.dicoding.foundup.data.pref.UserPreference
import com.dicoding.foundup.data.remote.ApiConfig

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi ApiService dan UserPreference
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(applicationContext)

        // Inisialisasi UserRepository dan ViewModel
        val userRepository = UserRepository(apiService, userPreference)
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(userRepository)
        )[MainViewModel::class.java]

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_profile -> {
                    navigateToProfile(navController)
                    true
                }
                else -> {
                    navController.navigate(menuItem.itemId)
                    true
                }
            }
        }
    }

    private fun navigateToProfile(navController: NavController) {
        mainViewModel.viewModelScope.launch {
            val token = mainViewModel.getUserToken()
            val role = token?.let { mainViewModel.getUserRole(it) }
            when (role) {
                "owner" -> navController.navigate(R.id.navigation_profile_owner)
                "techworker" -> navController.navigate(R.id.navigation_profile_techworker)
                else -> {
                    navController.navigate(R.id.navigation_profile)
                }
            }
        }
    }
}
