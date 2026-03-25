package com.payforward.app.service

import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object PayloadExtractor {

    /**
     * Extracts specific financial details from standard unstructured SMS/notification strings
     * to form a clean structured JSON format. 
     */
    fun extract(smsBody: String, originalSender: String): JSONObject {
        // Amount Regex: Rs., INR, or ₹ followed by digits/decimals
        val amountPattern = Regex("(?:Rs\\.?|INR|₹)\\s*([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE)
        val amountMatch = amountPattern.find(smsBody)
        val amount = amountMatch?.groupValues?.get(1)?.replace(",", "") ?: ""

        // Sender Name Regex: usually right after "from" or "by" up to a keyword or punctuation
        val senderPattern = Regex("(?:from|by)\\s+([A-Za-z\\s]+)(?:on|ref|\\.|-)", RegexOption.IGNORE_CASE)
        val senderMatch = senderPattern.find(smsBody)
        val extractedSender = senderMatch?.groupValues?.get(1)?.trim() ?: originalSender

        // Payment Mode Regex
        val modePattern = Regex("(UPI|PhonePe|Paytm|GPay|NEFT|IMPS|RTGS|Cred)", RegexOption.IGNORE_CASE)
        val modeMatch = modePattern.find(smsBody)
        val paymentMode = modeMatch?.groupValues?.get(1) ?: "Unknown"

        // UTC ISO Timestamp
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        isoFormat.timeZone = TimeZone.getTimeZone("UTC")
        val timestamp = isoFormat.format(Date())

        return JSONObject().apply {
            put("amount", amount)
            put("sender", extractedSender)
            put("payment_mode", paymentMode)
            put("timestamp", timestamp)
            put("raw_message", smsBody)
        }
    }
}
