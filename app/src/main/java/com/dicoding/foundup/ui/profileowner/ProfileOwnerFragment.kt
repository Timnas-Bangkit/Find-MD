package com.dicoding.foundup.ui.profileowner

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.foundup.R
import com.dicoding.foundup.data.response.DataUser
import com.dicoding.foundup.databinding.FragmentProfileOwnerBinding
import com.dicoding.foundup.di.Injection
import com.dicoding.foundup.ui.ProfileOwnerViewModelFactory
import com.dicoding.foundup.ui.akun.login.LoginActivity
import com.dicoding.foundup.ui.candidate.CandidateActivity
import com.dicoding.foundup.ui.candidate.listcandidate.ListCandidateActivity
import com.dicoding.foundup.ui.myidea.MyIdeaActivity

class ProfileOwnerFragment : Fragment() {

    private var _binding: FragmentProfileOwnerBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileOwnerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileOwnerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inisialisasi ViewModel
        val factory = ProfileOwnerViewModelFactory(Injection.provideRepository(requireContext()))
        viewModel = ViewModelProvider(this, factory)[ProfileOwnerViewModel::class.java]

        // Memulai pengambilan token pengguna
        viewModel.fetchUserToken()

        // Observasi LiveData dari ViewModel
        observeViewModel()

        // Tombol logout
        binding.logoutButton.setOnClickListener { showLogoutConfirmation() }

        // Intent ke MyIdeaActivity
        binding.myIdeaButton.setOnClickListener {
            val intent = Intent(requireActivity(), MyIdeaActivity::class.java)
            startActivity(intent)
        }

        // Intent ke CandidateActivity
        binding.listCandidateButton.setOnClickListener {
            val intent = Intent(requireActivity(), ListCandidateActivity::class.java)
            val postIds = intArrayOf(14, 13)
            intent.putExtra("POST_ID", postIds)
            startActivity(intent)
        }

        return root
    }

    private fun observeViewModel() {
        // Observasi userToken untuk memuat profil pengguna
        viewModel.userToken.observe(viewLifecycleOwner) { token ->
            if (token.isNotEmpty()) {
                viewModel.fetchOwnerProfile()
            }
        }

        // Observasi loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observasi profil pengguna
        viewModel.ownerProfile.observe(viewLifecycleOwner) { userProfile ->
            if (userProfile == null) {
                // Tampilkan default jika profil tidak tersedia
                binding.ownerName.text = getString(R.string.default_username)
                binding.ownerProfileImage.setImageResource(R.drawable.ic_profile)
            } else {
                // Tampilkan profil pengguna
                profileUser(userProfile)
            }
        }

        // Observasi status logout
        viewModel.logoutStatus.observe(viewLifecycleOwner) { isLoggedOut ->
            if (isLoggedOut) navigateToLogin()
        }
    }

    private fun profileUser(dataUser: DataUser) {
        // Ambil username dari DataUser
        binding.ownerName.text = dataUser.username ?: getString(R.string.default_username)

        // Ambil URL gambar profil dari userProfile -> profilePic
        val profileImageUrl = dataUser.userProfile?.profilePic
        if (!profileImageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(profileImageUrl)
                .placeholder(R.drawable.ic_profile)  // Placeholder image
                .error(R.drawable.ic_profile)  // Fallback error image
                .circleCrop()  // Untuk gambar profil melingkar
                .into(binding.ownerProfileImage)
        } else {
            binding.ownerProfileImage.setImageResource(R.drawable.ic_profile)  // Gambar default
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { dialog, _ ->
                dialog.dismiss()
                viewModel.clearUserDataAndLogout()
            }
            .setNegativeButton("Tidak") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()  // Hentikan aktivitas saat ini agar tidak tersimpan di back stack
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
