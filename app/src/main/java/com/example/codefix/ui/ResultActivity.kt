package com.example.codefix.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.codefix.databinding.ActivityResultBinding
import com.example.codefix.repository.AIHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userCode = intent.getStringExtra("USER_CODE") ?: "⚠️ No code provided."
        binding.tvUserCode.text = userCode
        binding.tvDebugResult.text = "🔄 Debugging in progress..."

        debugCodeWithAI(userCode)

        binding.btnCopyResult.setOnClickListener {
            copyToClipboard(binding.tvDebugResult.text.toString())
        }

        binding.btnShareResult.setOnClickListener {
            shareDebugResult(binding.tvDebugResult.text.toString())
        }
    }

    private fun debugCodeWithAI(userCode: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = AIHelper.sendCodeForDebugging(userCode)  // ✅ Fixed function call
            binding.tvDebugResult.text = result
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Debug Result", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "📋 Copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    private fun shareDebugResult(text: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "📤 Share Debug Result"))
    }
}
