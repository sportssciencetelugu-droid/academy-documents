package com.example.ui.student

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.*
import com.example.ui.common.BromaAcademyLogo
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentPortalLayout(
    student: UserAccountEntity,
    siblingList: List<UserAccountEntity>,
    coachesList: List<UserAccountEntity> = emptyList(),
    batchesList: List<BatchEntity> = emptyList(),
    allStudentsList: List<UserAccountEntity> = emptyList(),
    attendanceList: List<AttendanceRecordEntity>,
    paymentsList: List<PaymentRecordEntity>,
    feeItemsList: List<FeeItemEntity>,
    certificatesList: List<CertificateEntity>,
    eventsList: List<CalendarEventEntity>,
    announcementsList: List<AnnouncementEntity>,
    requestsList: List<StudentRequestEntity>,
    adminSettings: AdminSettingsEntity?,
    tournamentsList: List<TournamentEntity> = emptyList(),
    leadershipList: List<AcademyLeadershipEntity> = emptyList(),
    standardsList: List<AcademyStandardEntity> = emptyList(),
    chatMessages: List<ChatMessageEntity> = emptyList(),
    trainingPrograms: List<TrainingProgramEntity> = emptyList(),
    onChildSwitch: (UserAccountEntity) -> Unit,
    onSavePersonalDetails: (String, String, String, String) -> Unit,
    onRequestChangeSubmit: (String, String, String, String) -> Unit,
    onEnrollBatch: (BatchEntity) -> Unit = {},
    onEnrollSpecialTraining: (String, Double) -> Unit,
    onSubmitPaymentRef: (String, String, Double, String, String) -> Unit,
    onSubmitCertificate: (String, String, String, String) -> Unit,
    onUpdateClassAndTraining: (String, String) -> Unit = { _, _ -> },
    onSaveCalendarEvent: (CalendarEventEntity) -> Unit = {},
    onSaveStudentProfile: ((UserAccountEntity) -> Unit)? = null,
    onSendMessageToAdmin: (String) -> Unit = {},
    onLogout: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentTab by remember { mutableStateOf("dashboard") }

    // Urgent Announcement Popup check
    val urgentAnnouncement = announcementsList.find { it.priority == "Urgent" && it.isPopupOnLaunch }
    var showUrgentPopup by remember { mutableStateOf(urgentAnnouncement != null) }

    if (showUrgentPopup && urgentAnnouncement != null) {
        UrgentAnnouncementDialog(
            announcement = urgentAnnouncement,
            onDismiss = { showUrgentPopup = false },
            onViewDetails = {
                showUrgentPopup = false
                currentTab = "announcements"
            }
        )
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 600.dp

        if (isWideScreen) {
            // DESKTOP / TABLET WIDE LAYOUT (Left Permanent Sidebar)
            Row(modifier = Modifier.fillMaxSize().background(LightGrayBg)) {
                // Left Sidebar
                Surface(
                    modifier = Modifier
                        .width(260.dp)
                        .fillMaxHeight(),
                    color = CardWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                ) {
                    StudentSidebarContent(
                        student = student,
                        siblingList = siblingList,
                        currentTab = currentTab,
                        onSelectTab = { currentTab = it },
                        onChildSwitch = onChildSwitch,
                        onLogout = onLogout
                    )
                }

                // Main Content View
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    StudentPortalMainContent(
                        currentTab = currentTab,
                        student = student,
                        siblingList = siblingList,
                        coachesList = coachesList,
                        batchesList = batchesList,
                        allStudentsList = allStudentsList,
                        attendanceList = attendanceList,
                        paymentsList = paymentsList,
                        feeItemsList = feeItemsList,
                        certificatesList = certificatesList,
                        eventsList = eventsList,
                        announcementsList = announcementsList,
                        requestsList = requestsList,
                        adminSettings = adminSettings,
                        tournamentsList = tournamentsList,
                        leadershipList = leadershipList,
                        standardsList = standardsList,
                        chatMessages = chatMessages,
                        trainingPrograms = trainingPrograms,
                        onChildSwitch = onChildSwitch,
                        onSavePersonalDetails = onSavePersonalDetails,
                        onRequestChangeSubmit = onRequestChangeSubmit,
                        onEnrollBatch = onEnrollBatch,
                        onEnrollSpecialTraining = onEnrollSpecialTraining,
                        onSubmitPaymentRef = onSubmitPaymentRef,
                        onSubmitCertificate = onSubmitCertificate,
                        onUpdateClassAndTraining = onUpdateClassAndTraining,
                        onSaveCalendarEvent = onSaveCalendarEvent,
                        onSaveStudentProfile = onSaveStudentProfile,
                        onSendMessageToAdmin = onSendMessageToAdmin,
                        onLogout = onLogout,
                        onNavigateTab = { currentTab = it }
                    )
                }
            }
        } else {
            // MOBILE COMPACT LAYOUT
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        drawerContainerColor = CardWhite,
                        modifier = Modifier.width(280.dp)
                    ) {
                        StudentSidebarContent(
                            student = student,
                            siblingList = siblingList,
                            currentTab = currentTab,
                            onSelectTab = {
                                currentTab = it
                                scope.launch { drawerState.close() }
                            },
                            onChildSwitch = onChildSwitch,
                            onLogout = {
                                scope.launch { drawerState.close() }
                                onLogout()
                            }
                        )
                    }
                }
            ) {
                Scaffold(
                    topBar = {
                        OptInTopAppBar(
                            title = getStudentTabTitle(currentTab),
                            onOpenDrawer = { scope.launch { drawerState.open() } }
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = CardWhite,
                            contentColor = TextNavy
                        ) {
                            NavigationBarItem(
                                selected = currentTab == "dashboard",
                                onClick = { currentTab = "dashboard" },
                                icon = { Icon(Icons.Default.Home, contentDescription = null) },
                                label = { Text("Home", fontSize = 11.sp, fontWeight = if (currentTab == "dashboard") FontWeight.Bold else FontWeight.Normal) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = RoyalBlue,
                                    selectedTextColor = RoyalBlue,
                                    unselectedIconColor = TextSlate,
                                    unselectedTextColor = TextSlate,
                                    indicatorColor = ActiveNavBg
                                )
                            )
                            NavigationBarItem(
                                selected = currentTab == "class",
                                onClick = { currentTab = "class" },
                                icon = { Icon(Icons.Default.FitnessCenter, contentDescription = null) },
                                label = { Text("My Batch", fontSize = 11.sp, fontWeight = if (currentTab == "class") FontWeight.Bold else FontWeight.Normal) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = RoyalBlue,
                                    selectedTextColor = RoyalBlue,
                                    unselectedIconColor = TextSlate,
                                    unselectedTextColor = TextSlate,
                                    indicatorColor = ActiveNavBg
                                )
                            )
                            NavigationBarItem(
                                selected = currentTab == "attendance",
                                onClick = { currentTab = "attendance" },
                                icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                                label = { Text("Attendance", fontSize = 11.sp, fontWeight = if (currentTab == "attendance") FontWeight.Bold else FontWeight.Normal) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = RoyalBlue,
                                    selectedTextColor = RoyalBlue,
                                    unselectedIconColor = TextSlate,
                                    unselectedTextColor = TextSlate,
                                    indicatorColor = ActiveNavBg
                                )
                            )
                            NavigationBarItem(
                                selected = currentTab == "fees",
                                onClick = { currentTab = "fees" },
                                icon = { Icon(Icons.Default.Payments, contentDescription = null) },
                                label = { Text("Fees", fontSize = 11.sp, fontWeight = if (currentTab == "fees") FontWeight.Bold else FontWeight.Normal) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = RoyalBlue,
                                    selectedTextColor = RoyalBlue,
                                    unselectedIconColor = TextSlate,
                                    unselectedTextColor = TextSlate,
                                    indicatorColor = ActiveNavBg
                                )
                            )
                            NavigationBarItem(
                                selected = currentTab == "settings",
                                onClick = { scope.launch { drawerState.open() } },
                                icon = { Icon(Icons.Default.Menu, contentDescription = null) },
                                label = { Text("More", fontSize = 11.sp, fontWeight = FontWeight.Normal) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = RoyalBlue,
                                    selectedTextColor = RoyalBlue,
                                    unselectedIconColor = TextSlate,
                                    unselectedTextColor = TextSlate,
                                    indicatorColor = ActiveNavBg
                                )
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        StudentPortalMainContent(
                            currentTab = currentTab,
                            student = student,
                            siblingList = siblingList,
                            coachesList = coachesList,
                            batchesList = batchesList,
                            allStudentsList = allStudentsList,
                            attendanceList = attendanceList,
                            paymentsList = paymentsList,
                            feeItemsList = feeItemsList,
                            certificatesList = certificatesList,
                            eventsList = eventsList,
                            announcementsList = announcementsList,
                            requestsList = requestsList,
                            adminSettings = adminSettings,
                            tournamentsList = tournamentsList,
                            leadershipList = leadershipList,
                            standardsList = standardsList,
                            chatMessages = chatMessages,
                            trainingPrograms = trainingPrograms,
                            onChildSwitch = onChildSwitch,
                            onSavePersonalDetails = onSavePersonalDetails,
                            onRequestChangeSubmit = onRequestChangeSubmit,
                            onEnrollBatch = onEnrollBatch,
                            onEnrollSpecialTraining = onEnrollSpecialTraining,
                            onSubmitPaymentRef = onSubmitPaymentRef,
                            onSubmitCertificate = onSubmitCertificate,
                            onUpdateClassAndTraining = onUpdateClassAndTraining,
                            onSaveCalendarEvent = onSaveCalendarEvent,
                            onSaveStudentProfile = onSaveStudentProfile,
                            onSendMessageToAdmin = onSendMessageToAdmin,
                            onLogout = onLogout,
                            onNavigateTab = { currentTab = it }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StudentSidebarContent(
    student: UserAccountEntity,
    siblingList: List<UserAccountEntity>,
    currentTab: String,
    onSelectTab: (String) -> Unit,
    onChildSwitch: (UserAccountEntity) -> Unit,
    onLogout: () -> Unit
) {
    var showChildDropdown by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Header Logo & Student Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = SecondaryBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BromaAcademyLogo(size = 36.dp, showBorder = true, borderColor = BorderLight)
                    Text(
                        "BROMA ACADEMY",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                        color = DeepNavy
                    )
                }

                Text(
                    text = student.fullName.uppercase(),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextNavy
                )

                Text(
                    text = "ID: ${student.userId} • ${student.currentBelt}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = RoyalBlue
                )

                if (siblingList.size > 1) {
                    Box {
                        OutlinedButton(
                            onClick = { showChildDropdown = !showChildDropdown },
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("switch_child_dropdown_button")
                        ) {
                            Text("Switch Child (${siblingList.size})", fontSize = 11.sp, color = RoyalBlue)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = RoyalBlue)
                        }

                        DropdownMenu(
                            expanded = showChildDropdown,
                            onDismissRequest = { showChildDropdown = false }
                        ) {
                            siblingList.forEach { child ->
                                DropdownMenuItem(
                                    text = { Text("${child.fullName} (${child.currentBelt})") },
                                    onClick = {
                                        onChildSwitch(child)
                                        showChildDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        HorizontalDivider(color = BorderLight)

        // NAVIGATION ITEMS
        DrawerSectionHeader("OVERVIEW")
        DrawerMenuItem("🏠 Dashboard", Icons.Default.Home, currentTab == "dashboard") { onSelectTab("dashboard") }
        DrawerMenuItem("👤 My Profile", Icons.Default.AccountCircle, currentTab == "profile") { onSelectTab("profile") }
        DrawerMenuItem("🥋 My Karate", Icons.Default.MilitaryTech, currentTab == "karate") { onSelectTab("karate") }
        DrawerMenuItem("📅 My Batch & Schedule", Icons.Default.FitnessCenter, currentTab == "class") { onSelectTab("class") }
        DrawerMenuItem("🥋 Instructors & Standards", Icons.Default.Groups, currentTab == "instructors") { onSelectTab("instructors") }

        DrawerSectionHeader("ACADEMICS & FEES")
        DrawerMenuItem("✅ Attendance", Icons.Default.CheckCircle, currentTab == "attendance") { onSelectTab("attendance") }
        DrawerMenuItem("💰 Fees & Payments", Icons.Default.Payments, currentTab == "fees") { onSelectTab("fees") }

        DrawerSectionHeader("EVENTS & HONORS")
        DrawerMenuItem("🏆 Tournaments", Icons.Default.EmojiEvents, currentTab == "tournaments") { onSelectTab("tournaments") }
        DrawerMenuItem("📜 Certificates", Icons.Default.WorkspacePremium, currentTab == "certificates") { onSelectTab("certificates") }
        DrawerMenuItem("🌟 Achievements", Icons.Default.Star, currentTab == "achievements") { onSelectTab("achievements") }
        DrawerMenuItem("📅 Calendar", Icons.Default.CalendarMonth, currentTab == "calendar") { onSelectTab("calendar") }
        DrawerMenuItem("📢 Announcements", Icons.Default.Campaign, currentTab == "announcements") { onSelectTab("announcements") }
        DrawerMenuItem("💬 Chat with Admin", Icons.Default.Chat, currentTab == "chat") { onSelectTab("chat") }

        DrawerSectionHeader("SYSTEM")
        DrawerMenuItem("⚙️ Settings & Privacy", Icons.Default.Settings, currentTab == "settings") { onSelectTab("settings") }
        DrawerMenuItem("📞 Contact Academy", Icons.Default.Call, currentTab == "contact") { onSelectTab("contact") }
        DrawerMenuItem("🚪 Logout", Icons.Default.ExitToApp, false) { onLogout() }
    }
}

@Composable
fun StudentPortalMainContent(
    currentTab: String,
    student: UserAccountEntity,
    siblingList: List<UserAccountEntity>,
    coachesList: List<UserAccountEntity>,
    batchesList: List<BatchEntity> = emptyList(),
    allStudentsList: List<UserAccountEntity> = emptyList(),
    attendanceList: List<AttendanceRecordEntity>,
    paymentsList: List<PaymentRecordEntity>,
    feeItemsList: List<FeeItemEntity>,
    certificatesList: List<CertificateEntity>,
    eventsList: List<CalendarEventEntity>,
    announcementsList: List<AnnouncementEntity>,
    requestsList: List<StudentRequestEntity>,
    adminSettings: AdminSettingsEntity?,
    tournamentsList: List<TournamentEntity> = emptyList(),
    leadershipList: List<AcademyLeadershipEntity> = emptyList(),
    standardsList: List<AcademyStandardEntity> = emptyList(),
    chatMessages: List<ChatMessageEntity> = emptyList(),
    trainingPrograms: List<TrainingProgramEntity> = emptyList(),
    onChildSwitch: (UserAccountEntity) -> Unit,
    onSavePersonalDetails: (String, String, String, String) -> Unit,
    onRequestChangeSubmit: (String, String, String, String) -> Unit,
    onEnrollBatch: (BatchEntity) -> Unit = {},
    onEnrollSpecialTraining: (String, Double) -> Unit,
    onSubmitPaymentRef: (String, String, Double, String, String) -> Unit,
    onSubmitCertificate: (String, String, String, String) -> Unit,
    onUpdateClassAndTraining: (String, String) -> Unit,
    onSaveCalendarEvent: (CalendarEventEntity) -> Unit,
    onSaveStudentProfile: ((UserAccountEntity) -> Unit)? = null,
    onSendMessageToAdmin: (String) -> Unit = {},
    onLogout: () -> Unit,
    onNavigateTab: (String) -> Unit
) {
    when (currentTab) {
        "dashboard" -> StudentDashboardScreen(
            student = student,
            attendanceList = attendanceList,
            paymentsList = paymentsList,
            nextEvent = eventsList.firstOrNull(),
            announcement = announcementsList.firstOrNull(),
            onNavigateToTab = onNavigateTab
        )
        "profile" -> StudentProfileScreen(
            student = student,
            siblingList = siblingList,
            requestsList = requestsList,
            onChildSwitch = onChildSwitch,
            onSavePersonalDetails = onSavePersonalDetails,
            onRequestChangeSubmit = onRequestChangeSubmit,
            onUpdateClassAndTraining = onUpdateClassAndTraining,
            onSaveStudentProfile = onSaveStudentProfile
        )
        "karate" -> StudentKarateScreen(student = student, allStudentsList = allStudentsList)
        "instructors" -> StudentInstructorsScreen(
            leadershipList = leadershipList,
            standardsList = standardsList,
            coachesList = coachesList
        )
        "class" -> StudentClassScreen(
            student = student,
            coachesList = coachesList,
            batchesList = batchesList,
            paymentsList = paymentsList,
            eventsList = eventsList,
            requestsList = requestsList,
            trainingPrograms = trainingPrograms,
            feeItemsList = feeItemsList,
            onEnrollBatch = onEnrollBatch,
            onEnrollSpecialTraining = onEnrollSpecialTraining,
            onSubmitPaymentRef = onSubmitPaymentRef,
            onRequestChangeSubmit = onRequestChangeSubmit,
            onUpdateClassAndTraining = onUpdateClassAndTraining,
            onSaveCalendarEvent = onSaveCalendarEvent
        )
        "attendance" -> StudentAttendanceScreen(
            student = student,
            attendanceList = attendanceList
        )
        "fees" -> StudentFeesScreen(
            student = student,
            paymentsList = paymentsList,
            feeItemsList = feeItemsList,
            trainingPrograms = trainingPrograms,
            adminSettings = adminSettings,
            onSubmitPaymentRef = onSubmitPaymentRef
        )
        "tournaments" -> StudentTournamentsScreen(
            student = student,
            tournamentsList = tournamentsList,
            eventsList = eventsList
        )
        "chat" -> StudentChatScreen(
            student = student,
            chatMessages = chatMessages,
            onSendMessage = onSendMessageToAdmin
        )
        "certificates" -> StudentCertificatesScreen(
            student = student,
            certificatesList = certificatesList,
            onSubmitCertificate = onSubmitCertificate
        )
        "achievements" -> StudentAchievementsScreen(student = student)
        "calendar" -> StudentCalendarScreen(eventsList = eventsList, student = student)
        "announcements" -> StudentAnnouncementsScreen(announcementsList = announcementsList)
        "settings" -> StudentSettingsScreen(student = student, onLogout = onLogout)
        "contact" -> StudentContactScreen(
            student = student,
            adminSettings = adminSettings,
            onSendMessageToAdmin = onSendMessageToAdmin,
            onNavigateToChat = { onNavigateTab("chat") }
        )
        else -> StudentDashboardScreen(
            student = student,
            attendanceList = attendanceList,
            paymentsList = paymentsList,
            nextEvent = eventsList.firstOrNull(),
            announcement = announcementsList.firstOrNull(),
            onNavigateToTab = onNavigateTab
        )
    }
}

fun getStudentTabTitle(tab: String): String = when (tab) {
    "dashboard" -> "BROMA STUDENT PORTAL"
    "profile" -> "MY PROFILE"
    "karate" -> "MY KARATE"
    "instructors" -> "INSTRUCTORS & STANDARDS"
    "class" -> "MY BATCH & SCHEDULE"
    "attendance" -> "ATTENDANCE RECORD"
    "fees" -> "FEES & PAYMENTS"
    "tournaments" -> "TOURNAMENTS"
    "chat" -> "CHAT WITH ADMIN"
    "certificates" -> "CERTIFICATE LOCKER"
    "achievements" -> "ACHIEVEMENTS"
    "calendar" -> "ACADEMY CALENDAR"
    "announcements" -> "ANNOUNCEMENTS"
    "settings" -> "SETTINGS & PRIVACY"
    "contact" -> "CONTACT ACADEMY"
    else -> "BROMA ACADEMY"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptInTopAppBar(title: String, onOpenDrawer: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                color = TextNavy
            )
        },
        navigationIcon = {
            IconButton(onClick = onOpenDrawer, modifier = Modifier.testTag("open_student_drawer_button")) {
                Icon(Icons.Default.Menu, contentDescription = "Open Drawer", tint = DeepNavy)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = CardWhite)
    )
}

@Composable
fun DrawerSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
        color = TextSlate,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp)
    )
}

@Composable
fun DrawerMenuItem(label: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = {
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) RoyalBlue else TextSlate
                )
            )
        },
        selected = isSelected,
        onClick = onClick,
        icon = {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isSelected) RoyalBlue else TextSlate
            )
        },
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = ActiveNavBg,
            unselectedContainerColor = Color.Transparent,
            selectedTextColor = RoyalBlue,
            unselectedTextColor = TextSlate
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(vertical = 2.dp)
    )
}
