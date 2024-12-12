package com.dicoding.foundup.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.foundup.R
import com.dicoding.foundup.data.response.ApplicationsItem
import com.dicoding.foundup.databinding.ItemApplicationBinding

class ApplicationAdapter : RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder>() {

    private val applicationList = mutableListOf<ApplicationsItem>()
    private val filteredList = mutableListOf<ApplicationsItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicationViewHolder {
        val binding =
            ItemApplicationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ApplicationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ApplicationViewHolder, position: Int) {
        val item = filteredList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = filteredList.size

    fun setData(newList: List<ApplicationsItem>) {
        val diffCallback = ApplicationDiffCallback(applicationList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        applicationList.clear()
        applicationList.addAll(newList)
        filteredList.clear()
        filteredList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    class ApplicationViewHolder(private val binding: ItemApplicationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ApplicationsItem) {
            binding.apply {
                // Mengisi data profil
                tvTitle.text = item.user?.userProfile?.name ?: "Unknown User"
                tvRole.text
                tvStatus.text = item.status ?: "Pending"

                Glide.with(itemView.context)
                    .load(item.user?.userProfile?.profilePic ?: R.drawable.ic_profile)
                    .circleCrop()
                    .into(imgProfile)

                // Menyesuaikan background status
                updateStatusBackground(item.status ?: "Pending")
            }
        }

        private fun updateStatusBackground(status: String) {
            when (status) {
                "Accepted" -> {
                    binding.tvStatus.setBackgroundResource(R.drawable.status_accepted_background)
                }
                "Interview Scheduled" -> {
                    binding.tvStatus.setBackgroundResource(R.drawable.status_interview_background)
                }
                "Rejected" -> {
                    binding.tvStatus.setBackgroundResource(R.drawable.status_rejected_background)
                }
                else -> {
                    binding.tvStatus.setBackgroundResource(R.drawable.status_pending_background)
                }
            }
        }
    }

    class ApplicationDiffCallback(
        private val oldList: List<ApplicationsItem>,
        private val newList: List<ApplicationsItem>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].user?.id == newList[newItemPosition].user?.id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
