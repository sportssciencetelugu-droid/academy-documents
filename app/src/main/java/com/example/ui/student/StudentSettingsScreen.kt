package com.example.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserAccountEntity
import com.example.ui.common.BromaPrimaryButton
import com.example.ui.common.BromaSecondaryButton
import com.example.ui.theme.*

@Composable
fun StudentSettingsScreen(
    student: UserAccountEntity,
    onLogout: () -> Unit
) {
    val scrollState = rememberScrollState()

    var pushEnabled by remember { mutableStateOf(true) }
    var emailAlertsEnabled by remember { mutableStateOf(true) }

    var currentPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }
    var passChangeMsg by remember { mutableStateOf<String?>(null) }
    var passErrorMsg by remember { mutableStateOf<String?>(null) }

    var showPrivacyModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "SETTINGS & PRIVACY",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
            color = RoyalBlue
        )

        // PASSWORD CHANGE CARD
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = CardWhite,
            shadowElevation = 1.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = RoyalBlue)
                    Text("ACCOUNT SECURITY & PASSWORD", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                }

                HorizontalDivider(color = BorderLight)

                OutlinedTextField(
                    value = currentPass,
                    onValueChange = { currentPass = it },
                    label = { Text("Current Password", color = TextSlate) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoyalBlue,
                        unfocusedBorderColor = BorderLight,
                        focusedTextColor = TextNavy,
                        unfocusedTextColor = TextNavy
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = newPass,
                    onValueChange = { newPass = it },
                    label = { Text("New Password", color = TextSlate) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoyalBlue,
                        unfocusedBorderColor = BorderLight,
                        focusedTextColor = TextNavy,
                        unfocusedTextColor = TextNavy
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = confirmPass,
                    onValueChange = { confirmPass = it },
                    label = { Text("Confirm New Password", color = TextSlate) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoyalBlue,
                        unfocusedBorderColor = BorderLight,
                        focusedTextColor = TextNavy,
                        unfocusedTextColor = TextNavy
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (passErrorMsg != null) {
                    Text(passErrorMsg!!, color = StatusError, style = MaterialTheme.typography.bodySmall)
                }

                if (passChangeMsg != null) {
                    Text(passChangeMsg!!, color = StatusSuccess, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }

                BromaPrimaryButton(
                    text = "Update Password",
                    onClick = {
                        if (currentPass.isBlank() || newPass.isBlank()) {
                            passErrorMsg = "Please fill all password fields"
                        } else if (newPass != confirmPass) {
                            passErrorMsg = "New passwords do not match"
                        } else {
                            passErrorMsg = null
                            passChangeMsg = "Password updated successfully!"
                            currentPass = ""
                            newPass = ""
                            confirmPass = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // NOTIFICATIONS CARD
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = CardWhite,
            shadowElevation = 1.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = RoyalBlue)
                    Text("NOTIFICATION PREFERENCES", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                }

                HorizontalDivider(color = BorderLight)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Push Notifications for Announcements", color = TextNavy, style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = pushEnabled,
                        onCheckedChange = { pushEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = RoyalBlue)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Fee Payment Alerts & Email Receipts", color = TextNavy, style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = emailAlertsEnabled,
                        onCheckedChange = { emailAlertsEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = RoyalBlue)
                    )
                }
            }
        }

        // PRIVACY POLICY & TOS
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = CardWhite,
            shadowElevation = 1.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.PrivacyTip, contentDescription = null, tint = RoyalBlue)
                    Text("PRIVACY POLICY & TERMS", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                }

                HorizontalDivider(color = BorderLight)

                Text(
                    text = "BROMA ACADEMY respects student privacy and data security. All student training records, attendance, and fee history are protected under strict encryption guidelines.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSlate
                )

                BromaSecondaryButton(
                    text = "Read Full Privacy Policy",
                    onClick = { showPrivacyModal = true },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("student_logout_button"),
            colors = ButtonDefaults.buttonColors(containerColor = StatusError),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("LOGOUT FROM PORTAL", fontWeight = FontWeight.Bold, color = TextOnAccent)
        }
    }

    if (showPrivacyModal) {
        AlertDialog(
            onDismissRequest = { showPrivacyModal = false },
            title = { Text("BROMA ACADEMY PRIVACY POLICY", fontWeight = FontWeight.Bold, color = TextNavy) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("1. Data Protection", fontWeight = FontWeight.Bold, color = RoyalBlue)
                    Text("BROMA Academy collects student profile, attendance, and fee information solely for dojo management, belt progress tracking, and tournament registrations.", color = TextSlate, fontSize = 12.sp)

                    Text("2. Parent & Guardian Consent", fontWeight = FontWeight.Bold, color = RoyalBlue)
                    Text("Guardian contact information is securely stored for emergency notifications and payment receipts.", color = TextSlate, fontSize = 12.sp)

                    Text("3. Official Academy Integrity", fontWeight = FontWeight.Bold, color = RoyalBlue)
                    Text("All certificates, belt promotions, and official seals issued by BROMA Academy remain protected property.", color = TextSlate, fontSize = 12.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyModal = false }) {
                    Text("I AGREE", fontWeight = FontWeight.Bold, color = RoyalBlue)
                }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
