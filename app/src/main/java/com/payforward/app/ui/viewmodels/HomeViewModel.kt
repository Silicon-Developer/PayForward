package com.payforward.app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.payforward.app.data.AppDatabase
import com.payforward.app.service.SecureStorage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val secureStorage = SecureStorage(application)
    private val messageLogDao = AppDatabase.getDatabase(application).messageLogDao()

    private val _isServiceActive = MutableStateFlow(secureStorage.isServiceActive)
    val isServiceActive: StateFlow<Boolean> = _isServiceActive.asStateFlow()

    private val startOfDay: Long
        get() {
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            return cal.timeInMillis
        }

    val scannedToday: StateFlow<Int> = messageLogDao.getScannedTodayCount(startOfDay)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val forwardedToday: StateFlow<Int> = messageLogDao.getForwardedTodayCount(startOfDay)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun toggleService() {
        val newState = !_isServiceActive.value
        _isServiceActive.value = newState
        secureStorage.isServiceActive = newState
    }

    fun setServiceActive(active: Boolean) {
        _isServiceActive.value = active
        secureStorage.isServiceActive = active
    }
}
