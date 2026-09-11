package com.akunne.aiassistant

import android.content.Context
import android.provider.CallLog
import android.provider.ContactsContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Contact(val name: String, val phoneNumber: String)
data class CallLogEntry(val name: String, val number: String, val timestamp: Long, val type: String)

class ContactsService(private val context: Context) {

    suspend fun findContact(query: String): Contact? = withContext(Dispatchers.IO) {
        try {
            val uri = ContactsContract.Contacts.CONTENT_URI
            val cursor = context.contentResolver.query(
                uri,
                arrayOf(
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER
                ),
                "${ContactsContract.Contacts.DISPLAY_NAME} LIKE ?",
                arrayOf("%$query%"),
                null
            ) ?: return@withContext null

            cursor.use {
                if (it.moveToFirst()) {
                    val id = it.getString(0)
                    val name = it.getString(1)
                    val phoneNumber = getPhoneNumberForContact(id)
                    return@withContext Contact(name, phoneNumber)
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    private fun getPhoneNumberForContact(contactId: String): String {
        val phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val cursor = context.contentResolver.query(
            phoneUri,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
            arrayOf(contactId),
            null
        ) ?: return ""

        cursor.use {
            if (it.moveToFirst()) {
                return it.getString(0)
            }
        }
        return ""
    }

    suspend fun getLastCaller(): CallLogEntry? = withContext(Dispatchers.IO) {
        try {
            val cursor = context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                arrayOf(
                    CallLog.Calls.CACHED_NAME,
                    CallLog.Calls.NUMBER,
                    CallLog.Calls.DATE,
                    CallLog.Calls.TYPE
                ),
                null,
                null,
                "${CallLog.Calls.DATE} DESC LIMIT 1"
            ) ?: return@withContext null

            cursor.use {
                if (it.moveToFirst()) {
                    val name = it.getString(0) ?: it.getString(1)
                    val number = it.getString(1)
                    val timestamp = it.getLong(2)
                    val type = when (it.getInt(3)) {
                        CallLog.Calls.INCOMING_TYPE -> "Incoming call"
                        CallLog.Calls.OUTGOING_TYPE -> "Outgoing call"
                        CallLog.Calls.MISSED_TYPE -> "Missed call"
                        else -> "Call"
                    }
                    return@withContext CallLogEntry(name, number, timestamp, type)
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    fun formatTime(timestamp: Long): String {
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        return sdf.format(date)
    }
}
