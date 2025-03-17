package com.example.codefix.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.codefix.databinding.ItemDebugHistoryBinding
import com.example.codefix.model.DebugHistoryItem

class DebugHistoryAdapter(
    private val onDeleteClick: (DebugHistoryItem) -> Unit,
    private val onViewDetailsClick: (DebugHistoryItem) -> Unit
) : ListAdapter<DebugHistoryItem, DebugHistoryAdapter.DebugHistoryViewHolder>(DebugDiffCallback()) {

    inner class DebugHistoryViewHolder(private val binding: ItemDebugHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(history: DebugHistoryItem) {
            binding.tvDebugDate.text = history.timestampFormatted()
            binding.tvDebuggedCode.text = history.debuggedCode

            // Handle "View Details" button click
            binding.btnViewDetails.setOnClickListener {
                onViewDetailsClick(history)
            }

            // Handle "Delete" button click
            binding.btnDeleteHistory.setOnClickListener {
                onDeleteClick(history)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebugHistoryViewHolder {
        val binding = ItemDebugHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DebugHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DebugHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

// ✅ Efficient DiffUtil for better performance
class DebugDiffCallback : DiffUtil.ItemCallback<DebugHistoryItem>() {
    override fun areItemsTheSame(oldItem: DebugHistoryItem, newItem: DebugHistoryItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DebugHistoryItem, newItem: DebugHistoryItem): Boolean {
        return oldItem == newItem
    }
}
