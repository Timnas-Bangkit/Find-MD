package com.dicoding.foundup.ui.ideDetail

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.foundup.R
import com.dicoding.foundup.ui.home.HomeFragment
import com.dicoding.foundup.ui.myaplication.MyAplicationActivity

class JoinTeamActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_team)

        // Button "See My Application"
        val btnSeeMyApplication: Button = findViewById(R.id.btn_see_my_application)
        btnSeeMyApplication.setOnClickListener {
            val intent = Intent(this@JoinTeamActivity, MyAplicationActivity::class.java)
            startActivity(intent)
        }

        // Button "Back"
        val btnBack: Button = findViewById(R.id.btn_back)
        btnBack.setOnClickListener {
            val intent = Intent(this@JoinTeamActivity, HomeFragment::class.java)
            startActivity(intent)
            finish()
        }
    }
}