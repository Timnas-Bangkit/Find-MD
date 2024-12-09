package com.dicoding.foundup.ui.ideDetail

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.foundup.R
import com.dicoding.foundup.data.response.DetaiIdeData
import com.dicoding.foundup.databinding.ActivityIdeDetailBinding

class IdeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIdeDetailBinding
    private val viewModel: IdeDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIdeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil ID post dari intent
        val postId = intent.getIntExtra("POST_ID", -1)

        if (postId == -1) {
            Log.e("Error", "Invalid post ID")
            Toast.makeText(this, "Invalid post ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        } else {
            Log.d("Debug", "Post ID: $postId")
        }

        // Setup observer
        setupObserver()

        // Panggil data detail dengan pengambilan token melalui ViewModel
        viewModel.getUserToken { token ->
            if (!token.isNullOrEmpty()) {
                viewModel.fetchDetailIde(postId)
            } else {
                Toast.makeText(this, "Failed to retrieve user token", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun setupObserver() {
        // Observer untuk data detail ide
        viewModel.ideDetail.observe(this) { detailData ->
            if (detailData != null) {
                Log.d("IdeDetailActivity", "Detail data loaded: $detailData")
                populateDetail(detailData)
            } else {
                Log.e("IdeDetailActivity", "Detail data is null")
                Toast.makeText(this, "Failed to load detail data", Toast.LENGTH_SHORT).show()
            }
        }

        // Observer untuk status loading
        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
            binding.btnJoin.isEnabled = !isLoading // Menonaktifkan tombol saat loading
        }

        // Observer untuk error message
        viewModel.error.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun populateDetail(detailData: DetaiIdeData) {
        // Menampilkan data pada UI
        binding.ideaTitle.text = detailData.title ?: getString(R.string.no_title)
        binding.ideaDescription.text = detailData.description ?: getString(R.string.no_description)
        binding.ideaDetailsContent.text = detailData.detail ?: getString(R.string.no_details)
        binding.summaryContent.text = detailData.summary ?: getString(R.string.no_summary)

        // Menampilkan peran yang dibutuhkan
        binding.neededRole1.text = detailData.neededRole?.getOrNull(0) ?: getString(R.string.no_roles_needed)
        binding.neededRole2.text = detailData.neededRole?.getOrNull(1) ?: ""

        // Menampilkan informasi founder
        val userProfile = detailData.user?.userProfile
        binding.founderName.text = userProfile?.name ?: getString(R.string.unknown_user)

        // Menampilkan gambar founder menggunakan Glide dengan pengecekan null
        val founderImageUrl = userProfile?.profilePic
        if (!founderImageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(founderImageUrl)
                .placeholder(R.drawable.ic_profile_24)
                .error(R.drawable.img)
                .circleCrop()
                .into(binding.founderImage)
        } else {
            binding.founderImage.setImageResource(R.drawable.ic_profile_24)
        }

        // Menampilkan gambar ide menggunakan Glide dengan pengecekan null
        val ideaImageUrl = detailData.image
        if (!ideaImageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(ideaImageUrl)
                .placeholder(R.drawable.ic_image_24)
                .error(R.drawable.img)
                .into(binding.ideaImage)
        } else {
            binding.ideaImage.setImageResource(R.drawable.ic_image_24)
        }
    }
}
