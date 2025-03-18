package com.example.codefix.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


object Config {
    const val GEMINI_API_KEY = "YOUR_API_KEY"
}


object GeminiApiHelper {
    private const val GEMINI_URL =
        "https://generativelanguage.googleapis.com/v1/models/gemini-pro:generateContent"
    private val client = OkHttpClient()

    suspend fun sendCodeForDebugging(code: String): String {
        return withContext(Dispatchers.IO) { // ✅ Runs API call in the background
            val apiKey = Config.GEMINI_API_KEY
            val requestBody = JSONObject()
                .put("contents", listOf(JSONObject().put("parts", listOf(JSONObject().put("text", code)))))
                .toString()
                .toRequestBody("application/json".toMediaTypeOrNull())

            val request = Request.Builder()
                .url("$GEMINI_URL?key=$apiKey")
                .post(requestBody)
                .build()

            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: return@withContext "❌ Error: No response from API"

                // ✅ Parse AI response properly
                parseAIResponse(responseBody)
            } catch (e: Exception) {
                Log.e("GeminiApiHelper", "API Call Failed: ${e.message}")
                "❌ Error: ${e.message}"
            }
        }
    }

    private fun parseAIResponse(responseBody: String): String {
        return try {
            val jsonObject = JSONObject(responseBody)
            val candidatesArray = jsonObject.optJSONArray("candidates")
            if (candidatesArray != null && candidatesArray.length() > 0) {
                val firstCandidate = candidatesArray.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val partsArray = content?.optJSONArray("parts")
                if (partsArray != null && partsArray.length() > 0) {
                    partsArray.getJSONObject(0).optString("text", "⚠️ No valid debug response.")
                } else {
                    "⚠️ No parts array found."
                }
            } else {
                "⚠️ No candidates found."
            }
        } catch (e: Exception) {
            Log.e("GeminiApiHelper", "Parsing Error: ${e.message}")
            "⚠️ Failed to parse AI response."
        }
    }
}
