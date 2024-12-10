package com.dicoding.foundup.ui.profileowner

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
import com.dicoding.foundup.databinding.FragmentProfileOwnerBinding
import com.dicoding.foundup.di.Injection
import com.dicoding.foundup.ui.akun.login.LoginActivity
import kotlinx.coroutines.launch

class ProfileOwnerFragment : Fragment() {
    private var _binding: FragmentProfileOwnerBinding? = null
    private val binding get() = _binding!!
    private lateinit var userRepository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this).get(ProfileOwnerViewModel::class.java)

        _binding = FragmentProfileOwnerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inisialisasi UserRepository menggunakan Injection
        userRepository = Injection.provideRepository(requireContext())

        // Observasi teks untuk profileName
        val textView: TextView = binding.ownerName
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
