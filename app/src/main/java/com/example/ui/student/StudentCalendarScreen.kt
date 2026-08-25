package com.example.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CalendarEventEntity
import com.example.data.UserAccountEntity
import com.example.ui.theme.*

@Composable
fun StudentCalendarScreen(
    eventsList: List<CalendarEventEntity>,
    student: UserAccountEntity? = null,
    onRegisterEvent: ((CalendarEventEntity, Map<String, String>) -> Unit)? = null
) {
    var selectedCategory by remember { mutableStateOf("ALL") }
    var selectedEventForModal by remember { mutableStateOf<CalendarEventEntity?>(null) }

    val categories = listOf("ALL", "Tournament", "Seminar", "Belt Examination", "Ceremony", "Holiday", "Special Training")
    val scrollState = rememberScrollState()

    val filteredList = if (selectedCategory == "ALL") eventsList else eventsList.filter { it.category == selectedCategory }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Column {
            Text("BROMA ACADEMY CALENDAR 2026", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = TextNavy)
            Text("Annual Tournament, Seminar & Belt Examination Schedule", style = MaterialTheme.typography.bodySmall, color = TextSlate)
        }

        // Filter Chips
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = CardWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
                containerColor = CardWhite,
                contentColor = RoyalBlue,
                edgePadding = 8.dp,
                divider = {},
                indicator = { tabPositions ->
                    val index = categories.indexOf(selectedCategory).coerceAtLeast(0)
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[index]),
                        color = RoyalBlue
                    )
                }
            ) {
                categories.forEach { cat ->
                    Tab(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        text = {
                            Text(
                                cat,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Medium),
                                color = if (selectedCategory == cat) RoyalBlue else TextSlate
                            )
                        },
                        modifier = Modifier.testTag("calendar_cat_tab_$cat")
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (filteredList.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = CardWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                    ) {
                        Text("No scheduled events in category '$selectedCategory'.", style = MaterialTheme.typography.bodyMedium, color = TextSlate, modifier = Modifier.padding(16.dp))
                    }
                } else {
                    filteredList.forEach { evt ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedEventForModal = evt }
                                .testTag("event_card_${evt.eventId}"),
                            shape = RoundedCornerShape(16.dp),
                            color = CardWhite,
                            shadowElevation = 1.dp,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = ActiveNavBg,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue.copy(alpha = 0.35f))
                                    ) {
                                        Text(
                                            text = evt.category.uppercase(),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = RoyalBlue,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }

                                    Text("${evt.startDate} • ${evt.time}", style = MaterialTheme.typography.labelMedium, color = TextSlate)
                                }

                                Text(evt.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                Text("📍 ${evt.location}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                                Text(evt.description, style = MaterialTheme.typography.bodySmall, color = TextSlate, maxLines = 2)

                                if (evt.isRegistrationEnabled) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Entry Fee: ₹${evt.registrationFee.toInt()}", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = StatusSuccess)
                                        Text("Deadline: ${evt.registrationDeadline ?: "Open"}", style = MaterialTheme.typography.labelSmall, color = StatusError)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Event Detail / Registration Modal
    if (selectedEventForModal != null) {
        val evt = selectedEventForModal!!
        var showRegistrationForm by remember { mutableStateOf(false) }
        var registered by remember { mutableStateOf(false) }

        // Registration form fields
        var participantName by remember { mutableStateOf(student?.fullName ?: "") }
        var contactPhone by remember { mutableStateOf(student?.phone ?: "") }
        var beltGrade by remember { mutableStateOf(student?.currentBelt ?: "Yellow Belt (7th Kyu)") }
        var eventDivision by remember { mutableStateOf("Kata Individual") }
        var ageCategory by remember { mutableStateOf("14 Yrs") }
        var weightCategory by remember { mutableStateOf("45-50 kg") }
        var emergencyPhone by remember { mutableStateOf("+91 98765 00000") }
        var paymentRef by remember { mutableStateOf("UPI-EVENT-${(1000..9999).random()}") }

        val divisionOptions = listOf("Kata Individual", "Kumite Sparring", "Team Kata", "Weapon Form", "Self Defence Demo")

        AlertDialog(
            onDismissRequest = { selectedEventForModal = null },
            icon = { Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = RoyalBlue) },
            title = { Text(evt.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (!showRegistrationForm && !registered) {
                        Text("Category: ${evt.category}", style = MaterialTheme.typography.labelSmall, color = RoyalBlue)
                        Text("📅 Date & Time: ${evt.startDate} (${evt.time})", style = MaterialTheme.typography.bodyMedium, color = TextNavy)
                        Text("📍 Location: ${evt.location}", style = MaterialTheme.typography.bodyMedium, color = TextNavy)
                        Text("📝 Description: ${evt.description}", style = MaterialTheme.typography.bodySmall, color = TextSlate)

                        if (evt.categoriesList != null) {
                            Text("🥋 Offered Categories: ${evt.categoriesList}", style = MaterialTheme.typography.bodySmall, color = RoyalBlue)
                        }

                        if (evt.isRegistrationEnabled) {
                            Surface(
                                color = SecondaryBg,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Entry / Registration Fee: ₹${evt.registrationFee.toInt()}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = StatusSuccess)
                                    Text("Deadline: ${evt.registrationDeadline ?: "Open until event day"}", style = MaterialTheme.typography.bodySmall, color = StatusError)
                                    Text("Official certificate and medal eligibility upon registration.", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                                }
                            }
                        }
                    } else if (showRegistrationForm && !registered) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = ActiveNavBg,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "📋 EVENT REGISTRATION FORM\nPlease provide the required participant information below:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = RoyalBlue,
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        OutlinedTextField(
                            value = participantName,
                            onValueChange = { participantName = it },
                            label = { Text("Participant Full Name *") },
                            modifier = Modifier.fillMaxWidth().testTag("reg_participant_name_input"),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = contactPhone,
                                onValueChange = { contactPhone = it },
                                label = { Text("Contact / WhatsApp *") },
                                modifier = Modifier.weight(1f).testTag("reg_phone_input"),
                                shape = RoundedCornerShape(8.dp)
                            )
                            OutlinedTextField(
                                value = ageCategory,
                                onValueChange = { ageCategory = it },
                                label = { Text("Age / Div") },
                                modifier = Modifier.width(100.dp),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = beltGrade,
                                onValueChange = { beltGrade = it },
                                label = { Text("Current Belt Grade") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            OutlinedTextField(
                                value = weightCategory,
                                onValueChange = { weightCategory = it },
                                label = { Text("Weight (kg)") },
                                modifier = Modifier.width(110.dp),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        Text("Select Event Division / Category:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            divisionOptions.chunked(2).forEach { rowOpts ->
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    rowOpts.forEach { opt ->
                                        val isSel = eventDivision == opt
                                        Surface(
                                            modifier = Modifier.clickable { eventDivision = opt },
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (isSel) RoyalBlue else SecondaryBg,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) RoyalBlue else BorderLight)
                                        ) {
                                            Text(
                                                text = opt,
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
                            value = emergencyPhone,
                            onValueChange = { emergencyPhone = it },
                            label = { Text("Emergency Contact Number") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )

                        OutlinedTextField(
                            value = paymentRef,
                            onValueChange = { paymentRef = it },
                            label = { Text("Fee Payment / UPI Reference (₹${evt.registrationFee.toInt()})") },
                            modifier = Modifier.fillMaxWidth().testTag("reg_payment_ref_input"),
                            shape = RoundedCornerShape(8.dp)
                        )
                    } else {
                        // Registration Confirmed Card
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = StatusSuccessBg,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccessBorder)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusSuccess)
                                    Text("REGISTRATION CONFIRMED!", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = StatusSuccessText)
                                }
                                Text("Participant: $participantName", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                Text("Division: $eventDivision • Belt: $beltGrade", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                                Text("Txn Ref: $paymentRef", style = MaterialTheme.typography.labelSmall, color = StatusSuccessText)
                                Text("Your spot has been registered with BROMA Academy Office!", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (evt.isRegistrationEnabled && !showRegistrationForm && !registered) {
                    Button(
                        onClick = { showRegistrationForm = true },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("event_register_button")
                    ) {
                        Text("Register For Event", color = TextOnAccent)
                    }
                } else if (showRegistrationForm && !registered) {
                    Button(
                        onClick = {
                            if (participantName.isNotBlank() && contactPhone.isNotBlank()) {
                                registered = true
                                onRegisterEvent?.invoke(
                                    evt,
                                    mapOf(
                                        "name" to participantName,
                                        "phone" to contactPhone,
                                        "belt" to beltGrade,
                                        "division" to eventDivision,
                                        "age" to ageCategory,
                                        "weight" to weightCategory,
                                        "emergency" to emergencyPhone,
                                        "paymentRef" to paymentRef
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("event_submit_registration_button"),
                        enabled = participantName.isNotBlank() && contactPhone.isNotBlank()
                    ) {
                        Text("Submit Registration", color = TextOnAccent)
                    }
                } else {
                    Button(
                        onClick = { selectedEventForModal = null },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Close", color = TextOnAccent)
                    }
                }
            },
            dismissButton = {
                if (showRegistrationForm && !registered) {
                    TextButton(onClick = { showRegistrationForm = false }) { Text("Back", color = TextSlate) }
                } else if (!registered) {
                    TextButton(onClick = { selectedEventForModal = null }) { Text("Cancel", color = TextSlate) }
                }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
