package com.payforward.app.service

import android.content.Context
import android.telephony.SmsManager
import com.payforward.app.data.AppDatabase
import com.payforward.app.data.ForwardStatus
import com.payforward.app.data.MessageLog
import com.payforward.app.data.MessageSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class ForwardingManager(private val context: Context) {

    private val secureStorage = SecureStorage(context)
    private val keywordEngine = KeywordEngine(context)
    private val messageLogDao = AppDatabase.getDatabase(context).messageLogDao()

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun processMessage(
        sender: String,
        body: String,
        source: MessageSource
    ) {
        if (!secureStorage.isServiceActive) return

        val matchedKeywords = keywordEngine.matches(body)
        if (matchedKeywords.isEmpty()) return

        val log = MessageLog(
            sender = sender,
            body = body,
            matchedKeyword = matchedKeywords.joinToString(", "),
            forwardedTo = getForwardingDestination(),
            status = ForwardStatus.PENDING,
            source = source
        )

        val logId = messageLogDao.insert(log)

        try {
            val success = when (secureStorage.forwardingMethod) {
                ForwardingMethod.WEBHOOK -> forwardViaWebhook(sender, body, matchedKeywords)
                ForwardingMethod.TELEGRAM -> forwardViaTelegram(sender, body, matchedKeywords)
                ForwardingMethod.SMS -> forwardViaSms(sender, body)
            }

            messageLogDao.update(
                log.copy(
                    id = logId,
                    status = if (success) ForwardStatus.SUCCESS else ForwardStatus.FAILED
                )
            )
        } catch (e: Exception) {
            messageLogDao.update(
                log.copy(id = logId, status = ForwardStatus.FAILED)
            )
        }
    }

    private suspend fun forwardViaWebhook(
        sender: String,
        body: String,
        keywords: List<String>
    ): Boolean = withContext(Dispatchers.IO) {
        val url = secureStorage.webhookUrl
        if (url.isBlank()) return@withContext false

        val json = JSONObject().apply {
            put("sender", sender)
            put("message", body)
            put("matched_keywords", keywords.joinToString(", "))
            put("timestamp", System.currentTimeMillis())
            put("app", "PayForward")
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = json.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .addHeader("User-Agent", "PayForward/1.0")
            .build()

        try {
            val response = httpClient.newCall(request).execute()
            response.use { it.isSuccessful }
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun forwardViaTelegram(
        sender: String,
        body: String,
        keywords: List<String>
    ): Boolean = withContext(Dispatchers.IO) {
        val token = secureStorage.telegramBotToken
        val chatId = secureStorage.telegramChatId
        if (token.isBlank() || chatId.isBlank()) return@withContext false

        val message = buildString {
            appendLine("💰 *PayForward Alert*")
            appendLine()
            appendLine("📱 *From:* `$sender`")
            appendLine("📝 *Message:* $body")
            appendLine("🔑 *Keywords:* ${keywords.joinToString(", ")}")
            appendLine("⏰ *Time:* ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}")
        }

        val url = "https://api.telegram.org/bot$token/sendMessage"
        val json = JSONObject().apply {
            put("chat_id", chatId)
            put("text", message)
            put("parse_mode", "Markdown")
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = json.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            val response = httpClient.newCall(request).execute()
            response.use { it.isSuccessful }
        } catch (e: Exception) {
            false
        }
    }

    @Suppress("DEPRECATION")
    private fun forwardViaSms(sender: String, body: String): Boolean {
        val number = secureStorage.smsForwardingNumber
        if (number.isBlank()) return false

        return try {
            val smsManager = SmsManager.getDefault()
            val forwardText = "[PayForward] From: $sender\n$body"
            val parts = smsManager.divideMessage(forwardText)
            smsManager.sendMultipartTextMessage(number, null, parts, null, null)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun getForwardingDestination(): String {
        return when (secureStorage.forwardingMethod) {
            ForwardingMethod.WEBHOOK -> secureStorage.webhookUrl
            ForwardingMethod.TELEGRAM -> "Telegram (${secureStorage.telegramChatId})"
            ForwardingMethod.SMS -> secureStorage.smsForwardingNumber
        }
    }

    suspend fun sendTestMessage(): Boolean {
        val testSender = "PayForward-Test"
        val testBody = "This is a test message from PayForward. Keywords: UPI, credited."

        return try {
            when (secureStorage.forwardingMethod) {
                ForwardingMethod.WEBHOOK -> forwardViaWebhook(testSender, testBody, listOf("test"))
                ForwardingMethod.TELEGRAM -> forwardViaTelegram(testSender, testBody, listOf("test"))
                ForwardingMethod.SMS -> forwardViaSms(testSender, testBody)
            }
        } catch (e: Exception) {
            false
        }
    }
}
