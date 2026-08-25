package com.example.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AcademyLeadershipEntity
import com.example.ui.theme.*
import java.util.UUID

@Composable
fun AdminLeadershipManagementView(
    leadershipList: List<AcademyLeadershipEntity>,
    onSaveLeadership: (AcademyLeadershipEntity) -> Unit,
    onDeleteLeadership: (String) -> Unit
) {
    var showAddEditDialog by remember { mutableStateOf(false) }
    var selectedLeader by remember { mutableStateOf<AcademyLeadershipEntity?>(null) }
    var leaderToDelete by remember { mutableStateOf<AcademyLeadershipEntity?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    if (showAddEditDialog) {
        AddEditLeadershipDialog(
            leader = selectedLeader,
            onDismiss = {
                showAddEditDialog = false
                selectedLeader = null
            },
            onSave = { updated ->
                onSaveLeadership(updated)
                successMessage = "Official '${updated.fullName}' (${updated.postTitle}) updated successfully! Synced across all student & coach portals."
                showAddEditDialog = false
                selectedLeader = null
            }
        )
    }

    if (leaderToDelete != null) {
        val target = leaderToDelete!!
        AlertDialog(
            onDismissRequest = { leaderToDelete = null },
            icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = StatusError) },
            title = { Text("Delete Official Post?", fontWeight = FontWeight.Bold, color = TextNavy) },
            text = {
                Text(
                    "Are you sure you want to delete '${target.fullName}' (${target.postTitle}) from the Academy Leadership Hierarchy? This action will remove this post from all portals.",
                    color = TextSlate,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteLeadership(target.leadershipId)
                        successMessage = "Post '${target.postTitle}' (${target.fullName}) deleted from official hierarchy."
                        leaderToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusError),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Yes, Delete", color = TextOnAccent)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { leaderToDelete = null }, shape = RoundedCornerShape(8.dp)) {
                    Text("Cancel", color = TextNavy)
                }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = CardWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = RoyalBlue)
                        Text(
                            text = "ACADEMY OFFICIALS & HIERARCHY",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextNavy
                        )
                    }
                    Text(
                        text = "Add, edit, change, or delete Academy Leadership posts (Technical Advisor, Director, Chairman, President, General Secretary, Treasurer). Changes instantly reflect in Student & Coach portals.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSlate
                    )
                }

                Button(
                    onClick = {
                        selectedLeader = null
                        showAddEditDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("add_leadership_post_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Post")
                }
            }
        }

        if (successMessage != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = StatusSuccessBg,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccessBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusSuccess)
                        Text(successMessage!!, style = MaterialTheme.typography.bodySmall, color = StatusSuccessText)
                    }
                    IconButton(onClick = { successMessage = null }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = StatusSuccessText, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // List of Leadership members
        if (leadershipList.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = CardWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Groups, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(48.dp))
                    Text("No Official Posts Configured", fontWeight = FontWeight.Bold, color = TextNavy)
                    Text("Click 'Add Post' above to add Technical Advisor, Director, Chairman, President, General Secretary, Treasurer, etc.", color = TextSlate, style = MaterialTheme.typography.bodySmall)
                }
            }
        } else {
            leadershipList.sortedBy { it.displayOrder }.forEachIndexed { index, leader ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_leader_card_${leader.leadershipId}"),
                    shape = RoundedCornerShape(12.dp),
                    color = CardWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = StatusInfoBg,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusInfoBorder)
                                ) {
                                    Text(
                                        text = "#${leader.displayOrder} • ${leader.postTitle.uppercase()}",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = StatusInfoText
                                    )
                                }
                                if (leader.isExecutiveBoard) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = StatusSuccessBg
                                    ) {
                                        Text(
                                            text = "Executive Board",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = StatusSuccessText
                                        )
                                    }
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = {
                                        selectedLeader = leader
                                        showAddEditDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp).testTag("edit_leader_${leader.leadershipId}")
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextOnAccent, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Edit / Change", fontSize = 11.sp, color = TextOnAccent)
                                }
                                OutlinedButton(
                                    onClick = {
                                        leaderToDelete = leader
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp).testTag("delete_leader_${leader.leadershipId}"),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusError)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusError, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Delete", fontSize = 11.sp, color = StatusError)
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(StatusInfoBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = RoyalBlue)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(leader.fullName, fontWeight = FontWeight.Bold, color = TextNavy, style = MaterialTheme.typography.titleMedium)
                                Text(leader.rankOrBelt, color = RoyalBlue, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium))
                            }
                        }

                        if (leader.messageOrBio.isNotBlank()) {
                            Text(leader.messageOrBio, color = TextSlate, style = MaterialTheme.typography.bodySmall)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            if (leader.contactPhone.isNotBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = TextSlate, modifier = Modifier.size(14.dp))
                                    Text(leader.contactPhone, style = MaterialTheme.typography.labelSmall, color = TextNavy)
                                }
                            }
                            if (leader.contactEmail.isNotBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Email, contentDescription = null, tint = TextSlate, modifier = Modifier.size(14.dp))
                                    Text(leader.contactEmail, style = MaterialTheme.typography.labelSmall, color = TextNavy)
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
fun AddEditLeadershipDialog(
    leader: AcademyLeadershipEntity?,
    onDismiss: () -> Unit,
    onSave: (AcademyLeadershipEntity) -> Unit
) {
    var postTitle by remember { mutableStateOf(leader?.postTitle ?: "Technical Advisor") }
    var fullName by remember { mutableStateOf(leader?.fullName ?: "") }
    var rankOrBelt by remember { mutableStateOf(leader?.rankOrBelt ?: "Kyoshi • 7th Dan Black Belt") }
    var contactPhone by remember { mutableStateOf(leader?.contactPhone ?: "+91 98480 12345") }
    var contactEmail by remember { mutableStateOf(leader?.contactEmail ?: "broma.director@martialacademy.org") }
    var messageOrBio by remember { mutableStateOf(leader?.messageOrBio ?: "") }
    var displayOrder by remember { mutableStateOf((leader?.displayOrder ?: 1).toString()) }
    var isExecutiveBoard by remember { mutableStateOf(leader?.isExecutiveBoard ?: true) }

    val presetPosts = listOf(
        "Technical Advisor",
        "Technical Advancer",
        "Chief Technical Director",
        "Grandmaster / Founder",
        "Chairman",
        "President",
        "Director",
        "General Secretary",
        "Treasurer",
        "Vice President",
        "Joint Secretary",
        "Senior Advisory Council"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = RoyalBlue)
                Text(
                    text = if (leader == null) "Add Academy Official Post" else "Edit Academy Official / Post",
                    fontWeight = FontWeight.Bold,
                    color = TextNavy
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Select preset or enter custom designation:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextNavy
                )

                // Quick preset post pills (horizontal scrollable row)
                presetPosts.chunked(3).forEach { rowPosts ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        rowPosts.forEach { post ->
                            val isSel = postTitle.equals(post, ignoreCase = true)
                            Surface(
                                modifier = Modifier
                                    .clickable { postTitle = post }
                                    .padding(vertical = 2.dp),
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) RoyalBlue else SecondaryBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) RoyalBlue else BorderLight)
                            ) {
                                Text(
                                    text = post,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) TextOnAccent else TextNavy,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = postTitle,
                    onValueChange = { postTitle = it },
                    label = { Text("Post Title / Designation *") },
                    placeholder = { Text("e.g. Technical Advisor, Chief Technical Director") },
                    modifier = Modifier.fillMaxWidth().testTag("post_title_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Official Full Name *") },
                    placeholder = { Text("e.g. Hanshi R. K. Sharma") },
                    modifier = Modifier.fillMaxWidth().testTag("post_fullname_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = rankOrBelt,
                    onValueChange = { rankOrBelt = it },
                    label = { Text("Martial Rank / Dan Grade") },
                    placeholder = { Text("e.g. 8th Dan Black Belt, Founder") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = contactPhone,
                        onValueChange = { contactPhone = it },
                        label = { Text("Contact Phone") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = displayOrder,
                        onValueChange = { displayOrder = it },
                        label = { Text("Display Order") },
                        modifier = Modifier.width(95.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                OutlinedTextField(
                    value = contactEmail,
                    onValueChange = { contactEmail = it },
                    label = { Text("Official Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = messageOrBio,
                    onValueChange = { messageOrBio = it },
                    label = { Text("Message / Bio / Responsibilities") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = isExecutiveBoard,
                        onCheckedChange = { isExecutiveBoard = it }
                    )
                    Text("Part of Governing Executive Board", style = MaterialTheme.typography.bodyMedium, color = TextNavy)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isNotBlank() && postTitle.isNotBlank()) {
                        val updated = AcademyLeadershipEntity(
                            leadershipId = leader?.leadershipId ?: "LEAD-${UUID.randomUUID().toString().take(8)}",
                            postTitle = postTitle.trim(),
                            fullName = fullName.trim(),
                            rankOrBelt = rankOrBelt.trim(),
                            contactPhone = contactPhone.trim(),
                            contactEmail = contactEmail.trim(),
                            messageOrBio = messageOrBio.trim(),
                            displayOrder = displayOrder.toIntOrNull() ?: 1,
                            isExecutiveBoard = isExecutiveBoard
                        )
                        onSave(updated)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                shape = RoundedCornerShape(8.dp),
                enabled = fullName.isNotBlank() && postTitle.isNotBlank(),
                modifier = Modifier.testTag("save_leadership_button")
            ) {
                Text(if (leader == null) "Create Official Post" else "Save Changes")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                Text("Cancel")
            }
        }
    )
}
