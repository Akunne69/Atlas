package com.akunne.aiassistant

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class HomeAssistantClient(private val prefs: Prefs) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    suspend fun callService(domain: String, service: String, entityId: String): Result<String> =
        withContext(Dispatchers.IO) {
            if (!prefs.isConfigured()) {
                return@withContext Result.failure(IllegalStateException(
                    "Home Assistant not configured"
                ))
            }
            try {
                val json = """{"entity_id": "$entityId"}"""
                val body = json.toRequestBody("application/json".toMediaType())
                val url = "${prefs.homeAssistantUrl.trimEnd('/')}/api/services/$domain/$service"

                val request = Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer ${prefs.homeAssistantToken}")
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        Result.success("Done")
                    } else {
                        Result.failure(Exception("Home Assistant returned ${response.code}"))
                    }
                }
            } catch (e: Exception) {
                Result.failure(Exception("Couldn't reach Home Assistant: ${e.message}"))
            }
        }
}
