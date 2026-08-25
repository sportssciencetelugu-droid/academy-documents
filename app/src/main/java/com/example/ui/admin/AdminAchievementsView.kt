package com.example.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AchievementEntity
import com.example.data.UserAccountEntity
import com.example.ui.theme.*

@Composable
fun AdminAchievementsView(
    achievementsList: List<AchievementEntity>,
    studentsList: List<UserAccountEntity>,
    onApproveAchievement: (String) -> Unit,
    onDeleteAchievement: (String) -> Unit,
    onSaveAchievement: (AchievementEntity) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedStudent by remember { mutableStateOf<UserAccountEntity?>(null) }
    var titleVal by remember { mutableStateOf("") }
    var categoryVal by remember { mutableStateOf("Gold Medal - Kumite Championship") }
    var dateVal by remember { mutableStateOf("2026-08-15") }
    var descVal by remember { mutableStateOf("") }
    var docVal by remember { mutableStateOf("") }

    var selectedTab by remember { mutableStateOf(0) } // 0: Pending, 1: Approved / All

    val pendingList = remember(achievementsList) { achievementsList.filter { it.status == "PENDING" || it.status == "PENDING_REQUEST" } }
    val approvedList = remember(achievementsList) { achievementsList.filter { it.status != "PENDING" && it.status != "PENDING_REQUEST" } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "STUDENT ACHIEVEMENTS & TOURNAMENT AWARDS",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextNavy
                )
                Text(
                    "Review student medal submissions, tournament honors & official awards",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSlate
                )
            }

            Button(
                onClick = {
                    titleVal = ""
                    categoryVal = "Gold Medal - Kumite Championship"
                    descVal = ""
                    docVal = ""
                    showAddDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("admin_add_achievement_button")
            ) {
                Icon(Icons.Default.EmojiEvents, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Award Medal", color = TextOnAccent, fontWeight = FontWeight.Bold)
            }
        }

        // Summary Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = CardWhite,
            contentColor = RoyalBlue
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        "Pending Approval (${pendingList.size})",
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTab == 0) RoyalBlue else TextSlate
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        "Official Records (${approvedList.size})",
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTab == 1) RoyalBlue else TextSlate
                    )
                }
            )
        }

        val displayList = if (selectedTab == 0) pendingList else approvedList

        if (displayList.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = CardWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = TextSlate, modifier = Modifier.size(36.dp))
                    Text(
                        if (selectedTab == 0) "No pending student achievement requests." else "No verified student awards yet.",
                        color = TextSlate,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            displayList.forEach { ach ->
                val isPending = ach.status == "PENDING" || ach.status == "PENDING_REQUEST"

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = CardWhite,
                    shadowElevation = 1.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isPending) StatusWarning else BorderLight)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    ach.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextNavy
                                )
                                Text(
                                    "Student: ${ach.studentName ?: "Academy Award"} (ID: ${ach.studentId ?: "N/A"}) • Event: ${ach.eventName}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = RoyalBlue
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isPending) StatusWarning.copy(alpha = 0.15f) else StatusSuccess.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isPending) StatusWarning else StatusSuccess)
                            ) {
                                Text(
                                    if (isPending) "⏳ PENDING REVIEW" else "🏆 VERIFIED & RECORDED",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isPending) StatusWarning else StatusSuccess,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        HorizontalDivider(color = BorderLight)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Category: ${ach.category}", style = MaterialTheme.typography.bodySmall, color = TextNavy)
                            Text("Date: ${ach.date}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                        }

                        if (ach.description.isNotBlank()) {
                            Text(ach.description, style = MaterialTheme.typography.bodySmall, color = TextSlate)
                        }

                        if (!ach.photoUri.isNullOrBlank()) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SecondaryBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.AttachFile, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(14.dp))
                                    Text("Verification Certificate: ${ach.photoUri}", style = MaterialTheme.typography.labelSmall, color = RoyalBlue)
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isPending) {
                                Button(
                                    onClick = { onApproveAchievement(ach.achievementId) },
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("approve_achievement_${ach.achievementId}")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextOnAccent)
                                    Spacer(Modifier.width(4.dp))
                                    Text("Verify & Approve", color = TextOnAccent, fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.width(8.dp))
                            }

                            OutlinedButton(
                                onClick = { onDeleteAchievement(ach.achievementId) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusDanger),
                                border = androidx.compose.foundation.BorderStroke(1.dp, StatusDanger),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Delete", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    "Award Student Achievement / Medal",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextNavy
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Select Recipient Student:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue)

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        studentsList.take(6).forEach { st ->
                            val isSel = selectedStudent?.userId == st.userId
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) ActiveNavBg else SecondaryBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) RoyalBlue else BorderLight),
                                onClick = { selectedStudent = st }
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${st.fullName} (${st.currentBelt})", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                    Text(if (isSel) "✓ Selected" else "Select", style = MaterialTheme.typography.labelSmall, color = RoyalBlue)
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = titleVal,
                        onValueChange = { titleVal = it },
                        label = { Text("Award Title / Tournament Name *", color = TextSlate) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = categoryVal,
                        onValueChange = { categoryVal = it },
                        label = { Text("Category / Medal *", color = TextSlate) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = dateVal,
                        onValueChange = { dateVal = it },
                        label = { Text("Date Awarded (YYYY-MM-DD)", color = TextSlate) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = descVal,
                        onValueChange = { descVal = it },
                        label = { Text("Description & Notes", color = TextSlate) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (titleVal.isNotBlank() && selectedStudent != null) {
                            val entity = AchievementEntity(
                                achievementId = "ACH-" + System.currentTimeMillis(),
                                studentId = selectedStudent!!.userId,
                                studentName = selectedStudent!!.fullName,
                                title = titleVal,
                                eventName = if (descVal.isNotBlank()) descVal else "BROMA Academy Tournament",
                                category = categoryVal,
                                date = dateVal,
                                description = descVal,
                                photoUri = if (docVal.isNotBlank()) docVal else null,
                                status = "APPROVED"
                            )
                            onSaveAchievement(entity)
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Record Award", color = TextOnAccent, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel", color = TextSlate) }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
