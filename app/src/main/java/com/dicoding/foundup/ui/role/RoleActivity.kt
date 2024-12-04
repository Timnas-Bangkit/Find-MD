package com.dicoding.foundup.ui.role

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.foundup.R

@Suppress("DEPRECATION")
class RoleActivity : AppCompatActivity() {

    private lateinit var ownerButton: Button
    private lateinit var techWorkerButton: Button
    private lateinit var nextButton: Button

    private var selectedRole: String? = null

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
                val resultIntent = Intent().apply {
                    putExtra(EXTRA_SELECTED_ROLE, role)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    private fun updateSelection(role: String) {
        selectedRole = role

        // Atur state tombol
        when (role) {
            "Owner" -> {
                ownerButton.isSelected = true
                techWorkerButton.isSelected = false
                Log.d("ButtonState", "Owner selected")
            }
            "Tech Worker" -> {
                ownerButton.isSelected = false
                techWorkerButton.isSelected = true
                Log.d("ButtonState", "Tech Worker selected")
            }
        }

        nextButton.isEnabled = true
    }



    companion object {
        const val EXTRA_SELECTED_ROLE = "extra_selected_role"
    }
}


