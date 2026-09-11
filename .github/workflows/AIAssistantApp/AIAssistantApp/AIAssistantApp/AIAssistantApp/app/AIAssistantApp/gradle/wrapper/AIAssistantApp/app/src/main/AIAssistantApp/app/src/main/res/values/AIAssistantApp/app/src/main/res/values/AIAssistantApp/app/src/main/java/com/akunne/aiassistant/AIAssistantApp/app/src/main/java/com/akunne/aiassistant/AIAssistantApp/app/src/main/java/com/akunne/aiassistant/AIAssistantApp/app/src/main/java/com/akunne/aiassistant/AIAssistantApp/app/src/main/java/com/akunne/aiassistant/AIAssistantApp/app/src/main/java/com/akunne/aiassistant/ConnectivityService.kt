package com.akunne.aiassistant

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.net.wifi.WifiManager
import android.provider.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConnectivityService(private val context: Context) {

    private val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    suspend fun toggleWiFi(): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val enabled = wifiManager.isWifiEnabled
            wifiManager.isWifiEnabled = !enabled
            val status = if (!enabled) "WiFi turned on" else "WiFi turned off"
            Result.success(status)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleBluetooth(): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            if (bluetoothAdapter == null) {
                return@withContext Result.failure(Exception("Bluetooth not available"))
            }
            if (bluetoothAdapter.isEnabled) {
                bluetoothAdapter.disable()
                Result.success("Bluetooth turned off")
            } else {
                bluetoothAdapter.enable()
                Result.success("Bluetooth turned on")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun increaseBrightness(): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val current = Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, 128)
            val newLevel = (current + 25).coerceAtMost(255)
            Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, newLevel)
            Result.success("Brightness increased")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun decreaseBrightness(): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val current = Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, 128)
            val newLevel = (current - 25).coerceAtLeast(1)
            Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, newLevel)
            Result.success("Brightness decreased")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getWiFiStatus(): String = if (wifiManager.isWifiEnabled) "WiFi is on" else "WiFi is off"
    fun getBluetoothStatus(): String = if (bluetoothAdapter?.isEnabled == true) "Bluetooth is on" else "Bluetooth is off"
}
