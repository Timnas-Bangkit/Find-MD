package com.dicoding.foundup.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.foundup.R
import com.dicoding.foundup.data.response.ApplicationItem
import com.dicoding.foundup.databinding.ItemApplicationBinding

class ApplicationAdapter : RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder>() {

    private val applicationList = mutableListOf<ApplicationItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicationViewHolder {
        val binding =
            ItemApplicationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ApplicationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ApplicationViewHolder, position: Int) {
        val item = applicationList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = applicationList.size

    fun setData(newList: List<ApplicationItem>) {
        val diffCallback = ApplicationDiffCallback(applicationList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        applicationList.clear()
        applicationList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    class ApplicationViewHolder(private val binding: ItemApplicationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ApplicationItem) {
            binding.apply {
                tvTitle.text = item.post?.title ?: "Unknown Title"
                tvDescription.text = item.post?.description ?: "No Description"
                tvStatus.text = item.status ?: "Pending"

                Glide.with(itemView.context)
                    .load(item.post?.image ?: R.drawable.ic_profile)
                    .circleCrop()
                    .into(imgProfile)

                // Update status background
                updateStatusBackground(item.status ?: "Pending")
            }
        }

        private fun updateStatusBackground(status: String) {
            val context = binding.root.context
            val statusBackground = when (status) {
                "Accepted" -> R.drawable.status_accepted_background
                "Interview Scheduled" -> R.drawable.status_interview_background
                "Rejected" -> R.drawable.status_rejected_background
                else -> R.drawable.status_pending_background
            }
            binding.tvStatus.setBackgroundResource(statusBackground)
        }
    }

    class ApplicationDiffCallback(
        private val oldList: List<ApplicationItem>,
        private val newList: List<ApplicationItem>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].post?.id == newList[newItemPosition].post?.id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
