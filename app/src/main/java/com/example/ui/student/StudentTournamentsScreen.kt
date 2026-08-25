package com.example.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CalendarEventEntity
import com.example.data.TournamentEntity
import com.example.data.UserAccountEntity
import com.example.ui.common.*
import com.example.ui.theme.*

@Composable
fun StudentTournamentsScreen(
    student: UserAccountEntity,
    tournamentsList: List<TournamentEntity> = emptyList(),
    eventsList: List<CalendarEventEntity> = emptyList()
) {
    val scrollState = rememberScrollState()
    var registeredTournamentIds by remember { mutableStateOf(setOf<String>()) }
    var selectedTournamentForDetails by remember { mutableStateOf<TournamentEntity?>(null) }
    var registeringTournament by remember { mutableStateOf<TournamentEntity?>(null) }
    var registrationSuccessMessage by remember { mutableStateOf<String?>(null) }

    // Combine published tournament entities or fallback to published calendar events
    val tournaments = tournamentsList.filter { it.isPublished }.ifEmpty {
        eventsList.filter { it.category.contains("Tournament", ignoreCase = true) || it.category.contains("Competition", ignoreCase = true) }.map {
            TournamentEntity(
                tournamentId = it.eventId,
                title = it.title,
                startDate = it.startDate,
                endDate = it.endDate,
                startTime = it.time,
                venue = it.venue?.ifBlank { it.location } ?: it.location,
                city = "Hyderabad",
                organizer = "BROMA Academy",
                categoryType = "State",
                description = it.description,
                eventsCategories = it.categoriesList ?: "Kata, Kumite",
                registrationFee = it.registrationFee ?: 1000.0,
                registrationDeadline = it.startDate,
                status = "UPCOMING",
                isPublished = true
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "OFFICIAL TOURNAMENTS & CHAMPIONSHIPS",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                color = TextNavy
            )
            Text(
                text = "State, National & Inter-Dojo sanctioned Karate championships",
                style = MaterialTheme.typography.bodySmall,
                color = TextSlate
            )
        }

        if (registrationSuccessMessage != null) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = StatusSuccessBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccessBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("✓", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = StatusSuccessText)
                    Text(
                        text = registrationSuccessMessage!!,
                        color = StatusSuccessText,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        if (tournaments.isEmpty()) {
            EmptyStateCard(
                title = "No Upcoming Tournaments",
                description = "Currently there are no active tournaments open for registration. Check back soon for announcements!"
            )
        } else {
            tournaments.forEach { t ->
                val isRegistered = registeredTournamentIds.contains(t.tournamentId)

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
                        // Category Pill & Status Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ActiveNavBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, StatusInfoBorder)
                            ) {
                                Text(
                                    text = t.categoryType.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                    color = RoyalBlue,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            if (isRegistered) {
                                StatusBadge(status = "REGISTERED")
                            } else {
                                StatusBadge(status = t.status)
                            }
                        }

                        // Title
                        Text(
                            text = t.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextNavy
                        )

                        // Date & Time
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(16.dp))
                            Text(
                                text = "${t.startDate}${if (!t.endDate.isNullOrBlank() && t.endDate != t.startDate) " to ${t.endDate}" else ""} • ${t.startTime}${if (!t.endTime.isNullOrBlank()) " – ${t.endTime}" else ""}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = TextNavy
                            )
                        }

                        // Venue
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = StatusWarning, modifier = Modifier.size(16.dp))
                            Text(
                                text = "${t.venue}, ${t.city}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSlate
                            )
                        }

                        // Description
                        if (t.description.isNotBlank()) {
                            Text(
                                text = t.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSlate
                            )
                        }

                        // Categories / Events
                        if (t.eventsCategories.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SecondaryBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text("EVENTS & CATEGORIES", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), color = TextSlate)
                                    Text(t.eventsCategories, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = TextNavy)
                                }
                            }
                        }

                        // Fee & Deadline
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("ENTRY FEE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextSlate)
                                Text(
                                    text = if (t.registrationFee > 0) "₹${t.registrationFee.toInt()}" else "Free Entry",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (t.registrationFee > 0) RoyalBlue else StatusSuccessText
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("DEADLINE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextSlate)
                                Text(
                                    text = t.registrationDeadline,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = StatusWarningText
                                )
                            }
                        }

                        HorizontalDivider(color = BorderLight)

                        // Registration Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { selectedTournamentForDetails = t },
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = CardWhite, contentColor = TextNavy)
                            ) {
                                Text("View Rules", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                            }

                            Button(
                                onClick = {
                                    if (!isRegistered) {
                                        registeringTournament = t
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isRegistered) StatusSuccess else RoyalBlue,
                                    contentColor = TextOnAccent
                                ),
                                enabled = !isRegistered && t.status != "COMPLETED" && t.status != "CANCELLED"
                            ) {
                                Text(
                                    text = if (isRegistered) "✓ REGISTERED" else "REGISTER NOW",
                                    fontWeight = FontWeight.Bold,
                                    color = TextOnAccent
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Registration Modal
    if (registeringTournament != null) {
        val t = registeringTournament!!
        var regName by remember { mutableStateOf(student.fullName) }
        var regAadhar by remember { mutableStateOf("") }
        var regAge by remember { mutableStateOf("14") }
        var regCategory by remember { mutableStateOf("Kata Individual") }
        var regWeight by remember { mutableStateOf("48 kg") }
        var regPhone by remember { mutableStateOf(student.phone) }

        val categoryList = listOf("Kata Individual", "Kumite Sparring", "Team Kata", "Weapon Kata", "Cadet Kumite", "Junior Kata")

        AlertDialog(
            onDismissRequest = { registeringTournament = null },
            title = {
                Text(
                    text = "Tournament Athlete Registration",
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
                    Text("Tournament: ${t.title}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue)
                    Text("Entry Fee: ₹${t.registrationFee.toInt()} • Venue: ${t.venue}", style = MaterialTheme.typography.labelSmall, color = TextSlate)

                    OutlinedTextField(
                        value = regName,
                        onValueChange = { regName = it },
                        label = { Text("Athlete Full Name", color = TextSlate) },
                        modifier = Modifier.fillMaxWidth().testTag("tournament_reg_name"),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RoyalBlue, unfocusedBorderColor = BorderLight, focusedTextColor = TextNavy, unfocusedTextColor = TextNavy)
                    )

                    OutlinedTextField(
                        value = regAadhar,
                        onValueChange = { regAadhar = it },
                        label = { Text("Aadhar Number", color = TextSlate) },
                        placeholder = { Text("XXXX-XXXX-XXXX", color = TextSlate) },
                        modifier = Modifier.fillMaxWidth().testTag("tournament_reg_aadhar"),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RoyalBlue, unfocusedBorderColor = BorderLight, focusedTextColor = TextNavy, unfocusedTextColor = TextNavy)
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = regAge,
                            onValueChange = { regAge = it },
                            label = { Text("Age (Yrs)", color = TextSlate) },
                            modifier = Modifier.weight(1f).testTag("tournament_reg_age"),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RoyalBlue, unfocusedBorderColor = BorderLight, focusedTextColor = TextNavy, unfocusedTextColor = TextNavy)
                        )

                        OutlinedTextField(
                            value = regWeight,
                            onValueChange = { regWeight = it },
                            label = { Text("Weight (kg)", color = TextSlate) },
                            placeholder = { Text("e.g. 52 kg", color = TextSlate) },
                            modifier = Modifier.weight(1f).testTag("tournament_reg_weight"),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RoyalBlue, unfocusedBorderColor = BorderLight, focusedTextColor = TextNavy, unfocusedTextColor = TextNavy)
                        )
                    }

                    Text("Competition Category:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        categoryList.chunked(2).forEach { rowList ->
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                rowList.forEach { cat ->
                                    val isSel = regCategory == cat
                                    Surface(
                                        modifier = Modifier.clickable { regCategory = cat },
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSel) RoyalBlue else SecondaryBg,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) RoyalBlue else BorderLight)
                                    ) {
                                        Text(
                                            text = cat,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal),
                                            color = if (isSel) TextOnAccent else TextNavy,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = regPhone,
                        onValueChange = { regPhone = it },
                        label = { Text("Contact Phone", color = TextSlate) },
                        modifier = Modifier.fillMaxWidth().testTag("tournament_reg_phone"),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RoyalBlue, unfocusedBorderColor = BorderLight, focusedTextColor = TextNavy, unfocusedTextColor = TextNavy)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        registeredTournamentIds = registeredTournamentIds + t.tournamentId
                        registrationSuccessMessage = "✓ Successfully registered $regName (Aadhar: $regAadhar, Age: $regAge, $regCategory, $regWeight) for '${t.title}'!"
                        registeringTournament = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("submit_tournament_registration_btn")
                ) {
                    Text("Submit Registration", color = TextOnAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { registeringTournament = null }) { Text("Cancel", color = TextSlate) }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Tournament Details Modal
    if (selectedTournamentForDetails != null) {
        val t = selectedTournamentForDetails!!
        AlertDialog(
            onDismissRequest = { selectedTournamentForDetails = null },
            title = {
                Text(
                    text = t.title,
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
                    Text("Organizer: ${t.organizer}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = RoyalBlue)
                    Text("Venue: ${t.venue}, ${t.city}", style = MaterialTheme.typography.bodySmall, color = TextNavy)
                    Text("Eligibility: ${t.eligibility}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                    if (t.importantInstructions.isNotBlank()) {
                        Text("Important Instructions:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                        Text(t.importantInstructions, style = MaterialTheme.typography.bodySmall, color = TextSlate)
                    }
                    if (!t.contactPerson.isNullOrBlank()) {
                        Text("Contact Person: ${t.contactPerson} (${t.contactPhone ?: ""})", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedTournamentForDetails = null },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue, contentColor = TextOnAccent)
                ) {
                    Text("Close", color = TextOnAccent)
                }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
