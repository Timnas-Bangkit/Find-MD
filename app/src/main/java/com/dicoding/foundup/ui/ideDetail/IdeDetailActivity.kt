package com.dicoding.foundup.ui.ideDetail

import android.os.Bundle
import android.util.Log
import android.view.View
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
            viewModel.setPostId(postId)
        }

        // Setup observer
        setupObserver()

        // Panggil data detail dengan pengambilan token melalui ViewModel
        viewModel.getUserToken { token ->
            if (!token.isNullOrEmpty()) {
                viewModel.fetchDetailIde()
                viewModel.fetchUserRole(token)
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
                populateDetail(detailData)
            } else {
                Toast.makeText(this, "Failed to load detail data", Toast.LENGTH_SHORT).show()
            }
        }

        // Observer untuk status loading
        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnJoin.isEnabled = !isLoading // Menonaktifkan tombol saat loading
        }


        // Observer untuk error message
        viewModel.error.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        // Observer untuk role user
        viewModel.userRole.observe(this) { role ->
            if (role == "owner") {
                binding.btnJoin.visibility = View.GONE
            } else {
                binding.btnJoin.visibility = View.VISIBLE
            }
        }

        // Observer untuk tombol join
        binding.btnJoin.setOnClickListener {
            viewModel.getUserToken { token ->
                if (!token.isNullOrEmpty()) {
                    viewModel.joinTeam(token)
                } else {
                    Toast.makeText(this, "Failed to retrieve user token", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        viewModel.resultJoin.observe(this) { response ->
            if (response != null) {
                if (response.error == false) {
                    Toast.makeText(this, "Berhasil bergabung", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Gagal bergabung", Toast.LENGTH_SHORT).show()
                }

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
        binding.neededRole1.text =
            detailData.neededRole?.getOrNull(0) ?: getString(R.string.no_roles_needed)
        binding.neededRole2.text = detailData.neededRole?.getOrNull(1) ?: ""

        // Menampilkan informasi founder
        val userProfile = detailData.user?.userProfile
        binding.founderName.text = userProfile?.name ?: getString(R.string.unknown_user)

        // Menampilkan gambar founder menggunakan Glide
        val founderImageUrl = userProfile?.profilePic
        if (!founderImageUrl.isNullOrEmpty()) {
            Glide.with(this).load(founderImageUrl).placeholder(R.drawable.ic_profile_24)
                .circleCrop().into(binding.founderImage)
        } else {
            binding.founderImage.setImageResource(R.drawable.ic_profile_24)
        }

        // Menampilkan gambar ide menggunakan Glide
        val ideaImageUrl = detailData.image
        if (!ideaImageUrl.isNullOrEmpty()) {
            Glide.with(this).load(ideaImageUrl).placeholder(R.drawable.ic_image_24)
                .into(binding.ideaImage)
        } else {
            binding.ideaImage.setImageResource(R.drawable.ic_image_24)
        }
    }
}
