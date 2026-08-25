package com.example.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.data.BatchEntity
import com.example.data.UserAccountEntity
import com.example.ui.common.*
import com.example.ui.theme.*

@Composable
fun AdminBatchesView(
    batchesList: List<BatchEntity>,
    coachesList: List<UserAccountEntity>,
    onSaveBatch: (BatchEntity) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var editingBatch by remember { mutableStateOf<BatchEntity?>(null) }
    var selectedDayFilter by remember { mutableStateOf("ALL") } // "ALL", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"

    val daysOfWeek = listOf("ALL", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    val filteredBatches = batchesList.filter { batch ->
        if (selectedDayFilter == "ALL") true
        else batch.activeDays.contains(selectedDayFilter, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "BATCH & SCHEDULE MANAGEMENT",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                    color = TextNavy
                )
                Text(
                    text = "${batchesList.size} Total Batches • Synced with Student & Coach Portals",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSlate
                )
            }

            BromaPrimaryButton(
                text = "+ ADD BATCH",
                onClick = {
                    editingBatch = BatchEntity(
                        batchId = "BATCH-${System.currentTimeMillis() % 10000}",
                        batchName = "New Training Batch",
                        programName = "Regular Karate & Kumite",
                        coachId = coachesList.firstOrNull()?.userId ?: "COACH-01",
                        coachName = coachesList.firstOrNull()?.fullName ?: "Sensei Rajesh Kumar",
                        location = "BROMA Central Dojo",
                        room = "Tatami Hall A",
                        startTime = "06:00 PM",
                        endTime = "07:30 PM",
                        activeDays = "Mon, Wed, Fri",
                        effectiveStartDate = "2026-01-01",
                        effectiveEndDate = "2026-12-31",
                        status = "ACTIVE",
                        studentCount = 20
                    )
                    showDialog = true
                },
                icon = Icons.Default.Add,
                modifier = Modifier.testTag("add_batch_centre_button")
            )
        }

        // Weekly Timetable Day Filter Tabs
        Text(
            text = "WEEKLY SCHEDULE TIMETABLE",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
            color = TextSlate
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardWhite, RoundedCornerShape(10.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            daysOfWeek.forEach { day ->
                val isSelected = selectedDayFilter == day
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) RoyalBlue else Color.Transparent)
                        .clickable { selectedDayFilter = day }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp
                        ),
                        color = if (isSelected) TextOnAccent else TextNavy
                    )
                }
            }
        }

        if (filteredBatches.isEmpty()) {
            EmptyStateCard(
                title = "No Batches for $selectedDayFilter",
                description = "No active training batches scheduled for this day.",
                actionLabel = "+ Create Batch",
                onActionClick = {
                    editingBatch = BatchEntity(
                        batchId = "BATCH-${System.currentTimeMillis() % 10000}",
                        batchName = "New Training Batch",
                        programName = "Regular Karate & Kumite",
                        coachId = coachesList.firstOrNull()?.userId ?: "COACH-01",
                        coachName = coachesList.firstOrNull()?.fullName ?: "Sensei Rajesh Kumar",
                        location = "BROMA Central Dojo",
                        room = "Tatami Hall A",
                        startTime = "06:00 PM",
                        endTime = "07:30 PM",
                        activeDays = if (selectedDayFilter == "ALL") "Mon, Wed, Fri" else selectedDayFilter,
                        effectiveStartDate = "2026-01-01",
                        effectiveEndDate = "2026-12-31",
                        status = "ACTIVE",
                        studentCount = 20
                    )
                    showDialog = true
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredBatches, key = { it.batchId }) { batch ->
                    BatchAdminCard(
                        batch = batch,
                        onEdit = {
                            editingBatch = batch
                            showDialog = true
                        }
                    )
                }
            }
        }
    }

    // Add / Edit Dialog
    if (showDialog && editingBatch != null) {
        var bId by remember { mutableStateOf(editingBatch!!.batchId) }
        var bName by remember { mutableStateOf(editingBatch!!.batchName) }
        var pName by remember { mutableStateOf(editingBatch!!.programName) }
        var loc by remember { mutableStateOf(editingBatch!!.location) }
        var room by remember { mutableStateOf(editingBatch!!.room) }
        var selectedCoach by remember { mutableStateOf(coachesList.find { it.userId == editingBatch!!.coachId } ?: coachesList.firstOrNull()) }
        var sTime by remember { mutableStateOf(editingBatch!!.startTime) }
        var eTime by remember { mutableStateOf(editingBatch!!.endTime) }
        var days by remember { mutableStateOf(editingBatch!!.activeDays) }
        var countStr by remember { mutableStateOf(editingBatch!!.studentCount.toString()) }
        var startDate by remember { mutableStateOf(editingBatch!!.effectiveStartDate ?: "2026-01-01") }
        var endDate by remember { mutableStateOf(editingBatch!!.effectiveEndDate ?: "") }
        var status by remember { mutableStateOf(editingBatch!!.status) }

        var coachDropdownExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = if (editingBatch!!.batchId.startsWith("BATCH-")) "Add / Edit Batch Schedule" else "Edit ${editingBatch!!.batchName}",
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
                    OutlinedTextField(
                        value = bName,
                        onValueChange = { bName = it },
                        label = { Text("Batch Name *") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = pName,
                        onValueChange = { pName = it },
                        label = { Text("Program / Category") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Coach Selector Dropdown
                    Text("Assigned Coach / Sensei *", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedCoach?.fullName ?: "Select Coach",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { coachDropdownExpanded = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { coachDropdownExpanded = true },
                            shape = RoundedCornerShape(10.dp)
                        )
                        DropdownMenu(
                            expanded = coachDropdownExpanded,
                            onDismissRequest = { coachDropdownExpanded = false }
                        ) {
                            coachesList.forEach { coach ->
                                DropdownMenuItem(
                                    text = { Text("${coach.fullName} (${coach.designation})", color = TextNavy) },
                                    onClick = {
                                        selectedCoach = coach
                                        coachDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Times
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = sTime,
                            onValueChange = { sTime = it },
                            label = { Text("Start Time *") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = eTime,
                            onValueChange = { eTime = it },
                            label = { Text("End Time *") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    // Active Days
                    OutlinedTextField(
                        value = days,
                        onValueChange = { days = it },
                        label = { Text("Active Days (e.g. Mon, Wed, Fri)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Quick day presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Mon, Wed, Fri", "Tue, Thu, Sat", "Daily (Mon-Sat)", "Weekend (Sat-Sun)").forEach { preset ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SecondaryBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                                modifier = Modifier.clickable { days = preset }
                            ) {
                                Text(preset, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = RoyalBlue, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                            }
                        }
                    }

                    // Location & Room
                    OutlinedTextField(
                        value = loc,
                        onValueChange = { loc = it },
                        label = { Text("Dojo Location / Branch *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = room,
                        onValueChange = { room = it },
                        label = { Text("Dojo Room / Tatami Hall") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Effective Dates
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = startDate,
                            onValueChange = { startDate = it },
                            label = { Text("Effective Start") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = endDate,
                            onValueChange = { endDate = it },
                            label = { Text("Effective End") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    // Status
                    Text("Batch Status", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("ACTIVE", "INACTIVE").forEach { st ->
                            val isSelected = status == st
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) RoyalBlue else SecondaryBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) RoyalBlue else BorderLight),
                                modifier = Modifier.clickable { status = st }
                            ) {
                                Text(
                                    text = st,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) TextOnAccent else TextNavy,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val count = countStr.toIntOrNull() ?: 20
                        val updated = editingBatch!!.copy(
                            batchId = bId,
                            batchName = bName.ifBlank { "Karate Batch" },
                            programName = pName.ifBlank { "Regular Training" },
                            location = loc.ifBlank { "Main Dojo" },
                            room = room.ifBlank { "Main Hall" },
                            coachId = selectedCoach?.userId ?: editingBatch!!.coachId,
                            coachName = selectedCoach?.fullName ?: editingBatch!!.coachName,
                            startTime = sTime.ifBlank { "06:00 PM" },
                            endTime = eTime.ifBlank { "07:30 PM" },
                            activeDays = days.ifBlank { "Mon, Wed, Fri" },
                            effectiveStartDate = startDate,
                            effectiveEndDate = endDate.ifBlank { "2026-12-31" },
                            status = status,
                            studentCount = count
                        )
                        onSaveBatch(updated)
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue, contentColor = TextOnAccent),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("save_batch_dialog_button")
                ) {
                    Text("SAVE BATCH", fontWeight = FontWeight.Bold, color = TextOnAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel", color = TextSlate) }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun BatchAdminCard(
    batch: BatchEntity,
    onEdit: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = CardWhite,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
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
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = batch.batchName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextNavy
                    )
                    StatusBadge(status = batch.status)
                }

                Button(
                    onClick = onEdit,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryBg, contentColor = TextNavy),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = RoyalBlue)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue)
                }
            }

            Text(
                text = "${batch.programName} • ${batch.location} (${batch.room})",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = TextSlate
            )

            HorizontalDivider(color = BorderLight)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(16.dp))
                    Text(
                        text = "${batch.startTime} – ${batch.endTime}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = RoyalBlue
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Event, contentDescription = null, tint = StatusWarning, modifier = Modifier.size(16.dp))
                    Text(
                        text = batch.activeDays,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = TextNavy
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = TextSlate, modifier = Modifier.size(16.dp))
                    Text("Coach: ${batch.coachName}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                }

                Text(
                    text = "${batch.studentCount} Students",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextNavy
                )
            }
        }
    }
}
