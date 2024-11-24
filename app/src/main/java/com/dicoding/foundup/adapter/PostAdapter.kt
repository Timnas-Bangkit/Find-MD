package com.dicoding.foundup.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.foundup.R
import com.dicoding.foundup.data.response.DataItem
import com.dicoding.foundup.databinding.CardviewPostBinding

class PostAdapter : RecyclerView.Adapter<PostAdapter.MyViewHolder>() {

    private val postList = mutableListOf<DataItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            CardviewPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = postList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = postList.size

    fun setData(newList: List<DataItem>) {
        val diffCallback = PostDiffCallback(postList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        postList.clear()
        postList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    class MyViewHolder(private val binding: CardviewPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DataItem) {
            binding.apply {
                // Mengisi data profil
                profileName.text = item.user?.userProfile?.name ?: "Unknown User"
                profileTime.text = item.createdAt ?: "Unknown Time"

                Glide.with(itemView.context)
                    .load(item.user?.userProfile?.profilePic ?: R.drawable.ic_profile_24) // Placeholder jika gambar profil kosong
                    .circleCrop()
                    .into(profileIcon)


                titleText.text = item.title ?: "No Title"
                descriptionText.text = item.description ?: "No Description"

                Glide.with(itemView.context)
                    .load(item.image)
                    .placeholder(R.drawable.ic_image_24)
                    .error(R.drawable.ic_broken_image_24)
                    .into(eventImage)
            }
        }
    }

    class PostDiffCallback(
        private val oldList: List<DataItem>,
        private val newList: List<DataItem>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
