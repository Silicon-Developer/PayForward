package com.payforward.app.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.payforward.app.data.MessageSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class NotificationListener : NotificationListenerService() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Payment app package names to monitor
    private val paymentAppPackages = setOf(
        "com.google.android.apps.nbu.paisa.user",  // GPay
        "com.phonepe.app",                           // PhonePe
        "net.one97.paytm",                           // Paytm
        "in.amazon.mShop.android.shopping",          // Amazon Pay
        "com.whatsapp",                               // WhatsApp Pay
        "in.org.npci.upiapp",                        // BHIM UPI
        "com.mobikwik_new",                           // MobiKwik
        "com.freecharge.android",                     // Freecharge
        "com.sbi.SBIFreedomPlus",                    // SBI YONO
        "com.csam.icici.bank.imobile",               // iMobile Pay
    )

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return

        val secureStorage = SecureStorage(applicationContext)
        if (!secureStorage.isServiceActive) return

        val packageName = sbn.packageName ?: return

        // Process notifications from payment apps OR any SMS app
        if (packageName !in paymentAppPackages &&
            !packageName.contains("messaging", ignoreCase = true) &&
            !packageName.contains("sms", ignoreCase = true)
        ) return

        val notification = sbn.notification ?: return
        val extras = notification.extras ?: return

        val title = extras.getCharSequence("android.title")?.toString() ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        val bigText = extras.getCharSequence("android.bigText")?.toString() ?: ""

        // Use bigText if available (contains full message), otherwise use text
        val body = bigText.ifBlank { text }
        if (body.isBlank()) return

        val sender = "$title ($packageName)"

        val forwardingManager = ForwardingManager(applicationContext)

        scope.launch {
            forwardingManager.processMessage(
                sender = sender,
                body = body,
                source = MessageSource.NOTIFICATION
            )
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // No action needed
    }
}
