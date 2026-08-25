package com.example.ui.coach

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChatMessageEntity
import com.example.data.UserAccountEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CoachChatScreen(
    coach: UserAccountEntity,
    chatMessages: List<ChatMessageEntity>,
    onSendMessage: (String) -> Unit
) {
    var messageInput by remember { mutableStateOf("") }

    val myMessages = chatMessages.filter {
        (it.senderId == coach.userId || it.recipientId == coach.userId)
    }.sortedBy { it.timestamp }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = CardWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(RoyalBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = TextOnAccent)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("BROMA Academy Administration", fontWeight = FontWeight.Bold, color = TextNavy, style = MaterialTheme.typography.titleMedium)
                    Text("Direct real-time support line for Instructors and Senseis", color = TextSlate, style = MaterialTheme.typography.bodySmall)
                }
                Surface(shape = RoundedCornerShape(6.dp), color = StatusSuccessBg) {
                    Text("Online", modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp), color = StatusSuccessText, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        // Messages Box
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = CardWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
            shadowElevation = 1.dp
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (myMessages.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No messages yet. Send a direct message to Academy Admin.", color = TextSlate, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                } else {
                    items(myMessages) { msg ->
                        val isMe = msg.senderId == coach.userId
                        val dateStr = SimpleDateFormat("hh:mm a, dd MMM", Locale.getDefault()).format(Date(msg.timestamp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                        ) {
                            Surface(
                                shape = RoundedCornerShape(
                                    topStart = 12.dp,
                                    topEnd = 12.dp,
                                    bottomStart = if (isMe) 12.dp else 2.dp,
                                    bottomEnd = if (isMe) 2.dp else 12.dp
                                ),
                                color = if (isMe) RoyalBlue else SecondaryBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isMe) RoyalBlue else BorderLight)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = if (isMe) "Sensei (You)" else "Admin",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (isMe) Color.White.copy(alpha = 0.8f) else RoyalBlue
                                    )
                                    Text(
                                        text = msg.messageText,
                                        color = if (isMe) TextOnAccent else TextNavy,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = dateStr,
                                        fontSize = 9.sp,
                                        color = if (isMe) Color.White.copy(alpha = 0.7f) else TextSlate
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = messageInput,
                onValueChange = { messageInput = it },
                placeholder = { Text("Ask admin question or batch update...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("coach_chat_input_field"),
                shape = RoundedCornerShape(24.dp)
            )

            IconButton(
                onClick = {
                    if (messageInput.isNotBlank()) {
                        onSendMessage(messageInput.trim())
                        messageInput = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(RoyalBlue)
                    .testTag("coach_chat_send_button")
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = TextOnAccent)
            }
        }
    }
}
