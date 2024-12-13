package com.dicoding.foundup.ui.myaplication

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.foundup.adapter.ApplicationAdapter
import com.dicoding.foundup.databinding.ActivityMyApplicationBinding

class MyApplicationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyApplicationBinding
    private val viewModel: MyApplicationViewModel by viewModels()

    private val applicationAdapter = ApplicationAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyApplicationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val postId = intent.getIntExtra("POST_ID", -1)
        viewModel.setPostId(postId)

        bindInit()
        bindView()
        bindObserver()

    }

    private fun bindInit() {
        viewModel.fetchApplication()
    }

    private fun bindView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MyApplicationActivity)
            adapter = applicationAdapter
        }

    }

    private fun bindObserver() {
        viewModel.application.observe(this) {
            if (!it.error!!) {
                applicationAdapter.setData(it.data!!.applications)

            }

        }

    }
}