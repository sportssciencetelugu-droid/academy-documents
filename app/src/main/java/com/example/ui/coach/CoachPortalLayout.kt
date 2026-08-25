package com.example.ui.coach

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.common.BromaAcademyLogo
import com.example.ui.common.BromaPrimaryButton
import com.example.ui.common.BromaSecondaryButton
import com.example.ui.student.DrawerMenuItem
import com.example.ui.student.DrawerSectionHeader
import com.example.ui.student.OptInTopAppBar
import com.example.ui.student.StudentTournamentsScreen
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun CoachPortalLayout(
    coach: UserAccountEntity,
    studentsList: List<UserAccountEntity>,
    batchesList: List<BatchEntity>,
    requestsList: List<StudentRequestEntity>,
    paymentsList: List<PaymentRecordEntity> = emptyList(),
    eventsList: List<CalendarEventEntity> = emptyList(),
    announcementsList: List<AnnouncementEntity> = emptyList(),
    tournamentsList: List<TournamentEntity> = emptyList(),
    availabilitiesList: List<CoachAvailabilityEntity> = emptyList(),
    chatMessages: List<ChatMessageEntity> = emptyList(),
    onSaveAttendance: (String, String, Map<String, String>, String) -> Unit,
    onProcessRequest: (StudentRequestEntity, Boolean) -> Unit,
    onGrantSpecialTimePermission: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onSaveCoachProfile: (UserAccountEntity) -> Unit = {},
    onSaveAvailability: (CoachAvailabilityEntity) -> Unit = {},
    onDeleteAvailability: (String) -> Unit = {},
    onSendMessageToAdmin: (String) -> Unit = {},
    onLogout: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentTab by remember { mutableStateOf("dashboard") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = CardWhite,
                modifier = Modifier.width(300.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = SecondaryBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            BromaAcademyLogo(size = 44.dp, showBorder = true, borderColor = BorderLight)
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    coach.fullName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextNavy
                                )
                                Text(
                                    coach.designation,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = RoyalBlue
                                )
                                Text(
                                    "🥋 ${coach.currentBelt}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = StatusSuccess
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = BorderLight)

                    DrawerSectionHeader("COACH PORTAL")
                    DrawerMenuItem("🏠 Dashboard", Icons.Default.Home, currentTab == "dashboard") {
                        currentTab = "dashboard"
                        scope.launch { drawerState.close() }
                    }
                    DrawerMenuItem("👤 Coach Profile", Icons.Default.AccountCircle, currentTab == "profile") {
                        currentTab = "profile"
                        scope.launch { drawerState.close() }
                    }
                    DrawerMenuItem("👥 My Batches", Icons.Default.Group, currentTab == "batches") {
                        currentTab = "batches"
                        scope.launch { drawerState.close() }
                    }
                    DrawerMenuItem("✅ Take Attendance", Icons.Default.CheckCircle, currentTab == "attendance") {
                        currentTab = "attendance"
                        scope.launch { drawerState.close() }
                    }
                    DrawerMenuItem("🗓️ My Availability & Schedule", Icons.Default.Schedule, currentTab == "availability") {
                        currentTab = "availability"
                        scope.launch { drawerState.close() }
                    }
                    DrawerMenuItem("🏆 Tournaments", Icons.Default.EmojiEvents, currentTab == "tournaments") {
                        currentTab = "tournaments"
                        scope.launch { drawerState.close() }
                    }
                    DrawerMenuItem("💬 Chat with Admin", Icons.Default.Chat, currentTab == "chat") {
                        currentTab = "chat"
                        scope.launch { drawerState.close() }
                    }
                    DrawerMenuItem("📩 Student Requests", Icons.Default.PendingActions, currentTab == "requests") {
                        currentTab = "requests"
                        scope.launch { drawerState.close() }
                    }

                    HorizontalDivider(color = BorderLight)
                    DrawerMenuItem("🚪 Logout", Icons.Default.ExitToApp, false) {
                        scope.launch { drawerState.close() }
                        onLogout()
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                OptInTopAppBar(
                    title = when (currentTab) {
                        "dashboard" -> "COACH DASHBOARD"
                        "profile" -> "SENSEI PROFILE"
                        "batches" -> "MY ASSIGNED BATCHES"
                        "attendance" -> "ATTENDANCE CENTRE"
                        "availability" -> "MY AVAILABILITY & SLOTS"
                        "tournaments" -> "ACADEMY TOURNAMENTS"
                        "chat" -> "CHAT WITH ADMIN"
                        "requests" -> "STUDENT REQUESTS"
                        else -> "COACH PORTAL"
                    },
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    "dashboard" -> {
                        CoachDashboardView(
                            coach = coach,
                            studentsList = studentsList,
                            batchesList = batchesList,
                            requestsList = requestsList,
                            paymentsList = paymentsList,
                            eventsList = eventsList,
                            announcementsList = announcementsList,
                            onNavigateToTab = { tab -> currentTab = tab }
                        )
                    }
                    "profile" -> CoachProfileScreen(coach = coach)
                    "batches" -> {
                        CoachBatchesView(
                            coach = coach,
                            batchesList = batchesList,
                            studentsList = studentsList,
                            onNavigateToAttendance = { batchId -> currentTab = "attendance" }
                        )
                    }
                    "attendance" -> CoachAttendanceCentreScreen(
                        studentsList = studentsList,
                        batchesList = batchesList,
                        onSaveAttendance = onSaveAttendance
                    )
                    "availability" -> CoachAvailabilityManagementView(
                        coach = coach,
                        availabilities = availabilitiesList,
                        onSaveAvailability = onSaveAvailability,
                        onDeleteAvailability = onDeleteAvailability
                    )
                    "tournaments" -> StudentTournamentsScreen(
                        student = coach,
                        tournamentsList = tournamentsList,
                        eventsList = eventsList
                    )
                    "chat" -> CoachChatScreen(
                        coach = coach,
                        chatMessages = chatMessages,
                        onSendMessage = onSendMessageToAdmin
                    )
                    "requests" -> {
                        CoachStudentRequestsView(
                            coach = coach,
                            studentsList = studentsList,
                            requestsList = requestsList,
                            onProcessRequest = onProcessRequest,
                            onGrantSpecialTimePermission = onGrantSpecialTimePermission
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CoachDashboardView(
    coach: UserAccountEntity,
    studentsList: List<UserAccountEntity>,
    batchesList: List<BatchEntity>,
    requestsList: List<StudentRequestEntity>,
    paymentsList: List<PaymentRecordEntity>,
    eventsList: List<CalendarEventEntity>,
    announcementsList: List<AnnouncementEntity>,
    onNavigateToTab: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    val pendingRequestsCount = requestsList.count { it.status == "PENDING" }
    val verifiedPaymentsCount = paymentsList.count { it.status == "PAID" }
    val nextEvent = eventsList.firstOrNull()
    val latestAnnouncement = announcementsList.lastOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header Banner
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
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Good day, Sensei ${coach.fullName.split(" ").lastOrNull() ?: coach.fullName}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextNavy
                    )
                    Text(
                        text = "Chief Martial Arts Coach • BROMA Academy",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = RoyalBlue
                    )
                    Text(
                        text = "Coach ID: ${coach.userId} • Batch: ${coach.batchId}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSlate
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = ActiveNavBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue.copy(alpha = 0.3f))
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsMartialArts,
                        contentDescription = null,
                        tint = RoyalBlue,
                        modifier = Modifier
                            .padding(12.dp)
                            .size(28.dp)
                    )
                }
            }
        }

        // 2x2 Grid for Coach Belt & Assigned Batches Card
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Coach Belt / Rank Card
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToTab("profile") }
                    .testTag("coach_dashboard_belt_card"),
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
                        Text("Coach Rank", style = MaterialTheme.typography.labelMedium, color = TextSlate)
                        Text("🥋", fontSize = 18.sp)
                    }

                    Text(
                        text = coach.currentBelt,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextNavy
                    )

                    Text(
                        text = "Joining: ${coach.joiningDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSlate
                    )
                }
            }

            // Assigned Batches Card
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToTab("batches") }
                    .testTag("coach_dashboard_batch_card"),
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
                        Text("My Batches", style = MaterialTheme.typography.labelMedium, color = TextSlate)
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(18.dp))
                    }

                    Text(
                        text = "${batchesList.size} Active Batches",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextNavy
                    )

                    Text(
                        text = "${studentsList.size} Students Enrolled",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSlate
                    )
                }
            }
        }

        // Attendance Status & Centre Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToTab("attendance") }
                .testTag("coach_dashboard_attendance_card"),
            shape = RoundedCornerShape(16.dp),
            color = CardWhite,
            shadowElevation = 1.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusSuccess)
                        Text(
                            "Attendance Status & Centre",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextNavy
                        )
                    }
                    Text(
                        text = "95%",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = StatusSuccess
                    )
                }

                LinearProgressIndicator(
                    progress = { 0.95f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = StatusSuccess,
                    trackColor = SecondaryBg
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Students: ${studentsList.size}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = TextNavy)
                    Text("Batches: ${batchesList.size}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = RoyalBlue)
                    Text("Pending Req: $pendingRequestsCount", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = StatusWarning)
                }
            }
        }

        // Student Fee Verification Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToTab("students") }
                .testTag("coach_dashboard_fees_card"),
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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        shape = CircleShape,
                        color = StatusSuccess.copy(alpha = 0.12f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = StatusSuccess,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Column {
                        Text("Student Fee Verification & Receipts", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                        Text(
                            "$verifiedPaymentsCount Paid & Verified Receipts",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextNavy
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = StatusSuccess.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccess.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = "VERIFIED ✓",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = StatusSuccess,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Next Class Session & Next Event Side-by-Side Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Next Class Card
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToTab("batches") },
                shape = RoundedCornerShape(16.dp),
                color = CardWhite,
                shadowElevation = 1.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        "NEXT CLASS SESSION",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                        color = TextSlate
                    )
                    Text("TODAY", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                    Text("06:00 PM – 08:00 PM", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                    Text(
                        batchesList.firstOrNull()?.batchName ?: "Regular Karate",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = RoyalBlue
                    )
                }
            }

            // Next Event Card
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToTab("students") },
                shape = RoundedCornerShape(16.dp),
                color = CardWhite,
                shadowElevation = 1.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        "NEXT EVENT",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                        color = TextSlate
                    )
                    Text(
                        nextEvent?.title ?: "State Championship",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextNavy,
                        maxLines = 1
                    )
                    Text(nextEvent?.startDate ?: "24 August", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                    Text(
                        "Tournament",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = StatusWarning
                    )
                }
            }
        }

        // Announcement Banner
        if (latestAnnouncement != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToTab("requests") },
                shape = RoundedCornerShape(16.dp),
                color = CardWhite,
                shadowElevation = 1.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ActiveNavBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null, tint = RoyalBlue)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "LATEST ACADEMY ANNOUNCEMENT",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = RoyalBlue
                        )
                        Text(
                            latestAnnouncement.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextNavy
                        )
                        Text(
                            latestAnnouncement.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSlate,
                            maxLines = 2
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSlate)
                }
            }
        }

        // Quick Actions
        BromaPrimaryButton(
            text = "Take Class Attendance",
            onClick = { onNavigateToTab("attendance") },
            icon = Icons.Default.CheckCircle,
            modifier = Modifier.fillMaxWidth(),
            testTagStr = "coach_dashboard_attendance_button"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BromaSecondaryButton(
                text = "Roster (${studentsList.size})",
                onClick = { onNavigateToTab("students") },
                icon = Icons.Default.School,
                modifier = Modifier.weight(1f)
            )

            BromaSecondaryButton(
                text = "Requests ($pendingRequestsCount)",
                onClick = { onNavigateToTab("requests") },
                icon = Icons.Default.PendingActions,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun CoachBatchesView(
    coach: UserAccountEntity,
    batchesList: List<BatchEntity>,
    studentsList: List<UserAccountEntity> = emptyList(),
    onNavigateToAttendance: (String) -> Unit = {}
) {
    var filterMode by remember { mutableStateOf("MY_BATCHES") } // "MY_BATCHES" or "ALL_BATCHES"
    var expandedBatchId by remember { mutableStateOf<String?>(null) }

    val coachBatches = batchesList.filter { batch ->
        batch.batchId.equals(coach.batchId, ignoreCase = true) ||
        batch.batchName.contains(coach.fullName.split(" ").lastOrNull() ?: "", ignoreCase = true) ||
        coach.batchId.isBlank() || coach.batchId.equals("ALL", ignoreCase = true)
    }

    val displayBatches = if (filterMode == "MY_BATCHES" && coachBatches.isNotEmpty()) coachBatches else batchesList

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = CardWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "MY ASSIGNED TRAINING BATCHES",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextNavy
                        )
                        Text(
                            "Assigned to Sensei ${coach.fullName} • ${displayBatches.size} Batches",
                            style = MaterialTheme.typography.bodySmall,
                            color = RoyalBlue
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ActiveNavBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue.copy(alpha = 0.4f))
                    ) {
                        Text(
                            "DOJO: ${coach.dojoCenter.ifBlank { "Main Dojang" }}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = RoyalBlue
                        )
                    }
                }

                // Filter Buttons
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { filterMode = "MY_BATCHES" }
                            .testTag("filter_my_batches"),
                        shape = RoundedCornerShape(8.dp),
                        color = if (filterMode == "MY_BATCHES") RoyalBlue else SecondaryBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (filterMode == "MY_BATCHES") RoyalBlue else BorderLight)
                    ) {
                        Text(
                            "🥋 My Assigned Batches (${coachBatches.size})",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (filterMode == "MY_BATCHES") TextOnAccent else TextNavy,
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { filterMode = "ALL_BATCHES" }
                            .testTag("filter_all_batches"),
                        shape = RoundedCornerShape(8.dp),
                        color = if (filterMode == "ALL_BATCHES") RoyalBlue else SecondaryBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (filterMode == "ALL_BATCHES") RoyalBlue else BorderLight)
                    ) {
                        Text(
                            "🏢 All Academy Batches (${batchesList.size})",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (filterMode == "ALL_BATCHES") TextOnAccent else TextNavy,
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }

        displayBatches.forEach { batch ->
            val batchStudents = studentsList.filter { it.batchId.equals(batch.batchId, ignoreCase = true) || (batch.batchId == "BROM-B2" && it.batchId.isBlank()) }
            val isExpanded = expandedBatchId == batch.batchId

            Surface(
                modifier = Modifier.fillMaxWidth().testTag("batch_card_${batch.batchId}"),
                shape = RoundedCornerShape(14.dp),
                color = CardWhite,
                shadowElevation = 1.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                batch.batchName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextNavy
                            )
                            Text("Batch Code: ${batch.batchId} • ${batch.programName}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = RoyalBlue)
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = StatusSuccessBg
                        ) {
                            Text(
                                "${batchStudents.size.coerceAtLeast(batch.studentCount)} Enrolled",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = StatusSuccessText
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = SecondaryBg
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("🕒 Schedule: ${batch.startTime} – ${batch.endTime}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = TextNavy)
                                Text("📍 ${batch.location}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                            }
                            Text("🗓️ Training Days: Mon, Wed, Fri & Sat (Regular & Special Sessions)", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                        }
                    }

                    // Expand / Collapse Students Roster
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                expandedBatchId = if (isExpanded) null else batch.batchId
                            }
                        ) {
                            Icon(if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null, tint = RoyalBlue)
                            Spacer(Modifier.width(4.dp))
                            Text(if (isExpanded) "Hide Enrolled Students (${batchStudents.size})" else "View Enrolled Students (${batchStudents.size})", color = RoyalBlue, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onNavigateToAttendance(batch.batchId) },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp), tint = TextOnAccent)
                            Spacer(Modifier.width(4.dp))
                            Text("Take Attendance", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextOnAccent)
                        }
                    }

                    if (isExpanded) {
                        HorizontalDivider(color = BorderLight)
                        Text("ENROLLED STUDENTS IN THIS BATCH:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextSlate)

                        if (batchStudents.isEmpty()) {
                            Text("No students currently assigned to this batch.", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                        } else {
                            batchStudents.forEach { st ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    color = SecondaryBg,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(st.fullName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                            Text("ID: ${st.userId} • Belt: ${st.currentBelt}", style = MaterialTheme.typography.labelSmall, color = RoyalBlue)
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = StatusSuccessBg
                                        ) {
                                            Text("Active", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = StatusSuccessText)
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

@Composable
fun CoachStudentsView(
    coach: UserAccountEntity? = null,
    studentsList: List<UserAccountEntity>,
    paymentsList: List<PaymentRecordEntity> = emptyList(),
    eventsList: List<CalendarEventEntity> = emptyList(),
    onGrantSpecialTimePermission: (String, String, String, String) -> Unit = { _, _, _, _ -> }
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedReceiptForModal by remember { mutableStateOf<PaymentRecordEntity?>(null) }
    var permissionTargetStudent by remember { mutableStateOf<UserAccountEntity?>(null) }
    var selectedTimeSlot by remember { mutableStateOf("06:00 PM – 08:00 PM (Evening Special)") }
    var permissionReason by remember { mutableStateOf("Granted special extra practice permission") }
    var toastMsg by remember { mutableStateOf("") }

    val timeSlotsList = listOf(
        "06:00 PM – 08:00 PM (Evening Special)",
        "06:00 AM – 08:00 AM (Morning Permission)",
        "08:00 PM – 09:30 PM (Special Night Practice)",
        "04:00 PM – 06:00 PM (Weekend Extra Session)",
        "Custom Extra Time Session"
    )

    val filtered = studentsList.filter { it.fullName.contains(searchQuery, ignoreCase = true) || it.userId.contains(searchQuery, ignoreCase = true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "STUDENT ROSTER & STATUS",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextNavy
            )
            Button(
                onClick = { permissionTargetStudent = studentsList.firstOrNull() },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                modifier = Modifier.testTag("coach_grant_time_permission_top_button"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Grant Time", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
            }
        }

        if (toastMsg.isNotBlank()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = StatusSuccess.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccess.copy(alpha = 0.35f))
            ) {
                Text(toastMsg, color = StatusSuccess, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(10.dp))
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Student Name or ID...", color = TextSlate) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("coach_student_search_input"),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CardWhite,
                unfocusedContainerColor = CardWhite,
                focusedBorderColor = RoyalBlue,
                unfocusedBorderColor = BorderLight,
                focusedTextColor = TextNavy,
                unfocusedTextColor = TextNavy
            )
        )

        filtered.forEach { student ->
            val stPayments = paymentsList.filter { it.studentId == student.userId }
            val verifiedPayment = stPayments.find { it.status == "PAID" }
            val pendingPayment = stPayments.find { it.status == "VERIFICATION_PENDING" }
            val stBookedEvents = eventsList.filter { it.title.contains(student.fullName, ignoreCase = true) || it.description.contains(student.fullName, ignoreCase = true) }

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
                        Column {
                            Text(
                                student.fullName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextNavy
                            )
                            Text(
                                "ID: ${student.userId} • Batch: ${student.batchId}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSlate
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ActiveNavBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue.copy(alpha = 0.3f))
                        ) {
                            Text(
                                student.currentBelt,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = RoyalBlue,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = BorderLight)

                    Text(
                        "🥋 Enrolled Programs: ${student.trainingPrograms}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = TextNavy
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = when {
                                    verifiedPayment != null -> "✓ Fee Verified & Paid"
                                    pendingPayment != null -> "⏳ Payment Verification Pending"
                                    else -> "⚠️ Fee Payment Due / Unverified"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = when {
                                    verifiedPayment != null -> StatusSuccess
                                    pendingPayment != null -> StatusWarning
                                    else -> StatusError
                                }
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = { permissionTargetStudent = student },
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("grant_perm_${student.userId}")
                            ) {
                                Text("⏰ Permission", style = MaterialTheme.typography.labelSmall, color = RoyalBlue)
                            }

                            if (verifiedPayment != null) {
                                Button(
                                    onClick = { selectedReceiptForModal = verifiedPayment },
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("coach_view_receipt_${student.userId}")
                                ) {
                                    Text("📄 Receipt", style = MaterialTheme.typography.labelSmall, color = TextOnAccent)
                                }
                            }
                        }
                    }

                    if (stBookedEvents.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SecondaryBg,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    "📅 Booked Training Date & Time Slot:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextNavy
                                )
                                stBookedEvents.forEach { evt ->
                                    Text(
                                        "• ${evt.startDate} | ${evt.time} (${evt.location})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSlate
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Grant Time Permission Dialog for Coach
    if (permissionTargetStudent != null) {
        val targetSt = permissionTargetStudent!!
        AlertDialog(
            onDismissRequest = { permissionTargetStudent = null },
            title = {
                Text(
                    "GRANT SPECIAL TRAINING TIME PERMISSION",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextNavy
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Student: ${targetSt.fullName} (${targetSt.userId})",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextNavy
                    )
                    Text(
                        "Select Special Training Time Slot:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = TextSlate
                    )

                    timeSlotsList.forEach { slot ->
                        val isSelected = selectedTimeSlot == slot
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedTimeSlot = slot },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) ActiveNavBg else CardWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) RoyalBlue else BorderLight)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    slot,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isSelected) RoyalBlue else TextNavy
                                )
                                if (isSelected) Text("✓ Selected", style = MaterialTheme.typography.labelSmall, color = RoyalBlue)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = permissionReason,
                        onValueChange = { permissionReason = it },
                        label = { Text("Permission Reason / Note", color = TextSlate) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onGrantSpecialTimePermission(
                            targetSt.userId,
                            selectedTimeSlot,
                            permissionReason,
                            coach?.fullName ?: "Sensei Coach"
                        )
                        toastMsg = "✓ Special Training Time Permission Granted to ${targetSt.fullName} for $selectedTimeSlot!"
                        permissionTargetStudent = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    modifier = Modifier.testTag("submit_coach_grant_permission_button")
                ) {
                    Text("Grant Permission", color = TextOnAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { permissionTargetStudent = null }) {
                    Text("Cancel", color = TextSlate)
                }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Digital Receipt Modal Dialog for Coach
    if (selectedReceiptForModal != null) {
        val r = selectedReceiptForModal!!
        AlertDialog(
            onDismissRequest = { selectedReceiptForModal = null },
            confirmButton = {
                Button(
                    onClick = { selectedReceiptForModal = null },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
                ) {
                    Text("Close Receipt", color = TextOnAccent)
                }
            },
            title = {
                Text(
                    "BROMA MARTIAL ARTS RECEIPT",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextNavy
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Receipt No: ${r.receiptNo}", fontWeight = FontWeight.Bold, color = RoyalBlue)
                    Text("Student: ${r.studentName} (${r.studentId})", color = TextNavy)
                    Text("Fee Category: ${r.feeCategory}", color = TextSlate)
                    Text("Amount Paid: ₹${r.amount.toInt()}", fontWeight = FontWeight.Bold, color = StatusSuccess)
                    Text("UTR Ref: ${r.transactionRef}", color = RoyalBlue)
                    Text("Date: ${r.paymentDate}", color = TextSlate)
                    Text("Status: APPROVED & VERIFIED BY ADMIN", fontWeight = FontWeight.Bold, color = StatusSuccess)
                }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun CoachStudentRequestsView(
    coach: UserAccountEntity? = null,
    studentsList: List<UserAccountEntity> = emptyList(),
    requestsList: List<StudentRequestEntity>,
    onProcessRequest: (StudentRequestEntity, Boolean) -> Unit,
    onGrantSpecialTimePermission: (String, String, String, String) -> Unit = { _, _, _, _ -> }
) {
    var showGrantDialog by remember { mutableStateOf(false) }
    var selectedStudentForPerm by remember { mutableStateOf<UserAccountEntity?>(studentsList.firstOrNull()) }
    var timeSlotVal by remember { mutableStateOf("06:00 PM – 08:00 PM (Evening Special)") }
    var permReasonVal by remember { mutableStateOf("Granted by Sensei during coach review") }
    var successToast by remember { mutableStateOf("") }

    val timeSlotsList = listOf(
        "06:00 PM – 08:00 PM (Evening Special)",
        "06:00 AM – 08:00 AM (Morning Permission)",
        "08:00 PM – 09:30 PM (Special Night Practice)",
        "04:00 PM – 06:00 PM (Weekend Extra Session)"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "STUDENT REQUESTS & PERMISSIONS",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextNavy
            )
            Button(
                onClick = {
                    selectedStudentForPerm = studentsList.firstOrNull()
                    showGrantDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                modifier = Modifier.testTag("coach_issue_permission_button"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Grant Time", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
            }
        }

        if (successToast.isNotBlank()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = StatusSuccess.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccess.copy(alpha = 0.35f))
            ) {
                Text(successToast, color = StatusSuccess, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(10.dp))
            }
        }

        val pendingList = requestsList.filter { it.status == "PENDING" }
        if (pendingList.isEmpty()) {
            Text("No pending requests to review.", color = TextSlate)
        } else {
            pendingList.forEach { req ->
                val isTimePermissionReq = req.requestType.contains("Special Training", ignoreCase = true) || req.requestType.contains("Permission", ignoreCase = true) || req.requestType.contains("Time", ignoreCase = true)

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = CardWhite,
                    shadowElevation = 1.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Student: ${req.studentName} (${req.studentId})",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextNavy
                            )
                            if (isTimePermissionReq) {
                                Surface(shape = RoundedCornerShape(8.dp), color = ActiveNavBg) {
                                    Text(
                                        "⏰ TIME PERMISSION",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = RoyalBlue,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Text("Type: ${req.requestType}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = RoyalBlue)
                        Text("Current: ${req.currentValue} -> Requested: ${req.requestedValue}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                        Text("Reason: ${req.reason}", style = MaterialTheme.typography.bodySmall, color = TextSlate)

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onProcessRequest(req, true) },
                                colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("approve_request_${req.requestId}")
                            ) {
                                Text("Approve & Grant", color = TextOnAccent)
                            }

                            Button(
                                onClick = { onProcessRequest(req, false) },
                                colors = ButtonDefaults.buttonColors(containerColor = StatusError),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("reject_request_${req.requestId}")
                            ) {
                                Text("Reject", color = TextOnAccent)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showGrantDialog) {
        AlertDialog(
            onDismissRequest = { showGrantDialog = false },
            title = {
                Text(
                    "GRANT SPECIAL TIME PERMISSION",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextNavy
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select Student:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = TextSlate)
                    studentsList.forEach { st ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedStudentForPerm = st },
                            shape = RoundedCornerShape(6.dp),
                            color = if (selectedStudentForPerm?.userId == st.userId) ActiveNavBg else CardWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedStudentForPerm?.userId == st.userId) RoyalBlue else BorderLight)
                        ) {
                            Text(
                                "${st.fullName} (${st.userId})",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (selectedStudentForPerm?.userId == st.userId) RoyalBlue else TextNavy,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    Text("Special Training Time Slot:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = TextSlate)
                    timeSlotsList.forEach { slot ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { timeSlotVal = slot },
                            shape = RoundedCornerShape(6.dp),
                            color = if (timeSlotVal == slot) ActiveNavBg else CardWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (timeSlotVal == slot) RoyalBlue else BorderLight)
                        ) {
                            Text(
                                slot,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (timeSlotVal == slot) RoyalBlue else TextNavy,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = permReasonVal,
                        onValueChange = { permReasonVal = it },
                        label = { Text("Permission Reason", color = TextSlate) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val st = selectedStudentForPerm
                        if (st != null) {
                            onGrantSpecialTimePermission(
                                st.userId,
                                timeSlotVal,
                                permReasonVal,
                                coach?.fullName ?: "Sensei Coach"
                            )
                            successToast = "✓ Special Training Permission Granted to ${st.fullName}!"
                        }
                        showGrantDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
                ) {
                    Text("Grant Permission", color = TextOnAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showGrantDialog = false }) { Text("Cancel", color = TextSlate) }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
