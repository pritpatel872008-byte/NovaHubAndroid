package com.novahub.android

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
    NaN }
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
                listOf("Home" to Icons.Default.Home, "SOS" to Icons.Default.Sos, "Wallet" to Icons.Default.AccountBalanceWallet, "Security" to Icons.Default.Security, "More" to Icons.Default.Menu).forEach { (name, icon) ->
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
                                    if (profileName.isNotBlank()) { Spacer(Modifier.height(8.dp)); Text("Welcome, $profileName", color = Color.White, fontSize = 13.sp) }
                                    Spacer(Modifier.height(8.dp)); Text("Swipe up from this panel to explore more tools.", color = Color(0xFFCBD8F2), fontSize = 11.sp)
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
                    "SOS" -> {
                        item { SectionHeading("Emergency SOS", "Choose the recipient and review the message before sending.") }
                        item {
                            Card(shape = RoundedCornerShape(22.dp)) {
                                Column(Modifier.padding(18.dp)) {
                                    Text("Emergency contact", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                    Spacer(Modifier.height(8.dp))
                                    OutlinedTextField(value = emergencyNumber, onValueChange = { emergencyNumber = it }, label = { Text("Phone number") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth(), singleLine = true)
                                    Spacer(Modifier.height(8.dp))
                                    OutlinedTextField(value = emergencyMessage, onValueChange = { emergencyMessage = it }, label = { Text("Message") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                                    Spacer(Modifier.height(12.dp))
                                    Button(onClick = {
                                        if (emergencyNumber.isBlank()) message = "Enter an emergency contact number first."
                                        else try {
                                            val uri = Uri.parse("smsto:" + Uri.encode(emergencyNumber))
                                            val intent = Intent(Intent.ACTION_SENDTO, uri).putExtra("sms_body", emergencyMessage)
                                            activity.startActivity(intent)
                                            message = "Messaging app opened. Review and send the SOS yourself."
                                        } catch (_: Exception) { message = "No compatible messaging app was found." }
                                    }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Sos, null); Spacer(Modifier.width(8.dp)); Text("Open SOS message") }
                                    OutlinedButton(onClick = { openSetting(activity, Settings.ACTION_LOCATION_SOURCE_SETTINGS) { message = it } }, modifier = Modifier.fillMaxWidth()) { Text("Check location settings") }
                                    Text("This opens your messaging app; it does not silently send SMS or contact emergency services. Location is not attached automatically.", color = Subtle, fontSize = 11.sp, lineHeight = 16.sp)
                                }
                            }
                        }
                    }
                    "Wallet" -> {
                        item { SectionHeading("Nova Wallet", "Payment hand-off and document selection.") }
                        item {
                            Card(shape = RoundedCornerShape(22.dp)) {
                                Column(Modifier.padding(18.dp)) {
                                    Text("UPI payment", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                    Text("Nova Hub can hand a payment request to an installed UPI app. Check recipient and amount in that app before approving.", color = Subtle, fontSize = 12.sp, lineHeight = 18.sp)
                                    Spacer(Modifier.height(12.dp))
                                    OutlinedTextField(value = payeeName, onValueChange = { payeeName = it }, label = { Text("Payee name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                                    Spacer(Modifier.height(8.dp))
                                    OutlinedTextField(value = upiId, onValueChange = { upiId = it }, label = { Text("Payee UPI ID") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                                    Spacer(Modifier.height(8.dp))
                                    OutlinedTextField(value = amount, onValueChange = { amount = it.filter { ch -> ch.isDigit() || ch == '.' }.take(10) }, label = { Text("Amount (INR)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth(), singleLine = true)
                                    Spacer(Modifier.height(12.dp))
                                    Button(onClick = {
                                        val parsed = amount.toBigDecimalOrNull()
                                        if (!upiId.matches(Regex("[A-Za-z0-9._-]{2,256}@[A-Za-z]{2,64}")) || parsed == null || parsed <= java.math.BigDecimal.ZERO) {
                                            message = "Enter a valid UPI ID and a positive amount."
                                        } else try {
                                            val uri = Uri.Builder().scheme("upi").authority("pay").appendQueryParameter("pa", upiId.trim()).appendQueryParameter("pn", payeeName.ifBlank { upiId.trim() }).appendQueryParameter("am", parsed.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString()).appendQueryParameter("cu", "INR").build()
                                            activity.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                            message = "UPI app opened. Verify recipient and amount there; Nova Hub does not process or confirm payments."
                                        } catch (_: Exception) { message = "No UPI app could handle this request. Install a supported UPI app first." }
                                    }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.QrCode, null); Spacer(Modifier.width(8.dp)); Text("Continue to UPI app") }
                                }
                            }
                        }
                        item {
                            Card(shape = RoundedCornerShape(22.dp)) {
                                Column(Modifier.padding(18.dp)) {
                                    Text("PAN / Aadhaar document picker", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                    Spacer(Modifier.height(6.dp))
                                    Text("Choose a document to locate it. This initial picker does not copy it into an encrypted Nova Hub vault, so do not treat this as secure document storage yet.", color = Subtle, fontSize = 12.sp, lineHeight = 18.sp)
                                    if (selectedDocumentName.isNotBlank()) { Spacer(Modifier.height(8.dp)); Text("Selected: $selectedDocumentName", color = TextDark, fontSize = 12.sp) }
                                    Spacer(Modifier.height(12.dp))
                                    OutlinedButton(onClick = { documentPicker.launch(arrayOf("application/pdf", "image/*")) }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.FolderOpen, null); Spacer(Modifier.width(8.dp)); Text("Choose document") }
                                }
                            }
                        }
                    }
                    "More" -> {
                        item { SectionHeading("More controls", "Open supported Android system controls.") }
                        item {
                            MetricCard(Icons.Default.Lock, "App lock", "Android system lock", "Nova Hub cannot universally lock other apps without special device-owner or accessibility setup.")
                        }
                        item {
                            Card(shape = RoundedCornerShape(22.dp)) {
                                Column(Modifier.padding(18.dp)) {
                                    Text("App-lock options", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                    Text("Use your phone's built-in App Lock if your manufacturer provides it. Nova Hub cannot enable another manufacturer's feature automatically.", color = Subtle, fontSize = 12.sp, lineHeight = 18.sp)
                                    Spacer(Modifier.height(12.dp))
                                    Button(onClick = { openSetting(activity, Settings.ACTION_SECURITY_SETTINGS) { message = it } }, modifier = Modifier.fillMaxWidth()) { Text("Open security settings") }
                                }
                            }
                        }
                        item {
                            Card(shape = RoundedCornerShape(22.dp)) {
                                Column(Modifier.padding(18.dp)) {
                                    Text("Parental controls", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                    Text("Android's Family Link / Digital Wellbeing controls require setup by a parent or device administrator. This button opens system settings where available.", color = Subtle, fontSize = 12.sp, lineHeight = 18.sp)
                                    Spacer(Modifier.height(12.dp))
                                    Button(onClick = { openSetting(activity, "android.settings.DIGITAL_WELLBEING_SETTINGS") { message = it } }, modifier = Modifier.fillMaxWidth()) { Text("Open Digital Wellbeing") }
                                    OutlinedButton(onClick = { openSetting(activity, Settings.ACTION_SETTINGS) { message = it } }, modifier = Modifier.fillMaxWidth()) { Text("Open all Android settings") }
                                }
                            }
                        }
                        item {
                            Card(shape = RoundedCornerShape(22.dp)) {
                                Column(Modifier.padding(18.dp)) {
                                    Text("Local profile", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                    Text("This profile name is stored on this device only; it is not a cloud account and cannot be recovered on another phone.", color = Subtle, fontSize = 12.sp, lineHeight = 18.sp)
                                    Spacer(Modifier.height(8.dp))
                                    OutlinedTextField(value = profileDraft, onValueChange = { profileDraft = it.take(50) }, label = { Text("Profile name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                                    Spacer(Modifier.height(10.dp))
                                    Button(onClick = {
                                        if (profileDraft.isBlank()) message = "Enter a profile name."
                                        else {
                                            profileName = profileDraft.trim()
                                            activity.getSharedPreferences("nova_profile", 0).edit().putString("name", profileName).apply()
                                            message = "Saved local profile: $profileName. This is not an online account or security certificate."
                                        }
                                    }, modifier = Modifier.fillMaxWidth()) { Text("Save local profile") }
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
