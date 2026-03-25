package com.payforward.app.service

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureStorage(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val securePrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "payforward_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val regularPrefs: SharedPreferences =
        context.getSharedPreferences("payforward_prefs", Context.MODE_PRIVATE)

    // --- Forwarding Method ---
    var forwardingMethod: ForwardingMethod
        get() = ForwardingMethod.valueOf(
            regularPrefs.getString(KEY_FORWARDING_METHOD, ForwardingMethod.WEBHOOK.name)
                ?: ForwardingMethod.WEBHOOK.name
        )
        set(value) = regularPrefs.edit().putString(KEY_FORWARDING_METHOD, value.name).apply()

    // --- Webhook URL (encrypted) ---
    var webhookUrl: String
        get() = securePrefs.getString(KEY_WEBHOOK_URL, "") ?: ""
        set(value) = securePrefs.edit().putString(KEY_WEBHOOK_URL, value).apply()

    // --- Telegram Bot Token (encrypted) ---
    var telegramBotToken: String
        get() = securePrefs.getString(KEY_TELEGRAM_TOKEN, "") ?: ""
        set(value) = securePrefs.edit().putString(KEY_TELEGRAM_TOKEN, value).apply()

    // --- Telegram Chat ID (encrypted) ---
    var telegramChatId: String
        get() = securePrefs.getString(KEY_TELEGRAM_CHAT_ID, "") ?: ""
        set(value) = securePrefs.edit().putString(KEY_TELEGRAM_CHAT_ID, value).apply()

    // --- SMS Forwarding Number (encrypted) ---
    var smsForwardingNumber: String
        get() = securePrefs.getString(KEY_SMS_NUMBER, "") ?: ""
        set(value) = securePrefs.edit().putString(KEY_SMS_NUMBER, value).apply()

    // --- Service Active State ---
    var isServiceActive: Boolean
        get() = regularPrefs.getBoolean(KEY_SERVICE_ACTIVE, false)
        set(value) = regularPrefs.edit().putBoolean(KEY_SERVICE_ACTIVE, value).apply()

    // --- Theme Mode ---
    var themeMode: ThemeMode
        get() = ThemeMode.valueOf(
            regularPrefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name)
                ?: ThemeMode.SYSTEM.name
        )
        set(value) = regularPrefs.edit().putString(KEY_THEME_MODE, value.name).apply()

    // --- Terms Accepted ---
    var termsAccepted: Boolean
        get() = regularPrefs.getBoolean(KEY_TERMS_ACCEPTED, false)
        set(value) = regularPrefs.edit().putBoolean(KEY_TERMS_ACCEPTED, value).apply()

    // --- Anti-Spoofing ---
    var isAntiSpoofingEnabled: Boolean
        get() = regularPrefs.getBoolean(KEY_ANTI_SPOOFING, false)
        set(value) = regularPrefs.edit().putBoolean(KEY_ANTI_SPOOFING, value).apply()

    var trustedSenderIds: Set<String>
        get() = regularPrefs.getStringSet(KEY_TRUSTED_SENDERS, emptySet()) ?: emptySet()
        set(value) = regularPrefs.edit().putStringSet(KEY_TRUSTED_SENDERS, value).apply()

    fun isForwardingConfigured(): Boolean {
        return when (forwardingMethod) {
            ForwardingMethod.WEBHOOK -> webhookUrl.isNotBlank()
            ForwardingMethod.TELEGRAM -> telegramBotToken.isNotBlank() && telegramChatId.isNotBlank()
            ForwardingMethod.SMS -> smsForwardingNumber.isNotBlank()
        }
    }

    companion object {
        private const val KEY_FORWARDING_METHOD = "forwarding_method"
        private const val KEY_WEBHOOK_URL = "webhook_url"
        private const val KEY_TELEGRAM_TOKEN = "telegram_bot_token"
        private const val KEY_TELEGRAM_CHAT_ID = "telegram_chat_id"
        private const val KEY_SMS_NUMBER = "sms_forwarding_number"
        private const val KEY_SERVICE_ACTIVE = "service_active"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_TERMS_ACCEPTED = "terms_accepted"
        private const val KEY_ANTI_SPOOFING = "anti_spoofing_enabled"
        private const val KEY_TRUSTED_SENDERS = "trusted_senders"
    }
}

enum class ForwardingMethod {
    WEBHOOK,
    TELEGRAM,
    SMS
}

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}
