package com.dicoding.foundup.ui.candidate.listcandidate

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.foundup.adapter.ListCandidateAdapter
import com.dicoding.foundup.databinding.ActivityMyIdeaBinding
import com.dicoding.foundup.ui.candidate.CandidateActivity

class ListCandidateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyIdeaBinding
    private val viewModel: ListCandidateViewModel by viewModels()

    private lateinit var listCandidateAdapter: ListCandidateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyIdeaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val postId = intent.getIntExtra("POST_ID", -1)
        viewModel.setPostId(postId)

        listCandidateAdapter = ListCandidateAdapter { myPostItem ->
            myPostItem?.id?.let { id ->
                val intent = Intent(this, CandidateActivity::class.java)
                intent.putExtra("POST_ID", intArrayOf(id))
                startActivity(intent)
            }
        }

        bindInit()
        bindView()
    }

    private fun bindInit() {
        viewModel.getPost()

        viewModel.myide.observe(this) { response ->
            if (response != null && response.data != null) {
                listCandidateAdapter.setData(response.data)
            }
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun bindView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ListCandidateActivity)
            adapter = listCandidateAdapter
        }
    }
}
