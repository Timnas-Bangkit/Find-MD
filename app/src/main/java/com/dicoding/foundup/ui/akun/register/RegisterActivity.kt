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
import androidx.lifecycle.lifecycleScope
import com.dicoding.foundup.databinding.ActivityRegisterBinding
import com.dicoding.foundup.di.Injection
import com.dicoding.foundup.ui.RegisterViewModelFactory
import com.dicoding.foundup.ui.akun.login.LoginActivity
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val registerViewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory(Injection.provideRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showLoading(false)
        setSignupButtonEnabled()

        binding.emailEditText.addTextChangedListener(signUpTextWatcher)
        binding.nameEditText.addTextChangedListener(signUpTextWatcher)
        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setSignupButtonEnabled()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.signupButton.setOnClickListener {
            lifecycleScope.launch {
                val username = binding.nameEditText.text.toString()
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()
                showLoading(true)

                try {
                    val response = registerViewModel.registerUser(username, email, password)
                    showLoading(false)

                    if (response != null && response.username != null && response.email != null) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Account created for ${response.username}",
                            Toast.LENGTH_LONG
                        ).show()
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registration failed. Please try again.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    showLoading(false)
                    Toast.makeText(this@RegisterActivity, e.message ?: "Error occurred", Toast.LENGTH_LONG).show()
                }
            }
        }

        playAnimation()
    }

    private fun setSignupButtonEnabled() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        val name = binding.nameEditText.text.toString()
        binding.signupButton.isEnabled =
            email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()
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
