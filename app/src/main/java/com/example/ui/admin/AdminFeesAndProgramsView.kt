package com.example.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.data.FeeItemEntity
import com.example.data.PaymentRecordEntity
import com.example.data.TrainingProgramEntity
import com.example.data.UserAccountEntity
import com.example.data.UserRole
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun AdminFeesAndProgramsManagementView(
    feeItemsList: List<FeeItemEntity>,
    trainingPrograms: List<TrainingProgramEntity>,
    studentsList: List<UserAccountEntity> = emptyList(),
    paymentsList: List<PaymentRecordEntity> = emptyList(),
    onSaveFeeItem: (FeeItemEntity) -> Unit,
    onSaveTrainingProgram: (TrainingProgramEntity) -> Unit,
    onDeleteTrainingProgram: (String) -> Unit,
    onAddManualPayment: (PaymentRecordEntity) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf("FEES") } // "FEES", "PROGRAMS", or "REGULAR_HISTORY"
    var showAddFeeDialog by remember { mutableStateOf(false) }
    var showAddProgramDialog by remember { mutableStateOf(false) }
    var showRecordPaymentDialogForStudent by remember { mutableStateOf<UserAccountEntity?>(null) }
    var selectedProgram by remember { mutableStateOf<TrainingProgramEntity?>(null) }
    var selectedFeeItem by remember { mutableStateOf<FeeItemEntity?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    if (showAddFeeDialog) {
        AddEditFeeItemDialog(
            feeItem = selectedFeeItem,
            onDismiss = {
                showAddFeeDialog = false
                selectedFeeItem = null
            },
            onSave = { fee ->
                onSaveFeeItem(fee)
                successMessage = "Fee structure for '${fee.feeName}' updated successfully! All portals synced in real-time."
                showAddFeeDialog = false
                selectedFeeItem = null
            }
        )
    }

    if (showAddProgramDialog) {
        AddEditTrainingProgramDialog(
            program = selectedProgram,
            onDismiss = {
                showAddProgramDialog = false
                selectedProgram = null
            },
            onSave = { prog ->
                onSaveTrainingProgram(prog)
                successMessage = "Training program '${prog.programName}' saved successfully! All portals synced in real-time."
                showAddProgramDialog = false
                selectedProgram = null
            }
        )
    }

    if (showRecordPaymentDialogForStudent != null) {
        val student = showRecordPaymentDialogForStudent!!
        RecordStudentFeeDialog(
            student = student,
            onDismiss = { showRecordPaymentDialogForStudent = null },
            onSave = { payment ->
                onAddManualPayment(payment)
                successMessage = "Recorded payment of ₹${payment.amount.toInt()} for ${student.fullName} (${payment.feeCategory})."
                showRecordPaymentDialogForStudent = null
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
        // Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = CardWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
            shadowElevation = 2.dp
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = RoyalBlue)
                        Text(
                            text = "FEES STRUCTURE & TRAINING PROGRAMS",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextNavy
                        )
                    }

                    if (selectedTab != "REGULAR_HISTORY") {
                        Button(
                            onClick = {
                                if (selectedTab == "FEES") {
                                    selectedFeeItem = null
                                    showAddFeeDialog = true
                                } else {
                                    selectedProgram = null
                                    showAddProgramDialog = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("add_fee_or_program_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (selectedTab == "FEES") "Add Fee Item" else "Add Program")
                        }
                    }
                }

                Text(
                    text = "Manage editable academy fee structures, training programs, and regular students' monthly training fees, admission fee, and dress/uniform history.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSlate
                )

                // Sub-tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedTab == "FEES",
                        onClick = { selectedTab = "FEES" },
                        label = { Text("Fees Structure (${feeItemsList.size})", fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RoyalBlue,
                            selectedLabelColor = TextOnAccent
                        )
                    )
                    FilterChip(
                        selected = selectedTab == "PROGRAMS",
                        onClick = { selectedTab = "PROGRAMS" },
                        label = { Text("Training Programs (${trainingPrograms.size})", fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RoyalBlue,
                            selectedLabelColor = TextOnAccent
                        )
                    )
                    FilterChip(
                        selected = selectedTab == "REGULAR_HISTORY",
                        onClick = { selectedTab = "REGULAR_HISTORY" },
                        label = { Text("📋 Regular Training Monthly Fees Register", fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CrimsonPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        if (successMessage != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = StatusSuccessBg,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccessBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusSuccess)
                        Text(successMessage!!, style = MaterialTheme.typography.bodySmall, color = StatusSuccessText)
                    }
                    IconButton(onClick = { successMessage = null }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = StatusSuccessText, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        when (selectedTab) {
            "FEES" -> {
                // Fee Items List
                if (feeItemsList.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = CardWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                    ) {
                        Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No fee items created yet. Click 'Add Fee Item' to set tuition and exam fees.", color = TextSlate)
                        }
                    }
                } else {
                    feeItemsList.forEach { item ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("fee_item_card_${item.feeItemId}"),
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
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(item.feeName, fontWeight = FontWeight.Bold, color = TextNavy, style = MaterialTheme.typography.titleSmall)
                                        Surface(shape = RoundedCornerShape(4.dp), color = StatusInfoBg) {
                                            Text(item.category, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = StatusInfoText)
                                        }
                                    }
                                    Text(item.description, color = TextSlate, style = MaterialTheme.typography.bodySmall)
                                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                        Text("Due: ${item.dueDate}", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                                        if (item.lateFee > 0) {
                                            Text("Late Fee: ₹${item.lateFee.toInt()}", style = MaterialTheme.typography.labelSmall, color = StatusWarningText)
                                        }
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        "₹${item.amount.toInt()}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = StatusSuccess
                                    )
                                    IconButton(
                                        onClick = {
                                            selectedFeeItem = item
                                            showAddFeeDialog = true
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = RoyalBlue, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            "PROGRAMS" -> {
                // Training Programs List
                if (trainingPrograms.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = CardWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                    ) {
                        Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No training programs registered. Click 'Add Program' to create one.", color = TextSlate)
                        }
                    }
                } else {
                    trainingPrograms.forEach { prog ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("training_program_card_${prog.programId}"),
                            shape = RoundedCornerShape(12.dp),
                            color = CardWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (prog.isActive) StatusSuccessBorder else BorderLight),
                            shadowElevation = 1.dp
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
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (prog.isActive) StatusSuccessBg else StatusWarningBg
                                        ) {
                                            Text(
                                                text = if (prog.isActive) "ACTIVE" else "INACTIVE",
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = if (prog.isActive) StatusSuccessText else StatusWarningText
                                            )
                                        }
                                        Text(prog.programName, fontWeight = FontWeight.Bold, color = TextNavy, style = MaterialTheme.typography.titleMedium)
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        IconButton(
                                            onClick = {
                                                selectedProgram = prog
                                                showAddProgramDialog = true
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = RoyalBlue, modifier = Modifier.size(18.dp))
                                        }
                                        IconButton(
                                            onClick = {
                                                onDeleteTrainingProgram(prog.programId)
                                                successMessage = "Program '${prog.programName}' removed."
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusError, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }

                                Text(prog.description, color = TextSlate, style = MaterialTheme.typography.bodySmall)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(Icons.Default.Payments, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(14.dp))
                                        Text("Monthly Fee: ₹${prog.monthlyFee.toInt()}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = StatusSuccess)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(Icons.Default.Timer, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(14.dp))
                                        Text("${prog.daysPerWeek} • ${prog.durationMonths} Months", style = MaterialTheme.typography.bodySmall, color = TextNavy)
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Text("Age Group: ${prog.ageGroup}", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                                    Text("Instructor: ${prog.coachName}", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                                }
                            }
                        }
                    }
                }
            }
            "REGULAR_HISTORY" -> {
                // Regular Students Monthly Fee History View
                RegularStudentMonthlyFeeHistorySection(
                    studentsList = studentsList.filter { it.role == UserRole.STUDENT },
                    paymentsList = paymentsList,
                    onRecordPaymentForStudent = { student ->
                        showRecordPaymentDialogForStudent = student
                    }
                )
            }
        }
    }
}

@Composable
fun RegularStudentMonthlyFeeHistorySection(
    studentsList: List<UserAccountEntity>,
    paymentsList: List<PaymentRecordEntity>,
    onRecordPaymentForStudent: (UserAccountEntity) -> Unit
) {
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val currentMonthIndex = remember {
        val cal = Calendar.getInstance()
        cal.get(Calendar.MONTH).coerceIn(0, 11)
    }
    var selectedMonth by remember { mutableStateOf(months[currentMonthIndex]) }
    var searchQuery by remember { mutableStateOf("") }
    var filterStatus by remember { mutableStateOf("ALL") } // ALL, PAID, DUE, ADMISSION_DUE, DRESS_DUE
    var viewMode by remember { mutableStateOf("TABLE") } // "TABLE" or "CARDS"

    val totalStudents = studentsList.size

    val paidStudentsCount = studentsList.count { student ->
        paymentsList.any { p ->
            (p.studentName.contains(student.fullName, ignoreCase = true) || p.studentId == student.userId) &&
                    p.status == "PAID" &&
                    (p.feeCategory.contains("Monthly", ignoreCase = true) || p.feeCategory.contains(selectedMonth, ignoreCase = true) || p.month.contains(selectedMonth, ignoreCase = true))
        }
    }

    val dueStudentsCount = (totalStudents - paidStudentsCount).coerceAtLeast(0)

    val admissionPaidCount = studentsList.count { student ->
        paymentsList.any { p ->
            (p.studentName.contains(student.fullName, ignoreCase = true) || p.studentId == student.userId) &&
                    p.status == "PAID" &&
                    (p.feeCategory.contains("Admission", ignoreCase = true) || p.month.contains("Admission", ignoreCase = true))
        }
    }

    val dressPaidCount = studentsList.count { student ->
        paymentsList.any { p ->
            (p.studentName.contains(student.fullName, ignoreCase = true) || p.studentId == student.userId) &&
                    p.status == "PAID" &&
                    (p.feeCategory.contains("Dress", ignoreCase = true) || p.feeCategory.contains("Uniform", ignoreCase = true) || p.feeCategory.contains("Gi", ignoreCase = true))
        }
    }

    val filteredStudents = studentsList.filter { student ->
        val matchesQuery = student.fullName.contains(searchQuery, ignoreCase = true) ||
                student.userId.contains(searchQuery, ignoreCase = true) ||
                student.phone.contains(searchQuery, ignoreCase = true) ||
                student.currentBelt.contains(searchQuery, ignoreCase = true)

        val isPaidThisMonth = paymentsList.any { p ->
            (p.studentName.contains(student.fullName, ignoreCase = true) || p.studentId == student.userId) &&
                    p.status == "PAID" &&
                    (p.feeCategory.contains("Monthly", ignoreCase = true) || p.feeCategory.contains(selectedMonth, ignoreCase = true) || p.month.contains(selectedMonth, ignoreCase = true))
        }

        val isAdmissionPaid = paymentsList.any { p ->
            (p.studentName.contains(student.fullName, ignoreCase = true) || p.studentId == student.userId) &&
                    p.status == "PAID" &&
                    (p.feeCategory.contains("Admission", ignoreCase = true) || p.month.contains("Admission", ignoreCase = true))
        }

        val isDressPaid = paymentsList.any { p ->
            (p.studentName.contains(student.fullName, ignoreCase = true) || p.studentId == student.userId) &&
                    p.status == "PAID" &&
                    (p.feeCategory.contains("Dress", ignoreCase = true) || p.feeCategory.contains("Uniform", ignoreCase = true) || p.feeCategory.contains("Gi", ignoreCase = true))
        }

        val matchesFilter = when (filterStatus) {
            "PAID" -> isPaidThisMonth
            "DUE" -> !isPaidThisMonth
            "ADMISSION_DUE" -> !isAdmissionPaid
            "DRESS_DUE" -> !isDressPaid
            else -> true
        }

        matchesQuery && matchesFilter
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Explanatory Guidance Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFEFF6FF),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(RoyalBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        "REGULAR TRAINING MONTHLY FEES REGISTER (₹2,000 / Month)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextNavy
                    )
                    Text(
                        "Track regular academy students across all 12 calendar months. Includes Karate training, Physical fitness, Practical self defence, and Weapon training. Tap any month or 'Record' button to mark fees.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextNavy.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Summary KPI Strip with Progress Indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = CardWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("TOTAL REGULAR STUDENTS", style = MaterialTheme.typography.labelSmall, color = TextSlate, fontWeight = FontWeight.SemiBold)
                    Text("$totalStudents", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                    Text("Academy Enrolled", fontSize = 10.sp, color = TextSlate)
                }
            }

            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = CardWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccessBorder),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("$selectedMonth FEES COLLECTED", style = MaterialTheme.typography.labelSmall, color = StatusSuccessText, fontWeight = FontWeight.SemiBold)
                    Text("₹${paidStudentsCount * 2000}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = StatusSuccess)
                    Text("$paidStudentsCount of $totalStudents Paid (100%)", fontSize = 10.sp, color = StatusSuccessText)
                }
            }

            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = CardWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (dueStudentsCount > 0) StatusErrorBorder else BorderLight),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("$selectedMonth PENDING / DUE", style = MaterialTheme.typography.labelSmall, color = if (dueStudentsCount > 0) StatusErrorText else TextSlate, fontWeight = FontWeight.SemiBold)
                    Text("₹${dueStudentsCount * 2000}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = if (dueStudentsCount > 0) StatusError else TextNavy)
                    Text("$dueStudentsCount Students Pending", fontSize = 10.sp, color = if (dueStudentsCount > 0) StatusErrorText else TextSlate)
                }
            }
        }

        // Monthly Collection Progress Bar
        val collectionPercentage = if (totalStudents > 0) (paidStudentsCount.toFloat() / totalStudents.toFloat()) else 1f
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = CardWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Monthly Collection Progress ($selectedMonth 2026)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextNavy
                    )
                    Text(
                        "${(collectionPercentage * 100).toInt()}% Collected",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (collectionPercentage >= 0.8f) StatusSuccess else CrimsonPrimary
                    )
                }
                LinearProgressIndicator(
                    progress = { collectionPercentage },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (collectionPercentage >= 0.8f) StatusSuccess else CrimsonPrimary,
                    trackColor = Color(0xFFE2E8F0)
                )
            }
        }

        // Admission & Uniform Fee KPI Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                color = CardWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🥋", fontSize = 20.sp)
                    Column {
                        Text("Karate Dress / Gi Fee (₹1,500)", style = MaterialTheme.typography.labelSmall, color = TextNavy, fontWeight = FontWeight.SemiBold)
                        Text("$dressPaidCount / $totalStudents Students Paid", fontSize = 11.sp, color = if (dressPaidCount == totalStudents) StatusSuccess else StatusWarningText)
                    }
                }
            }

            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                color = CardWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("📝", fontSize = 20.sp)
                    Column {
                        Text("One-Time Admission Fee (₹1,000)", style = MaterialTheme.typography.labelSmall, color = TextNavy, fontWeight = FontWeight.SemiBold)
                        Text("$admissionPaidCount / $totalStudents Students Paid", fontSize = 11.sp, color = if (admissionPaidCount == totalStudents) StatusSuccess else StatusWarningText)
                    }
                }
            }
        }

        // Month Selector Bar (2026 Academic & Training Year)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = CardWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("SELECT TRAINING MONTH (YEAR 2026)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                    Text("Selected: $selectedMonth 2026", fontSize = 11.sp, color = RoyalBlue, fontWeight = FontWeight.Bold)
                }

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(months) { month ->
                        val isSelected = selectedMonth == month
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) CrimsonPrimary else Color(0xFFF1F5F9),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) CrimsonPrimary else Color.Transparent),
                            modifier = Modifier.clickable { selectedMonth = month }
                        ) {
                            Text(
                                text = month,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextNavy,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Search and Filter Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search regular student by name, ID, phone...", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSlate, modifier = Modifier.size(16.dp)) },
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )
        }

        // Filter chips & View Switcher Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "ALL" to "All Trainees (${studentsList.size})",
                    "PAID" to "Paid $selectedMonth ($paidStudentsCount)",
                    "DUE" to "Due $selectedMonth ($dueStudentsCount)",
                    "ADMISSION_DUE" to "Admission Due (${studentsList.size - admissionPaidCount})",
                    "DRESS_DUE" to "Dress Due (${studentsList.size - dressPaidCount})"
                ).forEach { (fKey, fLabel) ->
                    val isSelected = filterStatus == fKey
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) RoyalBlue else Color(0xFFF1F5F9),
                        modifier = Modifier.clickable { filterStatus = fKey }
                    ) {
                        Text(
                            text = fLabel,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else TextNavy,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // View Mode Toggle (Table / Cards)
            Row(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                    .padding(2.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (viewMode == "TABLE") CrimsonPrimary else Color.Transparent,
                    modifier = Modifier.clickable { viewMode = "TABLE" }
                ) {
                    Text(
                        "📊 Table",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (viewMode == "TABLE") Color.White else TextNavy,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (viewMode == "CARDS") CrimsonPrimary else Color.Transparent,
                    modifier = Modifier.clickable { viewMode = "CARDS" }
                ) {
                    Text(
                        "🎴 Cards",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (viewMode == "CARDS") Color.White else TextNavy,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        if (viewMode == "TABLE" && filteredStudents.isNotEmpty()) {
            // Structured Table View showing Student, Month Fee, Admission Fee, Dress Fee, Total Status & Action
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = CardWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "REGULAR STUDENTS FEE BREAKDOWN TABLE ($selectedMonth 2026)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = CrimsonPrimary
                    )

                    // Horizontal Scrollable Table for clear column layout
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Table Header Row
                            Row(
                                modifier = Modifier
                                    .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
                                    .padding(vertical = 8.dp, horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("STUDENT", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy, modifier = Modifier.width(170.dp))
                                Text("MONTHLY HISTORY (JAN - DEC)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue, modifier = Modifier.width(360.dp))
                                Text("$selectedMonth FEE (₹2K)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy, modifier = Modifier.width(130.dp))
                                Text("ADMISSION (₹1K)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy, modifier = Modifier.width(120.dp))
                                Text("DRESS / GI (₹1.5K)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy, modifier = Modifier.width(125.dp))
                                Text("SUMMARY", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy, modifier = Modifier.width(110.dp))
                                Text("ACTION", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy, modifier = Modifier.width(90.dp))
                            }

                            HorizontalDivider(color = BorderLight)

                            // Table Student Rows
                            filteredStudents.forEach { student ->
                                val studentPayments = paymentsList.filter {
                                    it.studentName.contains(student.fullName, ignoreCase = true) || it.studentId == student.userId
                                }

                                val admissionPayment = studentPayments.firstOrNull {
                                    (it.feeCategory.contains("Admission", ignoreCase = true) || it.month.contains("Admission", ignoreCase = true)) && it.status == "PAID"
                                }

                                val dressPayment = studentPayments.firstOrNull {
                                    (it.feeCategory.contains("Dress", ignoreCase = true) || it.feeCategory.contains("Uniform", ignoreCase = true) || it.feeCategory.contains("Gi", ignoreCase = true)) && it.status == "PAID"
                                }

                                val selectedMonthPayment = studentPayments.firstOrNull {
                                    (it.feeCategory.contains("Monthly", ignoreCase = true) || it.feeCategory.contains(selectedMonth, ignoreCase = true) || it.month.contains(selectedMonth, ignoreCase = true)) &&
                                            it.status == "PAID"
                                }

                                val isPendingVerification = studentPayments.any {
                                    (it.feeCategory.contains("Monthly", ignoreCase = true) || it.feeCategory.contains(selectedMonth, ignoreCase = true) || it.month.contains(selectedMonth, ignoreCase = true)) &&
                                            it.status == "VERIFICATION_PENDING"
                                }

                                val allPaid = (selectedMonthPayment != null) && (admissionPayment != null) && (dressPayment != null)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(if (allPaid) StatusSuccessBg.copy(alpha = 0.3f) else Color.Transparent, RoundedCornerShape(4.dp))
                                        .padding(vertical = 6.dp, horizontal = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Student Column
                                    Column(modifier = Modifier.width(170.dp)) {
                                        Text(student.fullName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextNavy, maxLines = 1)
                                        Text("${student.userId} • ${student.currentBelt}", fontSize = 10.sp, color = RoyalBlue)
                                    }

                                    // Interconnected Monthly History Column (Jan to Dec)
                                    Row(
                                        modifier = Modifier.width(360.dp),
                                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        months.forEach { m ->
                                            val mPaid = studentPayments.any {
                                                (it.month.contains(m, ignoreCase = true) || it.feeCategory.contains(m, ignoreCase = true)) && it.status == "PAID"
                                            }
                                            val mPending = !mPaid && studentPayments.any {
                                                (it.month.contains(m, ignoreCase = true) || it.feeCategory.contains(m, ignoreCase = true)) && it.status == "VERIFICATION_PENDING"
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(3.dp),
                                                color = when {
                                                    mPaid -> StatusSuccessBg
                                                    mPending -> StatusWarningBg
                                                    else -> Color(0xFFF1F5F9)
                                                },
                                                border = androidx.compose.foundation.BorderStroke(
                                                    0.5.dp,
                                                    when {
                                                        mPaid -> StatusSuccessBorder
                                                        mPending -> StatusWarningBorder
                                                        else -> BorderLight
                                                    }
                                                ),
                                                modifier = Modifier.clickable {
                                                    onRecordPaymentForStudent(student)
                                                }
                                            ) {
                                                Text(
                                                    text = if (mPaid) "$m✓" else if (mPending) "$m⏳" else m,
                                                    fontSize = 8.5.sp,
                                                    fontWeight = if (mPaid) FontWeight.Bold else FontWeight.Normal,
                                                    color = when {
                                                        mPaid -> StatusSuccessText
                                                        mPending -> StatusWarningText
                                                        else -> TextSlate
                                                    },
                                                    modifier = Modifier.padding(horizontal = 2.5.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    // Selected Month Column
                                    Box(modifier = Modifier.width(130.dp)) {
                                        if (selectedMonthPayment != null) {
                                            Surface(shape = RoundedCornerShape(4.dp), color = StatusSuccessBg, border = androidx.compose.foundation.BorderStroke(0.5.dp, StatusSuccessBorder)) {
                                                Text("✅ PAID (₹${selectedMonthPayment.amount.toInt()})", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusSuccessText, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                        } else if (isPendingVerification) {
                                            Surface(shape = RoundedCornerShape(4.dp), color = StatusWarningBg, border = androidx.compose.foundation.BorderStroke(0.5.dp, StatusWarningBorder)) {
                                                Text("⏳ PENDING", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusWarningText, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                        } else {
                                            Surface(shape = RoundedCornerShape(4.dp), color = StatusErrorBg, border = androidx.compose.foundation.BorderStroke(0.5.dp, StatusErrorBorder)) {
                                                Text("❌ DUE (₹2,000)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusErrorText, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                        }
                                    }

                                    // Admission Fee Column
                                    Box(modifier = Modifier.width(120.dp)) {
                                        if (admissionPayment != null) {
                                            Surface(shape = RoundedCornerShape(4.dp), color = StatusSuccessBg, border = androidx.compose.foundation.BorderStroke(0.5.dp, StatusSuccessBorder)) {
                                                Text("✅ PAID", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusSuccessText, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                        } else {
                                            Surface(shape = RoundedCornerShape(4.dp), color = StatusWarningBg, border = androidx.compose.foundation.BorderStroke(0.5.dp, StatusWarningBorder)) {
                                                Text("⚠️ DUE (₹1K)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusWarningText, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                        }
                                    }

                                    // Dress / Gi Column
                                    Box(modifier = Modifier.width(125.dp)) {
                                        if (dressPayment != null) {
                                            Surface(shape = RoundedCornerShape(4.dp), color = StatusSuccessBg, border = androidx.compose.foundation.BorderStroke(0.5.dp, StatusSuccessBorder)) {
                                                Text("✅ PAID", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusSuccessText, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                        } else {
                                            Surface(shape = RoundedCornerShape(4.dp), color = StatusWarningBg, border = androidx.compose.foundation.BorderStroke(0.5.dp, StatusWarningBorder)) {
                                                Text("⚠️ DUE (₹1.5K)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusWarningText, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                        }
                                    }

                                    // Summary Column
                                    Box(modifier = Modifier.width(110.dp)) {
                                        if (allPaid) {
                                            Text("🎉 All Clear", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusSuccessText)
                                        } else {
                                            val dueCount = (if (selectedMonthPayment == null) 1 else 0) + (if (admissionPayment == null) 1 else 0) + (if (dressPayment == null) 1 else 0)
                                            Text("⚠️ $dueCount Pending", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = StatusWarningText)
                                        }
                                    }

                                    // Action Column
                                    Box(modifier = Modifier.width(90.dp)) {
                                        Button(
                                            onClick = { onRecordPaymentForStudent(student) },
                                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                            modifier = Modifier.height(26.dp)
                                        ) {
                                            Text("Record", fontSize = 10.sp)
                                        }
                                    }
                                }
                                HorizontalDivider(color = Color(0xFFF1F5F9))
                            }
                        }
                    }
                }
            }
        }

        // Empty State
        if (filteredStudents.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = CardWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No regular students match the selected filter.", color = TextSlate)
                }
            }
        } else if (viewMode == "CARDS") {
            // Student Fee History Cards View
            filteredStudents.forEach { student ->
                val studentPayments = paymentsList.filter {
                    it.studentName.contains(student.fullName, ignoreCase = true) || it.studentId == student.userId
                }

                val admissionPayment = studentPayments.firstOrNull {
                    (it.feeCategory.contains("Admission", ignoreCase = true) || it.month.contains("Admission", ignoreCase = true)) && it.status == "PAID"
                }

                val dressPayment = studentPayments.firstOrNull {
                    (it.feeCategory.contains("Dress", ignoreCase = true) || it.feeCategory.contains("Uniform", ignoreCase = true) || it.feeCategory.contains("Gi", ignoreCase = true)) && it.status == "PAID"
                }

                val selectedMonthPayment = studentPayments.firstOrNull {
                    (it.feeCategory.contains("Monthly", ignoreCase = true) || it.feeCategory.contains(selectedMonth, ignoreCase = true) || it.month.contains(selectedMonth, ignoreCase = true)) &&
                            it.status == "PAID"
                }

                val isPendingVerification = studentPayments.any {
                    (it.feeCategory.contains("Monthly", ignoreCase = true) || it.feeCategory.contains(selectedMonth, ignoreCase = true) || it.month.contains(selectedMonth, ignoreCase = true)) &&
                            it.status == "VERIFICATION_PENDING"
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("regular_student_fee_card_${student.userId}"),
                    shape = RoundedCornerShape(12.dp),
                    color = CardWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedMonthPayment != null) StatusSuccessBorder else BorderLight),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Top Header: Student info & Record Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(RoyalBlue.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🥋", fontSize = 18.sp)
                                }

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(student.fullName, fontWeight = FontWeight.Bold, color = TextNavy, style = MaterialTheme.typography.titleSmall)
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = RoyalBlue.copy(alpha = 0.1f)
                                        ) {
                                            Text(student.userId, fontSize = 10.sp, color = RoyalBlue, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                        }
                                    }
                                    Text(
                                        "${student.currentBelt} • ${student.batchName.ifBlank { "Regular Batch" }} • Ph: ${student.phone}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSlate
                                    )
                                }
                            }

                            Button(
                                onClick = { onRecordPaymentForStudent(student) },
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("record_payment_btn_${student.userId}")
                            ) {
                                Icon(Icons.Default.AddCard, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Record Fee", fontSize = 11.sp)
                            }
                        }

                        HorizontalDivider(color = BorderLight)

                        // 3-Pill Status Strip: Admission Fee, Dress Fee, Selected Month Fee
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Admission Fee
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                color = if (admissionPayment != null) StatusSuccessBg else StatusWarningBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (admissionPayment != null) StatusSuccessBorder else StatusWarningBorder)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text("Admission Fee (₹1,000)", fontSize = 10.sp, color = TextSlate, fontWeight = FontWeight.SemiBold)
                                    if (admissionPayment != null) {
                                        Text("✅ PAID", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusSuccessText)
                                        Text("Ref: ${admissionPayment.receiptNo}", fontSize = 9.sp, color = TextSlate, maxLines = 1)
                                    } else {
                                        Text("⚠️ DUE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusWarningText)
                                        Text("Pending on join", fontSize = 9.sp, color = TextSlate)
                                    }
                                }
                            }

                            // Dress Fee
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                color = if (dressPayment != null) StatusSuccessBg else StatusWarningBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (dressPayment != null) StatusSuccessBorder else StatusWarningBorder)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text("Karate Gi / Dress (₹1,500)", fontSize = 10.sp, color = TextSlate, fontWeight = FontWeight.SemiBold)
                                    if (dressPayment != null) {
                                        Text("✅ PAID", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusSuccessText)
                                        Text("Ref: ${dressPayment.receiptNo}", fontSize = 9.sp, color = TextSlate, maxLines = 1)
                                    } else {
                                        Text("⚠️ DUE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusWarningText)
                                        Text("Uniform fee pending", fontSize = 9.sp, color = TextSlate)
                                    }
                                }
                            }

                            // Selected Month Fee
                            Surface(
                                modifier = Modifier.weight(1.2f),
                                shape = RoundedCornerShape(8.dp),
                                color = if (selectedMonthPayment != null) StatusSuccessBg else if (isPendingVerification) StatusWarningBg else StatusErrorBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedMonthPayment != null) StatusSuccessBorder else if (isPendingVerification) StatusWarningBorder else StatusErrorBorder)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text("$selectedMonth Training (₹2,000)", fontSize = 10.sp, color = TextSlate, fontWeight = FontWeight.SemiBold)
                                    if (selectedMonthPayment != null) {
                                        Text("✅ PAID (₹${selectedMonthPayment.amount.toInt()})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusSuccessText)
                                        Text("Receipt: ${selectedMonthPayment.receiptNo}", fontSize = 9.sp, color = TextSlate, maxLines = 1)
                                    } else if (isPendingVerification) {
                                        Text("⏳ VERIFY PENDING", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusWarningText)
                                        Text("Awaiting verification", fontSize = 9.sp, color = TextSlate)
                                    } else {
                                        Text("❌ UNPAID / DUE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusErrorText)
                                        Text("Due 5th of $selectedMonth", fontSize = 9.sp, color = TextSlate)
                                    }
                                }
                            }
                        }

                        // 12-Month Year Progress Matrix (Jan to Dec)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("2026 Year Monthly Track (Jan - Dec):", fontSize = 10.sp, color = TextSlate, fontWeight = FontWeight.SemiBold)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                months.forEach { m ->
                                    val isMonthPaid = studentPayments.any {
                                        (it.feeCategory.contains("Monthly", ignoreCase = true) || it.feeCategory.contains(m, ignoreCase = true) || it.month.contains(m, ignoreCase = true)) &&
                                                it.status == "PAID"
                                    }
                                    val isSelected = selectedMonth == m

                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (isMonthPaid) StatusSuccess else Color(0xFFE2E8F0),
                                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, CrimsonPrimary) else null,
                                        modifier = Modifier.clickable { selectedMonth = m }
                                    ) {
                                        Text(
                                            text = m,
                                            fontSize = 9.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isMonthPaid) Color.White else TextNavy,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
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
fun RecordStudentFeeDialog(
    student: UserAccountEntity,
    onDismiss: () -> Unit,
    onSave: (PaymentRecordEntity) -> Unit
) {
    var feeCategory by remember { mutableStateOf("Monthly Training Fee") }
    var selectedMonth by remember { mutableStateOf("August 2026") }
    var amount by remember { mutableStateOf("2000") }
    var paymentMethod by remember { mutableStateOf("UPI / QR Code") }
    var transactionRef by remember { mutableStateOf("UTR-${UUID.randomUUID().toString().take(8).uppercase()}") }
    var receiptNo by remember { mutableStateOf("REC-${SimpleDateFormat("yyMMdd", Locale.getDefault()).format(Date())}-${(1000..9999).random()}") }

    val presetCategories = listOf(
        "Monthly Training Fee" to "2000",
        "Admission Fee" to "1000",
        "Karate Gi & Uniform Dress" to "1500",
        "Belt Examination Fee" to "1500",
        "Tournament Entry Fee" to "800"
    )

    val dateStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Payments, contentDescription = null, tint = CrimsonPrimary)
                Column {
                    Text("Record Regular Fee", fontWeight = FontWeight.Bold, color = TextNavy, style = MaterialTheme.typography.titleMedium)
                    Text("Student: ${student.fullName} (${student.userId})", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Select Fee Category:", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetCategories.forEach { (catName, defaultAmt) ->
                        val isSelected = feeCategory == catName
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) CrimsonPrimary else Color(0xFFF1F5F9),
                            modifier = Modifier.clickable {
                                feeCategory = catName
                                amount = defaultAmt
                            }
                        ) {
                            Text(
                                text = catName,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextNavy,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                if (feeCategory == "Monthly Training Fee") {
                    OutlinedTextField(
                        value = selectedMonth,
                        onValueChange = { selectedMonth = it },
                        label = { Text("Fee Month & Year") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Amount (₹) *") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = paymentMethod,
                        onValueChange = { paymentMethod = it },
                        label = { Text("Mode (UPI/Cash)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                OutlinedTextField(
                    value = transactionRef,
                    onValueChange = { transactionRef = it },
                    label = { Text("Transaction / UTR Ref #") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amtVal = amount.toDoubleOrNull() ?: 2000.0
                    val fullCategory = if (feeCategory == "Monthly Training Fee") "Regular Training Fee - $selectedMonth" else feeCategory
                    val record = PaymentRecordEntity(
                        receiptNo = receiptNo,
                        studentId = student.userId,
                        studentName = student.fullName,
                        amount = amtVal,
                        feeCategory = fullCategory,
                        month = if (feeCategory == "Monthly Training Fee") selectedMonth else "2026",
                        paymentDate = dateStr,
                        paymentMethod = paymentMethod,
                        transactionRef = transactionRef,
                        status = "PAID"
                    )
                    onSave(record)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                shape = RoundedCornerShape(8.dp),
                enabled = amount.isNotBlank()
            ) {
                Text("Confirm & Record Payment")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddEditFeeItemDialog(
    feeItem: FeeItemEntity?,
    onDismiss: () -> Unit,
    onSave: (FeeItemEntity) -> Unit
) {
    var feeName by remember { mutableStateOf(feeItem?.feeName ?: "Monthly Tuition Fee") }
    var category by remember { mutableStateOf(feeItem?.category ?: "Monthly") }
    var amount by remember { mutableStateOf((feeItem?.amount ?: 2500.0).toInt().toString()) }
    var dueDate by remember { mutableStateOf(feeItem?.dueDate ?: "5th of every month") }
    var lateFee by remember { mutableStateOf((feeItem?.lateFee ?: 200.0).toInt().toString()) }
    var description by remember { mutableStateOf(feeItem?.description ?: "Official monthly training and dojo facility charges") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Payments, contentDescription = null, tint = RoyalBlue)
                Text(if (feeItem == null) "Add Fee Item" else "Edit Fee Item", fontWeight = FontWeight.Bold, color = TextNavy)
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
                    value = feeName,
                    onValueChange = { feeName = it },
                    label = { Text("Fee Name / Head *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Amount (₹) *") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = lateFee,
                        onValueChange = { lateFee = it },
                        label = { Text("Late Fee (₹)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        label = { Text("Due Date") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (feeName.isNotBlank() && amount.isNotBlank()) {
                        val updated = FeeItemEntity(
                            feeId = feeItem?.feeItemId ?: "FEE-${UUID.randomUUID().toString().take(8)}",
                            feeCategory = category.trim(),
                            amount = amount.toDoubleOrNull() ?: 2000.0,
                            frequency = category.trim(),
                            description = description.trim(),
                            feeName = feeName.trim(),
                            dueDate = dueDate.trim(),
                            lateFee = lateFee.toDoubleOrNull() ?: 0.0
                        )
                        onSave(updated)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                shape = RoundedCornerShape(8.dp),
                enabled = feeName.isNotBlank() && amount.isNotBlank()
            ) {
                Text(if (feeItem == null) "Create Fee Item" else "Save Changes")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddEditTrainingProgramDialog(
    program: TrainingProgramEntity?,
    onDismiss: () -> Unit,
    onSave: (TrainingProgramEntity) -> Unit
) {
    var programName by remember { mutableStateOf(program?.programName ?: "") }
    var description by remember { mutableStateOf(program?.description ?: "") }
    var ageGroup by remember { mutableStateOf(program?.ageGroup ?: "6 to 18 Years") }
    var monthlyFee by remember { mutableStateOf((program?.monthlyFee ?: 2000.0).toInt().toString()) }
    var durationMonths by remember { mutableStateOf((program?.durationMonths ?: 12).toString()) }
    var daysPerWeek by remember { mutableStateOf(program?.daysPerWeek ?: "Mon, Wed, Fri (3 days/wk)") }
    var coachName by remember { mutableStateOf(program?.coachName ?: "Shihan A. Tatarao (6th Dan)") }
    var isActive by remember { mutableStateOf(program?.isActive ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = RoyalBlue)
                Text(if (program == null) "Add Training Program" else "Edit Program", fontWeight = FontWeight.Bold, color = TextNavy)
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
                    value = programName,
                    onValueChange = { programName = it },
                    label = { Text("Program Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = monthlyFee,
                        onValueChange = { monthlyFee = it },
                        label = { Text("Monthly Fee (₹) *") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = ageGroup,
                        onValueChange = { ageGroup = it },
                        label = { Text("Age Criteria") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = daysPerWeek,
                        onValueChange = { daysPerWeek = it },
                        label = { Text("Schedule / Days") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = durationMonths,
                        onValueChange = { durationMonths = it },
                        label = { Text("Duration (Months)") },
                        modifier = Modifier.width(100.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                OutlinedTextField(
                    value = coachName,
                    onValueChange = { coachName = it },
                    label = { Text("Lead Faculty / Coach") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Program Description") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = isActive,
                        onCheckedChange = { isActive = it }
                    )
                    Text("Active for Student Enrollment", style = MaterialTheme.typography.bodyMedium, color = TextNavy)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (programName.isNotBlank()) {
                        val prog = TrainingProgramEntity(
                            programId = program?.programId ?: "PROG-${UUID.randomUUID().toString().take(8)}",
                            programTitle = programName.trim(),
                            feeAmount = monthlyFee.toDoubleOrNull() ?: 2000.0,
                            feeFrequency = "Monthly",
                            targetAudience = ageGroup.trim(),
                            durationText = "${durationMonths.toIntOrNull() ?: 12} Months",
                            syllabusOverview = description.trim(),
                            scheduleSummary = daysPerWeek.trim(),
                            isActive = isActive,
                            programName = programName.trim(),
                            description = description.trim(),
                            ageGroup = ageGroup.trim(),
                            monthlyFee = monthlyFee.toDoubleOrNull() ?: 2000.0,
                            durationMonths = durationMonths.toIntOrNull() ?: 12,
                            daysPerWeek = daysPerWeek.trim(),
                            coachName = coachName.trim()
                        )
                        onSave(prog)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                shape = RoundedCornerShape(8.dp),
                enabled = programName.isNotBlank()
            ) {
                Text(if (program == null) "Create Program" else "Save Changes")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                Text("Cancel")
            }
        }
    )
}
