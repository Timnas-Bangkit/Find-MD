package com.dicoding.foundup.ui.profile

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.foundup.R
import com.dicoding.foundup.data.response.UserData
import com.dicoding.foundup.data.util.CustomResult
import com.dicoding.foundup.databinding.FragmentProfileBinding
import com.dicoding.foundup.di.Injection
import com.dicoding.foundup.ui.ProfileViewModelFactory
import com.dicoding.foundup.ui.akun.login.LoginActivity
import com.dicoding.foundup.ui.candidate.CandidateActivity
import com.dicoding.foundup.ui.myaplication.MyApplicationActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileViewModel
    private var progressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inisialisasi ViewModel
        val factory = ProfileViewModelFactory(Injection.provideRepository(requireContext()))
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        // Observasi data LiveData dari ViewModel
        viewModel.userToken.observe(viewLifecycleOwner) { token ->
            if (token.isNotEmpty()) {
                viewModel.fetchUserProfile()
            }
        }

        viewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            if (userProfile == null) {
                showProgressDialog("Memuat Profil", "Harap tunggu...")
            } else {
                dismissProgressDialog()
                profileUser(userProfile)
            }
        }

        viewModel.uploadStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is CustomResult.Success -> {
                    dismissProgressDialog()
                    showAlert("Sukses", result.data)
                }
                is CustomResult.Failure -> {
                    dismissProgressDialog()
                    showAlert("Error", result.exception.message)
                }
                is CustomResult.Loading -> showProgressDialog("Mengunggah CV", "Harap tunggu...")
                else -> {
                    dismissProgressDialog()
                    showAlert("Error", "Status tidak diketahui.")
                }
            }
        }

        viewModel.logoutStatus.observe(viewLifecycleOwner) { isLoggedOut ->
            if (isLoggedOut) navigateToLogin()
        }

        // Memulai pengambilan token pengguna
        viewModel.fetchUserToken()

        // Setup event listener
        binding.uploadCVButton.setOnClickListener { pickFile() }
        binding.logoutButton.setOnClickListener { showLogoutConfirmation() }

        // Tambahkan logika intent ke MyApplicationActivity
        binding.myApplicationButton.setOnClickListener {
            val intent = Intent(requireActivity(), MyApplicationActivity::class.java)
            val postIds = intArrayOf(14, 13) // Mengirimkan dua ID
            intent.putExtra("POST_ID", postIds)
            startActivity(intent)
        }

        return root
    }

    private fun profileUser(userData: UserData) {
        binding.profileName.text = userData.username
        binding.profileRole.text = userData.cv?.jobRole ?: getString(R.string.unknown_role)

        val profileImageUrl = userData.cv?.certifications?.firstOrNull()
        if (!profileImageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(profileImageUrl)
                .placeholder(R.drawable.ic_profile)
                .circleCrop()
                .into(binding.profileImage)
        } else {
            binding.profileImage.setImageResource(R.drawable.ic_profile)
        }
    }

    private fun pickFile() {
        filePickerLauncher.launch("application/pdf")
    }

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val mimeType = requireContext().contentResolver.getType(it)
                if (mimeType == "application/pdf") {
                    val multipartBodyPart = uriToMultipartBodyPart(it)
                    viewModel.uploadCV(multipartBodyPart)
                } else {
                    showAlert("Error", "File yang dipilih bukan PDF.")
                }
            }
        }


    private fun uriToMultipartBodyPart(uri: Uri): MultipartBody.Part {
        val file = uriToFile(uri)
        val requestBody = RequestBody.create("application/pdf".toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData("cv", file.name, requestBody)
    }

    private fun uriToFile(uri: Uri): File {
        val contentResolver = requireContext().contentResolver
        val tempFile = File.createTempFile("temp_", ".pdf", requireContext().cacheDir)
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } finally {
            tempFile.deleteOnExit() // File akan dihapus saat aplikasi ditutup
        }
        return tempFile
    }


    private fun showAlert(title: String, message: String?) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
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
    }

    private fun showProgressDialog(title: String, message: String) {
        progressDialog = ProgressDialog(requireContext()).apply {
            setTitle(title)
            setMessage(message)
            setCancelable(false)
            show()
        }
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
