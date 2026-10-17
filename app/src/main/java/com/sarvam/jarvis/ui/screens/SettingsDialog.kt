package com.sarvam.jarvis.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarvam.jarvis.ui.theme.*

@Composable
fun SettingsDialog(
    currentApiKey: String,
    currentLanguage: String,
    onDismiss: () -> Unit,
    onSave: (apiKey: String, language: String) -> Unit
) {
    var apiKey by remember { mutableStateOf(currentApiKey) }
    var selectedLanguage by remember { mutableStateOf(currentLanguage) }

    val languages = listOf(
        "ta-IN" to "Tamil (தமிழ்)",
        "en-IN" to "Indian English",
        "hi-IN" to "Hindi (हिन्दी)"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Jarvis AI Settings", color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Configure your Sarvam AI API subscription key for cloud voice & reasoning features.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text("Sarvam API Subscription Key") },
                    placeholder = { Text("Paste your API key here") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text(
                    text = "Voice Response Language",
                    fontSize = 13.sp,
                    color = TextPrimary
                )

                languages.forEach { (code, label) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedLanguage == code,
                            onClick = { selectedLanguage = code },
                            colors = RadioButtonDefaults.colors(selectedColor = CyanGlow)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(label, color = TextPrimary, fontSize = 13.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(apiKey.trim(), selectedLanguage)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanGlow)
            ) {
                Text("Save Settings", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = DarkSurface
    )
}
