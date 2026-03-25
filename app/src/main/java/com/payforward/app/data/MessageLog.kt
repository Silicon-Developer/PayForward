package com.payforward.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message_logs")
data class MessageLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sender: String,
    val body: String,
    val matchedKeyword: String,
    val forwardedTo: String,
    val status: ForwardStatus,
    val timestamp: Long = System.currentTimeMillis(),
    val source: MessageSource = MessageSource.SMS
)

enum class ForwardStatus {
    SUCCESS,
    FAILED,
    PENDING
}

enum class MessageSource {
    SMS,
    NOTIFICATION
}
