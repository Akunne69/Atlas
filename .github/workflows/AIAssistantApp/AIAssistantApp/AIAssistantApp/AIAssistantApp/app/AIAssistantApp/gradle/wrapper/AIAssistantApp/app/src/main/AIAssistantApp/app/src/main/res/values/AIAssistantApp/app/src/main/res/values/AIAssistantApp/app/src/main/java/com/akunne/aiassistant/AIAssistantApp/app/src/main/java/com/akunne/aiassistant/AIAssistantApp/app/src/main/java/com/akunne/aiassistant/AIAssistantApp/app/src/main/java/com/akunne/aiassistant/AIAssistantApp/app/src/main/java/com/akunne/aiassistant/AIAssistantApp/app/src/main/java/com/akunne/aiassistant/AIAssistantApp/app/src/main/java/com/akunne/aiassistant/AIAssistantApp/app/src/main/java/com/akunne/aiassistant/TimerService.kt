package com.akunne.aiassistant

import android.os.CountDownLatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class TimerService {

    private var timerRunning = false
    private var timerSeconds = 0

    suspend fun startTimer(minutes: Int): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            if (timerRunning) {
                return@withContext Result.failure(Exception("Timer already running"))
            }
            timerSeconds = minutes * 60
            timerRunning = true
            Result.success("Timer started for $minutes minutes")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun stopTimer(): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            timerRunning = false
            Result.success("Timer stopped")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getTimerStatus(): String {
        if (!timerRunning) return "No timer running"
        val minutes = timerSeconds / 60
        val seconds = timerSeconds % 60
        return String.format("Timer: %d:%02d remaining", minutes, seconds)
    }
}
