package com.dicoding.foundup.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.foundup.R
import com.dicoding.foundup.data.response.ApplicationsItem
import com.dicoding.foundup.databinding.ItemCandidateBinding

class CandidateAdapter : RecyclerView.Adapter<CandidateAdapter.MyViewHolder>() {

    private val postList = mutableListOf<ApplicationsItem>()
    private val filteredList = mutableListOf<ApplicationsItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemCandidateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = filteredList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = filteredList.size

    fun setData(newList: List<ApplicationsItem>) {
        // Mengurutkan berdasarkan score secara descending (score tertinggi di atas)
        val sortedList = newList.sortedByDescending {
            (it.user?.cv?.score as? Number)?.toFloat() ?: 0f
        }

        val diffCallback = PostDiffCallback(postList, sortedList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        postList.clear()
        postList.addAll(sortedList)
        filteredList.clear()
        filteredList.addAll(sortedList)
        diffResult.dispatchUpdatesTo(this)
    }



    class MyViewHolder(private val binding: ItemCandidateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ApplicationsItem) {
            binding.apply {
                // Mengisi data profil
                tvCandidateName.text = item.user?.userProfile?.name ?: "Unknown User"
                tvPosition.text
                tvScore.text = "Score: ${item.user?.cv?.score ?: "N/A"}"
                tvStatus.text = item.status ?: "Pending"

                Glide.with(itemView.context)
                    .load(item.user?.userProfile?.profilePic ?: R.drawable.ic_profile_24)
                    .circleCrop().into(imgProfile)

                // Logika tombol Accept
                btnAccept.setOnClickListener {
                    updateStatus("Accepted")
                }

                // Logika tombol Interview
                btnInterview.setOnClickListener {
                    updateStatus("Interview Scheduled")
                }

                // Logika tombol Reject
                btnReject.setOnClickListener {
                    updateStatus("Rejected")
                }
            }
        }

        private fun updateStatus(newStatus: String) {
            binding.tvStatus.text = newStatus
            when (newStatus) {
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

    class PostDiffCallback(
        private val oldList: List<ApplicationsItem>, private val newList: List<ApplicationsItem>
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
