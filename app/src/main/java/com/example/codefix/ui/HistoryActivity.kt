package com.example.codefix.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.codefix.adapter.DebugHistoryAdapter
import com.example.codefix.databinding.ActivityHistoryBinding
import com.example.codefix.model.DebugHistoryItem

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var adapter: DebugHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadHistory()

        // Swipe to Refresh Listener
        binding.swipeRefresh.setOnRefreshListener {
            loadHistory() // Reload history
            binding.swipeRefresh.isRefreshing = false // Stop refresh animation
        }
    }

    private fun setupRecyclerView() {
        adapter = DebugHistoryAdapter(
            onViewDetails = { item -> showDetails(item) },
            onDelete = { id -> deleteHistoryItem(id) }
        )
        binding.rvDebugHistory.layoutManager = LinearLayoutManager(this)
        binding.rvDebugHistory.adapter = adapter
    }

    private fun loadHistory() {
        // Sample data with correct Long timestamp values
        val sampleData = listOf(
            DebugHistoryItem("1", "print('Hello World')", 1710528000000L), // Correct Long timestamp
            DebugHistoryItem("2", "val x = null", 1710441600000L)  // Another correct Long timestamp
        )

        if (sampleData.isEmpty()) {
            binding.tvEmptyMessage.visibility = View.VISIBLE
            binding.rvDebugHistory.visibility = View.GONE
        } else {
            binding.tvEmptyMessage.visibility = View.GONE
            binding.rvDebugHistory.visibility = View.VISIBLE
            adapter.submitList(sampleData)
        }
    }


    private fun showDetails(item: DebugHistoryItem) {
        // TODO: Implement navigation to details screen
    }

    private fun deleteHistoryItem(id: String) {
        val updatedList = adapter.currentList.filter { it.id != id }
        adapter.submitList(updatedList)
    }

}
