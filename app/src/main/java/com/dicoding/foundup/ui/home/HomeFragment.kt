package com.dicoding.foundup.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.foundup.R
import com.dicoding.foundup.adapter.PostAdapter
import com.dicoding.foundup.databinding.FragmentHomeBinding
import com.dicoding.foundup.di.Injection
import com.dicoding.foundup.ui.HomeViewModelFactory
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

    private var selectedCategory: String = "All" // Default kategori awal

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchView()
        setupCategoryButtons()
        handleAddIdeaSuccess()
        observeUserToken()

        homeViewModel.userData.observe(viewLifecycleOwner) { userData ->
            postAdapter.setData(userData)
        }

        // Set default kategori ke "All" saat fragment pertama kali dibuka
        filterByCategory("All")
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchView.apply {
            setOnClickListener {
                requestFocus()
            }

            findViewById<EditText>(androidx.appcompat.R.id.search_src_text).setTextColor(Color.BLACK)
            findViewById<EditText>(androidx.appcompat.R.id.search_src_text).setHintTextColor(Color.GRAY)

            setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { filterPosts(it) }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let { filterPosts(it) }
                    return true
                }
            })
        }
    }

    private fun filterPosts(query: String) {
        postAdapter.filter.filter(query)
    }

    private fun setupCategoryButtons() {
        binding.buttonAll.setOnClickListener { filterByCategory("All") }
        binding.buttonEducation.setOnClickListener { filterByCategory("Education") }
        binding.buttonFinance.setOnClickListener { filterByCategory("Finance") }
        binding.buttonDesign.setOnClickListener { filterByCategory("Design") }
    }

    private fun filterByCategory(category: String) {
        selectedCategory = category

        val filteredPosts = homeViewModel.userData.value?.filter { post ->
            category == "All" || post.description!!.contains(category, ignoreCase = true)
        }

        postAdapter.setData(filteredPosts ?: emptyList())
        updateCategoryButtonState()
    }

    private fun updateCategoryButtonState() {
        val buttons = listOf(
            binding.buttonAll,
            binding.buttonEducation,
            binding.buttonFinance,
            binding.buttonDesign
        )

        buttons.forEach { button ->
            val isSelected = button.text.toString().equals(selectedCategory, ignoreCase = true)
            button.setBackgroundColor(
                if (isSelected) Color.parseColor("#FF3700B3")
                else Color.parseColor("#FFBB86FC")
            )
            button.setTextColor(
                if (isSelected) Color.WHITE else Color.BLACK
            )
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
                    if (role == "owner") {
                        setupPostButton()
                    } else if (role == "techWorker") {
                        binding.postButton.visibility = View.GONE
                    } else {
                        navigateToRoleActivity()
                    }
                    homeViewModel.fetchUserData()
                } else {
                    Toast.makeText(requireContext(), "Token tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error fetching token: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupPostButton() {
        binding.postButton.visibility = View.VISIBLE
        binding.postButton.setOnClickListener {
            val intent = Intent(requireContext(), UploadIdeActivity::class.java)
            startActivity(intent)
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