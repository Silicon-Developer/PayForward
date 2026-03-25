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
            val rawSender = sms.displayOriginatingAddress ?: "Unknown"
            // Ensure trailing details are handled if needed, usually string exact match is better
            messageMap.getOrPut(rawSender) { StringBuilder() }.append(sms.messageBody ?: "")
        }

        val forwardingManager = ForwardingManager(context)

        for ((sender, bodyBuilder) in messageMap) {
            val body = bodyBuilder.toString()
            
            // --- Anti-Spoofing Filter Engine ---
            if (secureStorage.isAntiSpoofingEnabled) {
                // 1. Block purely numeric 10-digit mobile numbers
                if (sender.matches(Regex("^[0-9]{10}$"))) {
                    continue // Drop this message silently
                }
                
                // 2. Block if sender is not in the trusted list (if list is populated)
                val trustedSenders = secureStorage.trustedSenderIds
                if (trustedSenders.isNotEmpty()) {
                    var isTrusted = false
                    for (trusted in trustedSenders) {
                        if (sender.contains(trusted, ignoreCase = true)) {
                            isTrusted = true
                            break
                        }
                    }
                    if (!isTrusted) {
                        continue // Drop this message silently
                    }
                }
            }
            // ------------------------------------
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
