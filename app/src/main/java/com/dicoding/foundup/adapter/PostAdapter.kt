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
import com.dicoding.foundup.ui.ideDetail.IdeDetailActivity
import java.text.SimpleDateFormat
import java.util.Locale

class PostAdapter : RecyclerView.Adapter<PostAdapter.MyViewHolder>(), Filterable {

    private val postList = mutableListOf<DataItem>()
    private val filteredList = mutableListOf<DataItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CardviewPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = filteredList[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, IdeDetailActivity::class.java)
            intent.putExtra("POST_ID", item.id)  // Kirim ID post yang dipilih
            intent.putExtra("TOKEN", "Bearer <Your_Token>")  // Kirim token
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = filteredList.size

    // Method untuk meng-update data dan melakukan perbandingan dengan DiffUtil
    fun setData(newList: List<DataItem>) {
        val diffCallback = PostDiffCallback(postList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        postList.clear()
        postList.addAll(newList)
        filteredList.clear()
        filteredList.addAll(newList) // Update data hasil filter
        diffResult.dispatchUpdatesTo(this)
    }

    // Implementasi Filterable interface untuk memfilter data
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase() ?: ""
                val resultList = if (query.isEmpty()) {
                    postList
                } else {
                    postList.filter {
                        // Filter berdasarkan title dan description
                        it.title?.lowercase()?.contains(query) == true ||
                                it.description?.lowercase()?.contains(query) == true
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = resultList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList.clear()
                filteredList.addAll(results?.values as List<DataItem>)
                notifyDataSetChanged() // Notify adapter untuk memperbarui data
            }
        }
    }

    // ViewHolder untuk binding data di RecyclerView
    class MyViewHolder(private val binding: CardviewPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DataItem) {
            binding.apply {
                // Mengisi data profil
                profileName.text = item.user?.userProfile?.name ?: "Unknown User"

                // Format createdAt date
                item.createdAt?.let { dateString ->
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                    val date = inputFormat.parse(dateString)

                    val formattedDate = date?.let {
                        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        outputFormat.format(it)
                    } ?: "Invalid Date"  // Default if parsing fails

                    profileTime.text = formattedDate
                } ?: run {
                    profileTime.text = "Unknown Time"
                }

                // Set title and description
                titleText.text = item.title ?: "No Title"
                descriptionText.text = item.description ?: "No Description"

                // Load profile image with Glide
                Glide.with(itemView.context)
                    .load(item.user?.userProfile?.profilePic ?: R.drawable.ic_profile_24) // Placeholder if profile picture is empty
                    .circleCrop()
                    .into(profileIcon)

                // Load event image with Glide
                Glide.with(itemView.context)
                    .load(item.image)
                    .placeholder(R.drawable.ic_image_24)
                    .error(R.drawable.ic_broken_image_24)
                    .into(eventImage)
            }
        }
    }

    // DiffUtil.Callback untuk mendeteksi perbedaan data di antara dua list
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
