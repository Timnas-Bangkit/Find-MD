package com.dicoding.foundup.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.foundup.R
import com.dicoding.foundup.adapter.PostAdapter
import com.dicoding.foundup.databinding.FragmentHomeBinding
import com.dicoding.foundup.di.Injection
import com.dicoding.foundup.ui.HomeViewModelFactory
import com.dicoding.foundup.ui.profile.ProfileFragment
import com.dicoding.foundup.ui.role.RoleActivity
import com.dicoding.foundup.ui.uploadIde.UploadIdeActivity
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val SUCCESS_ADD_IDEA = "success_add_idea"
    }

    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(Injection.provideRepository(requireContext()))
    }

    private val postAdapter = PostAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        handleAddIdeaSuccess()
        viewProfile()
        homeViewModel.userData.observe(viewLifecycleOwner) { userData ->
            postAdapter.setData(userData)
        }
        observeUserToken()

    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }
    }

    private fun viewProfile() {
        binding.profileIcon.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
    }

    private fun setupPostButton() {
        binding.postButton.visibility = View.VISIBLE // Tampilkan tombol jika role adalah "owner"
        binding.postButton.setOnClickListener {
            val intent = Intent(requireContext(), UploadIdeActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleAddIdeaSuccess() {
        val success = requireActivity().intent.getBooleanExtra(SUCCESS_ADD_IDEA, false)
        if (success) {
            Toast.makeText(requireContext(), "Idea berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
            postAdapter.notifyDataSetChanged()
        }
    }


    private fun observeUserToken() {
        lifecycleScope.launch {
            try {
                val token = homeViewModel.getUserToken()
                if (!token.isNullOrEmpty()) {
                    val role = homeViewModel.getUserRole(token)
                    if (role == "user") {
                        // Role adalah "owner" atau "techWorker", lanjutkan untuk fetch user data
                        navigateToRoleActivity()
                    } else {
                        // Role selain itu (atau null), navigasikan ke RoleActivity
                        homeViewModel.fetchUserData()
                        setupPostButton()
                    }
                } else {
                    Toast.makeText(requireContext(), "Token tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error fetching token: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToRoleActivity() {
        val intent = Intent(requireContext(), RoleActivity::class.java)
        startActivity(intent)
        requireActivity().finishAffinity()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}