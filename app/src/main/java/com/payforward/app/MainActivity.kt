package com.payforward.app

import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.payforward.app.data.AppDatabase
import com.payforward.app.service.SecureStorage
import com.payforward.app.service.ThemeMode
import com.payforward.app.ui.PayForwardApp
import com.payforward.app.ui.screens.TermsScreen
import com.payforward.app.ui.theme.PayForwardTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val storage = SecureStorage(this)

        setContent {
            var termsAccepted by remember { mutableStateOf(storage.termsAccepted) }
            var themeMode by remember { mutableStateOf(storage.themeMode) }

            // Listen for theme changes from settings
            LaunchedEffect(Unit) {
                kotlinx.coroutines.flow.flow {
                    while (true) {
                        emit(storage.themeMode)
                        kotlinx.coroutines.delay(500)
                    }
                }.collect { mode ->
                    themeMode = mode
                }
            }

            PayForwardTheme(themeMode = themeMode) {
                if (!termsAccepted) {
                    TermsScreen(
                        onAccept = {
                            storage.termsAccepted = true
                            termsAccepted = true
                        }
                    )
                } else {
                    PayForwardApp(
                        onExportCsv = { exportLogsCsv() }
                    )
                }
            }
        }
    }

    private fun exportLogsCsv() {
        val db = AppDatabase.getDatabase(this)
        val context = this

        kotlinx.coroutines.MainScope().launch(Dispatchers.IO) {
            try {
                val logs = db.messageLogDao().getAllLogsSync()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault())
                val fileName = "PayForward_Logs_${dateFormat.format(Date())}.csv"

                val downloadsDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                )
                val file = File(downloadsDir, fileName)

                file.bufferedWriter().use { writer ->
                    writer.write("ID,Sender,Body,Timestamp,Status,Source,Matched Keyword,Forwarded To")
                    writer.newLine()
                    for (log in logs) {
                        val ts = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .format(Date(log.timestamp))
                        val escapedBody = "\"${log.body.replace("\"", "\"\"")}\""
                        writer.write("${log.id},${log.sender},$escapedBody,$ts,${log.status},${log.source},${log.matchedKeyword},${log.forwardedTo}")
                        writer.newLine()
                    }
                }

                launch(Dispatchers.Main) {
                    Toast.makeText(context, "Exported to Downloads/$fileName", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
