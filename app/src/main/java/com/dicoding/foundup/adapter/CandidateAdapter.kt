package com.dicoding.foundup.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.foundup.R
import com.dicoding.foundup.data.response.DataItem
import com.dicoding.foundup.databinding.CardviewPostBinding
import android.widget.Filter
import android.widget.Filterable
import com.dicoding.foundup.data.response.ApplicationsItem
import com.dicoding.foundup.databinding.ItemCandidateBinding
import com.dicoding.foundup.ui.ideDetail.IdeDetailActivity

class CandidateAdapter : RecyclerView.Adapter<CandidateAdapter.MyViewHolder>() {

    private val postList = mutableListOf<ApplicationsItem>()
    private val filteredList = mutableListOf<ApplicationsItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemCandidateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = filteredList[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, IdeDetailActivity::class.java)
            intent.putExtra("POST_ID", item.user?.id)  // Kirim ID post yang dipilih
            intent.putExtra("TOKEN", "Bearer <Your_Token>")  // Kirim token
            it.context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int = filteredList.size

    // Method untuk meng-update data dan melakukan perbandingan dengan DiffUtil
    fun setData(newList: List<ApplicationsItem>) {
        val diffCallback = PostDiffCallback(postList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        postList.clear()
        postList.addAll(newList)
        filteredList.clear()
        filteredList.addAll(newList) // Update data hasil filter
        diffResult.dispatchUpdatesTo(this)
    }

    // ViewHolder untuk binding data di RecyclerView
    class MyViewHolder(private val binding: ItemCandidateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ApplicationsItem) {
            binding.apply {
                // Mengisi data profil
                tvCandidateName.text = item.user?.userProfile?.name ?: "Unknown User"
                tvPosition.text
                tvStatus.text
                btnAccept.text
                btnInterview.text
                btnReject.text

                Glide.with(itemView.context)
                    .load(item.user?.userProfile?.profilePic ?: R.drawable.ic_profile_24) // Placeholder jika gambar profil kosong
                    .circleCrop()
                    .into(imgProfile)

            }
        }
    }

    // DiffUtil.Callback untuk mendeteksi perbedaan data di antara dua list
    class PostDiffCallback(
        private val oldList: List<ApplicationsItem>,
        private val newList: List<ApplicationsItem>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] .user?.id == newList[newItemPosition] .user?.id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}