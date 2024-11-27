package com.dicoding.foundup.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.foundup.data.UserRepository
import com.dicoding.foundup.databinding.FragmentProfileBinding
import com.dicoding.foundup.di.Injection
import com.dicoding.foundup.ui.akun.login.LoginActivity
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var userRepository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inisialisasi UserRepository menggunakan Injection
        userRepository = Injection.provideRepository(requireContext())

        // Observasi teks untuk profileName
        val textView: TextView = binding.profileName
        profileViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // Tambahkan logika logout
        binding.logoutButton.setOnClickListener {
            handleLogout()
        }

        return root
    }

    private fun handleLogout() {
        val logoutDialog = AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { dialog, _ ->
                dialog.dismiss()
                showLogoutDialog() // Fungsi untuk melakukan proses logout
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss() // Tutup dialog tanpa melakukan apa pun
            }
            .create()

        logoutDialog.show()
    }

    private fun showLogoutDialog() {
        // Jalankan proses logout di dalam Coroutine
        viewLifecycleOwner.lifecycleScope.launch {
            userRepository.clearUserData() // Membersihkan data login pengguna

            // Arahkan pengguna ke layar login
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }


}
