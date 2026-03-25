package com.payforward.app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.payforward.app.service.ForwardingManager
import com.payforward.app.service.ForwardingMethod
import com.payforward.app.service.SecureStorage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val secureStorage = SecureStorage(application)
    private val forwardingManager = ForwardingManager(application)

    private val _forwardingMethod = MutableStateFlow(secureStorage.forwardingMethod)
    val forwardingMethod: StateFlow<ForwardingMethod> = _forwardingMethod.asStateFlow()

    private val _webhookUrl = MutableStateFlow(secureStorage.webhookUrl)
    val webhookUrl: StateFlow<String> = _webhookUrl.asStateFlow()

    private val _telegramToken = MutableStateFlow(secureStorage.telegramBotToken)
    val telegramToken: StateFlow<String> = _telegramToken.asStateFlow()

    private val _telegramChatId = MutableStateFlow(secureStorage.telegramChatId)
    val telegramChatId: StateFlow<String> = _telegramChatId.asStateFlow()

    private val _smsNumber = MutableStateFlow(secureStorage.smsForwardingNumber)
    val smsNumber: StateFlow<String> = _smsNumber.asStateFlow()

    private val _testResult = MutableStateFlow<TestResult?>(null)
    val testResult: StateFlow<TestResult?> = _testResult.asStateFlow()

    private val _isTesting = MutableStateFlow(false)
    val isTesting: StateFlow<Boolean> = _isTesting.asStateFlow()

    fun setForwardingMethod(method: ForwardingMethod) {
        _forwardingMethod.value = method
        secureStorage.forwardingMethod = method
    }

    fun setWebhookUrl(url: String) {
        _webhookUrl.value = url
        secureStorage.webhookUrl = url
    }

    fun setTelegramToken(token: String) {
        _telegramToken.value = token
        secureStorage.telegramBotToken = token
    }

    fun setTelegramChatId(chatId: String) {
        _telegramChatId.value = chatId
        secureStorage.telegramChatId = chatId
    }

    fun setSmsNumber(number: String) {
        _smsNumber.value = number
        secureStorage.smsForwardingNumber = number
    }

    fun sendTestMessage() {
        viewModelScope.launch {
            _isTesting.value = true
            _testResult.value = null
            try {
                val success = forwardingManager.sendTestMessage()
                _testResult.value = if (success) TestResult.SUCCESS else TestResult.FAILED
            } catch (e: Exception) {
                _testResult.value = TestResult.FAILED
            } finally {
                _isTesting.value = false
            }
        }
    }

    fun clearTestResult() {
        _testResult.value = null
    }
}

enum class TestResult {
    SUCCESS,
    FAILED
}
