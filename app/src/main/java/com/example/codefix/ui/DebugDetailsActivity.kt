package com.example.codefix.ui

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.codefix.databinding.ActivityDebugDetailsBinding
import com.example.codefix.model.DebugHistoryItem

class DebugDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDebugDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDebugDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable Back Navigation
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Debug Details"

        // Retrieve DebugHistoryItem safely
        val debugItem: DebugHistoryItem? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("debugItem", DebugHistoryItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("debugItem")
        }

        if (debugItem != null) {
            binding.tvDebugTimestamp.text = debugItem.timestampFormatted()
            binding.tvCodeInput.text = debugItem.originalCode.ifEmpty { "No Input Code Provided" }
            binding.tvCodeDebugged.text = debugItem.debuggedCode.ifEmpty { "No Debugged Code Available" }

            // Improve Accessibility
            binding.tvDebugTimestamp.contentDescription = "Debug timestamp: ${binding.tvDebugTimestamp.text}"
            binding.tvCodeInput.contentDescription = "Original code: ${binding.tvCodeInput.text}"
            binding.tvCodeDebugged.contentDescription = "Debugged code: ${binding.tvCodeDebugged.text}"
        } else {
            Toast.makeText(this, "Failed to retrieve debug details.", Toast.LENGTH_LONG).show()
            finish()
        }

        // Handle Copy Button
        binding.btnCopyCode.setOnClickListener {
            val textToCopy = binding.tvCodeDebugged.text.toString()
            if (textToCopy.isNotEmpty()) {
                copyToClipboard(textToCopy)
                Toast.makeText(this, "Debugged code copied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Debugged Code", text)
        clipboard.setPrimaryClip(clip)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
