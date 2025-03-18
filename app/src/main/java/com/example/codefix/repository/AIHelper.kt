package com.example.codefix.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import com.example.codefix.BuildConfig
import org.json.JSONArray
import java.util.concurrent.TimeUnit

object AIHelper {
    private const val HUGGINGFACE_API_URL = "https://api-inference.huggingface.co/models/bigcode/starcoder"

    // ✅ Set longer timeout to avoid API timeout issues
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)  // Increased connection timeout
        .readTimeout(60, TimeUnit.SECONDS)     // Increased read timeout
        .writeTimeout(60, TimeUnit.SECONDS)    // Increased write timeout
        .build()

    suspend fun sendCodeForDebugging(code: String): String {
        return withContext(Dispatchers.IO) {
            val apiKey = BuildConfig.HUGGINGFACE_API_KEY

            if (apiKey.isEmpty()) {
                return@withContext "⚠️ API Key is missing. Check `local.properties`."
            }

            // ✅ Sanitize input to prevent JSON formatting issues
            val sanitizedCode = code.replace("\\", "\\\\") // Escape backslashes
                .replace("\"", "\\\"")  // Escape quotes
                .replace("\n", "\\n")   // Escape new lines

            val requestBody = JSONObject()
                .put("inputs", sanitizedCode)
                .toString()
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(HUGGINGFACE_API_URL)
                .addHeader("Authorization", "Bearer $apiKey")
                .post(requestBody)
                .build()

            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: return@withContext "❌ No response from AI."

                Log.d("API_RESPONSE", "Response: $responseBody")
                parseAIResponse(responseBody)
            } catch (e: Exception) {
                Log.e("AIHelper", "API Call Failed: ${e.message}")
                "❌ Error: ${e.message}"
            }
        }
    }

    private fun parseAIResponse(responseBody: String): String {
        return try {
            val jsonArray = JSONArray(responseBody)
            if (jsonArray.length() > 0) {
                val firstObject = jsonArray.getJSONObject(0)
                firstObject.optString("generated_text", "⚠️ No valid response.")  // ✅ Extract generated_text
            } else {
                "⚠️ No valid response from AI."
            }
        } catch (e: Exception) {
            Log.e("AIHelper", "Parsing Error: ${e.message}")
            "⚠️ Failed to parse AI response."
        }
    }


}
