package com.example.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AdminSettingsEntity
import com.example.data.UserAccountEntity
import com.example.ui.theme.*

@Composable
fun StudentContactScreen(
    student: UserAccountEntity,
    adminSettings: AdminSettingsEntity?,
    onSendMessageToAdmin: (String) -> Unit = {},
    onNavigateToChat: () -> Unit = {}
) {
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var sentSuccess by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text("CONTACT BROMA ACADEMY", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = TextNavy)
            Text("Reach out to your Sensei or Academy Office", style = MaterialTheme.typography.bodySmall, color = TextSlate)
        }

        // Assigned Coach Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = CardWhite,
            shadowElevation = 1.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("ASSIGNED CHIEF COACH", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue)
                Text(student.coachName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                Text("Chief Martial Arts Coach • Black Belt 4th Dan", style = MaterialTheme.typography.bodySmall, color = TextSlate)

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("📞 +91 98765 11111", style = MaterialTheme.typography.bodySmall, color = TextNavy)
                    Text("✉️ sensei@broma.com", style = MaterialTheme.typography.bodySmall, color = RoyalBlue)
                }
            }
        }

        // Academy Main Office Details
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = CardWhite,
            shadowElevation = 1.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(adminSettings?.academyName ?: "BROMA ACADEMY MAIN DOJO", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                Text("📍 ${adminSettings?.academyAddress ?: "BROMA Academy Complex, Sector 4, Martial Arts Hub"}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                Text("📞 Phone: ${adminSettings?.academyPhone ?: "+91 98765 43210"}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                Text("✉️ Email: ${adminSettings?.academyEmail ?: "info@bromaacademy.com"}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
            }
        }

        // Direct Message Form
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
                Text("SEND DIRECT MESSAGE TO ADMIN", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy)

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject / Reason", color = TextSlate) },
                    placeholder = { Text("e.g. Schedule Query, Belt Exam, Leave Request", color = TextSlate) },
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoyalBlue,
                        unfocusedBorderColor = BorderLight,
                        focusedTextColor = TextNavy,
                        unfocusedTextColor = TextNavy
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("contact_subject_input")
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Message to Sensei / Admin", color = TextSlate) },
                    placeholder = { Text("Type your query or request in detail...", color = TextSlate) },
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoyalBlue,
                        unfocusedBorderColor = BorderLight,
                        focusedTextColor = TextNavy,
                        unfocusedTextColor = TextNavy
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .testTag("contact_message_input")
                )

                if (sentSuccess) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = StatusSuccessBg,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccessBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("✓ Message successfully dispatched to Academy Office & Admin Portal!", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = StatusSuccessText)
                            TextButton(onClick = onNavigateToChat) {
                                Text("💬 View Live Chat Thread with Admin →", color = RoyalBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        if (message.isNotBlank()) {
                            val formattedMsg = if (subject.isNotBlank()) "📌 [$subject] $message" else message
                            onSendMessageToAdmin(formattedMsg)
                            sentSuccess = true
                            subject = ""
                            message = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("send_contact_message_button"),
                    enabled = message.isNotBlank()
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Send Message to Admin", color = TextOnAccent)
                }
            }
        }
    }
}
