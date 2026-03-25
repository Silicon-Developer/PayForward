package com.payforward.app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.payforward.app.data.AppDatabase
import com.payforward.app.data.ForwardStatus
import com.payforward.app.data.MessageLog
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LogsViewModel(application: Application) : AndroidViewModel(application) {

    private val messageLogDao = AppDatabase.getDatabase(application).messageLogDao()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _statusFilter = MutableStateFlow<ForwardStatus?>(null)
    val statusFilter: StateFlow<ForwardStatus?> = _statusFilter.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val logs: StateFlow<List<MessageLog>> = combine(_searchQuery, _statusFilter) { query, status ->
        Pair(query, status)
    }.flatMapLatest { (query, status) ->
        if (query.isBlank() && status == null) {
            messageLogDao.getAll()
        } else {
            messageLogDao.searchWithFilter(
                query = query.ifBlank { null },
                statusFilter = status
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStatusFilter(status: ForwardStatus?) {
        _statusFilter.value = status
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            messageLogDao.deleteAll()
        }
    }
}
