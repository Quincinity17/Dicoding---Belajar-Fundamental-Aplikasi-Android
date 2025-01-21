package com.example.halfsubmission.ui.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.halfsubmission.databinding.ItemCardBinding
import com.example.halfsubmission.databinding.ItemShimmerCardBinding
import com.example.halfsubmission.data.response.ListEventsItem

class EventAdapter(
    private val listEvent: List<ListEventsItem>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_LOADING = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (listEvent[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        return if (viewType == VIEW_TYPE_LOADING) {
            val binding = ItemShimmerCardBinding.inflate(inflater, viewGroup, false)
            LoadingViewHolder(binding)
        } else {
            val binding = ItemCardBinding.inflate(inflater, viewGroup, false)
            ItemViewHolder(binding)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is ItemViewHolder) {
            val event = listEvent[position]
            if (event != null) {
                viewHolder.bind(event, onItemClick)
            }
        }
    }

    override fun getItemCount(): Int {
        return listEvent.size
    }

    class ItemViewHolder(private val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem, onItemClick: (String) -> Unit) {
            binding.tvItem.text = "\n${event.name}"
            Glide.with(binding.root.context)
                .load(event.imageLogo)
                .into(binding.imageView)
            binding.root.setOnClickListener {
                onItemClick(event.id.toString())
            }
        }
    }

    class LoadingViewHolder(binding: ItemShimmerCardBinding) : RecyclerView.ViewHolder(binding.root)
}
