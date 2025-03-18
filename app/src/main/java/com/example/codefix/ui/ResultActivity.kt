package com.example.codefix.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.codefix.R
import com.example.codefix.databinding.ActivityResultBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.*
import java.io.IOException

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val apiKey = getString(R.string.gemini_api_key)
        if (apiKey.isEmpty()) {
            binding.tvDebugResult.text = getString(R.string.api_key_missing)
            return
        }

        val userCode = intent.getStringExtra("USER_CODE") ?: getString(R.string.no_code_provided)
        binding.tvUserCode.text = userCode
        binding.tvDebugResult.text = getString(R.string.debugging_in_progress)

        // Call Gemini API for debugging
        debugCodeWithGemini(apiKey, userCode)

        // Copy result to clipboard
        binding.btnCopyResult.setOnClickListener {
            copyToClipboard(binding.tvDebugResult.text.toString())
        }

        // Share debugging result
        binding.btnShareResult.setOnClickListener {
            shareDebugResult(binding.tvDebugResult.text.toString())
        }
    }

    private fun debugCodeWithGemini(apiKey: String, userCode: String) {
        val url = "https://generativelanguage.googleapis.com/v1/models/gemini-pro:generateContent?key=$apiKey"

        val requestBody = """
            {
                "contents": [{
                    "parts": [{
                        "text": "Analyze and debug this code. Provide suggestions and fixes:\n$userCode"
                    }]
                }]
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), requestBody))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    binding.tvDebugResult.text = getString(R.string.error_fetching_ai)
                    Log.e("GeminiAPI", "API Call Failed: ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { responseBody ->
                    val result = parseAIResponse(responseBody)
                    runOnUiThread {
                        binding.tvDebugResult.text = result
                    }
                }
            }
        })
    }

    private fun parseAIResponse(responseBody: String): String {
        return try {
            val jsonObject = com.google.gson.JsonParser.parseString(responseBody).asJsonObject
            jsonObject["candidates"]?.asJsonArray?.firstOrNull()?.asJsonObject
                ?.get("content")?.asJsonObject?.get("parts")?.asJsonArray?.firstOrNull()?.asJsonObject
                ?.get("text")?.asString ?: getString(R.string.no_valid_response)
        } catch (e: Exception) {
            Log.e("GeminiAPI", "Parsing Error: ${e.message}")
            getString(R.string.parsing_error)
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Debug Result", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
    }

    private fun shareDebugResult(text: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_debug_result)))
    }
}

