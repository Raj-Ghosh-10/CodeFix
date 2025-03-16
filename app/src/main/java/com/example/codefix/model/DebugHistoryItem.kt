package com.example.codefix.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DebugHistoryItem(
    val id: String = "",
    val debuggedCode: String = "",
    val timestamp: Long = System.currentTimeMillis()  // Ensure timestamp is stored
) {
    fun timestampFormatted(): String {
        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }
}