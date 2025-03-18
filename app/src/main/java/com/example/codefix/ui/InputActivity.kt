package com.example.codefix.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.codefix.databinding.ActivityInputBinding

class InputActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInputBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkApiKey()  // ✅ Check if API Key is loaded

        binding.btnSubmit.setOnClickListener {
            val userCode = binding.etCodeInput.text.toString().trim()
            if (userCode.isEmpty()) {
                Toast.makeText(this, "⚠️ Please enter code to debug", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra("USER_CODE", userCode)
                startActivity(intent)
            }
        }
    }

    private fun checkApiKey() {
        val apiKey = getString(com.example.codefix.R.string.huggingface_api_key)  // ✅ Fetch from `strings.xml`
        Log.d("API_KEY_TEST", "Hugging Face API Key: $apiKey")

        if (apiKey.isEmpty()) {
            Log.e("API_KEY_ERROR", "❌ Missing API Key! Check `strings.xml` or `local.properties`.")
        }
    }
}
