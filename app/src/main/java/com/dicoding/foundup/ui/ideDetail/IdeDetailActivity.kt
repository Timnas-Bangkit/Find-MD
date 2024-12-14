package com.dicoding.foundup.ui.ideDetail

import android.content.Intent
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

        val postId = intent.getIntExtra("POST_ID", -1)

        if (postId == -1) {
            Log.e("Error", "Invalid post ID")
            Toast.makeText(this, "Invalid post ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        } else {
            viewModel.setPostId(postId)
        }

        setupObserver()

        viewModel.getUserToken { token ->
            if (!token.isNullOrEmpty()) {
                viewModel.fetchDetailIde()
                viewModel.fetchUserRole(token)
                viewModel.checkIfUserJoined(token) // Periksa apakah sudah bergabung
            } else {
                Toast.makeText(this, "Failed to retrieve user token", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun setupObserver() {
        viewModel.ideDetail.observe(this) { detailData ->
            if (detailData != null) {
                populateDetail(detailData)
                binding.likeButton.setImageResource(
                    if (detailData.isLiked == true) R.drawable.ic_thumb_up_24 else R.drawable.ic_thumb_up_off_alt_24
                )
                binding.likeCount.text = detailData.likeCount?.toString() ?: "0"
            }
        }


        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnJoin.isEnabled = !isLoading
        }

        viewModel.error.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                viewModel.resetError()
            }
        }

        viewModel.userRole.observe(this) { role ->
            if (role.equals("owner", ignoreCase = true)) {
                binding.btnJoin.visibility = View.GONE
            } else {
                binding.btnJoin.visibility = View.VISIBLE
            }
        }


        viewModel.userHasJoined.observe(this) { hasJoined ->
            if (hasJoined == true) {
                binding.btnJoin.text = getString(R.string.already_joined)
            } else {
                binding.btnJoin.text = getString(R.string.join)
            }
        }

        viewModel.hasUploadedCV.observe(this) { hasCV ->
            if (hasCV == true) {
                viewModel.getUserToken { token ->
                    if (!token.isNullOrEmpty() && viewModel.userHasJoined.value == false) {
                        viewModel.joinTeam(token)
                    } else {
                        Toast.makeText(this, "You have already joined this team", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please upload your CV before joining the team", Toast.LENGTH_LONG).show()
            }
        }


        binding.btnJoin.setOnClickListener {
            viewModel.getUserToken { token ->
                if (!token.isNullOrEmpty()) {
                    // Memastikan CV sudah dicek hanya sekali
                    viewModel.checkIfUserUploadedCV(token) // Periksa apakah CV sudah diunggah
                    viewModel.hasUploadedCV.observe(this) { hasUploadedCV ->
                        if (hasUploadedCV != null) { // Pastikan status CV sudah didapat
                            // Menghentikan observer untuk menghindari pemanggilan berulang
                            viewModel.hasUploadedCV.removeObservers(this)

                            if (hasUploadedCV) { // Jika CV sudah diunggah
                                if (viewModel.userHasJoined.value == false) { // Periksa apakah belum bergabung
                                    viewModel.joinTeam(token) // Gabung ke tim
                                } else {
                                    Toast.makeText(
                                        this,
                                        "You have already joined this team",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else { // CV belum diunggah
                                Toast.makeText(
                                    this,
                                    "Please upload your CV before joining the team",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "User token is missing", Toast.LENGTH_SHORT).show()
                }
            }
        }



        binding.likeButton.setOnClickListener {
            viewModel.getUserToken { token ->
                val isLiked = viewModel.ideDetail.value?.isLiked ?: false
                if (!token.isNullOrEmpty()) {
                    if (isLiked) {
                        viewModel.unlikePost(token)
                    } else {
                        viewModel.likePost(token)
                    }
                    // Periksa status bergabung setelah melakukan like/unlike
                    viewModel.checkIfUserJoined(token)
                } else {
                    Toast.makeText(this, "User token is missing", Toast.LENGTH_SHORT).show()
                }
            }
        }


        viewModel.resultJoin.observe(this) { response ->
            if (response != null) {
                if (response.error == false) {
                    Toast.makeText(this, "Berhasil bergabung", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, JoinTeamActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Gagal bergabung", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun populateDetail(detailData: DetaiIdeData) {
        binding.ideaTitle.text = detailData.title ?: getString(R.string.no_title)
        binding.ideaDescription.text = detailData.description ?: getString(R.string.no_description)
        binding.ideaDetailsContent.text = detailData.detail ?: getString(R.string.no_details)
        binding.summaryContent.text = detailData.summary ?: getString(R.string.no_summary)

        binding.neededRole1.text =
            detailData.neededRole?.getOrNull(0) ?: getString(R.string.no_roles_needed)
        binding.neededRole2.text = detailData.neededRole?.getOrNull(1) ?: ""

        val userProfile = detailData.user?.userProfile
        binding.founderName.text = userProfile?.name ?: getString(R.string.unknown_user)

        val founderImageUrl = userProfile?.profilePic
        if (!founderImageUrl.isNullOrEmpty()) {
            Glide.with(this).load(founderImageUrl).placeholder(R.drawable.ic_profile)
                .circleCrop().into(binding.founderImage)
        } else {
            binding.founderImage.setImageResource(R.drawable.ic_profile)
        }

        val ideaImageUrl = detailData.image
        if (!ideaImageUrl.isNullOrEmpty()) {
            Glide.with(this).load(ideaImageUrl).placeholder(R.drawable.ic_image_24)
                .into(binding.ideaImage)
        } else {
            binding.ideaImage.setImageResource(R.drawable.ic_image_24)
        }
    }
}