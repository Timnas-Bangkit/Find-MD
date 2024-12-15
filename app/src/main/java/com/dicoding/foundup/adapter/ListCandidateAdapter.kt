package com.dicoding.foundup.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.foundup.data.response.MyPostItem
import com.dicoding.foundup.databinding.ItemMyIdeaBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ListCandidateAdapter(
    private val onItemClicked: (MyPostItem?) -> Unit
) : RecyclerView.Adapter<ListCandidateAdapter.ViewHolder>() {

    private val myPostList = mutableListOf<MyPostItem?>()

    fun setData(newList: List<MyPostItem?>) {
        myPostList.clear()
        myPostList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMyIdeaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onItemClicked)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myPostItem = myPostList[position]
        holder.bind(myPostItem)
    }

    override fun getItemCount(): Int = myPostList.size

    class ViewHolder(
        private val binding: ItemMyIdeaBinding,
        private val onItemClicked: (MyPostItem?) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(myPostItem: MyPostItem?) {
            binding.titleText.text = myPostItem?.title

            // Format the createdAt date
            myPostItem?.createdAt?.let { dateString ->
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val date = inputFormat.parse(dateString)

                val formattedDate = date?.let {
                    val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    outputFormat.format(it)
                } ?: "Invalid Date"

                binding.profileTime.text = formattedDate
            }

            binding.descriptionText.text = myPostItem?.description

            // Load image using Glide
            Glide.with(binding.eventImage.context)
                .load(myPostItem?.image)
                .into(binding.eventImage)

            // Handle item click
            binding.root.setOnClickListener {
                onItemClicked(myPostItem)
            }
        }
    }
}
