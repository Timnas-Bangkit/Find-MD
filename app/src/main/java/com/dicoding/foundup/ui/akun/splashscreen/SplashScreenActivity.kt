package com.dicoding.foundup.ui.akun.splashscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.foundup.data.UserRepository
import com.dicoding.foundup.data.pref.UserPreference
import com.dicoding.foundup.data.remote.ApiConfig
import com.dicoding.foundup.ui.akun.welcome.WelcomeActivity
import com.dicoding.foundup.ui.main.MainActivity
import com.dicoding.foundup.R
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        userRepository =
            UserRepository.getInstance(ApiConfig.getApiService(), UserPreference.getInstance(this))

        // Lakukan pengecekan login di sini
        lifecycleScope.launch {
            val isLoggedIn = userRepository.isUserLoggedIn()
            if (isLoggedIn) {
                navigateToMainActivity()
            } else {
                navigateToWelcomeActivity()
            }
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
        finishAffinity()
    }

    private fun navigateToWelcomeActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@SplashScreenActivity, WelcomeActivity::class.java))
            finishAffinity()
        }, 2000)
    }
}
