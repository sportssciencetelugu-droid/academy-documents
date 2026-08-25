package com.example.ui.coach

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BatchEntity
import com.example.data.UserAccountEntity
import com.example.ui.common.AttendanceDonutChart
import com.example.ui.common.BatchAttendanceComparisonChart
import com.example.ui.theme.*

@Composable
fun CoachAttendanceCentreScreen(
    studentsList: List<UserAccountEntity>,
    batchesList: List<BatchEntity>,
    onSaveAttendance: (String, String, Map<String, String>, String) -> Unit
) {
    var selectedBatchId by remember { mutableStateOf(batchesList.firstOrNull()?.batchId ?: "BROM-B2") }
    var attendanceDate by remember { mutableStateOf("2026-08-12") }
    var timeSlot by remember { mutableStateOf("06:00 PM – 08:00 PM") }
    var saveSuccess by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf("ALL") }
    var showAnalyticsGraph by remember { mutableStateOf(false) }

    // Map of studentId -> Status ("PRESENT", "ABSENT", "LATE", "LEAVE")
    val attendanceMap = remember { mutableStateMapOf<String, String>() }
    // Map of studentId -> Optional Note/Reason
    val studentNotesMap = remember { mutableStateMapOf<String, String>() }

    // Initialize default status to PRESENT
    LaunchedEffect(studentsList) {
        studentsList.forEach { student ->
            if (!attendanceMap.containsKey(student.userId)) {
                attendanceMap[student.userId] = "PRESENT"
            }
        }
    }

    val scrollState = rememberScrollState()

    // Filter students by search and batch/status
    val filteredStudents = studentsList.filter { student ->
        val matchesSearch = student.fullName.contains(searchQuery, ignoreCase = true) || 
                          student.userId.contains(searchQuery, ignoreCase = true)
        val matchesStatus = when (statusFilter) {
            "ALL" -> true
            else -> attendanceMap[student.userId] == statusFilter
        }
        matchesSearch && matchesStatus
    }

    // Real-Time Attendance Metrics
    val livePresent = attendanceMap.values.count { it == "PRESENT" }
    val liveAbsent = attendanceMap.values.count { it == "ABSENT" }
    val liveLate = attendanceMap.values.count { it == "LATE" }
    val liveLeave = attendanceMap.values.count { it == "LEAVE" }
    val totalStudents = studentsList.size
    val presentPercentage = if (totalStudents > 0) ((livePresent.toFloat() / totalStudents) * 100).toInt() else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // --- SCREEN TITLE HEADER ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ATTENDANCE REGISTER TABLE",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = GoldSecondary
                )
                Text(
                    text = "Table format for 1-tap individual attendance marking",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = BeltGreen.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, BeltGreen.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BeltGreen, modifier = Modifier.size(14.dp))
                    Text("$presentPercentage% Present", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = BeltGreen)
                }
            }
        }

        // --- TOP SUMMARY COUNTER ROW (5 COLUMNS TABLE METRICS) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MetricSummaryPill(title = "TOTAL", count = "$totalStudents", color = Color.White, modifier = Modifier.weight(1f))
            MetricSummaryPill(title = "PRESENT (P)", count = "$livePresent", color = BeltGreen, modifier = Modifier.weight(1f))
            MetricSummaryPill(title = "ABSENT (A)", count = "$liveAbsent", color = CrimsonPrimary, modifier = Modifier.weight(1f))
            MetricSummaryPill(title = "LATE (LT)", count = "$liveLate", color = BeltOrange, modifier = Modifier.weight(1f))
            MetricSummaryPill(title = "LEAVE (L)", count = "$liveLeave", color = BeltYellow, modifier = Modifier.weight(1f))
        }

        // --- SESSION & BATCH CONTROLS ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.DarkGray)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Session Schedule & Batch Selector:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = GoldSecondary)

                // Batch Selection Pills
                val batchOptions = if (batchesList.isNotEmpty()) batchesList else listOf(
                    BatchEntity(batchId = "BROM-B1", batchName = "Batch B1 (Morning)", programName = "Regular Karate", coachId = "COACH-01", coachName = "Sensei Rajesh", location = "Main Dojo", startTime = "06:00 AM", endTime = "08:00 AM", activeDays = "Mon,Wed,Fri", studentCount = 25),
                    BatchEntity(batchId = "BROM-B2", batchName = "Batch B2 (Evening)", programName = "Regular Karate", coachId = "COACH-01", coachName = "Sensei Rajesh", location = "Main Dojo", startTime = "06:00 PM", endTime = "08:00 PM", activeDays = "Tue,Thu,Sat", studentCount = 25),
                    BatchEntity(batchId = "BROM-CAMP", batchName = "Special Intensive Camp", programName = "Special Training", coachId = "COACH-01", coachName = "Sensei Rajesh", location = "Main Dojo Arena", startTime = "06:00 AM", endTime = "09:00 AM", activeDays = "Daily", studentCount = 30)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    batchOptions.forEach { b ->
                        val isSelected = selectedBatchId == b.batchId
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { 
                                    selectedBatchId = b.batchId
                                    timeSlot = "${b.startTime} – ${b.endTime}"
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) CrimsonPrimary else DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) GoldSecondary else Color.Gray.copy(alpha = 0.3f))
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(b.batchName, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("${b.startTime}-${b.endTime}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = if (isSelected) GoldSecondary else Color.Gray)
                            }
                        }
                    }
                }

                // Inline Config Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = selectedBatchId,
                        onValueChange = { selectedBatchId = it },
                        label = { Text("Batch ID", fontSize = 10.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("coach_batch_id_input"),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodySmall
                    )

                    OutlinedTextField(
                        value = attendanceDate,
                        onValueChange = { attendanceDate = it },
                        label = { Text("Date (YYYY-MM-DD)", fontSize = 10.sp) },
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("coach_attendance_date_input"),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodySmall
                    )

                    OutlinedTextField(
                        value = timeSlot,
                        onValueChange = { timeSlot = it },
                        label = { Text("Time Slot", fontSize = 10.sp) },
                        modifier = Modifier.weight(1.5f),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                }

                // Batch Actions Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = {
                                studentsList.forEach { attendanceMap[it.userId] = "PRESENT" }
                                saveSuccess = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BeltGreen),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("all_present_button")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("All Present", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                studentsList.forEach { attendanceMap[it.userId] = "ABSENT" }
                                saveSuccess = false
                            },
                            border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonPrimary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = CrimsonPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("All Absent", fontSize = 11.sp, color = CrimsonPrimary, fontWeight = FontWeight.Bold)
                        }

                        IconButton(
                            onClick = {
                                studentsList.forEach { attendanceMap[it.userId] = "PRESENT" }
                                studentNotesMap.clear()
                                saveSuccess = false
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = GoldSecondary, modifier = Modifier.size(18.dp))
                        }
                    }

                    // Toggle Button for Visual Analytics
                    TextButton(
                        onClick = { showAnalyticsGraph = !showAnalyticsGraph },
                        contentPadding = PaddingValues(horizontal = 6.dp)
                    ) {
                        Icon(
                            imageVector = if (showAnalyticsGraph) Icons.Default.ExpandLess else Icons.Default.Analytics,
                            contentDescription = null,
                            tint = GoldSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (showAnalyticsGraph) "Hide Charts" else "Show Analytics", fontSize = 11.sp, color = GoldSecondary)
                    }
                }
            }
        }

        // --- EXPANDABLE ANALYTICS SECTION ---
        AnimatedVisibility(visible = showAnalyticsGraph) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AttendanceDonutChart(
                    present = livePresent,
                    absent = liveAbsent,
                    leave = liveLeave,
                    late = liveLate
                )

                BatchAttendanceComparisonChart(
                    batchData = listOf(
                        Triple("Batch B1 (Morning)", 92, BeltGreen),
                        Triple("Batch B2 (Evening)", if (totalStudents > 0) presentPercentage else 85, GoldSecondary),
                        Triple("Batch B3 (Weekend)", 78, BeltOrange),
                        Triple("Special Camp", 95, CrimsonPrimary)
                    ),
                    title = "📊 BATCH ATTENDANCE SUMMARY (%)"
                )
            }
        }

        // --- SEARCH & FILTER BAR ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search ID or Name...", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldSecondary, modifier = Modifier.size(16.dp)) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("coach_attendance_search"),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall
            )

            ScrollableTabRow(
                selectedTabIndex = listOf("ALL", "PRESENT", "ABSENT", "LATE", "LEAVE").indexOf(statusFilter),
                containerColor = DarkSurface,
                contentColor = GoldSecondary,
                edgePadding = 0.dp,
                modifier = Modifier
                    .width(210.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                listOf("ALL", "PRESENT", "ABSENT", "LATE", "LEAVE").forEach { st ->
                    Tab(
                        selected = statusFilter == st,
                        onClick = { statusFilter = st },
                        text = {
                            Text(
                                text = when(st) {
                                    "PRESENT" -> "P"
                                    "ABSENT" -> "A"
                                    "LATE" -> "LT"
                                    "LEAVE" -> "L"
                                    else -> "ALL"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                }
            }
        }

        // --- MAIN ATTENDANCE TABLE UI ---
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(12.dp),
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldSecondary.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // TABLE HEADER ROW
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Black,
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "STUDENT ID & NAME",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                            color = GoldSecondary,
                            modifier = Modifier.weight(1.2f)
                        )
                        Text(
                            text = "BELT",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                            color = GoldSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(60.dp)
                        )
                        Text(
                            text = "ATTENDANCE STATUS [ P | A | L | LT ]",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                            color = GoldSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1.6f)
                        )
                    }
                }

                HorizontalDivider(color = GoldSecondary.copy(alpha = 0.3f))

                // TABLE DATA ROWS
                Box(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
                    Column {
                        if (filteredStudents.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "No students matching '$searchQuery' or filter '$statusFilter'",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        } else {
                            filteredStudents.forEachIndexed { index, student ->
                                val currentStatus = attendanceMap[student.userId] ?: "PRESENT"
                                val currentNote = studentNotesMap[student.userId] ?: ""
                                val isEvenRow = index % 2 == 0
                                val beltColor = getBeltColor(student.currentBelt)

                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = if (isEvenRow) DarkSurface else DarkSurfaceVariant,
                                    border = androidx.compose.foundation.BorderStroke(
                                        0.5.dp,
                                        when (currentStatus) {
                                            "PRESENT" -> BeltGreen.copy(alpha = 0.2f)
                                            "ABSENT" -> CrimsonPrimary.copy(alpha = 0.2f)
                                            "LATE" -> BeltOrange.copy(alpha = 0.2f)
                                            else -> BeltYellow.copy(alpha = 0.2f)
                                        }
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // COLUMN 1: STUDENT ID, AVATAR & NAME
                                            Row(
                                                modifier = Modifier.weight(1.2f),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                // Initial Circle Avatar
                                                Surface(
                                                    modifier = Modifier.size(28.dp),
                                                    shape = CircleShape,
                                                    color = beltColor.copy(alpha = 0.2f),
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, beltColor)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text(
                                                            text = student.fullName.take(1).uppercase(),
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color.White
                                                        )
                                                    }
                                                }

                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = student.fullName,
                                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                        color = Color.White,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Text(
                                                        text = "ID: ${student.userId}",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                        color = GoldSecondary
                                                    )
                                                }
                                            }

                                            // COLUMN 2: BELT RANK BADGE
                                            Box(
                                                modifier = Modifier.width(60.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = beltColor.copy(alpha = 0.2f),
                                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, beltColor)
                                                ) {
                                                    Text(
                                                        text = student.currentBelt.take(6),
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                                        color = if (student.currentBelt.contains("White", ignoreCase = true)) Color.White else beltColor,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                                        maxLines = 1
                                                    )
                                                }
                                            }

                                            // COLUMN 3: SEGMENTED TABLE ATTENDANCE BUTTON MATRIX [ P | A | L | LT ]
                                            Row(
                                                modifier = Modifier.weight(1.6f),
                                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                TableStatusButton(
                                                    label = "P",
                                                    fullText = "Present",
                                                    statusKey = "PRESENT",
                                                    currentStatus = currentStatus,
                                                    activeColor = BeltGreen,
                                                    testTag = "status_${student.userId}_PRESENT",
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    attendanceMap[student.userId] = "PRESENT"
                                                    saveSuccess = false
                                                }

                                                TableStatusButton(
                                                    label = "A",
                                                    fullText = "Absent",
                                                    statusKey = "ABSENT",
                                                    currentStatus = currentStatus,
                                                    activeColor = CrimsonPrimary,
                                                    testTag = "status_${student.userId}_ABSENT",
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    attendanceMap[student.userId] = "ABSENT"
                                                    saveSuccess = false
                                                }

                                                TableStatusButton(
                                                    label = "L",
                                                    fullText = "Leave",
                                                    statusKey = "LEAVE",
                                                    currentStatus = currentStatus,
                                                    activeColor = BeltYellow,
                                                    testTag = "status_${student.userId}_LEAVE",
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    attendanceMap[student.userId] = "LEAVE"
                                                    saveSuccess = false
                                                }

                                                TableStatusButton(
                                                    label = "LT",
                                                    fullText = "Late",
                                                    statusKey = "LATE",
                                                    currentStatus = currentStatus,
                                                    activeColor = BeltOrange,
                                                    testTag = "status_${student.userId}_LATE",
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    attendanceMap[student.userId] = "LATE"
                                                    saveSuccess = false
                                                }
                                            }
                                        }

                                        // OPTIONAL INLINE REASON FIELD FOR ABSENT / LEAVE / LATE
                                        if (currentStatus != "PRESENT") {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = null,
                                                    tint = Color.Gray,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                OutlinedTextField(
                                                    value = currentNote,
                                                    onValueChange = { studentNotesMap[student.userId] = it },
                                                    placeholder = { Text("Reason for $currentStatus (Optional)...", fontSize = 10.sp) },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    singleLine = true,
                                                    textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp)
                                                )
                                            }
                                        }
                                    }
                                }
                                HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.3f))
                            }
                        }
                    }
                }
            }
        }

        // --- SUCCESS TOAST FEEDBACK ---
        if (saveSuccess) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = BeltGreen.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, BeltGreen)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = BeltGreen)
                    Text(
                        "✓ Attendance for $totalStudents students saved and updated reliably in Student & Admin Portals!",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = BeltGreen
                    )
                }
            }
        }

        // --- SAVE & SYNC ATTENDANCE ACTION BUTTON ---
        Button(
            onClick = {
                onSaveAttendance(selectedBatchId, attendanceDate, attendanceMap.toMap(), timeSlot)
                saveSuccess = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("save_attendance_button")
        ) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "SAVE & SYNC ATTENDANCE REGISTER",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun MetricSummaryPill(
    title: String,
    count: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold), color = color, maxLines = 1)
            Text(count, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold), color = Color.White)
        }
    }
}

@Composable
private fun TableStatusButton(
    label: String,
    fullText: String,
    statusKey: String,
    currentStatus: String,
    activeColor: Color,
    testTag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isSelected = currentStatus == statusKey

    Surface(
        modifier = modifier
            .height(28.dp)
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(6.dp),
        color = if (isSelected) activeColor else DarkBackground,
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 1.dp else 0.5.dp,
            if (isSelected) Color.White else activeColor.copy(alpha = 0.4f)
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 2.dp)
        ) {
            Text(
                text = if (isSelected) "✓ $label" else label,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isSelected) Color.Black else activeColor
            )
        }
    }
}

private fun getBeltColor(belt: String): Color {
    return when {
        belt.contains("Yellow", ignoreCase = true) -> BeltYellow
        belt.contains("Orange", ignoreCase = true) -> BeltOrange
        belt.contains("Green", ignoreCase = true) -> BeltGreen
        belt.contains("Blue", ignoreCase = true) -> BeltBlue
        belt.contains("Purple", ignoreCase = true) -> BeltPurple
        belt.contains("Brown", ignoreCase = true) -> BeltBrown
        belt.contains("Black", ignoreCase = true) -> BeltBlack
        else -> BeltWhite
    }
}
