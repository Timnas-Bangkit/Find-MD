package com.dicoding.foundup.ui.ideDetail

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.foundup.R
import com.dicoding.foundup.ui.home.HomeFragment
import com.dicoding.foundup.ui.main.MainActivity
import com.dicoding.foundup.ui.myaplication.MyApplicationActivity

class JoinTeamActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_team)

        // Button "See My Application"
        val btnSeeMyApplication: Button = findViewById(R.id.btn_see_my_application)
        btnSeeMyApplication.setOnClickListener {
            val intent = Intent(this@JoinTeamActivity, MyApplicationActivity::class.java)
            startActivity(intent)
        }

        // Button "Back"
        val btnBack: Button = findViewById(R.id.btn_back)
        btnBack.setOnClickListener {
            val intent = Intent(this@JoinTeamActivity, MainActivity::class.java)
            intent.putExtra("navigateTo", "HomeFragment")
            startActivity(intent)
            finish()
        }

    }
}