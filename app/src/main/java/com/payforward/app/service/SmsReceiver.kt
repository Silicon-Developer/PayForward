package com.payforward.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.payforward.app.data.MessageSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val secureStorage = SecureStorage(context)
        if (!secureStorage.isServiceActive) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isNullOrEmpty()) return

        // Group SMS parts by sender
        val messageMap = mutableMapOf<String, StringBuilder>()
        for (sms in messages) {
            val sender = sms.displayOriginatingAddress ?: "Unknown"
            messageMap.getOrPut(sender) { StringBuilder() }.append(sms.messageBody ?: "")
        }

        val forwardingManager = ForwardingManager(context)

        for ((sender, bodyBuilder) in messageMap) {
            val body = bodyBuilder.toString()
            scope.launch {
                forwardingManager.processMessage(
                    sender = sender,
                    body = body,
                    source = MessageSource.SMS
                )
            }
        }
    }
}
