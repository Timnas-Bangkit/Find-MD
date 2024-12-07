package com.dicoding.foundup.ui.role

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.foundup.R
import com.dicoding.foundup.di.Injection
import com.dicoding.foundup.ui.RoleViewModelFactory
import com.dicoding.foundup.ui.main.MainActivity

class RoleActivity : AppCompatActivity() {

    private lateinit var ownerButton: Button
    private lateinit var techWorkerButton: Button
    private lateinit var nextButton: Button

    private var selectedRole: String? = null

    private val roleViewModel: RoleViewModel by viewModels {
        RoleViewModelFactory(Injection.provideRepository(application))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_role)

        // Inisialisasi Views
        ownerButton = findViewById(R.id.ownerButton)
        techWorkerButton = findViewById(R.id.techWorkerButton)
        nextButton = findViewById(R.id.nextButton)

        // Atur logika pemilihan
        ownerButton.setOnClickListener {
            updateSelection("Owner")
        }

        techWorkerButton.setOnClickListener {
            updateSelection("Tech Worker")
        }

        // Kirim role yang dipilih saat tombol "Next" diklik
        nextButton.setOnClickListener {
            selectedRole?.let { role ->
                roleViewModel.getUserToken { token ->
                    if (token != null) {
                        roleViewModel.setUserRole(
                            token = token,
                            role = role,
                            onSuccess = {
                                Toast.makeText(this, "Role updated successfully", Toast.LENGTH_SHORT).show()
                                navigateToNextScreen()
                            },
                            onError = { errorMessage ->
                                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                            }
                        )
                    } else {
                        Toast.makeText(this, "Token is missing", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun updateSelection(role: String) {
        selectedRole = role

        // Reset warna semua tombol
        ownerButton.setBackgroundResource(R.drawable.btn_unselected_bg)
        techWorkerButton.setBackgroundResource(R.drawable.btn_unselected_bg)

        // Atur warna tombol yang dipilih
        when (role) {
            "Owner" -> {
                ownerButton.setBackgroundResource(R.drawable.btn_selected_bg)
                techWorkerButton.setBackgroundResource(R.drawable.btn_unselected_bg)
            }
            "Tech Worker" -> {
                techWorkerButton.setBackgroundResource(R.drawable.btn_selected_bg)
                ownerButton.setBackgroundResource(R.drawable.btn_unselected_bg)
            }
        }

        nextButton.isEnabled = true
    }


//    private fun updateSelection(role: String) {
//        selectedRole = role
//
//        // Atur state tombol
//        when (role) {
//            "Owner" -> {
//                ownerButton.isSelected = true
//                techWorkerButton.isSelected = false
//                Log.d("ButtonState", "Owner selected")
//            }
//            "Tech Worker" -> {
//                ownerButton.isSelected = false
//                techWorkerButton.isSelected = true
//                Log.d("ButtonState", "Tech Worker selected")
//            }
//        }
//
//        nextButton.isEnabled = true
//    }

    private fun navigateToNextScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}
