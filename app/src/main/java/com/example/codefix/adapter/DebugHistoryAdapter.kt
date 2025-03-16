package com.example.codefix.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.codefix.databinding.ItemDebugHistoryBinding
import com.example.codefix.model.DebugHistoryItem

class DebugHistoryAdapter(
    private val onViewDetails: (DebugHistoryItem) -> Unit,
    private val onDelete: (String) -> Unit
) : ListAdapter<DebugHistoryItem, DebugHistoryAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: ItemDebugHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DebugHistoryItem) {
            binding.tvDebuggedCode.text = item.debuggedCode
            binding.tvDebugDate.text = item.timestampFormatted()

            binding.btnViewDetails.setOnClickListener { onViewDetails(item) }
            binding.btnDeleteHistory.setOnClickListener { onDelete(item.id) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDebugHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<DebugHistoryItem>() {
            override fun areItemsTheSame(oldItem: DebugHistoryItem, newItem: DebugHistoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DebugHistoryItem, newItem: DebugHistoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}