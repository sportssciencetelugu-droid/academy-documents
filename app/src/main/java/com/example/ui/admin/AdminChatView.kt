package com.example.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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

private val WhatsAppTeal = Color(0xFF008069)
private val WhatsAppDarkTeal = Color(0xFF075E54)
private val WhatsAppChatBg = Color(0xFFEFEAE2)
private val WhatsAppOutgoingBubble = Color(0xFFD9FDD3)
private val WhatsAppIncomingBubble = Color(0xFFFFFFFF)
private val WhatsAppCheckBlue = Color(0xFF53BDEB)

@Composable
fun AdminChatManagementView(
    allUsersList: List<UserAccountEntity>,
    chatMessages: List<ChatMessageEntity>,
    onSendChatMessage: (String, String, String, String, String, String) -> Unit,
    onDeleteChatMessage: (String) -> Unit
) {
    val studentsAndCoaches = allUsersList.filter { it.role != com.example.data.UserRole.ADMIN }
    var searchQuery by remember { mutableStateOf("") }
    var filterTab by remember { mutableStateOf("ALL") } // ALL, STUDENTS, COACHES, UNREAD
    var selectedUser by remember { mutableStateOf<UserAccountEntity?>(null) }
    var messageInput by remember { mutableStateOf("") }

    val filteredUsers = studentsAndCoaches.filter { user ->
        val matchesQuery = user.fullName.contains(searchQuery, ignoreCase = true) ||
                user.userId.contains(searchQuery, ignoreCase = true) ||
                user.phone.contains(searchQuery, ignoreCase = true)
        val userMsgs = chatMessages.filter { it.senderId == user.userId || it.recipientId == user.userId }
        val hasUnread = userMsgs.any { it.senderId == user.userId && !it.isRead }

        val matchesTab = when (filterTab) {
            "STUDENTS" -> user.role == com.example.data.UserRole.STUDENT
            "COACHES" -> user.role == com.example.data.UserRole.COACH
            "UNREAD" -> hasUnread
            else -> true
        }
        matchesQuery && matchesTab
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
    ) {
        val isCompact = maxWidth < 650.dp

        if (isCompact) {
            // MOBILE COMPACT VIEW: Contact List OR Specific Person Chat Screen
            if (selectedUser == null) {
                // ALL CONTACTS LIST (WhatsApp Style)
                WhatsAppContactListPane(
                    contacts = filteredUsers,
                    totalCount = studentsAndCoaches.size,
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    filterTab = filterTab,
                    onFilterTabChange = { filterTab = it },
                    selectedUser = selectedUser,
                    chatMessages = chatMessages,
                    onSelectUser = { selectedUser = it },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )
            } else {
                // SPECIFIC PERSON CHAT CONVERSATION (Full Screen with Back Button)
                WhatsAppConversationPane(
                    activeUser = selectedUser!!,
                    chatMessages = chatMessages,
                    messageInput = messageInput,
                    onMessageInputChange = { messageInput = it },
                    onSendChatMessage = onSendChatMessage,
                    onBackToContacts = { selectedUser = null },
                    isCompact = true,
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            // EXPANDED / TABLET DUAL-PANE VIEW
            val activeUser = selectedUser ?: studentsAndCoaches.firstOrNull()

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Left Column: WhatsApp Contacts List
                WhatsAppContactListPane(
                    contacts = filteredUsers,
                    totalCount = studentsAndCoaches.size,
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    filterTab = filterTab,
                    onFilterTabChange = { filterTab = it },
                    selectedUser = activeUser,
                    chatMessages = chatMessages,
                    onSelectUser = { selectedUser = it },
                    modifier = Modifier
                        .width(320.dp)
                        .fillMaxHeight()
                )

                // Right Column: Conversation Window
                if (activeUser != null) {
                    WhatsAppConversationPane(
                        activeUser = activeUser,
                        chatMessages = chatMessages,
                        messageInput = messageInput,
                        onMessageInputChange = { messageInput = it },
                        onSendChatMessage = onSendChatMessage,
                        onBackToContacts = null,
                        isCompact = false,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                } else {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(12.dp),
                        color = WhatsAppChatBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("💬", fontSize = 48.sp)
                                Text("BROMA WhatsApp Admin Messenger", fontWeight = FontWeight.Bold, color = TextNavy, style = MaterialTheme.typography.titleMedium)
                                Text("Select a student or coach from the list to start messaging in real-time.", color = TextSlate, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WhatsAppContactListPane(
    contacts: List<UserAccountEntity>,
    totalCount: Int,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    filterTab: String,
    onFilterTabChange: (String) -> Unit,
    selectedUser: UserAccountEntity?,
    chatMessages: List<ChatMessageEntity>,
    onSelectUser: (UserAccountEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = CardWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // WhatsApp Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = WhatsAppTeal,
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Text("BROMA WhatsApp Chat", fontWeight = FontWeight.Bold, color = Color.White, style = MaterialTheme.typography.titleSmall)
                        }
                        Text("$totalCount chats", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp)
                    }

                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = { Text("Search name, phone, ID...", fontSize = 12.sp, color = TextSlate) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSlate, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CardWhite,
                            unfocusedContainerColor = CardWhite,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        )
                    )
                }
            }

            // Filter Pills (All, Students, Coaches, Unread)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("ALL" to "All", "STUDENTS" to "Students", "COACHES" to "Coaches", "UNREAD" to "Unread").forEach { (tabKey, tabLabel) ->
                    val isSelected = filterTab == tabKey
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) WhatsAppTeal.copy(alpha = 0.15f) else Color(0xFFF1F5F9),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) WhatsAppTeal else Color.Transparent),
                        modifier = Modifier.clickable { onFilterTabChange(tabKey) }
                    ) {
                        Text(
                            text = tabLabel,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) WhatsAppTeal else TextSlate,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = BorderLight)

            // Contact List
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                if (contacts.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("No contacts found", color = TextSlate, fontSize = 12.sp)
                        }
                    }
                } else {
                    items(contacts) { user ->
                        val isSelected = selectedUser?.userId == user.userId
                        val userMsgs = chatMessages.filter { it.senderId == user.userId || it.recipientId == user.userId }
                        val lastMsg = userMsgs.maxByOrNull { it.timestamp }
                        val unreadCount = userMsgs.count { it.senderId == user.userId && !it.isRead }
                        val lastMsgTime = if (lastMsg != null) {
                            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(lastMsg.timestamp))
                        } else ""

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectUser(user) }
                                .testTag("chat_user_item_${user.userId}"),
                            color = if (isSelected) Color(0xFFE9EDEF) else CardWhite
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Avatar
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(if (user.role == com.example.data.UserRole.COACH) Color(0xFFE0F2FE) else Color(0xFFDCFCE7)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (user.role == com.example.data.UserRole.COACH) "🥋" else "🎓",
                                        fontSize = 18.sp
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(user.fullName, fontWeight = FontWeight.Bold, color = TextNavy, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                                        Text(lastMsgTime, fontSize = 10.sp, color = if (unreadCount > 0) WhatsAppTeal else TextSlate)
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = lastMsg?.messageText ?: "${user.role} • ${user.currentBelt}",
                                            color = if (unreadCount > 0) TextNavy else TextSlate,
                                            fontWeight = if (unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal,
                                            style = MaterialTheme.typography.bodySmall,
                                            maxLines = 1
                                        )

                                        if (unreadCount > 0) {
                                            Badge(containerColor = WhatsAppTeal, contentColor = Color.White) {
                                                Text(unreadCount.toString(), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        HorizontalDivider(color = BorderLight.copy(alpha = 0.5f), modifier = Modifier.padding(start = 64.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun WhatsAppConversationPane(
    activeUser: UserAccountEntity,
    chatMessages: List<ChatMessageEntity>,
    messageInput: String,
    onMessageInputChange: (String) -> Unit,
    onSendChatMessage: (String, String, String, String, String, String) -> Unit,
    onBackToContacts: (() -> Unit)?,
    isCompact: Boolean,
    modifier: Modifier = Modifier
) {
    val conversationMessages = remember(chatMessages, activeUser.userId) {
        chatMessages.filter {
            (it.senderId == activeUser.userId || it.recipientId == activeUser.userId)
        }.sortedBy { it.timestamp }
    }

    Surface(
        modifier = modifier,
        shape = if (isCompact) RoundedCornerShape(0.dp) else RoundedCornerShape(12.dp),
        color = WhatsAppChatBg,
        border = if (isCompact) null else androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // WhatsApp Chat Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = WhatsAppDarkTeal,
                shape = if (isCompact) RoundedCornerShape(0.dp) else RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onBackToContacts != null) {
                        IconButton(
                            onClick = onBackToContacts,
                            modifier = Modifier.size(36.dp).testTag("chat_back_to_contacts_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Contacts", tint = Color.White)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (activeUser.role == com.example.data.UserRole.COACH) "🥋" else "🎓",
                            fontSize = 17.sp
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(activeUser.fullName, fontWeight = FontWeight.Bold, color = Color.White, style = MaterialTheme.typography.titleSmall)
                        Text(
                            "online • ${activeUser.role} • ${activeUser.currentBelt} • ${activeUser.phone}",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }

                    IconButton(onClick = { /* call */ }) {
                        Icon(Icons.Default.Phone, contentDescription = "Call", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = { /* info */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }

            // Message history list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .background(WhatsAppChatBg)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Encryption Notice Banner
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp), contentAlignment = Alignment.Center) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFF3C4),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFE082))
                        ) {
                            Text(
                                "🔒 Messages are end-to-end synced with Supabase & BROMA Academy Portal.",
                                fontSize = 10.sp,
                                color = Color(0xFF5D4037),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                if (conversationMessages.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Surface(shape = RoundedCornerShape(10.dp), color = Color.White.copy(alpha = 0.9f)) {
                                Text(
                                    "No prior chat messages. Start a conversation with ${activeUser.fullName}!",
                                    color = TextSlate,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                } else {
                    items(conversationMessages) { msg ->
                        val isMe = msg.isFromAdmin
                        val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(msg.timestamp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                        ) {
                            Surface(
                                shape = RoundedCornerShape(
                                    topStart = 10.dp,
                                    topEnd = 10.dp,
                                    bottomStart = if (isMe) 10.dp else 2.dp,
                                    bottomEnd = if (isMe) 2.dp else 10.dp
                                ),
                                color = if (isMe) WhatsAppOutgoingBubble else WhatsAppIncomingBubble,
                                shadowElevation = 1.dp
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                        .widthIn(max = 280.dp),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    if (!isMe) {
                                        Text(
                                            text = msg.senderName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = WhatsAppTeal
                                        )
                                    }
                                    Text(
                                        text = msg.messageText,
                                        color = TextNavy,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Row(
                                        modifier = Modifier.align(Alignment.End),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = timeStr,
                                            fontSize = 9.sp,
                                            color = TextSlate
                                        )
                                        if (isMe) {
                                            Text("✓✓", fontSize = 10.sp, color = WhatsAppCheckBlue, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Quick response chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0F2F5))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "✅ Fee Received",
                    "🥋 Belt Exam Confirmed",
                    "⏰ 6:00 AM Dojo Class",
                    "📞 Please Call Office"
                ).forEach { quickReply ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CardWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                        modifier = Modifier.clickable {
                            onMessageInputChange(quickReply)
                        }
                    ) {
                        Text(quickReply, fontSize = 10.sp, color = TextNavy, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
            }

            // WhatsApp Input Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0F2F5))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    color = CardWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("😊", fontSize = 16.sp, modifier = Modifier.clickable { })
                        OutlinedTextField(
                            value = messageInput,
                            onValueChange = onMessageInputChange,
                            placeholder = { Text("Message ${activeUser.fullName}...", fontSize = 13.sp, color = TextSlate) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_chat_input_field"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = TextNavy,
                                unfocusedTextColor = TextNavy
                            )
                        )
                        Icon(Icons.Default.AttachFile, contentDescription = "Attach", tint = TextSlate, modifier = Modifier.size(20.dp))
                        Icon(Icons.Default.CameraAlt, contentDescription = "Camera", tint = TextSlate, modifier = Modifier.size(20.dp))
                    }
                }

                IconButton(
                    onClick = {
                        if (messageInput.isNotBlank()) {
                            onSendChatMessage(
                                "ADMIN-01",
                                "BROMA Academy Admin",
                                "ADMIN",
                                activeUser.userId,
                                activeUser.role.name,
                                messageInput.trim()
                            )
                            onMessageInputChange("")
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(WhatsAppTeal)
                        .testTag("admin_chat_send_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
