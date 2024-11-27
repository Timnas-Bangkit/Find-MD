package com.dicoding.foundup.ui.akun.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.foundup.databinding.ActivityRegisterBinding
import com.dicoding.foundup.di.Injection
import com.dicoding.foundup.ui.RegisterViewModelFactory
import com.dicoding.foundup.ui.akun.login.LoginActivity
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding


    private val registerViewModel: RegisterViewModel by lazy {
        ViewModelProvider(
            this, RegisterViewModelFactory(application)
        ).get(RegisterViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showLoading(false)
        setSignupButtonEnabled()

        binding.nameEditText.addTextChangedListener(signUpTextWatcher)
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

                setSignupButtonEnabled()
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

                setSignupButtonEnabled()
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        binding.signupButton.setOnClickListener {
            lifecycleScope.launch {
                val name = binding.nameEditText.text.toString()
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()
                showLoading(true)
                registerViewModel.registerUser(name, email, password) { response ->
                    showLoading(false)
                    if (!response.error!!) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Account created: ${response.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registration failed: ${response.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        playAnimation()
    }

    private fun setSignupButtonEnabled() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        val name = binding.nameEditText.text.toString()

        // Validasi format email
        val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

        // Validasi password (minimal 8 karakter dan mengandung angka)
        val isPasswordValid = password.length >= 8 && password.any { it.isDigit() }

        binding.signupButton.isEnabled =
            email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() && isEmailValid
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.INVISIBLE
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 5000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            start()
        }

        val animations = listOf(
            ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500),
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(500),
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500),
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500),
            ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(500)
        )

        AnimatorSet().apply {
            playSequentially(animations)
            start()
        }
    }

    private val signUpTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            setSignupButtonEnabled()
        }

        override fun afterTextChanged(s: Editable?) {}
    }
}
