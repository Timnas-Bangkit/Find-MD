package com.dicoding.foundup.ui.profile

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dicoding.foundup.data.UserRepository
import com.dicoding.foundup.databinding.FragmentProfileBinding
import com.dicoding.foundup.di.Injection
import kotlinx.coroutines.launch
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import com.dicoding.foundup.ui.akun.login.LoginActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var userRepository: UserRepository
    private lateinit var token: String

    // Tambahkan launcher untuk mengambil file
    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val file = uriToFile(uri)
                uploadCV(file)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        userRepository = Injection.provideRepository(requireContext())

        // Tambahkan logika untuk tombol upload CV
        binding.uploadCVButton.setOnClickListener {
            pickFile()
        }

        // Ambil token user
        viewLifecycleOwner.lifecycleScope.launch {
            token = userRepository.getUserToken() ?: ""
        }

        // Tambahkan logika logout
        binding.logoutButton.setOnClickListener {
            handleLogout()
        }

        return root
    }

    private fun pickFile() {
        filePickerLauncher.launch("application/pdf")
    }

    private fun uriToFile(uri: Uri): File {
        val contentResolver = requireContext().contentResolver
        val tempFile = File.createTempFile("temp_", ".pdf", requireContext().cacheDir)
        contentResolver.openInputStream(uri)?.use { inputStream ->
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return tempFile
    }

    private fun uploadCV(file: File) {
        val progressDialog = ProgressDialog(requireContext()).apply {
            setTitle("Mengunggah CV")
            setMessage("Harap tunggu...")
            setCancelable(false)
            show()
        }

        val requestBody = RequestBody.create("application/pdf".toMediaTypeOrNull(), file)
        val part = MultipartBody.Part.createFormData("cv", file.name, requestBody)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = userRepository.uploadCV(token, part)
                progressDialog.dismiss()
                if (response.error == false) {
                    showAlert("Sukses", "CV berhasil diunggah.")
                } else {
                    showAlert("Error", "Gagal mengunggah CV.")
                }
            } catch (e: Exception) {
                progressDialog.dismiss()
                showAlert("Error", "Terjadi kesalahan: ${e.message}")
            }
        }
    }


    private fun showAlert(title: String, message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun handleLogout() {
        val logoutDialog = AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { dialog, _ ->
                dialog.dismiss()
                showLogoutDialog()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        logoutDialog.show()
    }

    private fun showLogoutDialog() {
        viewLifecycleOwner.lifecycleScope.launch {
            userRepository.clearUserData()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
