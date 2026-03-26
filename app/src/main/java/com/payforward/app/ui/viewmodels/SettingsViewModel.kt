package com.payforward.app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.payforward.app.service.ForwardingManager
import com.payforward.app.service.ForwardingMethod
import com.payforward.app.service.SecureStorage
import com.payforward.app.service.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class TestResult { SUCCESS, FAILURE }

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val storage = SecureStorage(application)

    private val _forwardingMethod = MutableStateFlow(storage.forwardingMethod)
    val forwardingMethod: StateFlow<ForwardingMethod> = _forwardingMethod

    private val _webhookUrl = MutableStateFlow(storage.webhookUrl)
    val webhookUrl: StateFlow<String> = _webhookUrl

    private val _telegramToken = MutableStateFlow(storage.telegramBotToken)
    val telegramToken: StateFlow<String> = _telegramToken

    private val _telegramChatId = MutableStateFlow(storage.telegramChatId)
    val telegramChatId: StateFlow<String> = _telegramChatId

    private val _smsNumber = MutableStateFlow(storage.smsForwardingNumber)
    val smsNumber: StateFlow<String> = _smsNumber

    private val _isTesting = MutableStateFlow(false)
    val isTesting: StateFlow<Boolean> = _isTesting

    private val _testResult = MutableStateFlow<TestResult?>(null)
    val testResult: StateFlow<TestResult?> = _testResult

    private val _testErrorMessage = MutableStateFlow<String?>(null)
    val testErrorMessage: StateFlow<String?> = _testErrorMessage

    private val _themeMode = MutableStateFlow(storage.themeMode)
    val themeMode: StateFlow<ThemeMode> = _themeMode

    private val _isAntiSpoofingEnabled = MutableStateFlow(storage.isAntiSpoofingEnabled)
    val isAntiSpoofingEnabled: StateFlow<Boolean> = _isAntiSpoofingEnabled

    private val _trustedSenderIds = MutableStateFlow(storage.trustedSenderIds)
    val trustedSenderIds: StateFlow<Set<String>> = _trustedSenderIds

    fun setForwardingMethod(method: ForwardingMethod) {
        _forwardingMethod.value = method
        storage.forwardingMethod = method
    }

    fun setWebhookUrl(url: String) {
        _webhookUrl.value = url
        storage.webhookUrl = url
    }

    fun setTelegramToken(token: String) {
        _telegramToken.value = token
        storage.telegramBotToken = token
    }

    fun setTelegramChatId(chatId: String) {
        _telegramChatId.value = chatId
        storage.telegramChatId = chatId
    }

    fun setSmsNumber(number: String) {
        _smsNumber.value = number
        storage.smsForwardingNumber = number
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        storage.themeMode = mode
    }

    fun setAntiSpoofingEnabled(enabled: Boolean) {
        _isAntiSpoofingEnabled.value = enabled
        storage.isAntiSpoofingEnabled = enabled
    }

    fun addTrustedSender(sender: String) {
        val updated = _trustedSenderIds.value + sender.uppercase()
        _trustedSenderIds.value = updated
        storage.trustedSenderIds = updated
    }

    fun removeTrustedSender(sender: String) {
        val updated = _trustedSenderIds.value - sender
        _trustedSenderIds.value = updated
        storage.trustedSenderIds = updated
    }

    fun sendTestMessage() {
        viewModelScope.launch {
            _isTesting.value = true
            _testResult.value = null
            _testErrorMessage.value = null

            try {
                val success = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    val manager = ForwardingManager(getApplication())
                    manager.sendTestMessage()
                }
                _testResult.value = if (success) TestResult.SUCCESS else TestResult.FAILURE
                if (!success) {
                    _testErrorMessage.value = "Payload delivery returned a non-success status."
                }
            } catch (e: Throwable) {
                _testResult.value = TestResult.FAILURE
                _testErrorMessage.value = e.localizedMessage ?: "Unknown runtime error"
            }

            _isTesting.value = false
        }
    }
}
