package com.akunne.aiassistant

import android.content.Context

class Prefs(context: Context) {
    private val sp = context.getSharedPreferences("atlas_prefs", Context.MODE_PRIVATE)

    var homeAssistantUrl: String
        get() = sp.getString("ha_url", "") ?: ""
        set(value) = sp.edit().putString("ha_url", value).apply()

    var homeAssistantToken: String
        get() = sp.getString("ha_token", "") ?: ""
        set(value) = sp.edit().putString("ha_token", value).apply()

    var backgroundListeningEnabled: Boolean
        get() = sp.getBoolean("bg_listening", true)
        set(value) = sp.edit().putBoolean("bg_listening", value).apply()

    fun isConfigured(): Boolean = homeAssistantUrl.isNotBlank() && homeAssistantToken.isNotBlank()
}
