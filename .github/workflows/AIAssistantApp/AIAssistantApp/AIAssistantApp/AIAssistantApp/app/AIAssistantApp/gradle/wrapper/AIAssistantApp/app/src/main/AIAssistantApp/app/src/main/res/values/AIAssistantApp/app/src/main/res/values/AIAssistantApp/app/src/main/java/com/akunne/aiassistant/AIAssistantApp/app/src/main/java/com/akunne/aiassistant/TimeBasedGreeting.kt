package com.akunne.aiassistant

import android.text.format.DateFormat
import java.util.Calendar

object TimeBasedGreeting {
    
    fun getGreeting(userName: String = "Nonny"): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        
        return when {
            hour in 5..11 -> "Good morning $userName"
            hour in 12..16 -> "Good afternoon $userName"
            hour in 17..20 -> "Good evening $userName"
            else -> "Hello $userName"
        }
    }
    
    fun getTimeString(): String {
        val calendar = Calendar.getInstance()
        val is24Hour = DateFormat.is24HourFormat(null)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val ampm = if (is24Hour) "" else if (hour >= 12) " PM" else " AM"
        val displayHour = if (is24Hour) hour else if (hour > 12) hour - 12 else if (hour == 0) 12 else hour
        
        return String.format("%d:%02d%s", displayHour, minute, ampm)
    }
}
