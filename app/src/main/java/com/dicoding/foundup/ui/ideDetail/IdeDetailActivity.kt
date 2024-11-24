package com.dicoding.foundup.ui.ideDetail

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.foundup.R

class IdeDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ide_detail)

        // Bind views
        val ideaImage: ImageView = findViewById(R.id.ideaImage)
        val likeButton: ImageButton = findViewById(R.id.likeButton)
        val likeCount: TextView = findViewById(R.id.likeCount)
        val ideaTitle: TextView = findViewById(R.id.ideaTitle)
        val ideaDescription: TextView = findViewById(R.id.ideaDescription)
        val foundedByLabel: TextView = findViewById(R.id.foundedByLabel)
        val founderImage: ImageView = findViewById(R.id.founderImage)
        val founderName: TextView = findViewById(R.id.founderName)
        val founderRole: TextView = findViewById(R.id.founderRole)
        val detailsLabel: TextView = findViewById(R.id.detailsLabel)
        val ideaDetailsContent: TextView = findViewById(R.id.ideaDetailsContent)
        val joinButton: Button = findViewById(R.id.btn_join)

        // Get data from intent
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "No Title"
        val description = intent.getStringExtra(EXTRA_DESCRIPTION) ?: "No Description"
        val founderNameText = intent.getStringExtra(EXTRA_FOUNDER_NAME) ?: "Unknown"
        val founderRoleText = intent.getStringExtra(EXTRA_FOUNDER_ROLE) ?: "Unknown"
        val details = intent.getStringExtra(EXTRA_DETAILS) ?: "No Details"
        val imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL)
        val likes = intent.getIntExtra(EXTRA_LIKES, 0)

        // Populate views
        ideaTitle.text = title
        ideaDescription.text = description
        founderName.text = founderNameText
        founderRole.text = founderRoleText
        ideaDetailsContent.text = details

        // Load image using Glide
        Glide.with(this).load(imageUrl ?: R.drawable.ic_image_24)
            .placeholder(R.drawable.ic_image_24).centerCrop().into(ideaImage)

        // Join button action
        joinButton.setOnClickListener {
            // Example action: Show a message or navigate
        }
    }

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_DESCRIPTION = "extra_description"
        const val EXTRA_FOUNDER_NAME = "extra_founder_name"
        const val EXTRA_FOUNDER_ROLE = "extra_founder_role"
        const val EXTRA_DETAILS = "extra_details"
        const val EXTRA_IMAGE_URL = "extra_image_url"
        const val EXTRA_LIKES = "extra_likes"
    }
}
