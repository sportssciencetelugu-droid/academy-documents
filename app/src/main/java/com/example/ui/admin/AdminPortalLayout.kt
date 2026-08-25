package com.example.ui.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.common.BromaAcademyLogo
import com.example.ui.common.AttendanceDonutChart
import com.example.ui.common.AttendanceTrendLineChart
import com.example.ui.common.AttendanceWeeklyBarChart
import com.example.ui.common.BatchAttendanceComparisonChart
import com.example.ui.common.PhotoPickerSelector
import com.example.ui.student.DrawerMenuItem
import com.example.ui.student.DrawerSectionHeader
import com.example.ui.student.OptInTopAppBar
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun AdminPortalLayout(
    adminUser: UserAccountEntity,
    allUsersList: List<UserAccountEntity> = emptyList(),
    studentsList: List<UserAccountEntity>,
    coachesList: List<UserAccountEntity>,
    batchesList: List<BatchEntity>,
    feeItemsList: List<FeeItemEntity>,
    paymentsList: List<PaymentRecordEntity>,
    certificatesList: List<CertificateEntity>,
    eventsList: List<CalendarEventEntity>,
    announcementsList: List<AnnouncementEntity>,
    requestsList: List<StudentRequestEntity>,
    adminSettings: AdminSettingsEntity?,
    dojoRulesList: List<DojoRuleEntity> = emptyList(),
    achievementsList: List<AchievementEntity> = emptyList(),
    tournamentsList: List<TournamentEntity> = emptyList(),
    leadershipList: List<AcademyLeadershipEntity> = emptyList(),
    standardsList: List<AcademyStandardEntity> = emptyList(),
    chatMessages: List<ChatMessageEntity> = emptyList(),
    trainingPrograms: List<TrainingProgramEntity> = emptyList(),
    onSaveStudent: (UserAccountEntity) -> Unit,
    onSaveCoach: (UserAccountEntity) -> Unit,
    onApproveCoach: (String) -> Unit = {},
    onDeleteCoach: (String) -> Unit = {},
    onSaveBatch: (BatchEntity) -> Unit,
    onVerifyPayment: (String) -> Unit,
    onVerifyPaymentWithStatus: (String, String) -> Unit = { receiptNo, status -> onVerifyPayment(receiptNo) },
    onAddManualPayment: (PaymentRecordEntity) -> Unit = {},
    onProcessRequest: (StudentRequestEntity, Boolean) -> Unit,
    onGrantSpecialTimePermission: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onProcessCertReview: (CertificateEntity, Boolean) -> Unit,
    onApproveAchievement: (String) -> Unit = {},
    onDeleteAchievement: (String) -> Unit = {},
    onSaveAchievement: (AchievementEntity) -> Unit = {},
    onSaveTournament: (TournamentEntity) -> Unit = {},
    onDeleteTournament: (String) -> Unit = {},
    onTogglePublishTournament: (String) -> Unit = {},
    onSaveLeadership: (AcademyLeadershipEntity) -> Unit = {},
    onDeleteLeadership: (String) -> Unit = {},
    onSaveStandard: (AcademyStandardEntity) -> Unit = {},
    onDeleteStandard: (String) -> Unit = {},
    onSendChatMessage: (String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _ -> },
    onDeleteChatMessage: (String) -> Unit = {},
    onSaveTrainingProgram: (TrainingProgramEntity) -> Unit = {},
    onDeleteTrainingProgram: (String) -> Unit = {},
    onSaveCalendarEvent: (CalendarEventEntity) -> Unit,
    onSaveFeeItem: (FeeItemEntity) -> Unit = {},
    onSaveAnnouncement: (AnnouncementEntity) -> Unit,
    onSaveSettings: (AdminSettingsEntity) -> Unit,
    onSaveLogo: (String?, String) -> Unit = { _, _ -> },
    onSaveAcademyContact: (String, String, String, String, String, String, String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _, _, _, _, _, _, _ -> },
    onSaveDojoRule: (DojoRuleEntity) -> Unit = {},
    onDeleteDojoRule: (String) -> Unit = {},
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
                            BromaAcademyLogo(
                                size = 44.dp,
                                showBorder = true,
                                borderColor = BorderLight,
                                logoUri = adminSettings?.logoUri
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text("ADMIN CONTROL CENTER", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                Text(adminUser.fullName, style = MaterialTheme.typography.bodySmall, color = TextSlate)
                            }
                        }
                    }

                    HorizontalDivider(color = BorderLight)

                    DrawerSectionHeader("ADMINISTRATION")
                    DrawerMenuItem("🏠 Dashboard", Icons.Default.Home, currentTab == "dashboard") {
                        currentTab = "dashboard"
                        scope.launch { drawerState.close() }
                    }
                    DrawerMenuItem("👨🎓 Student Centre", Icons.Default.School, currentTab == "students") {
                        currentTab = "students"
                        scope.launch { drawerState.close() }
                    }
                    DrawerMenuItem("👨🏫 Coach Centre", Icons.Default.Sports, currentTab == "coaches") {
                        currentTab = "coaches"
                        scope.launch { drawerState.close() }
                    }
                    DrawerMenuItem("👥 Batch Centre", Icons.Default.Group, currentTab == "batches") {
                        currentTab = "batches"
                        scope.launch { drawerState.close() }
                    }
                    DrawerMenuItem("🏆 Tournament Details & Upload", Icons.Default.EmojiEvents, currentTab == "tournaments") {
                        currentTab = "tournaments"
                        scope.launch { drawerState.close() }
                    }
                    DrawerMenuItem("🏛️ Officials & Hierarchy", Icons.Default.AccountBalance, currentTab == "leadership") {
                        currentTab = "leadership"
                        scope.launch { drawerState.close() }
                    }
                    DrawerMenuItem("💬 Message & Chat Centre", Icons.Default.Chat, currentTab == "chat") {
                        currentTab = "chat"
                        scope.launch { drawerState.close() }
                    }
                    DrawerMenuItem("💰 Fees & Training Programs", Icons.Default.AccountBalanceWallet, currentTab == "fees_programs") {
                        currentTab = "fees_programs"
                        scope.launch { drawerState.close() }
                    }
                    DrawerMenuItem("💳 Payment Verification", Icons.Default.Payments, currentTab == "payments") {
                        currentTab = "payments"
                        scope.launch { drawerState.close() }
                    }
                    DrawerMenuItem("📜 Certificate Centre", Icons.Default.WorkspacePremium, currentTab == "certificates") {
                        currentTab = "certificates"
                        scope.launch { drawerState.close() }
                    }
                    DrawerMenuItem("🌟 Student Achievements", Icons.Default.Star, currentTab == "achievements") {
                        currentTab = "achievements"
                        scope.launch { drawerState.close() }
                    }
                    DrawerMenuItem("📅 Calendar Centre", Icons.Default.CalendarMonth, currentTab == "calendar") {
                        currentTab = "calendar"
                        scope.launch { drawerState.close() }
                    }
                    DrawerMenuItem("📢 Announcement Centre", Icons.Default.Campaign, currentTab == "announcements") {
                        currentTab = "announcements"
                        scope.launch { drawerState.close() }
                    }
                    DrawerMenuItem("⏰ Special Time Permissions", Icons.Default.Schedule, currentTab == "time_permissions") {
                        currentTab = "time_permissions"
                        scope.launch { drawerState.close() }
                    }
                    DrawerMenuItem("📊 Attendance Analytics", Icons.Default.BarChart, currentTab == "attendance_analytics") {
                        currentTab = "attendance_analytics"
                        scope.launch { drawerState.close() }
                    }
                    DrawerMenuItem("📍 Academy Contact Settings", Icons.Default.Contacts, currentTab == "contact_settings") {
                        currentTab = "contact_settings"
                        scope.launch { drawerState.close() }
                    }
                    DrawerMenuItem("⚙️ Payment & UPI Settings", Icons.Default.Settings, currentTab == "settings") {
                        currentTab = "settings"
                        scope.launch { drawerState.close() }
                    }
                    DrawerMenuItem("📜 Dojo Rules & Standards", Icons.Default.Gavel, currentTab == "dojo_rules") {
                        currentTab = "dojo_rules"
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
                        "dashboard" -> "BROMA ADMIN DASHBOARD"
                        "students" -> "STUDENT MANAGEMENT CENTRE"
                        "coaches" -> "COACH MANAGEMENT CENTRE"
                        "batches" -> "BATCH MANAGEMENT CENTRE"
                        "tournaments" -> "TOURNAMENT DETAILS & UPLOADS"
                        "leadership" -> "ACADEMY OFFICIALS & HIERARCHY"
                        "chat" -> "MESSAGE & CHAT CENTRE"
                        "fees_programs" -> "FEES & TRAINING PROGRAMS"
                        "payments" -> "PAYMENT VERIFICATION CENTRE"
                        "certificates" -> "CERTIFICATE VERIFICATION"
                        "achievements" -> "STUDENT ACHIEVEMENTS"
                        "calendar" -> "CALENDAR & EVENT CENTRE"
                        "announcements" -> "ANNOUNCEMENT CENTRE"
                        "time_permissions" -> "SPECIAL TIME PERMISSIONS"
                        "attendance_analytics" -> "DOJO ATTENDANCE ANALYTICS"
                        "contact_settings" -> "ACADEMY CONTACT SETTINGS"
                        "settings" -> "PAYMENT & UPI SETTINGS"
                        "dojo_rules" -> "DOJO RULES & ACADEMY STANDARDS"
                        else -> "ADMIN PORTAL"
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
                        AdminDashboardView(
                            studentsCount = studentsList.size,
                            coachesCount = coachesList.size,
                            batchesCount = batchesList.size,
                            paymentsList = paymentsList
                        )
                    }
                    "students" -> {
                        AdminStudentsView(
                            allUsersList = allUsersList,
                            studentsList = studentsList,
                            batchesList = batchesList,
                            onSaveStudent = onSaveStudent
                        )
                    }
                    "coaches" -> {
                        AdminCoachesView(
                            allUsersList = allUsersList,
                            coachesList = coachesList,
                            onSaveCoach = onSaveCoach,
                            onApproveCoach = onApproveCoach,
                            onDeleteCoach = onDeleteCoach
                        )
                    }
                    "batches" -> {
                        AdminBatchesView(
                            batchesList = batchesList,
                            coachesList = coachesList,
                            onSaveBatch = onSaveBatch
                        )
                    }
                    "tournaments" -> {
                        AdminTournamentsManagementView(
                            tournamentsList = tournamentsList,
                            onSaveTournament = onSaveTournament,
                            onDeleteTournament = onDeleteTournament,
                            onTogglePublishTournament = onTogglePublishTournament
                        )
                    }
                    "leadership" -> {
                        AdminLeadershipManagementView(
                            leadershipList = leadershipList,
                            onSaveLeadership = onSaveLeadership,
                            onDeleteLeadership = onDeleteLeadership
                        )
                    }
                    "chat" -> {
                        AdminChatManagementView(
                            allUsersList = allUsersList,
                            chatMessages = chatMessages,
                            onSendChatMessage = onSendChatMessage,
                            onDeleteChatMessage = onDeleteChatMessage
                        )
                    }
                    "fees_programs" -> {
                        AdminFeesAndProgramsManagementView(
                            feeItemsList = feeItemsList,
                            trainingPrograms = trainingPrograms,
                            studentsList = studentsList,
                            paymentsList = paymentsList,
                            onSaveFeeItem = onSaveFeeItem,
                            onSaveTrainingProgram = onSaveTrainingProgram,
                            onDeleteTrainingProgram = onDeleteTrainingProgram,
                            onAddManualPayment = onAddManualPayment
                        )
                    }
                    "payments" -> {
                        AdminPaymentsView(
                            paymentsList = paymentsList,
                            studentsList = studentsList,
                            adminSettings = adminSettings,
                            onVerifyPayment = onVerifyPayment,
                            onVerifyPaymentWithStatus = onVerifyPaymentWithStatus,
                            onAddManualPayment = onAddManualPayment,
                            onSaveSettings = onSaveSettings
                        )
                    }
                    "certificates" -> {
                        AdminCertificatesView(
                            certificatesList = certificatesList,
                            onProcessCertReview = onProcessCertReview
                        )
                    }
                    "achievements" -> {
                        AdminAchievementsView(
                            achievementsList = achievementsList,
                            studentsList = studentsList,
                            onApproveAchievement = onApproveAchievement,
                            onDeleteAchievement = onDeleteAchievement,
                            onSaveAchievement = onSaveAchievement
                        )
                    }
                    "calendar" -> {
                        AdminCalendarView(
                            eventsList = eventsList,
                            onSaveCalendarEvent = onSaveCalendarEvent
                        )
                    }
                    "announcements" -> {
                        AdminAnnouncementsView(
                            announcementsList = announcementsList,
                            onSaveAnnouncement = onSaveAnnouncement
                        )
                    }
                    "time_permissions" -> {
                        AdminSpecialTimePermissionsView(
                            studentsList = studentsList,
                            requestsList = requestsList,
                            onProcessRequest = onProcessRequest,
                            onGrantSpecialTimePermission = onGrantSpecialTimePermission
                        )
                    }
                    "attendance_analytics" -> {
                        AdminAttendanceAnalyticsView(
                            studentsList = studentsList,
                            batchesList = batchesList
                        )
                    }
                    "contact_settings" -> {
                        AdminContactSettingsView(
                            adminSettings = adminSettings,
                            onSaveContact = onSaveAcademyContact
                        )
                    }
                    "settings" -> {
                        AdminSettingsView(
                            adminSettings = adminSettings,
                            batchesList = batchesList,
                            feeItemsList = feeItemsList,
                            currentAdmin = adminUser,
                            onSaveSettings = onSaveSettings,
                            onSaveLogo = onSaveLogo
                        )
                    }
                    "dojo_rules" -> {
                        AdminDojoRulesView(
                            dojoRulesList = dojoRulesList,
                            onSaveDojoRule = onSaveDojoRule,
                            onDeleteDojoRule = onDeleteDojoRule
                        )
                    }
                }
            }
        }
    }
}

private fun String?.isNull_or_blank(): Boolean {
    return this == null || this.trim().isEmpty()
}

@Composable
fun AdminPaymentsView(
    paymentsList: List<PaymentRecordEntity>,
    studentsList: List<UserAccountEntity> = emptyList(),
    adminSettings: AdminSettingsEntity?,
    onVerifyPayment: (String) -> Unit,
    onVerifyPaymentWithStatus: (String, String) -> Unit = { receiptNo, status -> onVerifyPayment(receiptNo) },
    onAddManualPayment: (PaymentRecordEntity) -> Unit = {},
    onSaveSettings: (AdminSettingsEntity) -> Unit
) {
    var upiIdVal by remember { mutableStateOf(adminSettings?.upiId ?: "bromaacademy@upi") }
    var paymentPhoneVal by remember { mutableStateOf(adminSettings?.paymentPhone ?: "+91 98765 43210") }
    var paymentReceiverNameVal by remember { mutableStateOf(adminSettings?.paymentReceiverName ?: "BROMA Martial Arts") }
    var qrCodeUriVal by remember { mutableStateOf(adminSettings?.qrCodeUri ?: "") }
    var qrSaveSuccess by remember { mutableStateOf(false) }

    var showQrSettings by remember { mutableStateOf(false) }
    var showManualPaymentDialog by remember { mutableStateOf(false) }
    var viewingReceipt by remember { mutableStateOf<PaymentRecordEntity?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterTab by remember { mutableStateOf("PENDING") } // "ALL", "PENDING", "PAID", "DUE_REJECTED"
    var copyToastMsg by remember { mutableStateOf<String?>(null) }

    val pendingCount = remember(paymentsList) { paymentsList.count { it.status == "VERIFICATION_PENDING" } }
    val paidCount = remember(paymentsList) { paymentsList.count { it.status == "PAID" } }
    val dueCount = remember(paymentsList) { paymentsList.count { it.status == "DUE" || it.status == "REJECTED" || it.status == "OVERDUE" } }

    val totalCollected = remember(paymentsList) { paymentsList.filter { it.status == "PAID" }.sumOf { it.amount } }
    val totalPendingAmount = remember(paymentsList) { paymentsList.filter { it.status == "VERIFICATION_PENDING" }.sumOf { it.amount } }

    val filteredList = remember(paymentsList, selectedFilterTab, searchQuery) {
        paymentsList.filter { p ->
            val matchesFilter = when (selectedFilterTab) {
                "PENDING" -> p.status == "VERIFICATION_PENDING"
                "PAID" -> p.status == "PAID"
                "DUE_REJECTED" -> p.status == "DUE" || p.status == "REJECTED" || p.status == "OVERDUE"
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() ||
                    p.studentName.contains(searchQuery, ignoreCase = true) ||
                    p.receiptNo.contains(searchQuery, ignoreCase = true) ||
                    p.transactionRef.contains(searchQuery, ignoreCase = true) ||
                    p.feeCategory.contains(searchQuery, ignoreCase = true)
            matchesFilter && matchesSearch
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Title & Actions Toolbar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("PAYMENT VERIFICATION CENTRE", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = GoldSecondary)
                Text("Verify student UPI transaction references, approve fee submissions & manage records", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = { showQrSettings = !showQrSettings },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldSecondary),
                    modifier = Modifier.testTag("toggle_qr_settings_button")
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = GoldSecondary, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(if (showQrSettings) "Hide QR Config" else "QR & UPI Config", color = GoldSecondary, style = MaterialTheme.typography.labelSmall)
                }

                Button(
                    onClick = { showManualPaymentDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                    modifier = Modifier.testTag("add_manual_payment_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Record Payment", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        // Summary KPI Stats Banner
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BeltGreen.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("TOTAL COLLECTED", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text("₹${totalCollected.toInt()}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = BeltGreen)
                    Text("$paidCount verified receipts", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                }
            }

            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (pendingCount > 0) BeltYellow else Color.DarkGray)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("PENDING VERIFICATION", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        if (pendingCount > 0) {
                            Surface(shape = CircleShape, color = BeltYellow) {
                                Text("$pendingCount", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }
                    Text("₹${totalPendingAmount.toInt()}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = BeltYellow)
                    Text("Awaiting Admin Review", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                }
            }
        }

        // Admin Payment QR Code & UPI Configuration Box (Collapsible)
        if (showQrSettings) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldSecondary)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("ACADEMY PAYMENT OPTIONS & QR CODE SETTINGS", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = GoldSecondary)
                    
                    OutlinedTextField(
                        value = upiIdVal,
                        onValueChange = { upiIdVal = it },
                        label = { Text("Academy Official UPI ID") },
                        placeholder = { Text("bromaacademy@upi") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = paymentPhoneVal,
                            onValueChange = { paymentPhoneVal = it },
                            label = { Text("PhonePe / Pay Phone Number") },
                            placeholder = { Text("+91 98765 43210") },
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = paymentReceiverNameVal,
                            onValueChange = { paymentReceiverNameVal = it },
                            label = { Text("PhonePe User / Account Name") },
                            placeholder = { Text("BROMA Martial Arts") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    CustomQrUploadSection(
                        currentQrUri = qrCodeUriVal,
                        onQrUriChange = {
                            qrCodeUriVal = it
                            qrSaveSuccess = false
                        }
                    )

                    if (qrSaveSuccess) {
                        Text("✓ Payment QR Code photo, PhonePe & UPI settings updated!", color = BeltGreen, style = MaterialTheme.typography.bodySmall)
                    }

                    Button(
                        onClick = {
                            val updated = (adminSettings ?: AdminSettingsEntity()).copy(
                                upiId = upiIdVal,
                                paymentPhone = paymentPhoneVal,
                                paymentReceiverName = paymentReceiverNameVal,
                                qrCodeUri = qrCodeUriVal
                            )
                            onSaveSettings(updated)
                            qrSaveSuccess = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_upi_settings_button")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("[ SAVE & UPDATE PAYMENT QR ]", fontWeight = FontWeight.Bold)
                    }

                    Text("ACTIVE PAYMENT QR CODE PHOTO PREVIEW", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    
                    com.example.ui.student.UpiQrCodePhotoCard(
                        upiId = upiIdVal,
                        academyName = adminSettings?.academyName ?: "BROMA Martial Arts Academy",
                        qrCodeUri = qrCodeUriVal,
                        paymentPhone = paymentPhoneVal,
                        paymentReceiverName = paymentReceiverNameVal
                    )
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by student name, receipt #, or UTR ref ID...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldSecondary) },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.Gray)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_payment_field"),
            shape = RoundedCornerShape(10.dp)
        )

        // Filter Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = selectedFilterTab == "PENDING",
                onClick = { selectedFilterTab = "PENDING" },
                label = { Text("PENDING ($pendingCount)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BeltYellow.copy(alpha = 0.3f),
                    selectedLabelColor = BeltYellow
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedFilterTab == "PENDING",
                    borderColor = Color.DarkGray,
                    selectedBorderColor = BeltYellow
                )
            )

            FilterChip(
                selected = selectedFilterTab == "PAID",
                onClick = { selectedFilterTab = "PAID" },
                label = { Text("VERIFIED ($paidCount)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BeltGreen.copy(alpha = 0.3f),
                    selectedLabelColor = BeltGreen
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedFilterTab == "PAID",
                    borderColor = Color.DarkGray,
                    selectedBorderColor = BeltGreen
                )
            )

            FilterChip(
                selected = selectedFilterTab == "DUE_REJECTED",
                onClick = { selectedFilterTab = "DUE_REJECTED" },
                label = { Text("DUE / REJECTED ($dueCount)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CrimsonPrimary.copy(alpha = 0.3f),
                    selectedLabelColor = CrimsonLight
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedFilterTab == "DUE_REJECTED",
                    borderColor = Color.DarkGray,
                    selectedBorderColor = CrimsonLight
                )
            )

            FilterChip(
                selected = selectedFilterTab == "ALL",
                onClick = { selectedFilterTab = "ALL" },
                label = { Text("ALL (${paymentsList.size})") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = GoldSecondary.copy(alpha = 0.3f),
                    selectedLabelColor = GoldSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedFilterTab == "ALL",
                    borderColor = Color.DarkGray,
                    selectedBorderColor = GoldSecondary
                )
            )
        }

        if (copyToastMsg != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = BeltGreen.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, BeltGreen)
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BeltGreen, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(copyToastMsg!!, style = MaterialTheme.typography.bodySmall, color = BeltGreen)
                }
            }
        }

        // List of Payment Cards
        if (filteredList.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(36.dp))
                    Text("No payment submissions found matching criteria", style = MaterialTheme.typography.bodyMedium, color = Color.LightGray)
                    if (selectedFilterTab == "PENDING" && pendingCount == 0) {
                        Text("All student payments have been verified! 🎉", style = MaterialTheme.typography.labelSmall, color = BeltGreen)
                    }
                }
            }
        } else {
            filteredList.forEach { p ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = when (p.status) {
                            "VERIFICATION_PENDING" -> BeltYellow
                            "PAID" -> BeltGreen.copy(alpha = 0.5f)
                            "REJECTED" -> CrimsonPrimary
                            else -> Color.DarkGray
                        }
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Header: Student Name + Status Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(p.studentName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                Text("Receipt #: ${p.receiptNo} • Student ID: ${p.studentId}", style = MaterialTheme.typography.labelSmall, color = GoldSecondary)
                            }

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = when (p.status) {
                                    "VERIFICATION_PENDING" -> BeltYellow.copy(alpha = 0.2f)
                                    "PAID" -> BeltGreen.copy(alpha = 0.2f)
                                    "REJECTED" -> CrimsonPrimary.copy(alpha = 0.2f)
                                    else -> Color.DarkGray.copy(alpha = 0.4f)
                                },
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    when (p.status) {
                                        "VERIFICATION_PENDING" -> BeltYellow
                                        "PAID" -> BeltGreen
                                        "REJECTED" -> CrimsonPrimary
                                        else -> Color.Gray
                                    }
                                )
                            ) {
                                Text(
                                    text = when (p.status) {
                                        "VERIFICATION_PENDING" -> "⏳ VERIFICATION PENDING"
                                        "PAID" -> "✓ VERIFIED & PAID"
                                        "REJECTED" -> "✕ REJECTED"
                                        else -> "⚠️ DUE / UNPAID"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = when (p.status) {
                                        "VERIFICATION_PENDING" -> BeltYellow
                                        "PAID" -> BeltGreen
                                        "REJECTED" -> CrimsonLight
                                        else -> Color.LightGray
                                    },
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.5f))

                        // Fee & Amount Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(p.feeCategory, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                Text("Period: ${p.month} • Date: ${p.paymentDate}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                Text("Method: ${p.paymentMethod}", style = MaterialTheme.typography.labelSmall, color = GoldSecondary)
                            }

                            Text("₹${p.amount.toInt()}", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = GoldSecondary)
                        }

                        // UTR / Transaction Reference Box with Quick Copy
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.DarkGray)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("TRANSACTION UTR / REF ID:", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                    Text(
                                        p.transactionRef.ifBlank { "NO UTR PROVIDED" },
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (p.transactionRef.isNotBlank()) Color.White else CrimsonLight
                                    )
                                }

                                if (p.transactionRef.isNotBlank()) {
                                    TextButton(
                                        onClick = {
                                            copyToastMsg = "Copied UTR: ${p.transactionRef}"
                                        }
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = GoldSecondary, modifier = Modifier.size(14.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Copy Ref", color = GoldSecondary, style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }

                        // Actions Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (p.status != "PAID") {
                                Button(
                                    onClick = {
                                        onVerifyPaymentWithStatus(p.receiptNo, "PAID")
                                        copyToastMsg = "✓ Payment verified for ${p.studentName}!"
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BeltGreen),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("verify_payment_${p.receiptNo}")
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Approve & Mark Paid", style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            if (p.status == "VERIFICATION_PENDING") {
                                Button(
                                    onClick = {
                                        onVerifyPaymentWithStatus(p.receiptNo, "REJECTED")
                                        copyToastMsg = "✕ Payment rejected for ${p.studentName}"
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkBackground),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonPrimary),
                                    modifier = Modifier.testTag("reject_payment_${p.receiptNo}")
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = CrimsonLight, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Reject", color = CrimsonLight, style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            OutlinedButton(
                                onClick = { viewingReceipt = p },
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldSecondary)
                            ) {
                                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = GoldSecondary, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Digital Receipt", color = GoldSecondary, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal 1: Digital Receipt Modal View
    if (viewingReceipt != null) {
        val r = viewingReceipt!!
        AlertDialog(
            onDismissRequest = { viewingReceipt = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = GoldSecondary)
                    Text("BROMA OFFICIAL RECEIPT", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = GoldSecondary)
                }
            },
            text = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldSecondary)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(adminSettings?.academyName ?: "BROMA MARTIAL ARTS ACADEMY", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        Text("Official Fee Payment Receipt", style = MaterialTheme.typography.labelSmall, color = GoldSecondary)

                        HorizontalDivider(color = Color.DarkGray)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Receipt No:", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Text(r.receiptNo, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Student Name:", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Text(r.studentName, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Fee Category:", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Text(r.feeCategory, style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Fee Period / Month:", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Text(r.month, style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Payment Date:", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Text(r.paymentDate, style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Payment Mode:", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Text(r.paymentMethod, style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("UTR Reference Ref:", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Text(r.transactionRef.ifBlank { "N/A" }, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = GoldSecondary)
                        }

                        HorizontalDivider(color = Color.DarkGray)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("TOTAL PAID AMOUNT:", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            Text("₹${r.amount.toInt()}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = BeltGreen)
                        }

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = if (r.status == "PAID") BeltGreen.copy(alpha = 0.2f) else BeltYellow.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (r.status == "PAID") BeltGreen else BeltYellow)
                        ) {
                            Text(
                                "STATUS: ${r.status} • VERIFIED BY ACADEMY ADMIN",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (r.status == "PAID") BeltGreen else BeltYellow,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewingReceipt = null },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
                ) {
                    Text("Close Receipt")
                }
            }
        )
    }

    // Modal 2: Record Manual Offline Payment
    if (showManualPaymentDialog) {
        var selectedStudent by remember { mutableStateOf<UserAccountEntity?>(studentsList.firstOrNull()) }
        var manualStudentName by remember { mutableStateOf(studentsList.firstOrNull()?.fullName ?: "") }
        var manualStudentId by remember { mutableStateOf(studentsList.firstOrNull()?.userId ?: "") }
        var manualCategory by remember { mutableStateOf("Regular Karate Training Fees") }
        var manualMonth by remember { mutableStateOf("August 2026") }
        var manualAmountStr by remember { mutableStateOf("2000") }
        var manualPaymentMethod by remember { mutableStateOf("Cash") }
        var manualUtr by remember { mutableStateOf("CASH-DESK-" + (1000..9999).random()) }

        AlertDialog(
            onDismissRequest = { showManualPaymentDialog = false },
            title = { Text("Record Manual / Cash Payment") },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("STUDENT SELECTION", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = GoldSecondary)

                    if (studentsList.isNotEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.DarkGray)
                        ) {
                            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Select Enrolled Student:", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                studentsList.take(5).forEach { st ->
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (selectedStudent?.userId == st.userId) CrimsonPrimary.copy(alpha = 0.3f) else DarkSurface,
                                        onClick = {
                                            selectedStudent = st
                                            manualStudentName = st.fullName
                                            manualStudentId = st.userId
                                        }
                                    ) {
                                        Text(
                                            "${st.fullName} (@${st.username})",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    OutlinedTextField(value = manualStudentName, onValueChange = { manualStudentName = it }, label = { Text("Student Full Name *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = manualStudentId, onValueChange = { manualStudentId = it }, label = { Text("Student ID Code *") }, modifier = Modifier.fillMaxWidth())

                    HorizontalDivider(color = Color.DarkGray)
                    Text("FEE & PAYMENT DETAILS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = GoldSecondary)

                    OutlinedTextField(value = manualCategory, onValueChange = { manualCategory = it }, label = { Text("Fee Category") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = manualMonth, onValueChange = { manualMonth = it }, label = { Text("Fee Period / Month") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = manualAmountStr, onValueChange = { manualAmountStr = it }, label = { Text("Amount Paid (₹)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = manualPaymentMethod, onValueChange = { manualPaymentMethod = it }, label = { Text("Payment Mode (Cash / Bank / POS)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = manualUtr, onValueChange = { manualUtr = it }, label = { Text("Receipt Ref / Transaction ID") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = manualAmountStr.toDoubleOrNull() ?: 2000.0
                        if (manualStudentName.isNotBlank()) {
                            val newRecord = PaymentRecordEntity(
                                receiptNo = "BROM-REC-" + Calendar.getInstance().get(Calendar.YEAR) + "-" + (10000..99999).random(),
                                studentId = manualStudentId.ifBlank { "BROMA-" + (1000..9999).random() },
                                studentName = manualStudentName,
                                feeCategory = manualCategory,
                                month = manualMonth,
                                amount = amt,
                                paymentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                                paymentMethod = manualPaymentMethod,
                                transactionRef = manualUtr,
                                status = "PAID"
                            )
                            onAddManualPayment(newRecord)
                            showManualPaymentDialog = false
                            copyToastMsg = "✓ Recorded manual payment receipt for $manualStudentName"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                    modifier = Modifier.testTag("submit_manual_payment_button")
                ) {
                    Text("Save & Issue Receipt")
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualPaymentDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun AdminCertificatesView(
    certificatesList: List<CertificateEntity>,
    onProcessCertReview: (CertificateEntity, Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("CERTIFICATE VERIFICATION CENTRE", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = GoldSecondary)

        certificatesList.forEach { cert ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(cert.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    Text("Student: ${cert.studentName} • Org: ${cert.issuingOrg}", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                    Text("Status: ${cert.status}", style = MaterialTheme.typography.labelSmall, color = GoldSecondary)

                    if (cert.status == "PENDING_VERIFICATION") {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { onProcessCertReview(cert, true) }, colors = ButtonDefaults.buttonColors(containerColor = BeltGreen), modifier = Modifier.weight(1f)) { Text("Approve") }
                            Button(onClick = { onProcessCertReview(cert, false) }, colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary), modifier = Modifier.weight(1f)) { Text("Reject") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminCalendarView(
    eventsList: List<CalendarEventEntity>,
    onSaveCalendarEvent: (CalendarEventEntity) -> Unit
) {
    var showAddEventDialog by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Tournament") }
    var startDate by remember { mutableStateOf("2026-09-15") }
    var time by remember { mutableStateOf("09:00 AM") }
    var location by remember { mutableStateOf("BROMA Main Dojo, Hyderabad") }
    var description by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<String?>(null) }
    var isRegEnabled by remember { mutableStateOf(false) }
    var regFee by remember { mutableStateOf("500") }
    var regDeadline by remember { mutableStateOf("2026-09-10") }

    val categories = listOf("Tournament", "Seminar", "Belt Examination", "Ceremony", "Holiday", "Special Training", "Academy Event")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("CALENDAR EVENT PUBLISHER", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = GoldSecondary)
            Button(
                onClick = { showAddEventDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                modifier = Modifier.testTag("add_event_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Add Event")
            }
        }

        if (eventsList.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface
            ) {
                Text("No calendar events published yet. Click 'Add Event' above to create one.", modifier = Modifier.padding(16.dp), color = Color.Gray)
            }
        } else {
            eventsList.forEach { evt ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.DarkGray)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(evt.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GoldSecondary.copy(alpha = 0.2f)
                            ) {
                                Text(evt.category, style = MaterialTheme.typography.labelSmall, color = GoldSecondary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                            }
                        }
                        Text("📅 Date: ${evt.startDate} • Time: ${evt.time}", style = MaterialTheme.typography.bodySmall, color = BeltBlue)
                        Text("📍 Location: ${evt.location}", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                        Text(evt.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        if (evt.isRegistrationEnabled) {
                            Text("Entry Fee: ₹${evt.registrationFee.toInt()} | Deadline: ${evt.registrationDeadline ?: "N/A"}", style = MaterialTheme.typography.labelSmall, color = BeltGreen)
                        }
                    }
                }
            }
        }
    }

    if (showAddEventDialog) {
        AlertDialog(
            onDismissRequest = { showAddEventDialog = false },
            title = { Text("Publish New Calendar Event") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Event Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Category", style = MaterialTheme.typography.labelSmall, color = GoldSecondary)
                    ScrollableTabRow(
                        selectedTabIndex = categories.indexOf(category).coerceAtLeast(0),
                        containerColor = DarkSurface,
                        contentColor = GoldSecondary
                    ) {
                        categories.forEach { cat ->
                            Tab(
                                selected = category == cat,
                                onClick = { category = cat },
                                text = { Text(cat) }
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = startDate,
                            onValueChange = { startDate = it },
                            label = { Text("Start Date (YYYY-MM-DD)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = time,
                            onValueChange = { time = it },
                            label = { Text("Time") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location / Venue") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Event Description") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    PhotoPickerSelector(
                        selectedImageUri = photoUri,
                        onImageSelected = { photoUri = it },
                        label = "EVENT BANNER / BROCHURE PHOTO",
                        categoryHint = "Tournaments"
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isRegEnabled,
                            onCheckedChange = { isRegEnabled = it }
                        )
                        Text("Enable Student Registration & Fee", style = MaterialTheme.typography.bodySmall, color = Color.White)
                    }

                    if (isRegEnabled) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = regFee,
                                onValueChange = { regFee = it },
                                label = { Text("Registration Fee (₹)") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = regDeadline,
                                onValueChange = { regDeadline = it },
                                label = { Text("Deadline Date") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val newEvt = CalendarEventEntity(
                                eventId = "EVT-${System.currentTimeMillis()}",
                                title = title,
                                category = category,
                                startDate = startDate,
                                time = time,
                                location = location,
                                description = description,
                                isRegistrationEnabled = isRegEnabled,
                                registrationFee = regFee.toDoubleOrNull() ?: 0.0,
                                registrationDeadline = if (isRegEnabled) regDeadline else null
                            )
                            onSaveCalendarEvent(newEvt)
                            showAddEventDialog = false
                            title = ""
                            description = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                    modifier = Modifier.testTag("submit_new_event_button")
                ) {
                    Text("Publish Event")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddEventDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun AdminAnnouncementsView(
    announcementsList: List<AnnouncementEntity>,
    onSaveAnnouncement: (AnnouncementEntity) -> Unit
) {
    var showAddAnnDialog by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Important") }
    var priority by remember { mutableStateOf("Important") }
    var audience by remember { mutableStateOf("All Students") }
    var message by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("ANNOUNCEMENT PUBLISHER", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = GoldSecondary)
            Button(
                onClick = { showAddAnnDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                modifier = Modifier.testTag("add_announcement_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Publish Notice")
            }
        }

        if (announcementsList.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface
            ) {
                Text("No announcements published yet. Click 'Publish Notice' above.", modifier = Modifier.padding(16.dp), color = Color.Gray)
            }
        } else {
            announcementsList.forEach { ann ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (ann.priority == "Urgent") CrimsonPrimary else Color.DarkGray)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(ann.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            Text(ann.publishDate, style = MaterialTheme.typography.labelSmall, color = BeltBlue)
                        }
                        Text("Priority: ${ann.priority} • Audience: ${ann.audience}", style = MaterialTheme.typography.bodySmall, color = GoldSecondary)
                        Text(ann.message, style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                    }
                }
            }
        }
    }

    if (showAddAnnDialog) {
        AlertDialog(
            onDismissRequest = { showAddAnnDialog = false },
            title = { Text("Publish New Announcement") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = priority,
                            onValueChange = { priority = it },
                            label = { Text("Priority (Normal/Urgent)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = audience,
                            onValueChange = { audience = it },
                            label = { Text("Audience") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Notice / Message Body") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    PhotoPickerSelector(
                        selectedImageUri = photoUri,
                        onImageSelected = { photoUri = it },
                        label = "ATTACH NOTICE / BANNER PHOTO",
                        categoryHint = "Announcements"
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank() && message.isNotBlank()) {
                            val newAnn = AnnouncementEntity(
                                announcementId = "ANN-${System.currentTimeMillis()}",
                                title = title,
                                type = type,
                                message = message,
                                priority = priority,
                                audience = audience,
                                publishDate = "2026-08-10"
                            )
                            onSaveAnnouncement(newAnn)
                            showAddAnnDialog = false
                            title = ""
                            message = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
                ) {
                    Text("Publish Notice")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAnnDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun CustomQrUploadSection(
    currentQrUri: String,
    onQrUriChange: (String) -> Unit
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onQrUriChange(it.toString())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurface, RoundedCornerShape(10.dp))
            .border(androidx.compose.foundation.BorderStroke(1.dp, GoldSecondary.copy(alpha = 0.5f)), RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "Custom QR Code Photo",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = GoldSecondary
        )

        Button(
            onClick = { photoPickerLauncher.launch("image/*") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2C3E)),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldSecondary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("upload_qr_code_photo_button")
        ) {
            Icon(Icons.Default.CloudUpload, contentDescription = "Upload QR Code Photo", tint = GoldSecondary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("[ Upload QR Code Photo ]", color = Color.White, fontWeight = FontWeight.Bold)
        }

        if (currentQrUri.isNotBlank() && (currentQrUri.startsWith("content") || currentQrUri.startsWith("file") || currentQrUri.startsWith("http") || currentQrUri.startsWith("data"))) {
            val fileName = try {
                val parsed = Uri.parse(currentQrUri)
                val lastSeg = parsed.lastPathSegment ?: "broma_qr.png"
                if (lastSeg.contains(".")) lastSeg else "$lastSeg.png"
            } catch (e: Exception) {
                "broma_qr.png"
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1E2C), RoundedCornerShape(6.dp))
                    .padding(8.dp)
            ) {
                Text("Selected:", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = BeltGreen, modifier = Modifier.size(16.dp))
                    Text(
                        fileName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = BeltGreen
                    )
                }
            }
        }

        Text("OR SELECT BRAND PRESET:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.Gray)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = currentQrUri == "" || currentQrUri == "THEME_GOLD",
                onClick = { onQrUriChange("THEME_GOLD") },
                label = { Text("🥋 Gold") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = currentQrUri == "THEME_PHONEPE",
                onClick = { onQrUriChange("THEME_PHONEPE") },
                label = { Text("🟣 PhonePe") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = currentQrUri == "THEME_GPAY",
                onClick = { onQrUriChange("THEME_GPAY") },
                label = { Text("🟢 GPay") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = currentQrUri == "THEME_PAYTM",
                onClick = { onQrUriChange("THEME_PAYTM") },
                label = { Text("🔵 Paytm") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun AdminSpecialTimePermissionsView(
    studentsList: List<UserAccountEntity>,
    requestsList: List<StudentRequestEntity>,
    onProcessRequest: (StudentRequestEntity, Boolean) -> Unit,
    onGrantSpecialTimePermission: (String, String, String, String) -> Unit
) {
    var showGrantModal by remember { mutableStateOf(false) }
    var selectedStudent by remember { mutableStateOf<UserAccountEntity?>(studentsList.firstOrNull()) }
    var selectedTimeSlot by remember { mutableStateOf("06:00 PM – 08:00 PM (Evening Special Schedule)") }
    var customTimeInput by remember { mutableStateOf("") }
    var reasonInput by remember { mutableStateOf("Approved special evening training permission") }
    var successToast by remember { mutableStateOf("") }

    val presetTimeSlots = listOf(
        "06:00 PM – 08:00 PM (Evening Special Schedule)",
        "06:00 AM – 08:00 AM (Morning Conditioning)",
        "08:00 PM – 09:30 PM (Advanced Sparring Session)",
        "04:00 PM – 06:00 PM (Weekend Extra Practice)",
        "Custom Time Slot"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("SPECIAL TIME PERMISSION CENTRE", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = GoldSecondary)
                Text("Manage non-regular & evening training permissions", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            Button(
                onClick = {
                    selectedStudent = studentsList.firstOrNull()
                    showGrantModal = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                modifier = Modifier.testTag("admin_grant_permission_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Issue Permission")
            }
        }

        if (successToast.isNotBlank()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = BeltGreen.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, BeltGreen)
            ) {
                Text(successToast, color = BeltGreen, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(10.dp))
            }
        }

        // Section 1: Pending Requests for Special Training Permission
        val timeRequests = requestsList.filter {
            it.status == "PENDING" && (
                it.requestType.contains("Special Training", ignoreCase = true) ||
                it.requestType.contains("Permission", ignoreCase = true) ||
                it.requestType.contains("Time", ignoreCase = true)
            )
        }

        Text("PENDING TIME PERMISSION REQUESTS (${timeRequests.size})", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)

        if (timeRequests.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = DarkSurface
            ) {
                Text("No pending special time permission requests.", color = Color.Gray, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(12.dp))
            }
        } else {
            timeRequests.forEach { req ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonPrimary)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Student: ${req.studentName} (${req.studentId})", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            Surface(shape = RoundedCornerShape(8.dp), color = CrimsonPrimary.copy(alpha = 0.2f)) {
                                Text("⏰ TIME REQUEST", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = CrimsonPrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }

                        Text("Requested Slot: ${req.requestedValue}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = GoldSecondary)
                        Text("Reason: ${req.reason}", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onProcessRequest(req, true) },
                                colors = ButtonDefaults.buttonColors(containerColor = BeltGreen),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("admin_approve_perm_${req.requestId}")
                            ) {
                                Text("Approve Permission")
                            }

                            Button(
                                onClick = { onProcessRequest(req, false) },
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("admin_reject_perm_${req.requestId}")
                            ) {
                                Text("Reject")
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Active Granted Permissions Overview
        Text("ACTIVE TIME PERMISSIONS ACROSS ACADEMY", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)

        val studentsWithPerms = studentsList.filter { it.trainingPrograms.contains("Permission", ignoreCase = true) || it.trainingPrograms.contains("06:00 PM", ignoreCase = true) || it.trainingPrograms.contains("Special", ignoreCase = true) }

        if (studentsWithPerms.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = DarkSurface
            ) {
                Text("All students are on regular schedule. Tap 'Issue Permission' above to grant special time slots.", color = Color.Gray, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(12.dp))
            }
        } else {
            studentsWithPerms.forEach { st ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BeltGreen)
                ) {
                    Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(st.fullName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            Text("ID: ${st.userId} • Batch: ${st.batchName}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Text("Permissions: ${st.trainingPrograms}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = GoldSecondary)
                        }

                        Surface(shape = RoundedCornerShape(12.dp), color = BeltGreen.copy(alpha = 0.2f)) {
                            Text("✓ PERMISSION ACTIVE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = BeltGreen, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                }
            }
        }
    }

    // Modal Dialog to Issue Permission
    if (showGrantModal) {
        AlertDialog(
            onDismissRequest = { showGrantModal = false },
            title = { Text("GRANT SPECIAL TRAINING TIME PERMISSION") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select Student:", style = MaterialTheme.typography.labelSmall, color = GoldSecondary)

                    studentsList.forEach { st ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedStudent = st },
                            shape = RoundedCornerShape(6.dp),
                            color = if (selectedStudent?.userId == st.userId) CrimsonPrimary.copy(alpha = 0.3f) else DarkBackground
                        ) {
                            Text("${st.fullName} (${st.userId}) - ${st.batchName}", style = MaterialTheme.typography.bodySmall, color = Color.White, modifier = Modifier.padding(8.dp))
                        }
                    }

                    Text("Select Time Slot Permission:", style = MaterialTheme.typography.labelSmall, color = GoldSecondary)

                    presetTimeSlots.forEach { slot ->
                        val isSel = selectedTimeSlot == slot
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedTimeSlot = slot },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSel) CrimsonPrimary.copy(alpha = 0.3f) else DarkBackground,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) CrimsonPrimary else Color.DarkGray)
                        ) {
                            Text(slot, style = MaterialTheme.typography.bodySmall, color = if (isSel) GoldSecondary else Color.White, modifier = Modifier.padding(8.dp))
                        }
                    }

                    if (selectedTimeSlot == "Custom Time Slot") {
                        OutlinedTextField(
                            value = customTimeInput,
                            onValueChange = { customTimeInput = it },
                            label = { Text("Enter Custom Time Slot (e.g. 06:00 PM – 08:00 PM)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    OutlinedTextField(
                        value = reasonInput,
                        onValueChange = { reasonInput = it },
                        label = { Text("Reason / Permission Note") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val st = selectedStudent
                        val timeSlot = if (selectedTimeSlot == "Custom Time Slot") customTimeInput.ifBlank { "06:00 PM – 08:00 PM" } else selectedTimeSlot
                        if (st != null) {
                            onGrantSpecialTimePermission(
                                st.userId,
                                timeSlot,
                                reasonInput,
                                "Chief Academy Admin"
                            )
                            successToast = "✓ Granted Special Time Permission ($timeSlot) to ${st.fullName}!"
                        }
                        showGrantModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BeltGreen),
                    modifier = Modifier.testTag("submit_admin_grant_permission_button")
                ) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGrantModal = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun AdminAttendanceAnalyticsView(
    studentsList: List<UserAccountEntity>,
    batchesList: List<BatchEntity>
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text("DOJO ATTENDANCE ANALYTICS & VISUAL GRAPH CENTRE", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = GoldSecondary)
            Text("Real-time visual graphs, batch performance comparisons & attendance trends", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }

        // Summary Metric Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BeltGreen)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("OVERALL ATTENDANCE", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text("89.4%", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = BeltGreen)
                    Text("🟢 High Performing", style = MaterialTheme.typography.labelSmall, color = BeltGreen)
                }
            }

            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldSecondary)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("ACTIVE STUDENTS", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text("${studentsList.size.coerceAtLeast(14)}", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = GoldSecondary)
                    Text("Across 6 Dojo Batches + Special", style = MaterialTheme.typography.labelSmall, color = Color.White)
                }
            }
        }

        // VISUAL GRAPH 1: Dojo Attendance Status Donut Chart
        AttendanceDonutChart(
            present = (studentsList.size * 0.88).toInt().coerceAtLeast(18),
            absent = 2,
            leave = 1,
            late = 1
        )

        // VISUAL GRAPH 2: Weekly Attendance Performance Bar Chart
        AttendanceWeeklyBarChart(
            weeklyData = listOf(
                "Week 1" to 90f,
                "Week 2" to 87f,
                "Week 3" to 94f,
                "Week 4" to 91f,
                "Week 5" to 96f
            ),
            title = "📊 ACADEMY WEEKLY ATTENDANCE AVERAGE (%)"
        )

        // VISUAL GRAPH 3: Monthly Attendance Progression Trend Line
        AttendanceTrendLineChart(
            trendPoints = listOf(
                "Mar" to 82f,
                "Apr" to 85f,
                "May" to 89f,
                "Jun" to 86f,
                "Jul" to 91f,
                "Aug" to 89.4f
            )
        )

        // All 6 Dojo Batches + Special Training Attendance Comparison
        val batchColors = listOf(
            BeltGreen,
            GoldSecondary,
            BeltBlue,
            BeltBrown,
            BeltOrange,
            BeltPurple,
            CrimsonPrimary
        )

        val batchComparisonData: List<Triple<String, Int, Color>> = if (batchesList.isNotEmpty()) {
            val list = batchesList.mapIndexed { index, batch ->
                val color = batchColors.getOrElse(index) { GoldSecondary }
                val rate = when (index) {
                    0 -> 94
                    1 -> 88
                    2 -> 92
                    3 -> 85
                    4 -> 90
                    5 -> 87
                    else -> 89
                }
                val label = "Batch ${index + 1}: ${batch.batchName} (${batch.scheduleTiming})"
                Triple(label, rate, color)
            }.toMutableList()

            list.add(Triple("Special Intensive Training Camp (All Levels)", 96, CrimsonPrimary))
            list
        } else {
            listOf(
                Triple("Batch 1: Kata Training (Black Belts) (05:00-06:00 PM)", 94, BeltGreen),
                Triple("Batch 2: Beginners Training (White & Yellow) (06:00-08:00 PM)", 88, GoldSecondary),
                Triple("Batch 3: Morning Special Training (All Belts) (06:30-08:00 AM)", 92, BeltBlue),
                Triple("Batch 4: Kumite Sparring (Black Belts) (05:00-08:00 PM)", 85, BeltBrown),
                Triple("Batch 5: Kata Training (Colour Belts) (06:00-07:00 PM)", 90, BeltOrange),
                Triple("Batch 6: Kumite Sparring (Colour Belts) (07:00-08:00 PM)", 87, BeltPurple),
                Triple("Special Intensive Training Camp (Bootcamp / Masterclass)", 96, CrimsonPrimary)
            )
        }

        // VISUAL GRAPH 4: Batch Attendance Comparison (All 6 Batches + Special Training)
        BatchAttendanceComparisonChart(
            batchData = batchComparisonData,
            title = "📊 ALL 6 BATCHES & SPECIAL TRAINING ATTENDANCE (%)"
        )
    }
}

@Composable
fun AdminDojoRulesView(
    dojoRulesList: List<DojoRuleEntity>,
    onSaveDojoRule: (DojoRuleEntity) -> Unit,
    onDeleteDojoRule: (String) -> Unit
) {
    var showRuleDialog by remember { mutableStateOf(false) }
    var editingRule by remember { mutableStateOf<DojoRuleEntity?>(null) }

    var ruleNumberStr by remember { mutableStateOf("") }
    var ruleCategory by remember { mutableStateOf("Dojo Discipline & Etiquette") }
    var ruleDescription by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("DOJO RULES & ACADEMY STANDARDS", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                Text("Customize & publish official academy rules, belt expectations & instructions for all portals", style = MaterialTheme.typography.bodySmall, color = TextSlate)
            }

            Button(
                onClick = {
                    editingRule = null
                    ruleNumberStr = (dojoRulesList.size + 1).toString()
                    ruleCategory = "Dojo Discipline & Etiquette"
                    ruleDescription = ""
                    showRuleDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("add_dojo_rule_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Add Rule / Standard", color = TextOnAccent)
            }
        }

        if (dojoRulesList.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = CardWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Text("No Dojo rules configured yet. Click 'Add Rule / Standard' above to create official rules.", modifier = Modifier.padding(16.dp), color = TextSlate)
            }
        } else {
            dojoRulesList.sortedBy { it.ruleNumber }.forEach { rule ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = CardWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(ActiveNavBg)
                                    .border(1.dp, RoyalBlue, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("#${rule.ruleNumber}", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = RoyalBlue)
                            }

                            Column {
                                Text(rule.ruleText, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                Text("Category: ${rule.category}", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = {
                                    editingRule = rule
                                    ruleNumberStr = rule.ruleNumber.toString()
                                    ruleCategory = rule.category
                                    ruleDescription = rule.ruleText
                                    showRuleDialog = true
                                }
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Rule", tint = RoyalBlue, modifier = Modifier.size(18.dp))
                            }

                            IconButton(
                                onClick = { onDeleteDojoRule(rule.ruleId) }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Rule", tint = StatusDanger, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showRuleDialog) {
        AlertDialog(
            onDismissRequest = { showRuleDialog = false },
            title = { Text(if (editingRule != null) "Edit Dojo Rule & Standard" else "Add New Dojo Rule & Standard", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = ruleNumberStr,
                        onValueChange = { ruleNumberStr = it },
                        label = { Text("Rule Order Number (e.g. 1, 2, 3)") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = ruleCategory,
                        onValueChange = { ruleCategory = it },
                        label = { Text("Category (e.g. Dojo Discipline, Belts, Safety)") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = ruleDescription,
                        onValueChange = { ruleDescription = it },
                        label = { Text("Rule / Standard Instructions *") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (ruleDescription.isNotBlank()) {
                            val num = ruleNumberStr.toIntOrNull() ?: (dojoRulesList.size + 1)
                            val ruleObj = DojoRuleEntity(
                                ruleId = editingRule?.ruleId ?: "RULE-${System.currentTimeMillis()}",
                                ruleNumber = num,
                                ruleText = ruleDescription,
                                category = ruleCategory
                            )
                            onSaveDojoRule(ruleObj)
                            showRuleDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("save_dojo_rule_dialog_button")
                ) {
                    Text("Save Rule", color = TextOnAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRuleDialog = false }) { Text("Cancel", color = TextSlate) }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun AdminSpecialCampsView(
    eventsList: List<CalendarEventEntity>,
    feeItemsList: List<FeeItemEntity>,
    coachesList: List<UserAccountEntity>,
    batchesList: List<BatchEntity>,
    onSaveCalendarEvent: (CalendarEventEntity) -> Unit,
    onSaveFeeItem: (FeeItemEntity) -> Unit
) {
    var showCampDialog by remember { mutableStateOf(false) }
    var editingCamp by remember { mutableStateOf<CalendarEventEntity?>(null) }

    var titleVal by remember { mutableStateOf("") }
    var categoryVal by remember { mutableStateOf("Special Camp") }
    var startDateVal by remember { mutableStateOf("2026-08-20") }
    var endDateVal by remember { mutableStateOf("2026-08-25") }
    var timingVal by remember { mutableStateOf("06:00 AM – 09:00 AM") }
    var locationVal by remember { mutableStateOf("BROMA Main Dojo - Central Arena") }
    var coachVal by remember { mutableStateOf(coachesList.firstOrNull()?.fullName ?: "Sensei Rajesh") }
    var feeVal by remember { mutableStateOf("1500") }
    var feeScheduleVal by remember { mutableStateOf("Per Special Camp") }
    var capacityVal by remember { mutableStateOf("30") }
    var descVal by remember { mutableStateOf("") }
    var registrationEnabledVal by remember { mutableStateOf(true) }

    var saveToastMsg by remember { mutableStateOf<String?>(null) }

    val campEvents = eventsList.filter { 
        it.category.contains("Camp", ignoreCase = true) || 
        it.category.contains("Training", ignoreCase = true) ||
        it.category.contains("Workshop", ignoreCase = true) ||
        it.category.contains("Masterclass", ignoreCase = true)
    }.ifEmpty {
        eventsList
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("SPECIAL CAMPS & INTENSIVE TRAINING CENTRE", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = GoldSecondary)
                Text("Configure special camps, fee schedules, training sessions, and coach allocations without modifying code", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            Button(
                onClick = {
                    editingCamp = null
                    titleVal = ""
                    categoryVal = "Special Camp"
                    startDateVal = "2026-08-20"
                    endDateVal = "2026-08-25"
                    timingVal = "06:00 AM – 09:00 AM"
                    locationVal = "BROMA Main Dojo - Central Arena"
                    coachVal = coachesList.firstOrNull()?.fullName ?: "Sensei Rajesh"
                    feeVal = "1500"
                    feeScheduleVal = "Per Special Camp"
                    capacityVal = "30"
                    descVal = "Intensive Karate & Kumite training camp including kata perfection, sparring strategies, and physical conditioning."
                    registrationEnabledVal = true
                    showCampDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                modifier = Modifier.testTag("admin_add_special_camp_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Create Special Camp")
            }
        }

        saveToastMsg?.let { msg ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = BeltGreen.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, BeltGreen)
            ) {
                Text(msg, color = BeltGreen, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(10.dp))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            campEvents.forEach { event ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldSecondary.copy(alpha = 0.4f))
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(event.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                Text("Category: ${event.category} • Venue: ${event.location}", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CrimsonPrimary.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonPrimary)
                            ) {
                                Text(
                                    "₹${event.registrationFee.toInt()} (${if (event.registrationFee > 0) "Fee Schedule Configured" else "Free"})",
                                    color = GoldSecondary,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        HorizontalDivider(color = Color.DarkGray)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("📅 Dates:", style = MaterialTheme.typography.labelSmall, color = GoldSecondary)
                                Text("${event.startDate} to ${event.endDate ?: ""}", style = MaterialTheme.typography.bodySmall, color = Color.White)
                            }

                            Column {
                                Text("⏰ Timings:", style = MaterialTheme.typography.labelSmall, color = GoldSecondary)
                                Text(event.time, style = MaterialTheme.typography.bodySmall, color = Color.White)
                            }

                            Column {
                                Text("STATUS:", style = MaterialTheme.typography.labelSmall, color = GoldSecondary)
                                Text(
                                    if (event.isRegistrationEnabled) "🟢 Open for Enrollment" else "🔴 Registration Closed",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (event.isRegistrationEnabled) BeltGreen else CrimsonPrimary
                                )
                            }
                        }

                        if (event.description.isNotBlank()) {
                            Text("Description: ${event.description}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = {
                                    editingCamp = event
                                    titleVal = event.title
                                    categoryVal = event.category
                                    startDateVal = event.startDate
                                    endDateVal = event.endDate ?: "2026-08-25"
                                    timingVal = event.time
                                    locationVal = event.location
                                    coachVal = "Sensei Rajesh"
                                    feeVal = event.registrationFee.toInt().toString()
                                    feeScheduleVal = "Per Special Camp"
                                    capacityVal = "30"
                                    descVal = event.description
                                    registrationEnabledVal = event.isRegistrationEnabled
                                    showCampDialog = true
                                },
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldSecondary)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp), tint = GoldSecondary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit Camp & Fee Schedule", color = GoldSecondary, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCampDialog) {
        AlertDialog(
            onDismissRequest = { showCampDialog = false },
            title = { Text(if (editingCamp != null) "Edit Special Camp & Fee Schedule" else "Create Special Camp & Fee Schedule", color = GoldSecondary, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = titleVal,
                        onValueChange = { titleVal = it },
                        label = { Text("Special Camp / Training Title *") },
                        modifier = Modifier.fillMaxWidth().testTag("camp_title_input")
                    )

                    OutlinedTextField(
                        value = categoryVal,
                        onValueChange = { categoryVal = it },
                        label = { Text("Category (e.g. Special Camp, Intensive Training)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = startDateVal,
                            onValueChange = { startDateVal = it },
                            label = { Text("Start Date") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = endDateVal,
                            onValueChange = { endDateVal = it },
                            label = { Text("End Date") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = timingVal,
                        onValueChange = { timingVal = it },
                        label = { Text("Daily Timings / Duration (e.g. 06:00 AM - 09:00 AM)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = locationVal,
                        onValueChange = { locationVal = it },
                        label = { Text("Venue / Dojo Location") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = feeVal,
                            onValueChange = { feeVal = it },
                            label = { Text("Special Camp Fee (₹)") },
                            modifier = Modifier.weight(1f).testTag("camp_fee_input")
                        )
                        OutlinedTextField(
                            value = feeScheduleVal,
                            onValueChange = { feeScheduleVal = it },
                            label = { Text("Fee Schedule Frequency") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = coachVal,
                        onValueChange = { coachVal = it },
                        label = { Text("Assigned Lead Coach / Sensei") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = descVal,
                        onValueChange = { descVal = it },
                        label = { Text("Camp Rules & Curriculum Description") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Enable Student Enrollment:", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                        Switch(
                            checked = registrationEnabledVal,
                            onCheckedChange = { registrationEnabledVal = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CrimsonPrimary)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (titleVal.isNotBlank()) {
                            val feeDouble = feeVal.toDoubleOrNull() ?: 1500.0
                            val eventObj = CalendarEventEntity(
                                eventId = editingCamp?.eventId ?: "CAMP-${System.currentTimeMillis()}",
                                title = titleVal,
                                category = categoryVal,
                                startDate = startDateVal,
                                endDate = endDateVal,
                                time = timingVal,
                                location = locationVal,
                                description = descVal,
                                registrationFee = feeDouble,
                                isRegistrationEnabled = registrationEnabledVal
                            )
                            onSaveCalendarEvent(eventObj)

                            val feeItemObj = FeeItemEntity(
                                feeId = "FEE-CAMP-${System.currentTimeMillis()}",
                                feeCategory = titleVal,
                                amount = feeDouble,
                                frequency = feeScheduleVal,
                                description = "Special Camp Fee for $titleVal ($timingVal)"
                            )
                            onSaveFeeItem(feeItemObj)

                            saveToastMsg = "✓ Special Camp '$titleVal' and Fee Schedule (₹${feeDouble.toInt()}) created & synced across all portals!"
                            showCampDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                    modifier = Modifier.testTag("save_special_camp_dialog_button")
                ) {
                    Text("Save Camp & Fee Schedule")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCampDialog = false }) { Text("Cancel") }
            },
            containerColor = DarkBackground,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

