package com.dicoding.foundup.ui.akun.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.foundup.databinding.ActivityLoginBinding
import com.dicoding.foundup.di.Injection
import com.dicoding.foundup.ui.LoginViewModelFactory
import com.dicoding.foundup.ui.main.MainActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(Injection.provideRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            val isLoggedIn = loginViewModel.isUserLoggedIn()
            if (isLoggedIn) {
                navigateToMainActivity()
            }
        }

        showLoading(false)
        setLoginButtonEnabled()

        binding.emailEditText.addTextChangedListener(signInTextWatcher)
        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.length >= 8) setLoginButtonEnabled() else binding.loginButton.isEnabled = false
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

        loginViewModel.loginResult.observe(this) { response ->
            showLoading(false)
            if (response?.email != null && response.password != null) {
                lifecycleScope.launch {
                    loginViewModel.saveUserToken(response.password)
                    loginViewModel.setStatusLogin(true)
                }
                navigateToMainActivity()
            } else {
                Toast.makeText(this@LoginActivity, "Login Failed!", Toast.LENGTH_LONG).show()
            }
        }

        loginViewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this@LoginActivity, it, Toast.LENGTH_LONG).show()
            }
        }

        loginViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
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

    private val signInTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            setLoginButtonEnabled()
        }

        override fun afterTextChanged(s: Editable?) {}
    }
}
