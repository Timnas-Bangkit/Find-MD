package com.dicoding.foundup.ui.akun.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.foundup.data.UserRepository
import com.dicoding.foundup.data.pref.UserPreference
import com.dicoding.foundup.data.remote.ApiConfig
import com.dicoding.foundup.databinding.ActivityLoginBinding
import com.dicoding.foundup.ui.LoginViewModelFactory
import com.dicoding.foundup.ui.akun.register.RegisterActivity
import com.dicoding.foundup.ui.main.MainActivity
import com.dicoding.foundup.ui.role.RoleActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userRepository: UserRepository
    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(userRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRepository =
            UserRepository.getInstance(ApiConfig.getApiService(), UserPreference.getInstance(this))

        lifecycleScope.launch {
            val isLoggedIn = userRepository.isUserLoggedIn()
            if (isLoggedIn) {
                navigateToMainActivity()
            }
        }

        showLoading(false)
        setLoginButtonEnabled()

        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString()
                val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

                if (!isEmailValid) {
                    binding.emailEditTextLayout.error = "Invalid email format"
                } else {
                    binding.emailEditTextLayout.error = null
                }

                setLoginButtonEnabled()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString()

                // Validasi panjang password dan apakah mengandung angka
                val isPasswordValid = password.length >= 8 && password.any { it.isDigit() }

                if (!isPasswordValid) {
                    binding.passwordEditTextLayout.error = "Password must be at least 8 characters and contain a number"
                } else {
                    binding.passwordEditTextLayout.error = null
                }

                setLoginButtonEnabled()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.loginButton.setOnClickListener {
            lifecycleScope.launch {
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()
                loginViewModel.login(email, password)
                showLoading(true)
            }
        }

        loginViewModel.data.observe(this) { response ->
            showLoading(false)
            if (!response.error!!) {
                lifecycleScope.launch {
                    val token = response.data?.token ?: ""
                    userRepository.saveUserToken(token)
                    userRepository.setStatusLogin(true)

                    // Fetch user role and decide navigation
                    loginViewModel.fetchUserRole(token)
                }
            } else {
                Toast.makeText(this@LoginActivity, "Login Failed!", Toast.LENGTH_LONG).show()
            }
        }

// Observe navigation events
        loginViewModel.navigateToRoleActivity.observe(this) { navigate ->
            Log.d("LoginActivity", "navigateToRoleActivity: $navigate")  // Debugging log
            if (navigate) {
                val intent = Intent(this@LoginActivity, RoleActivity::class.java)
                startActivity(intent)
                finishAffinity()  // Cek apakah finishAffinity() di sini tidak menghalangi navigasi
            }
        }

        loginViewModel.navigateToMainActivity.observe(this) { navigate ->
            Log.d("LoginActivity", "navigateToMainActivity: $navigate")  // Debugging log
            if (navigate) {
                navigateToMainActivity()
            }
        }


        loginViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        binding.tvSignUpLink.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setLoginButtonEnabled() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        binding.loginButton.isEnabled = email.isNotEmpty() && password.isNotEmpty()
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.INVISIBLE
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finishAffinity()
    }

    private fun navigateToRoleActivity() {
        startActivity(Intent(this@LoginActivity, RoleActivity::class.java))
        finishAffinity()
    }

}