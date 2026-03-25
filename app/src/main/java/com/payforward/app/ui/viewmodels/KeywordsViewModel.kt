package com.payforward.app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.payforward.app.data.AppDatabase
import com.payforward.app.data.Keyword
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class KeywordsViewModel(application: Application) : AndroidViewModel(application) {

    private val keywordDao = AppDatabase.getDatabase(application).keywordDao()

    val keywords: StateFlow<List<Keyword>> = keywordDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    fun showAddKeywordDialog() {
        _showAddDialog.value = true
    }

    fun dismissAddKeywordDialog() {
        _showAddDialog.value = false
    }

    fun addKeyword(word: String) {
        if (word.isBlank()) return
        viewModelScope.launch {
            keywordDao.insert(Keyword(word = word.trim(), isDefault = false, isEnabled = true))
            _showAddDialog.value = false
        }
    }

    fun deleteKeyword(keyword: Keyword) {
        viewModelScope.launch {
            keywordDao.delete(keyword)
        }
    }

    fun toggleKeyword(keyword: Keyword) {
        viewModelScope.launch {
            keywordDao.update(keyword.copy(isEnabled = !keyword.isEnabled))
        }
    }
}
