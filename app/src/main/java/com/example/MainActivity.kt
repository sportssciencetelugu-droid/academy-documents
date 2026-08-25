package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.UserRole
import com.example.ui.admin.AdminPortalLayout
import com.example.ui.auth.ForgotPasswordDialog
import com.example.ui.auth.LoginScreen
import com.example.ui.auth.RegisterScreen
import com.example.ui.auth.WelcomeScreen
import com.example.ui.coach.CoachPortalLayout
import com.example.ui.student.StudentPortalLayout
import com.example.ui.theme.BromaAcademyTheme
import com.example.ui.viewmodel.BromaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BromaAcademyTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BromaAcademyApp()
                }
            }
        }
    }
}

@Composable
fun BromaAcademyApp(viewModel: BromaViewModel = viewModel()) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val activeStudent by viewModel.activeStudent.collectAsStateWithLifecycle()

    val allUsersList by viewModel.allUsers.collectAsStateWithLifecycle()
    val studentsList by viewModel.allStudents.collectAsStateWithLifecycle()
    val siblingList by viewModel.parentChildren.collectAsStateWithLifecycle()
    val coachesList by viewModel.allCoaches.collectAsStateWithLifecycle()
    val batchesList by viewModel.allBatches.collectAsStateWithLifecycle()
    val attendanceList by viewModel.currentStudentAttendance.collectAsStateWithLifecycle()
    val paymentsList by viewModel.currentStudentPayments.collectAsStateWithLifecycle()
    val allPaymentsList by viewModel.allPaymentRecords.collectAsStateWithLifecycle()
    val feeItemsList by viewModel.allFeeItems.collectAsStateWithLifecycle()
    val certificatesList by viewModel.currentStudentCertificates.collectAsStateWithLifecycle()
    val allCertificatesList by viewModel.allCertificates.collectAsStateWithLifecycle()
    val eventsList by viewModel.allCalendarEvents.collectAsStateWithLifecycle()
    val announcementsList by viewModel.allAnnouncements.collectAsStateWithLifecycle()
    val requestsList by viewModel.allStudentRequests.collectAsStateWithLifecycle()
    val adminSettings by viewModel.adminSettings.collectAsStateWithLifecycle()
    val dojoRulesList by viewModel.allDojoRules.collectAsStateWithLifecycle()
    val allAchievementsList by viewModel.allAchievements.collectAsStateWithLifecycle()
    val studentAchievementsList by viewModel.currentStudentAchievements.collectAsStateWithLifecycle()
    val allTournamentsList by viewModel.allTournaments.collectAsStateWithLifecycle()
    val publishedTournamentsList by viewModel.publishedTournaments.collectAsStateWithLifecycle()
    val allLeadershipList by viewModel.allLeadership.collectAsStateWithLifecycle()
    val allStandardsList by viewModel.allStandards.collectAsStateWithLifecycle()
    val studentChatMessages by viewModel.currentStudentChatMessages.collectAsStateWithLifecycle()
    val allChatMessages by viewModel.allChatMessages.collectAsStateWithLifecycle()
    val allTrainingPrograms by viewModel.allTrainingPrograms.collectAsStateWithLifecycle()
    val coachAvailabilities by viewModel.allCoachAvailabilities.collectAsStateWithLifecycle()

    var authScreenState by remember { mutableStateOf("welcome") }
    var loginErrorMsg by remember { mutableStateOf<String?>(null) }
    var showForgotDialog by remember { mutableStateOf(false) }

    if (showForgotDialog) {
        ForgotPasswordDialog(
            onDismiss = { showForgotDialog = false },
            onSubmit = { email ->
                showForgotDialog = false
            }
        )
    }

    if (currentUser == null) {
        // Auth Navigation Flow
        when (authScreenState) {
            "welcome" -> WelcomeScreen(
                onNavigateToLogin = { authScreenState = "login" },
                onNavigateToRegister = { authScreenState = "register" },
                onForgotPasswordClick = { showForgotDialog = true },
                leadershipList = allLeadershipList,
                onQuickDemoLogin = { identifier, pass ->
                    viewModel.login(identifier, pass) { success, msg ->
                        if (!success) {
                            loginErrorMsg = msg
                        }
                    }
                }
            )
            "login" -> LoginScreen(
                onLoginClick = { identifier, pass ->
                    viewModel.login(identifier, pass) { success, msg ->
                        if (!success) {
                            loginErrorMsg = msg
                        }
                    }
                },
                onBackClick = { authScreenState = "welcome" },
                onForgotPasswordClick = { showForgotDialog = true },
                leadershipList = allLeadershipList,
                onQuickDemoLogin = { identifier, pass ->
                    viewModel.login(identifier, pass) { success, msg ->
                        if (!success) {
                            loginErrorMsg = msg
                        }
                    }
                }
            )
            "register" -> RegisterScreen(
                onRegisterSubmit = { email, username, pass, fullName, parentEmail, phone ->
                    viewModel.registerStudent(email, username, pass, fullName, parentEmail, phone) { success, msg ->
                        if (!success) {
                            loginErrorMsg = msg
                        } else {
                            authScreenState = "login"
                        }
                    }
                },
                onRegisterCoachSubmit = { email, username, pass, fullName, phone, spec, exp ->
                    viewModel.registerCoach(email, username, pass, fullName, phone, spec, exp) { success, msg ->
                        loginErrorMsg = msg
                        if (success) {
                            authScreenState = "login"
                        }
                    }
                },
                onBackClick = { authScreenState = "welcome" },
                leadershipList = allLeadershipList
            )
            else -> WelcomeScreen(
                onNavigateToLogin = { authScreenState = "login" },
                onNavigateToRegister = { authScreenState = "register" },
                onForgotPasswordClick = { showForgotDialog = true },
                leadershipList = allLeadershipList,
                onQuickDemoLogin = { identifier, pass ->
                    viewModel.login(identifier, pass) { success, msg ->
                        if (!success) {
                            loginErrorMsg = msg
                        }
                    }
                }
            )
        }
    } else {
        val activeUser = currentUser!!
        val studentAccount = activeStudent ?: activeUser

        when (activeUser.role) {
            UserRole.STUDENT -> {
                StudentPortalLayout(
                    student = studentAccount,
                    siblingList = siblingList,
                    coachesList = coachesList,
                    batchesList = batchesList,
                    attendanceList = attendanceList,
                    paymentsList = paymentsList,
                    feeItemsList = feeItemsList,
                    certificatesList = certificatesList,
                    eventsList = eventsList,
                    announcementsList = announcementsList,
                    requestsList = requestsList,
                    adminSettings = adminSettings,
                    allStudentsList = studentsList,
                    tournamentsList = publishedTournamentsList,
                    leadershipList = allLeadershipList,
                    standardsList = allStandardsList,
                    chatMessages = studentChatMessages,
                    trainingPrograms = allTrainingPrograms,
                    onSendMessageToAdmin = { text ->
                        viewModel.sendChatMessage(
                            senderId = studentAccount.userId,
                            senderName = studentAccount.fullName,
                            senderRole = "STUDENT",
                            recipientId = "ADMIN",
                            recipientRole = "ADMIN",
                            text = text,
                            isFromAdmin = false
                        )
                    },
                    onChildSwitch = { child -> viewModel.switchChild(child) },
                    onSavePersonalDetails = { phone, email, addr, emergency ->
                        viewModel.updateStudentPersonalProfile(phone, email, addr, emergency) {}
                    },
                    onRequestChangeSubmit = { reqType, current, target, reason ->
                        viewModel.submitStudentRequest(reqType, current, target, reason) {}
                    },
                    onEnrollBatch = { batch -> viewModel.enrollStudentInBatch(batch) {} },
                    onEnrollSpecialTraining = { progName, fee ->
                        viewModel.enrollSpecialTraining(progName, fee) {}
                    },
                    onSubmitPaymentRef = { feeCat, month, amt, method, ref ->
                        viewModel.submitPaymentReference(feeCat, month, amt, method, ref) {}
                    },
                    onSubmitCertificate = { title, cat, date, org ->
                        viewModel.submitCertificate(title, cat, date, org) {}
                    },
                    onUpdateClassAndTraining = { batch, progStr ->
                        viewModel.updateStudentClassAndTraining(batch, progStr) {}
                    },
                    onSaveCalendarEvent = { evt -> viewModel.saveCalendarEvent(evt) {} },
                    onSaveStudentProfile = { updated -> viewModel.updateStudentProfile(updated) {} },
                    onLogout = { viewModel.logout() }
                )
            }
            UserRole.COACH -> {
                CoachPortalLayout(
                    coach = activeUser,
                    studentsList = studentsList,
                    batchesList = batchesList,
                    requestsList = requestsList,
                    paymentsList = paymentsList,
                    eventsList = eventsList,
                    announcementsList = announcementsList,
                    chatMessages = allChatMessages,
                    availabilitiesList = coachAvailabilities,
                    onSaveAttendance = { batchId, date, map, time ->
                        viewModel.recordBatchAttendance(batchId, date, map, time) {}
                    },
                    onProcessRequest = { req, approve ->
                        viewModel.processStudentRequest(req, approve)
                    },
                    onGrantSpecialTimePermission = { stId, slot, reason, grantedBy ->
                        viewModel.grantSpecialTimePermission(stId, slot, reason, grantedBy)
                    },
                    onSaveCoachProfile = { updatedCoach ->
                        viewModel.updateCoachProfile(updatedCoach)
                    },
                    onSaveAvailability = { avail ->
                        viewModel.saveCoachAvailability(avail)
                    },
                    onDeleteAvailability = { id ->
                        viewModel.deleteCoachAvailability(id)
                    },
                    onSendMessageToAdmin = { text ->
                        viewModel.sendChatMessage(
                            senderId = activeUser.userId,
                            senderName = activeUser.fullName,
                            senderRole = "COACH",
                            recipientId = "ADMIN",
                            recipientRole = "ADMIN",
                            text = text,
                            isFromAdmin = false
                        )
                    },
                    onLogout = { viewModel.logout() }
                )
            }
            UserRole.ADMIN -> {
                AdminPortalLayout(
                    adminUser = activeUser,
                    allUsersList = allUsersList,
                    studentsList = studentsList,
                    coachesList = coachesList,
                    batchesList = batchesList,
                    feeItemsList = feeItemsList,
                    paymentsList = allPaymentsList,
                    certificatesList = allCertificatesList,
                    eventsList = eventsList,
                    announcementsList = announcementsList,
                    requestsList = requestsList,
                    adminSettings = adminSettings,
                    dojoRulesList = dojoRulesList,
                    achievementsList = allAchievementsList,
                    tournamentsList = allTournamentsList,
                    leadershipList = allLeadershipList,
                    standardsList = allStandardsList,
                    chatMessages = allChatMessages,
                    trainingPrograms = allTrainingPrograms,
                    onSaveStudent = { student -> viewModel.saveStudent(student) {} },
                    onSaveCoach = { coach -> viewModel.saveCoach(coach) {} },
                    onApproveCoach = { coachId -> viewModel.approveCoachAccount(coachId) {} },
                    onDeleteCoach = { coachId -> viewModel.deleteUser(coachId) },
                    onSaveBatch = { batch -> viewModel.saveBatch(batch) {} },
                    onVerifyPayment = { receiptNo -> viewModel.verifyPayment(receiptNo, "PAID") },
                    onVerifyPaymentWithStatus = { receiptNo, status -> viewModel.verifyPayment(receiptNo, status) },
                    onAddManualPayment = { record -> viewModel.addManualPayment(record) },
                    onProcessRequest = { req, approve -> viewModel.processStudentRequest(req, approve) },
                    onGrantSpecialTimePermission = { stId, slot, reason, grantedBy ->
                        viewModel.grantSpecialTimePermission(stId, slot, reason, grantedBy)
                    },
                    onProcessCertReview = { cert, approve -> viewModel.processCertificateReview(cert, approve) },
                    onApproveAchievement = { id -> viewModel.approveAchievement(id) },
                    onDeleteAchievement = { id -> viewModel.deleteAchievement(id) },
                    onSaveAchievement = { ach -> viewModel.saveAchievement(ach) },
                    onSaveTournament = { trn -> viewModel.saveTournament(trn) },
                    onDeleteTournament = { trnId -> viewModel.deleteTournament(trnId) },
                    onTogglePublishTournament = { trn -> viewModel.togglePublishTournament(trn) },
                    onSaveLeadership = { lead -> viewModel.saveLeadership(lead) },
                    onDeleteLeadership = { leadId -> viewModel.deleteLeadership(leadId) },
                    onSaveStandard = { std -> viewModel.saveStandard(std) },
                    onDeleteStandard = { stdId -> viewModel.deleteStandard(stdId) },
                    onSaveCalendarEvent = { evt -> viewModel.saveCalendarEvent(evt) {} },
                    onSaveFeeItem = { feeItem -> viewModel.saveFeeItem(feeItem) {} },
                    onSaveAnnouncement = { ann -> viewModel.saveAnnouncement(ann) {} },
                    onSaveSettings = { settings -> viewModel.saveAdminSettings(settings) {} },
                    onSaveDojoRule = { rule -> viewModel.saveDojoRule(rule) },
                    onDeleteDojoRule = { ruleId -> viewModel.deleteDojoRule(ruleId) },
                    onSendChatMessage = { senderId, senderName, senderRole, recipientId, recipientRole, text ->
                        viewModel.sendChatMessage(
                            senderId = senderId,
                            senderName = senderName,
                            senderRole = senderRole,
                            recipientId = recipientId,
                            recipientRole = recipientRole,
                            text = text,
                            isFromAdmin = true
                        )
                    },
                    onDeleteChatMessage = { id ->
                        viewModel.deleteChatMessage(id)
                    },
                    onSaveTrainingProgram = { prog ->
                        viewModel.saveTrainingProgram(prog)
                    },
                    onDeleteTrainingProgram = { progId ->
                        viewModel.deleteTrainingProgram(progId)
                    },
                    onLogout = { viewModel.logout() }
                )
            }
        }
    }
}
