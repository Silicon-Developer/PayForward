package com.payforward.app

import android.app.Application
import com.payforward.app.data.AppDatabase
import com.payforward.app.service.KeywordEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PayForwardApp : Application() {

    val database by lazy { AppDatabase.getInstance(this) }
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Seed default keywords on first launch
        applicationScope.launch {
            KeywordEngine(this@PayForwardApp).initializeDefaults()
        }
    }

    companion object {
        lateinit var instance: PayForwardApp
            private set
    }
}
