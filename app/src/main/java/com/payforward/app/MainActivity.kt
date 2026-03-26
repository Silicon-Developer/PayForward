package com.payforward.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
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
                    // Permission gate - show overlay until granted
                    PermissionGate {
                        PayForwardApp(
                            onExportCsv = { exportLogsCsv() }
                        )
                    }
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

@Composable
fun PermissionGate(content: @Composable () -> Unit) {
    val context = LocalContext.current

    // Build required permissions list
    val requiredPermissions = buildList {
        add(Manifest.permission.RECEIVE_SMS)
        add(Manifest.permission.READ_SMS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Check if all permissions are already granted
    var allGranted by remember {
        mutableStateOf(
            requiredPermissions.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        allGranted = results.values.all { it }
    }

    // Request on first composition if not granted
    LaunchedEffect(Unit) {
        if (!allGranted) {
            permissionLauncher.launch(requiredPermissions.toTypedArray())
        }
    }

    if (allGranted) {
        content()
    } else {
        // Dark overlay waiting for permissions
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Security,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color(0xFF00E676)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Waiting for Permissions...",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "PayForward Pro requires SMS and notification access to intercept and forward financial messages.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        permissionLauncher.launch(requiredPermissions.toTypedArray())
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00E676),
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        text = "Grant Permissions",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
