package com.example.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.data.CalendarEventEntity
import com.example.data.FeeItemEntity
import com.example.data.PaymentRecordEntity
import com.example.data.StudentRequestEntity
import com.example.data.TrainingProgramEntity
import com.example.data.UserAccountEntity
import com.example.data.UserRole
import com.example.ui.theme.*

@Composable
fun StudentClassScreen(
    student: UserAccountEntity,
    coachesList: List<UserAccountEntity> = emptyList(),
    batchesList: List<BatchEntity> = emptyList(),
    paymentsList: List<PaymentRecordEntity> = emptyList(),
    eventsList: List<CalendarEventEntity> = emptyList(),
    requestsList: List<StudentRequestEntity> = emptyList(),
    trainingPrograms: List<TrainingProgramEntity> = emptyList(),
    feeItemsList: List<FeeItemEntity> = emptyList(),
    onEnrollBatch: (BatchEntity) -> Unit = {},
    onEnrollSpecialTraining: (String, Double) -> Unit = { _, _ -> },
    onSubmitPaymentRef: (String, String, Double, String, String) -> Unit = { _, _, _, _, _ -> },
    onRequestChangeSubmit: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onUpdateClassAndTraining: (String, String) -> Unit = { _, _ -> },
    onSaveCalendarEvent: (CalendarEventEntity) -> Unit = {}
) {
    var selectedSubTab by remember { mutableStateOf(0) } // 0: Batch Timetable, 1: Sensei & Dojo, 2: Special Camps & Programs
    val scrollState = rememberScrollState()

    var showClassEditDialog by remember { mutableStateOf(false) }
    var showTimePermissionDialog by remember { mutableStateOf(false) }
    var viewReceiptForRecord by remember { mutableStateOf<PaymentRecordEntity?>(null) }

    val programSelectedDates = remember { mutableStateMapOf<String, String>() }
    val programSelectedTimeSlots = remember { mutableStateMapOf<String, String>() }
    val availableProgramDates = remember { mutableStateMapOf<String, String>() }

    var activeCalendarProgramKey by remember { mutableStateOf<String?>(null) }
    var activeCalendarProgramName by remember { mutableStateOf("Special Training Program") }
    var showCalendarModal by remember { mutableStateOf(false) }
    var calendarTargetPurpose by remember { mutableStateOf("request") }

    var reqProgramType by remember { mutableStateOf("1-Day Special Intensive Camp") }
    var reqTimeSlot by remember { mutableStateOf("06:00 PM – 08:00 PM (Evening Special Schedule)") }
    var reqStartDate by remember { mutableStateOf("2026-08-15") }
    var reqReason by remember { mutableStateOf("Requesting special evening training permission for grading exam preparation.") }
    var reqToastMsg by remember { mutableStateOf("") }

    val assignedCoach = coachesList.find {
        it.fullName.equals(student.coachName, ignoreCase = true)
    } ?: coachesList.firstOrNull { it.role == UserRole.COACH }

    val mySpecialPayments = paymentsList.filter {
        it.studentId == student.userId &&
        (it.feeCategory.contains("Special", ignoreCase = true) ||
         it.feeCategory.contains("Boot Camp", ignoreCase = true) ||
         it.feeCategory.contains("Masterclass", ignoreCase = true) ||
         it.feeCategory.contains("1-Day", ignoreCase = true) ||
         it.feeCategory.contains("3-Days", ignoreCase = true) ||
         it.feeCategory.contains("1-Week", ignoreCase = true))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Batch Title Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = CardWhite,
            shadowElevation = 1.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "MY ASSIGNED BATCH & SCHEDULE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                        color = RoyalBlue
                    )
                    Text(
                        text = student.batchName.ifBlank { "Regular Evening Batch" }.uppercase(),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextNavy
                    )
                    Text(
                        text = "Coach: ${student.coachName} • Batch ID: ${student.batchId}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSlate
                    )
                }

                IconButton(
                    onClick = { showClassEditDialog = true },
                    modifier = Modifier.testTag("edit_class_from_class_screen")
                ) {
                    Icon(Icons.Default.School, contentDescription = "Edit Class", tint = RoyalBlue)
                }
            }
        }

        // Clean Segmented Navigation Tabs
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = CardWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            TabRow(
                selectedTabIndex = selectedSubTab,
                containerColor = CardWhite,
                contentColor = RoyalBlue,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
                        color = RoyalBlue
                    )
                },
                divider = {}
            ) {
                Tab(
                    selected = selectedSubTab == 0,
                    onClick = { selectedSubTab = 0 },
                    text = {
                        Text(
                            "🗓️ Timetable",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (selectedSubTab == 0) FontWeight.Bold else FontWeight.Medium),
                            color = if (selectedSubTab == 0) RoyalBlue else TextSlate
                        )
                    }
                )
                Tab(
                    selected = selectedSubTab == 1,
                    onClick = { selectedSubTab = 1 },
                    text = {
                        Text(
                            "🥋 Sensei & Dojo",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (selectedSubTab == 1) FontWeight.Bold else FontWeight.Medium),
                            color = if (selectedSubTab == 1) RoyalBlue else TextSlate
                        )
                    }
                )
                Tab(
                    selected = selectedSubTab == 2,
                    onClick = { selectedSubTab = 2 },
                    text = {
                        Text(
                            "⚡ Special Camps",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (selectedSubTab == 2) FontWeight.Bold else FontWeight.Medium),
                            color = if (selectedSubTab == 2) RoyalBlue else TextSlate
                        )
                    }
                )
                Tab(
                    selected = selectedSubTab == 3,
                    onClick = { selectedSubTab = 3 },
                    text = {
                        Text(
                            "📋 All Batches (${batchesList.size})",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (selectedSubTab == 3) FontWeight.Bold else FontWeight.Medium),
                            color = if (selectedSubTab == 3) RoyalBlue else TextSlate
                        )
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            when (selectedSubTab) {
                0 -> {
                    // Sub-Tab 0: Timetable & Special Time Permissions
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Weekly Timetable Card
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = CardWhite,
                            shadowElevation = 1.dp,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "WEEKLY BATCH TIMETABLE",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TextNavy
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = StatusSuccess.copy(alpha = 0.12f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccess.copy(alpha = 0.35f))
                                    ) {
                                        Text(
                                            text = "ACTIVE BATCH",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = StatusSuccess,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                HorizontalDivider(color = BorderLight)

                                val assignedBatchObj = batchesList.find {
                                    it.batchId == student.batchId || it.batchName.equals(student.batchName, ignoreCase = true)
                                }
                                val batchScheduleText = if (assignedBatchObj != null && assignedBatchObj.startTime.isNotBlank() && assignedBatchObj.endTime.isNotBlank()) {
                                    "${assignedBatchObj.startTime} – ${assignedBatchObj.endTime}"
                                } else if (student.batchName.contains("5", ignoreCase = true) && student.batchName.contains("8", ignoreCase = true)) {
                                    "05:00 AM – 08:00 AM"
                                } else {
                                    "06:00 PM – 08:00 PM"
                                }

                                val days = listOf(
                                    Triple("Monday", batchScheduleText, "Regular Training"),
                                    Triple("Tuesday", batchScheduleText, "Regular Training"),
                                    Triple("Wednesday", batchScheduleText, "Regular Training"),
                                    Triple("Thursday", batchScheduleText, "Regular Training"),
                                    Triple("Friday", batchScheduleText, "Regular Training"),
                                    Triple("Saturday", batchScheduleText, "Regular Training"),
                                    Triple("Sunday", "Rest Day / Holiday", "Holiday")
                                )

                                days.forEach { (day, schedule, status) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = day,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = TextNavy,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = schedule,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (day == "Sunday") TextSlate else TextNavy,
                                            modifier = Modifier.weight(1.5f)
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (day == "Sunday") SecondaryBg else StatusSuccess.copy(alpha = 0.12f)
                                        ) {
                                            Text(
                                                text = status,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                                color = if (day == "Sunday") TextSlate else StatusSuccess,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    HorizontalDivider(color = BorderLight)
                                }
                            }
                        }

                        // Time Permissions Card
                        val approvedTimeRequests = requestsList.filter {
                            it.studentId == student.userId && it.status == "APPROVED"
                        }
                        val pendingTimeRequests = requestsList.filter {
                            it.studentId == student.userId && it.status == "PENDING"
                        }

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = CardWhite,
                            shadowElevation = 1.dp,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(Icons.Default.Schedule, contentDescription = null, tint = RoyalBlue)
                                        Text(
                                            text = "SPECIAL CLASS TIME PERMISSION",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = TextNavy
                                        )
                                    }

                                    if (approvedTimeRequests.isNotEmpty()) {
                                        Surface(
                                            shape = RoundedCornerShape(20.dp),
                                            color = StatusSuccess.copy(alpha = 0.12f),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccess.copy(alpha = 0.35f))
                                        ) {
                                            Text(
                                                text = "✓ APPROVED",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = StatusSuccess,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                if (approvedTimeRequests.isNotEmpty()) {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp),
                                        color = StatusSuccess.copy(alpha = 0.12f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccess.copy(alpha = 0.35f))
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text("🎉 Special Time Approved by Sensei", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = StatusSuccess)
                                            approvedTimeRequests.forEach { req ->
                                                Text("Approved Slot: ${req.requestedValue}", style = MaterialTheme.typography.bodySmall, color = TextNavy)
                                            }
                                        }
                                    }
                                } else if (pendingTimeRequests.isNotEmpty()) {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp),
                                        color = StatusWarning.copy(alpha = 0.12f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, StatusWarning.copy(alpha = 0.35f))
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text("⏳ Time Permission Request Pending Approval", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = StatusWarning)
                                            Text("Requested Slot: ${pendingTimeRequests.first().requestedValue}", style = MaterialTheme.typography.bodySmall, color = TextNavy)
                                        }
                                    }
                                } else {
                                    Text(
                                        text = "Standard schedule active. Need special evening practice or exam timing permission? Request below.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSlate
                                    )
                                }

                                Button(
                                    onClick = { showTimePermissionDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("request_special_time_permission_button")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("⏰ Request Special Class Time Permission", color = TextOnAccent)
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Sub-Tab 1: Sensei Profile & Dojo Location
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = CardWhite,
                            shadowElevation = 1.dp,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(Icons.Default.School, contentDescription = null, tint = RoyalBlue)
                                        Text(
                                            text = "ASSIGNED SENSEI / COACH",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = TextNavy
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = ActiveNavBg,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue.copy(alpha = 0.35f))
                                    ) {
                                        Text(
                                            text = assignedCoach?.currentBelt?.ifBlank { "Black Belt 4th Dan" } ?: "Black Belt 4th Dan",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = RoyalBlue,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        modifier = Modifier.size(60.dp),
                                        shape = CircleShape,
                                        color = ActiveNavBg,
                                        border = androidx.compose.foundation.BorderStroke(2.dp, RoyalBlue)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = (assignedCoach?.fullName ?: student.coachName).take(1),
                                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                                color = RoyalBlue
                                            )
                                        }
                                    }

                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = assignedCoach?.fullName ?: student.coachName,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = TextNavy
                                        )
                                        Text(
                                            text = assignedCoach?.designation?.ifBlank { "Chief Martial Arts Coach" } ?: "Chief Martial Arts Coach",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextSlate
                                        )
                                        Text(
                                            text = "🏆 ${assignedCoach?.experienceYears ?: 14} Years Experience",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                            color = StatusSuccess
                                        )
                                    }
                                }

                                HorizontalDivider(color = BorderLight)

                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Specializations:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                    Text(
                                        text = assignedCoach?.specializations?.ifBlank { "Kata, Kumite, Weapon Training (Bo, Nunchaku)" }
                                            ?: "Kata, Kumite, Weapon Training (Bo, Nunchaku)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSlate
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Sensei Bio & Philosophy:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                    Text(
                                        text = assignedCoach?.bio?.ifBlank { "Dedicated martial arts master training national champions with discipline, respect, and technical precision." }
                                            ?: "Dedicated martial arts master training national champions with discipline, respect, and technical precision.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSlate
                                    )
                                }

                                HorizontalDivider(color = BorderLight)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.Call, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(16.dp))
                                        Text(assignedCoach?.phone?.ifBlank { "+91 98765 11111" } ?: "+91 98765 11111", style = MaterialTheme.typography.bodySmall, color = TextNavy)
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.Email, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(16.dp))
                                        Text(assignedCoach?.email?.ifBlank { "sensei@broma.com" } ?: "sensei@broma.com", style = MaterialTheme.typography.bodySmall, color = TextNavy)
                                    }
                                }
                            }
                        }

                        // Dojo Location Card
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = CardWhite,
                            shadowElevation = 1.dp,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = RoyalBlue)
                                    Text("ACADEMY DOJO LOCATION & HALL", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                }

                                Text("Main Dojo: BROMA Martial Arts Academy, Hall B", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                Text("Address: Plot 42, Academy Sports Complex, Road No. 12, Banjara Hills, Hyderabad, Telangana 500034", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                            }
                        }
                    }
                }

                2 -> {
                    // Sub-Tab 2: Special Camps & Intensive Programs
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            "SPECIAL KARATE CAMPS & INTENSIVE WORKSHOPS (1-DAY, 3-DAYS & 1-WEEK)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextNavy
                        )

                        val defaultCamps = listOf(
                            Triple("1-Day Special Karate Training Camp", "₹4,999", "6 Hours Intensive • 3 Sessions (Morning 6:00 AM – 8:00 AM, Afternoon 11:00 AM – 1:00 PM, Evening 6:00 PM – 8:00 PM) • Intensive Kata, Sparring & Weapons Drills"),
                            Triple("3-Days Special Karate Training Camp", "₹9,999", "4 Hours/Day (12 Hours Total) • 2 Sessions/Day (Morning 6:00 AM – 8:00 AM & Evening 6:00 PM – 8:00 PM) • Bunkai Mastery, Kumite Drills & Conditioning"),
                            Triple("1-Week (7-Days) Special Karate Camp", "₹14,999", "4 Hours/Day (28 Hours Total) • 2 Sessions/Day (Morning 6:00 AM – 8:00 AM & Evening 6:00 PM – 8:00 PM) • Masterclass, Weapon Flow & Black Belt Prep")
                        )

                        val availableCamps: List<Triple<String, String, String>> = if (trainingPrograms.isNotEmpty()) {
                            val customList = trainingPrograms.map { prog ->
                                val fee = "₹${prog.feeAmount.takeIf { it > 0 }?.toInt() ?: prog.monthlyFee.toInt()}"
                                val desc = prog.syllabusOverview.ifBlank {
                                    "${prog.durationText} • ${prog.scheduleSummary.ifBlank { prog.description }}"
                                }
                                Triple(prog.programTitle.ifBlank { prog.programName }, fee, desc)
                            }
                            // Prioritize custom list from Admin Portal, or combined
                            if (customList.isNotEmpty()) customList else defaultCamps
                        } else {
                            defaultCamps
                        }

                        availableCamps.forEach { (cTitle, cFee, cHours) ->
                            val cDate = availableProgramDates[cTitle] ?: "2026-08-06 to 2026-08-08 (3 Days)"
                            SpecialTrainingCard(
                                title = cTitle,
                                feeText = cFee,
                                hoursText = cHours,
                                selectedStartDate = cDate,
                                onOpenCalendar = {
                                    activeCalendarProgramKey = cTitle
                                    activeCalendarProgramName = cTitle
                                    calendarTargetPurpose = "available_program"
                                    showCalendarModal = true
                                },
                                onEnroll = {
                                    val feeVal = cFee.replace("₹", "").replace(",", "").toDoubleOrNull() ?: 1800.0
                                    onEnrollSpecialTraining(cTitle, feeVal)
                                },
                                testTag = "enroll_${cTitle.lowercase().replace(" ", "_")}_button"
                            )
                        }

                        if (mySpecialPayments.isNotEmpty()) {
                            Text("ENROLLED SPECIAL PROGRAM TIMETABLES", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = StatusSuccess)

                            mySpecialPayments.forEach { rec ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = CardWhite,
                                    shadowElevation = 1.dp,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccess.copy(alpha = 0.35f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(rec.feeCategory, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                            Text("Status: ${rec.status} • Amount: ₹${rec.amount.toInt()}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                                        }

                                        Button(
                                            onClick = { viewReceiptForRecord = rec },
                                            colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.testTag("view_digital_receipt_${rec.receiptNo}")
                                        ) {
                                            Text("📄 Receipt", color = TextOnAccent)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // Sub-Tab 3: All Available Batches & Direct Timing Enrollment
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = CardWhite,
                            shadowElevation = 1.dp,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "ALL ACADEMY DOJO BATCHES",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TextNavy
                                    )
                                    Text(
                                        text = "${batchesList.size} Batches",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = RoyalBlue
                                    )
                                }
                                Text(
                                    text = "All batches updated by Admin. Review batch timings, sensei, days, and enroll according to your convenient schedule.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSlate
                                )
                            }
                        }

                        if (batchesList.isEmpty()) {
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
                                    Text("🥋", fontSize = 36.sp)
                                    Text("No Batches Scheduled Yet", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                    Text("Admin will update batch schedules soon.", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                                }
                            }
                        } else {
                            batchesList.forEach { batch ->
                                val isCurrentlyEnrolled = student.batchId == batch.batchId ||
                                        student.batchName.contains(batch.batchName, ignoreCase = true) ||
                                        student.batchName.contains(batch.batchId, ignoreCase = true)

                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    color = CardWhite,
                                    shadowElevation = 1.dp,
                                    border = androidx.compose.foundation.BorderStroke(
                                        if (isCurrentlyEnrolled) 2.dp else 1.dp,
                                        if (isCurrentlyEnrolled) RoyalBlue else BorderLight
                                    )
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
                                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                                Text(
                                                    text = batch.batchName,
                                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = TextNavy
                                                )
                                                Text(
                                                    text = "Batch ID: ${batch.batchId} • ${batch.programName}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = RoyalBlue
                                                )
                                            }

                                            if (isCurrentlyEnrolled) {
                                                Surface(
                                                    shape = RoundedCornerShape(20.dp),
                                                    color = StatusSuccess.copy(alpha = 0.12f),
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccess.copy(alpha = 0.4f))
                                                ) {
                                                    Text(
                                                        text = "✓ Current Enrolled",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                        color = StatusSuccess,
                                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                                    )
                                                }
                                            }
                                        }

                                        HorizontalDivider(color = BorderLight)

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Text("⏰ Timing: ${batch.startTime} – ${batch.endTime}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = TextNavy)
                                                Text("🗓️ Days: ${batch.activeDays}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                                            }
                                            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Text("🥋 Sensei: ${batch.coachName}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = TextNavy)
                                                Text("📍 Dojo: ${batch.location}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Registered Students: ${batch.studentCount}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextSlate
                                            )

                                            if (!isCurrentlyEnrolled) {
                                                Button(
                                                    onClick = {
                                                        onEnrollBatch(batch)
                                                        reqToastMsg = "✓ Enrolled into ${batch.batchName} successfully!"
                                                    },
                                                    shape = RoundedCornerShape(8.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                                                    modifier = Modifier.testTag("enroll_batch_btn_${batch.batchId}")
                                                ) {
                                                    Text("Enroll in this Batch", color = TextOnAccent, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                                }
                                            } else {
                                                OutlinedButton(
                                                    onClick = { },
                                                    enabled = false,
                                                    shape = RoundedCornerShape(8.dp),
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccess)
                                                ) {
                                                    Text("Active Schedule", color = StatusSuccess, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Time Permission Dialog
    if (showTimePermissionDialog) {
        AlertDialog(
            onDismissRequest = { showTimePermissionDialog = false },
            title = {
                Text(
                    "Request Class Time Permission",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextNavy
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = reqProgramType,
                        onValueChange = { reqProgramType = it },
                        label = { Text("Program / Purpose", color = TextSlate) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        )
                    )
                    OutlinedTextField(
                        value = reqTimeSlot,
                        onValueChange = { reqTimeSlot = it },
                        label = { Text("Requested Time Slot", color = TextSlate) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        )
                    )
                    OutlinedTextField(
                        value = reqReason,
                        onValueChange = { reqReason = it },
                        label = { Text("Reason for Request", color = TextSlate) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        minLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRequestChangeSubmit("Special Time Permission", "$reqProgramType ($reqTimeSlot)", reqStartDate, reqReason)
                        reqToastMsg = "✓ Permission request submitted to Sensei!"
                        showTimePermissionDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("submit_time_permission_dialog_button")
                ) {
                    Text("Submit Request", color = TextOnAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePermissionDialog = false }) { Text("Cancel", color = TextSlate) }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Class Edit Dialog
    if (showClassEditDialog) {
        var selectedBatch by remember { mutableStateOf(student.batchName) }
        var selectedPrograms by remember { mutableStateOf(student.trainingPrograms) }

        AlertDialog(
            onDismissRequest = { showClassEditDialog = false },
            title = {
                Text(
                    "Update Class & Batch",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextNavy
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = selectedBatch,
                        onValueChange = { selectedBatch = it },
                        label = { Text("Batch Name / Timing", color = TextSlate) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        )
                    )
                    OutlinedTextField(
                        value = selectedPrograms,
                        onValueChange = { selectedPrograms = it },
                        label = { Text("Special Training Programs", color = TextSlate) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateClassAndTraining(selectedBatch, selectedPrograms)
                        showClassEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save Changes", color = TextOnAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClassEditDialog = false }) { Text("Cancel", color = TextSlate) }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Calendar Modal
    if (showCalendarModal && activeCalendarProgramKey != null) {
        CalendarProgramModal(
            programType = activeCalendarProgramName,
            onDismiss = { showCalendarModal = false },
            onDateSelected = { selectedDate ->
                when (calendarTargetPurpose) {
                    "available_program" -> availableProgramDates[activeCalendarProgramKey!!] = selectedDate
                    "enrolled_program" -> programSelectedDates[activeCalendarProgramKey!!] = selectedDate
                }
                showCalendarModal = false
            }
        )
    }

    // Receipt Modal
    if (viewReceiptForRecord != null) {
        val rec = viewReceiptForRecord!!
        AlertDialog(
            onDismissRequest = { viewReceiptForRecord = null },
            title = {
                Text(
                    "DIGITAL RECEIPT",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextNavy
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Receipt No: ${rec.receiptNo}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue)
                    Text("Student: ${rec.studentName}", color = TextNavy)
                    Text("Fee Category: ${rec.feeCategory}", color = TextNavy)
                    Text("Amount: ₹${rec.amount.toInt()}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = StatusSuccess)
                    Text("Status: ${rec.status}", color = StatusSuccess)
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewReceiptForRecord = null },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Close", color = TextOnAccent)
                }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun CalendarProgramModal(
    programType: String,
    onDismiss: () -> Unit,
    onDateSelected: (String) -> Unit
) {
    // Auto-detect initial camp duration from program name
    val autoDuration = remember(programType) {
        when {
            programType.contains("1-Day", ignoreCase = true) || programType.contains("1 Day", ignoreCase = true) -> 1
            programType.contains("3-Day", ignoreCase = true) || programType.contains("3 Day", ignoreCase = true) -> 3
            programType.contains("1-Week", ignoreCase = true) || programType.contains("1 Week", ignoreCase = true) || programType.contains("7 Day", ignoreCase = true) -> 7
            programType.contains("15-Day", ignoreCase = true) || programType.contains("15 Day", ignoreCase = true) -> 15
            else -> 3
        }
    }

    var selectedDay by remember { mutableStateOf(6) }
    var selectedDurationDays by remember { mutableStateOf(autoDuration) }
    var month by remember { mutableStateOf(8) }
    var year by remember { mutableStateOf(2026) }

    val monthNames = arrayOf("", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")

    val daysInMonth = when (month) {
        2 -> if (year % 4 == 0) 29 else 28
        4, 6, 9, 11 -> 30
        else -> 31
    }

    val endDay = (selectedDay + selectedDurationDays - 1).coerceAtMost(daysInMonth)
    val startFormatted = "$year-${month.toString().padStart(2, '0')}-${selectedDay.toString().padStart(2, '0')}"
    val endFormatted = "$year-${month.toString().padStart(2, '0')}-${endDay.toString().padStart(2, '0')}"

    val formattedResult = if (selectedDurationDays == 1) {
        "$startFormatted (1-Day Training)"
    } else {
        "$startFormatted to $endFormatted ($selectedDurationDays Days Camp)"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.DateRange, contentDescription = null, tint = CrimsonPrimary)
                Text(
                    "Select Camp Schedule Dates",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextNavy
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Program: $programType",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = RoyalBlue
                )

                // Duration Selector Chips (1-Day, 3-Days, 1-Week / 7-Days, 15-Days)
                Text("Camp Duration Option:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        1 to "1 Day",
                        3 to "3 Days",
                        7 to "1 Week (7D)",
                        15 to "15 Days"
                    ).forEach { (dCount, dLabel) ->
                        val isChosen = selectedDurationDays == dCount
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isChosen) CrimsonPrimary else Color(0xFFF1F5F9),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isChosen) CrimsonPrimary else BorderLight),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedDurationDays = dCount }
                        ) {
                            Text(
                                text = dLabel,
                                fontSize = 10.sp,
                                fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                                color = if (isChosen) Color.White else TextNavy,
                                modifier = Modifier.padding(vertical = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                HorizontalDivider(color = BorderLight)

                // Month Navigation Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { if (month > 1) month-- else { month = 12; year-- } }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Prev", tint = RoyalBlue)
                    }
                    Text(
                        "${monthNames[month]} $year",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextNavy
                    )
                    IconButton(onClick = { if (month < 12) month++ else { month = 1; year++ } }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = RoyalBlue)
                    }
                }

                // Days of week header
                val daysOfWeek = listOf("M", "T", "W", "T", "F", "S", "S")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    daysOfWeek.forEach { dayLetter ->
                        Text(
                            text = dayLetter,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextSlate
                        )
                    }
                }

                // Days grid with continuous range highlighting (start day, middle span, end day)
                val dayRows = (1..daysInMonth).chunked(7)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    dayRows.forEach { rowDays ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            rowDays.forEach { d ->
                                val isStart = d == selectedDay
                                val isEnd = d == endDay
                                val isInRange = d in selectedDay..endDay

                                val (cellBg, cellText, cellBorder) = when {
                                    isStart || (isEnd && selectedDurationDays > 1) -> Triple(CrimsonPrimary, Color.White, null)
                                    isInRange -> Triple(CrimsonPrimary.copy(alpha = 0.22f), TextNavy, androidx.compose.foundation.BorderStroke(1.dp, CrimsonPrimary.copy(alpha = 0.4f)))
                                    else -> Triple(Color.Transparent, TextNavy, androidx.compose.foundation.BorderStroke(0.5.dp, BorderLight))
                                }

                                Surface(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clickable { selectedDay = d },
                                    shape = if (isStart || isEnd) CircleShape else RoundedCornerShape(4.dp),
                                    color = cellBg,
                                    border = cellBorder
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "$d",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isInRange) FontWeight.Bold else FontWeight.Normal),
                                            color = cellText
                                        )
                                    }
                                }
                            }
                            // Fill remaining spaces if row has fewer than 7 days
                            for (i in 0 until (7 - rowDays.size)) {
                                Spacer(modifier = Modifier.size(32.dp))
                            }
                        }
                    }
                }

                // Multi-Day Selected Span Highlight Box
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = StatusSuccessBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccessBorder)
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(14.dp))
                            Text("Auto-Selected $selectedDurationDays Days Range:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusSuccessText)
                        }
                        Text(
                            text = if (selectedDurationDays == 1) {
                                "Start Date: $startFormatted (1 Day Intensive Session)"
                            } else {
                                "Dates: $startFormatted to $endFormatted (Total $selectedDurationDays Days Auto-Selected: ${selectedDay..endDay})"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = TextNavy
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDateSelected(formattedResult)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("confirm_calendar_date_selection_button")
            ) {
                Text("Confirm $selectedDurationDays-Day Schedule", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSlate) }
        },
        containerColor = CardWhite,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun SpecialTrainingCard(
    title: String,
    feeText: String,
    hoursText: String,
    selectedStartDate: String = "2026-08-15",
    onOpenCalendar: () -> Unit = {},
    onEnroll: () -> Unit,
    testTag: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = CardWhite,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextNavy,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    feeText,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = StatusSuccess
                )
            }

            Text(hoursText, style = MaterialTheme.typography.bodySmall, color = TextSlate)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📅 Start Date: $selectedStartDate",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = RoyalBlue
                )

                OutlinedButton(
                    onClick = onOpenCalendar,
                    modifier = Modifier.height(32.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(12.dp), tint = RoyalBlue)
                    Spacer(Modifier.width(4.dp))
                    Text("Select Date", style = MaterialTheme.typography.labelSmall, color = RoyalBlue)
                }
            }

            Button(
                onClick = onEnroll,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(testTag),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
            ) {
                Text("Enroll Now", style = MaterialTheme.typography.labelLarge, color = TextOnAccent)
            }
        }
    }
}
