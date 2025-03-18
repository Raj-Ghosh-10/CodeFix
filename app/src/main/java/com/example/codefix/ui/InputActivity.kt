package com.example.codefix.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.codefix.databinding.ActivityInputBinding

class InputActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInputBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Handle button click to pass input to ResultActivity
        binding.btnSubmit.setOnClickListener {
            val userCode = binding.etCodeInput.text.toString().trim()
            if (userCode.isEmpty()) {
                Toast.makeText(this, "Please enter code to debug", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra("USER_CODE", userCode)
                startActivity(intent)
            }
        }
    }
}
