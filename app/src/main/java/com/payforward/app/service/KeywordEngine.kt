package com.payforward.app.service

import com.payforward.app.data.AppDatabase
import com.payforward.app.data.Keyword
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class KeywordEngine(context: Context) {

    private val keywordDao = AppDatabase.getDatabase(context).keywordDao()

    companion object {
        val DEFAULT_KEYWORDS = listOf(
            "received", "credited", "UPI", "PhonePe", "Paytm",
            "GPay", "rupees", "INR", "debited", "transferred",
            "NEFT", "IMPS", "payment", "transaction"
        )
    }

    suspend fun initializeDefaults() {
        withContext(Dispatchers.IO) {
            val count = keywordDao.getCount()
            if (count == 0) {
                val defaults = DEFAULT_KEYWORDS.map { word ->
                    Keyword(word = word, isDefault = true, isEnabled = true)
                }
                keywordDao.insertAll(defaults)
            }
        }
    }

    suspend fun matches(text: String): List<String> {
        return withContext(Dispatchers.IO) {
            val enabledKeywords = keywordDao.getEnabledList()
            val lowerText = text.lowercase()
            enabledKeywords
                .filter { keyword -> lowerText.contains(keyword.word.lowercase()) }
                .map { it.word }
        }
    }

    suspend fun addKeyword(word: String): Boolean {
        return withContext(Dispatchers.IO) {
            val id = keywordDao.insert(Keyword(word = word.trim(), isDefault = false, isEnabled = true))
            id != -1L
        }
    }

    suspend fun removeKeyword(keyword: Keyword) {
        withContext(Dispatchers.IO) {
            keywordDao.delete(keyword)
        }
    }

    suspend fun toggleKeyword(keyword: Keyword) {
        withContext(Dispatchers.IO) {
            keywordDao.update(keyword.copy(isEnabled = !keyword.isEnabled))
        }
    }
}
