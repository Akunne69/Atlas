package com.akunne.aiassistant

import android.content.Context
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PhoneControlService(private val context: Context) {

    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    suspend fun setFlashlight(on: Boolean): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val cameraId = cameraManager.cameraIdList.firstOrNull() ?: ""
            if (cameraId.isEmpty()) return@withContext Result.failure(Exception("No camera found"))
            cameraManager.setTorchMode(cameraId, on)
            Result.success(if (on) "Flashlight turned on" else "Flashlight turned off")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setRingerMode(mode: String): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val ringerMode = when (mode.lowercase()) {
                "silent" -> AudioManager.RINGER_MODE_SILENT
                "vibrate" -> AudioManager.RINGER_MODE_VIBRATE
                "normal" -> AudioManager.RINGER_MODE_NORMAL
                else -> return@withContext Result.failure(Exception("Unknown ringer mode: $mode"))
            }
            audioManager.ringerMode = ringerMode
            Result.success("Ringer mode set to $mode")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun silenceCalls(): Result<String> = setRingerMode("vibrate")

    fun getRingerModeString(): String = when (audioManager.ringerMode) {
        AudioManager.RINGER_MODE_SILENT -> "Silent"
        AudioManager.RINGER_MODE_VIBRATE -> "Vibrate"
        AudioManager.RINGER_MODE_NORMAL -> "Normal"
        else -> "Unknown"
    }

    fun getBatteryPercentage(): Int {
        val batteryIntent = context.registerReceiver(null, android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent?.getIntExtra(android.os.BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(android.os.BatteryManager.EXTRA_SCALE, -1) ?: -1
        return if (level >= 0 && scale > 0) (level * 100) / scale else -1
    }
}
