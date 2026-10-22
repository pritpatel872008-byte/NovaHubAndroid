package com.novahub.android

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

private val Navy = Color(0xFF101A2C)
private val Blue = Color(0xFF4778F5)
private val Canvas = Color(0xFFF3F6FB)
private val TextDark = Color(0xFF18243A)
private val Subtle = Color(0xFF77839A)

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { NovaHubScreen(this) }
    }
}

@Composable
private fun NovaHubScreen(activity: FragmentActivity) {
    var page by remember { mutableStateOf("Home") }
    var message by remember { mutableStateOf("") }
    val model = listOf(Build.MANUFACTURER, Build.MODEL).filter { it.isNotBlank() }.distinct().joinToString(" ").ifBlank { "Android device" }
    val patch = if (Build.VERSION.SDK_INT >= 23) Build.VERSION.SECURITY_PATCH ?: "Not reported" else "Not available"
    val bio = when (BiometricManager.from(activity).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
        BiometricManager.BIOMETRIC_SUCCESS -> "Available on this device"
        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> "Set up in Android Settings"
        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> "No biometric hardware detected"
        else -> "Check device security settings"
    }
    MaterialTheme(colorScheme = lightColorScheme(primary = Blue, background = Canvas, surface = Color.White)) {
        Scaffold(containerColor = Canvas, bottomBar = {
            NavigationBar(containerColor = Color.White) {
                listOf("Home" to Icons.Default.Home, "Device" to Icons.Default.PhoneAndroid, "Security" to Icons.Default.Security, "Updates" to Icons.Default.SystemUpdate).forEach { (name, icon) ->
                    NavigationBarItem(selected = page == name, onClick = { page = name; message = "" }, icon = { Icon(icon, null) }, label = { Text(name) })
                }
            }
        }) { padding ->
            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(14.dp), contentPadding = PaddingValues(top = 18.dp, bottom = 20.dp)) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Surface(shape = RoundedCornerShape(15.dp), color = Navy, modifier = Modifier.size(46.dp)) {
                            Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.AutoAwesome, null, tint = Color(0xFF9FC1FF), modifier = Modifier.size(27.dp)) }
                        }
                        Column(Modifier.padding(start = 12.dp).weight(1f)) {
                            Text("Nova Hub", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            Text("YOUR DEVICE. YOUR CONTROL.", fontSize = 9.sp, letterSpacing = 1.4.sp, color = Subtle)
                        }
                        Icon(Icons.Default.Shield, null, tint = Blue, modifier = Modifier.size(26.dp))
                    }
                }
                if (message.isNotBlank()) item {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE7F0FF))) { Text(message, Modifier.padding(14.dp), color = Navy, fontSize = 13.sp) }
                }
                when (page) {
                    "Home" -> {
                        item {
                            Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Navy)) {
                                Column(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Color(0xFF111B30), Color(0xFF314E91), Color(0xFF5C64C8)))).padding(23.dp)) {
                                    Text("DEVICE SECURITY", color = Color(0xFFBBD2FF), fontSize = 10.sp, letterSpacing = 1.6.sp)
                                    Spacer(Modifier.height(12.dp))
                                    Text("Everything important.\nIn one place.", color = Color.White, fontSize = 28.sp, lineHeight = 34.sp, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.height(8.dp))
                                    Text("A calm, clear view of your Android device and its security options.", color = Color(0xFFE0E8FA), fontSize = 13.sp)
                                    Spacer(Modifier.height(18.dp))
                                    Button(onClick = { page = "Security" }, colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Navy)) { Text("Check security  →") }
                                }
                            }
                        }
                        item { Text("At a glance", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark) }
                        item { MetricCard(Icons.Default.PhoneAndroid, "Your device", model, "Detected from Android system") }
                        item { MetricCard(Icons.Default.Android, "Android version", "Android ${Build.VERSION.RELEASE ?: "Unknown"}", "SDK level ${Build.VERSION.SDK_INT}") }
                        item { MetricCard(Icons.Default.Security, "Security patch", patch, "Reported by Android") }
                        item {
                            Card(shape = RoundedCornerShape(22.dp)) {
                                Column(Modifier.padding(18.dp)) {
                                    Text("Privacy by design", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp)
                                    Spacer(Modifier.height(8.dp))
                                    Text("Nova Hub does not ask for your device PIN, IMEI, contacts, or fingerprint images. Device security remains controlled by Android.", color = Subtle, fontSize = 12.sp, lineHeight = 18.sp)
                                }
                            }
                        }
                    }
                    "Device" -> {
                        item { SectionHeading("Device information", "Read-only information reported by Android.") }
                        item { MetricCard(Icons.Default.Devices, "Manufacturer & model", model, "Brand: ${Build.BRAND}") }
                        item { MetricCard(Icons.Default.Android, "Android release", "Android ${Build.VERSION.RELEASE ?: "Unknown"}", "SDK ${Build.VERSION.SDK_INT}") }
                        item { MetricCard(Icons.Default.Security, "Security patch", patch, "System-reported patch level") }
                        item { MetricCard(Icons.Default.Info, "Build", Build.DISPLAY.ifBlank { "Not reported" }, "Read-only build identifier") }
                    }
                    "Security" -> {
                        item { SectionHeading("Security & privacy", "Use Android's secure system features.") }
                        item { MetricCard(Icons.Default.Fingerprint, "Biometric capability", bio, "Detected using AndroidX BiometricManager") }
                        item {
                            Card(shape = RoundedCornerShape(22.dp)) {
                                Column(Modifier.padding(18.dp)) {
                                    Text("Verify it's you", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                    Spacer(Modifier.height(6.dp))
                                    Text("Open the Android system authentication prompt. Nova Hub never sees your PIN or fingerprint data.", color = Subtle, fontSize = 12.sp, lineHeight = 18.sp)
                                    Spacer(Modifier.height(14.dp))
                                    Button(onClick = {
                                        val prompt = BiometricPrompt(activity, ContextCompat.getMainExecutor(activity), object : BiometricPrompt.AuthenticationCallback() {
                                            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) { message = "Android confirmed authentication for this session." }
                                            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) { message = "Authentication not completed: $errString" }
                                            override fun onAuthenticationFailed() { message = "Not recognized. Try again or cancel." }
                                        })
                                        val info = BiometricPrompt.PromptInfo.Builder().setTitle("Nova Hub").setSubtitle("Confirm your identity").setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL).build()
                                        try { prompt.authenticate(info) } catch (_: Exception) { message = "Authentication unavailable. Set up a screen lock or biometrics in Android Settings." }
                                    }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Fingerprint, null); Spacer(Modifier.width(8.dp)); Text("Authenticate securely") }
                                    OutlinedButton(onClick = { openSetting(activity, Settings.ACTION_SECURITY_SETTINGS) { message = it } }, modifier = Modifier.fillMaxWidth()) { Text("Open security settings") }
                                }
                            }
                        }
                    }
                    "Updates" -> {
                        item { SectionHeading("Updates", "Official updates depend on your device manufacturer and carrier.") }
                        item { MetricCard(Icons.Default.SystemUpdate, "Current security patch", patch, "Reported by Android") }
                        item {
                            Card(shape = RoundedCornerShape(22.dp)) {
                                Column(Modifier.padding(18.dp)) {
                                    Text("Check for official updates", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                    Spacer(Modifier.height(7.dp))
                                    Text("Nova Hub cannot install firmware or promise updates. Use your phone's built-in system updater to check eligibility.", color = Subtle, fontSize = 12.sp, lineHeight = 18.sp)
                                    Spacer(Modifier.height(14.dp))
                                    Button(onClick = { openSetting(activity, Settings.ACTION_SETTINGS) { message = it } }, modifier = Modifier.fillMaxWidth()) { Text("Open Android Settings") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeading(title: String, subtitle: String) {
    Column(Modifier.padding(vertical = 4.dp)) {
        Text(title, fontSize = 23.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Spacer(Modifier.height(5.dp))
        Text(subtitle, fontSize = 12.sp, color = Subtle)
    }
}

@Composable
private fun MetricCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String, subtitle: String) {
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFEAF0FF), modifier = Modifier.size(46.dp)) {
                Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = Blue) }
            }
            Column(Modifier.padding(start = 13.dp).weight(1f)) {
                Text(title, fontSize = 12.sp, color = Subtle)
                Spacer(Modifier.height(3.dp))
                Text(value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                Spacer(Modifier.height(3.dp))
                Text(subtitle, fontSize = 10.sp, color = Subtle)
            }
        }
    }
}

private fun openSetting(activity: FragmentActivity, action: String, onError: (String) -> Unit) {
    try { activity.startActivity(Intent(action)) } catch (_: Exception) { onError("This settings page is not available on this device.") }
}
