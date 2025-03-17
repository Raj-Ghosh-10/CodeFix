package com.example.codefix.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.codefix.adapter.DebugHistoryAdapter
import com.example.codefix.databinding.ActivityHistoryBinding
import com.example.codefix.model.DebugHistoryItem
import com.example.codefix.repository.FirestoreRepository
import com.google.firebase.firestore.ListenerRegistration

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var adapter: DebugHistoryAdapter
    private val repository = FirestoreRepository()
    private var historyListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        listenToHistoryUpdates()
    }

    private fun setupRecyclerView() {
        adapter = DebugHistoryAdapter(
            onDeleteClick = { item -> deleteDebugHistory(item.id) },
            onViewDetailsClick = { item -> viewDebugDetails(item) }
        )
        binding.rvDebugHistory.layoutManager = LinearLayoutManager(this)
        binding.rvDebugHistory.adapter = adapter
    }

    private fun listenToHistoryUpdates() {
        binding.progressBar.visibility = View.VISIBLE  // Show loading

        historyListener = repository.listenToDebugHistory { historyList ->
            binding.progressBar.visibility = View.GONE  // Hide loading

            if (historyList.isEmpty()) {
                binding.tvEmptyMessage.visibility = View.VISIBLE  // ✅ Corrected
                binding.rvDebugHistory.visibility = View.GONE
            } else {
                binding.tvEmptyMessage.visibility = View.GONE  // ✅ Corrected
                binding.rvDebugHistory.visibility = View.VISIBLE
                adapter.submitList(historyList)
            }
        }

        if (historyListener == null) {
            Log.e("Firestore", "Failed to attach Firestore listener")
        }
    }

    private fun deleteDebugHistory(id: String) {
        repository.deleteDebugHistory(id,
            onSuccess = {
                Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show()
            },
            onFailure = { e ->
                Log.e("Firestore", "Error deleting document: ${e.message}")
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun viewDebugDetails(item: DebugHistoryItem) {
        if (item.debuggedCode.isNotEmpty()) {
            val intent = Intent(this, DebugDetailsActivity::class.java).apply {
                putExtra("debugItem", item)
            }
            startActivity(intent)
        } else {
            Toast.makeText(this, "Debugged code is missing!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        historyListener?.remove()
        historyListener = null
    }
}
