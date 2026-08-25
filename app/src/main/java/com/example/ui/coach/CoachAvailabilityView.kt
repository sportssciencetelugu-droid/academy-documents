package com.example.ui.coach

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CoachAvailabilityEntity
import com.example.data.UserAccountEntity
import com.example.ui.theme.*
import java.util.UUID

@Composable
fun CoachAvailabilityManagementView(
    coach: UserAccountEntity,
    availabilities: List<CoachAvailabilityEntity>,
    onSaveAvailability: (CoachAvailabilityEntity) -> Unit,
    onDeleteAvailability: (String) -> Unit
) {
    val coachAvailabilities = availabilities.filter { it.coachId == coach.userId }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedAvailability by remember { mutableStateOf<CoachAvailabilityEntity?>(null) }
    var successMsg by remember { mutableStateOf<String?>(null) }

    if (showAddDialog) {
        AddEditAvailabilityDialog(
            coach = coach,
            availability = selectedAvailability,
            onDismiss = {
                showAddDialog = false
                selectedAvailability = null
            },
            onSave = { updated ->
                onSaveAvailability(updated)
                successMsg = "Availability schedule updated! Synced to Admin and Student portals in real-time."
                showAddDialog = false
                selectedAvailability = null
            }
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
                        Icon(Icons.Default.EventAvailable, contentDescription = null, tint = RoyalBlue)
                        Text("MY AVAILABILITY & SLOTS", fontWeight = FontWeight.Bold, color = TextNavy, style = MaterialTheme.typography.titleMedium)
                    }
                    Text("Configure your weekly training slots and availability status. Real-time updates automatically push to Admin and Students.", color = TextSlate, style = MaterialTheme.typography.bodySmall)
                }

                Button(
                    onClick = {
                        selectedAvailability = null
                        showAddDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("add_coach_slot_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Slot")
                }
            }
        }

        if (successMsg != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = StatusSuccessBg,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccessBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusSuccess)
                    Text(successMsg!!, style = MaterialTheme.typography.bodySmall, color = StatusSuccessText)
                }
            }
        }

        if (coachAvailabilities.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = CardWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No availability slots configured yet. Click 'Add Slot' to set up your training hours.", color = TextSlate)
                }
            }
        } else {
            coachAvailabilities.forEach { slot ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("availability_card_${slot.availabilityId}"),
                    shape = RoundedCornerShape(12.dp),
                    color = CardWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (slot.status == "AVAILABLE") StatusSuccessBg else StatusWarningBg
                                ) {
                                    Text(
                                        text = slot.status,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (slot.status == "AVAILABLE") StatusSuccessText else StatusWarningText
                                    )
                                }
                                Text(slot.dayOfWeek, fontWeight = FontWeight.Bold, color = TextNavy, style = MaterialTheme.typography.titleMedium)
                            }
                            Text("Timing: ${slot.startTime} – ${slot.endTime}", color = RoyalBlue, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                            Text("Location: ${slot.location}", color = TextSlate, style = MaterialTheme.typography.bodySmall)
                            if (slot.notes.isNotBlank()) {
                                Text("Note: ${slot.notes}", color = TextSlate, style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = {
                                    selectedAvailability = slot
                                    showAddDialog = true
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = RoyalBlue, modifier = Modifier.size(18.dp))
                            }
                            IconButton(
                                onClick = {
                                    onDeleteAvailability(slot.availabilityId)
                                    successMsg = "Availability slot deleted."
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusError, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddEditAvailabilityDialog(
    coach: UserAccountEntity,
    availability: CoachAvailabilityEntity?,
    onDismiss: () -> Unit,
    onSave: (CoachAvailabilityEntity) -> Unit
) {
    var dayOfWeek by remember { mutableStateOf(availability?.dayOfWeek ?: "Monday, Wednesday, Friday") }
    var startTime by remember { mutableStateOf(availability?.startTime ?: "06:00 PM") }
    var endTime by remember { mutableStateOf(availability?.endTime ?: "08:00 PM") }
    var location by remember { mutableStateOf(availability?.location ?: "Main Dojang - Tatami Hall A") }
    var status by remember { mutableStateOf(availability?.status ?: "AVAILABLE") }
    var notes by remember { mutableStateOf(availability?.notes ?: "Kata & Kumite sparring sessions") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = RoyalBlue)
                Text(if (availability == null) "Add Availability Slot" else "Edit Slot", fontWeight = FontWeight.Bold, color = TextNavy)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = dayOfWeek,
                    onValueChange = { dayOfWeek = it },
                    label = { Text("Days (e.g. Mon, Wed, Fri)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start Time") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End Time") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Dojo Location / Hall") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Session Focus / Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = status == "AVAILABLE",
                        onClick = { status = "AVAILABLE" },
                        label = { Text("Available", fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = StatusSuccess, selectedLabelColor = TextOnAccent)
                    )
                    FilterChip(
                        selected = status == "BUSY",
                        onClick = { status = "BUSY" },
                        label = { Text("Busy", fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = StatusWarning, selectedLabelColor = TextOnAccent)
                    )
                    FilterChip(
                        selected = status == "ON_LEAVE",
                        onClick = { status = "ON_LEAVE" },
                        label = { Text("On Leave", fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = StatusError, selectedLabelColor = TextOnAccent)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = CoachAvailabilityEntity(
                        availabilityId = availability?.availabilityId ?: "AVAIL-${UUID.randomUUID().toString().take(8)}",
                        coachId = coach.userId,
                        coachName = coach.fullName,
                        dayOfWeek = dayOfWeek.trim(),
                        startTime = startTime.trim(),
                        endTime = endTime.trim(),
                        status = status,
                        location = location.trim(),
                        notes = notes.trim()
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (availability == null) "Save Slot" else "Update Slot")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                Text("Cancel")
            }
        }
    )
}
