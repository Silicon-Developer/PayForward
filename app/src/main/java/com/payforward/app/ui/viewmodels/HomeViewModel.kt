package com.payforward.app.ui.viewmodels

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.payforward.app.data.AppDatabase
import com.payforward.app.data.ForwardStatus
import com.payforward.app.data.MessageLog
import com.payforward.app.data.MessageSource
import com.payforward.app.service.ForwardingManager
import com.payforward.app.service.KeywordEngine
import com.payforward.app.service.SecureStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val storage = SecureStorage(application)

    private val _isActive = MutableStateFlow(storage.isServiceActive)
    val isActive: StateFlow<Boolean> = _isActive

    val recentLogs: StateFlow<List<MessageLog>> = db.messageLogDao().getAll()
        .map { it.take(3) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeKeywords: StateFlow<List<com.payforward.app.data.Keyword>> = db.keywordDao().getEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _scannedToday = MutableStateFlow(0)
    val scannedToday: StateFlow<Int> = _scannedToday

    private val _forwardedToday = MutableStateFlow(0)
    val forwardedToday: StateFlow<Int> = _forwardedToday

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            val today = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
            _scannedToday.value = db.messageLogDao().getCountSince(today)
            _forwardedToday.value = db.messageLogDao().getSuccessCountSince(today)
        }
    }

    fun toggleService() {
        _isActive.value = !_isActive.value
        storage.isServiceActive = _isActive.value
    }

    fun simulatePayment(context: Context) {
        viewModelScope.launch {
            val testBody = "You've received Rs.500.00 via UPI from SILICON DEV (Ref: PAY2025TEST). Balance: Rs.15,230.00"
            val testSender = "SIM-PAYMENT"

            // Log to database
            val log = MessageLog(
                sender = testSender,
                body = testBody,
                timestamp = System.currentTimeMillis(),
                matchedKeyword = "received, UPI",
                status = ForwardStatus.PENDING,
                source = MessageSource.SMS,
                forwardedTo = storage.forwardingMethod.name
            )
            db.messageLogDao().insert(log)

            // Try forwarding if configured
            if (storage.isForwardingConfigured()) {
                val manager = ForwardingManager(context)
                val success = manager.sendTestMessage()
                val updatedLog = log.copy(
                    status = if (success) ForwardStatus.SUCCESS else ForwardStatus.FAILED
                )
                db.messageLogDao().insert(updatedLog)
            }

            loadStats()
            Toast.makeText(context, "Test payment simulated!", Toast.LENGTH_SHORT).show()
        }
    }
}
