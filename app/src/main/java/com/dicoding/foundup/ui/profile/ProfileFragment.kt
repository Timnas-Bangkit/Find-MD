package com.dicoding.foundup.ui.profile

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
import com.dicoding.foundup.data.response.DataCVUser
import com.dicoding.foundup.data.util.CustomResult
import com.dicoding.foundup.databinding.FragmentProfileBinding
import com.dicoding.foundup.di.Injection
import com.dicoding.foundup.ui.ProfileViewModelFactory
import com.dicoding.foundup.ui.akun.login.LoginActivity
import com.dicoding.foundup.ui.myaplication.MyApplicationActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileViewModel

    private var isAlertShown = false


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

        // Memulai pengambilan token pengguna
        viewModel.fetchUserToken()

        // Observasi data LiveData dari ViewModel
        viewModel.userToken.observe(viewLifecycleOwner) { token ->
            if (token.isNotEmpty()) {
                viewModel.fetchUserProfile()
            }
        }

        viewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            if (userProfile == null) {
                binding.profileName.text = getString(R.string.default_username)
                binding.profileRole.text = getString(R.string.unknown_role)
                binding.profileImage.setImageResource(R.drawable.ic_profile)

                // Berikan peringatan hanya jika belum pernah upload cv
                isAlertShown = true
                showAlert("Peringatan", "Anda harus mengunggah CV untuk melanjutkan.")
            } else {
                profileUser(userProfile)
            }
        }

        viewModel.uploadStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is CustomResult.Success -> {
                    hideProgressBar()
                    showAlert("Sukses", result.data)
                }
                is CustomResult.Failure -> {
                    hideProgressBar()
                    showAlert("Error", result.exception.message)
                }
                is CustomResult.Loading -> {
                    showProgressBar()
                }
                else -> {
                    hideProgressBar()
                    showAlert("Error", "Status tidak diketahui.")
                }
            }
        }


        viewModel.logoutStatus.observe(viewLifecycleOwner) { isLoggedOut ->
            if (isLoggedOut) navigateToLogin()
        }

        // Setup event listener
        binding.uploadCVButton.setOnClickListener { pickFile() }
        binding.logoutButton.setOnClickListener { showLogoutConfirmation() }

        // Logika intent ke MyApplicationActivity
        binding.myApplicationButton.setOnClickListener {
            if (!viewModel.isCvUploaded()) {
                // Peringatan jika CV belum diunggah
                showAlert("Peringatan", "Anda harus mengunggah CV terlebih dahulu sebelum dapat mengakses fitur ini.")
            } else {
                val intent = Intent(requireActivity(), MyApplicationActivity::class.java)
                val postIds = intArrayOf(14, 13) // Mengirimkan dua ID
                intent.putExtra("POST_ID", postIds)
                startActivity(intent)
            }
        }

        return root
    }

    private fun profileUser(dataCVUser: DataCVUser) {
        binding.profileName.text = dataCVUser.username
        binding.profileRole.text = dataCVUser.cv?.jobRole ?: getString(R.string.unknown_role)

        // Periksa apakah CV sudah diunggah
        if (dataCVUser.cv == null) {
            showAlert("Peringatan", "Anda harus mengunggah CV terlebih dahulu.")
        }

        val profileImageUrl = dataCVUser.cv?.certifications?.firstOrNull()
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

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
