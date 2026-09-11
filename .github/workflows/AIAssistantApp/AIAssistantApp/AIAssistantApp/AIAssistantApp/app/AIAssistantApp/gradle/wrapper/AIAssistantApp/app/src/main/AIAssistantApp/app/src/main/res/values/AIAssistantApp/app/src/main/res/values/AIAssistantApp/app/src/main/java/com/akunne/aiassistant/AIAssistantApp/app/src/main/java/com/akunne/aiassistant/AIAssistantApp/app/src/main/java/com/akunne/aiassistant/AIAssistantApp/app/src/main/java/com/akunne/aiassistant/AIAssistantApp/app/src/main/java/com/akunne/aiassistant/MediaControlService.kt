package com.akunne.aiassistant

import android.content.Context
import android.media.AudioManager
import android.view.KeyEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaControlService(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    suspend fun playPause(): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            simulateMediaKeyPress(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
            Result.success("Play/Pause toggled")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun nextTrack(): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            simulateMediaKeyPress(KeyEvent.KEYCODE_MEDIA_NEXT)
            Result.success("Playing next track")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun previousTrack(): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            simulateMediaKeyPress(KeyEvent.KEYCODE_MEDIA_PREVIOUS)
            Result.success("Playing previous track")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun volumeUp(): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val newLevel = (current + 1).coerceAtMost(max)
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newLevel, AudioManager.FLAG_SHOW_UI)
            Result.success("Volume increased")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun volumeDown(): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            val newLevel = (current - 1).coerceAtLeast(0)
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newLevel, AudioManager.FLAG_SHOW_UI)
            Result.success("Volume decreased")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun simulateMediaKeyPress(keyCode: Int) {
        val event = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
        audioManager.dispatchMediaKeyEvent(event)
        val event2 = KeyEvent(KeyEvent.ACTION_UP, keyCode)
        audioManager.dispatchMediaKeyEvent(event2)
    }
}
