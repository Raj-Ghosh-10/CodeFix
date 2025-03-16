package com.example.codefix.model

import androidx.annotation.Keep
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Keep
data class DebugHistory(
    val id: String = UUID.randomUUID().toString(), // Auto-generate ID if not provided
    val inputCode: String = "",
    val debuggedCode: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    fun timestampFormatted(): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
        return synchronized(sdf) { sdf.format(Date(timestamp)) } // Ensure thread safety
    }
}