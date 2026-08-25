package com.example.ui.student

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.*
import com.example.ui.theme.*
import java.util.Calendar

@Composable
fun StudentFeesScreen(
    student: UserAccountEntity,
    paymentsList: List<PaymentRecordEntity>,
    feeItemsList: List<FeeItemEntity>,
    trainingPrograms: List<TrainingProgramEntity> = emptyList(),
    adminSettings: AdminSettingsEntity?,
    onSubmitPaymentRef: (String, String, Double, String, String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Fee Structure, 1: Dues & Online Pay, 2: History & Receipts
    var showPayDialog by remember { mutableStateOf(false) }
    var selectedFeeCategory by remember { mutableStateOf("Regular Karate Training Fees") }
    var selectedFeeAmount by remember { mutableStateOf(2000.0) }
    var preselectedMonth by remember { mutableStateOf<String?>(null) }
    var preselectedMode by remember { mutableStateOf("MONTHLY") } // "MONTHLY" or "YEARLY"
    var selectedReceiptForModal by remember { mutableStateOf<PaymentRecordEntity?>(null) }

    val scrollState = rememberScrollState()

    // Filter payments for this specific student
    val studentPayments = paymentsList.filter {
        it.studentId == student.userId || it.studentName.contains(student.fullName, ignoreCase = true)
    }

    // 12 Months of 2026 Academic & Training Year
    val allMonths = listOf(
        "January 2026", "February 2026", "March 2026", "April 2026",
        "May 2026", "June 2026", "July 2026", "August 2026",
        "September 2026", "October 2026", "November 2026", "December 2026"
    )

    fun getMonthStatus(monthName: String): String {
        val shortName = monthName.split(" ").firstOrNull() ?: monthName
        val hasPaid = studentPayments.any {
            (it.month.contains(shortName, ignoreCase = true) || it.feeCategory.contains(shortName, ignoreCase = true)) &&
                    it.status == "PAID"
        }
        if (hasPaid) return "PAID"

        val hasPending = studentPayments.any {
            (it.month.contains(shortName, ignoreCase = true) || it.feeCategory.contains(shortName, ignoreCase = true)) &&
                    it.status == "VERIFICATION_PENDING"
        }
        if (hasPending) return "PENDING"

        return "DUE"
    }

    val paidMonthsCount = allMonths.count { getMonthStatus(it) == "PAID" }
    val pendingMonthsCount = allMonths.count { getMonthStatus(it) == "PENDING" }
    val remainingMonthsCount = allMonths.count { getMonthStatus(it) == "DUE" }

    // One-Time Fee Statuses
    val isAdmissionPaid = studentPayments.any {
        (it.feeCategory.contains("Admission", ignoreCase = true) || it.month.contains("Admission", ignoreCase = true)) &&
                it.status == "PAID"
    }
    val isAdmissionPending = studentPayments.any {
        (it.feeCategory.contains("Admission", ignoreCase = true) || it.month.contains("Admission", ignoreCase = true)) &&
                it.status == "VERIFICATION_PENDING"
    }

    val isDressPaid = studentPayments.any {
        (it.feeCategory.contains("Dress", ignoreCase = true) || it.feeCategory.contains("Uniform", ignoreCase = true) || it.feeCategory.contains("Gi", ignoreCase = true)) &&
                it.status == "PAID"
    }
    val isDressPending = studentPayments.any {
        (it.feeCategory.contains("Dress", ignoreCase = true) || it.feeCategory.contains("Uniform", ignoreCase = true) || it.feeCategory.contains("Gi", ignoreCase = true)) &&
                it.status == "VERIFICATION_PENDING"
    }

    // Totals
    val totalPaidSum = studentPayments.filter { it.status == "PAID" }.sumOf { it.amount }
    val regularMonthlyRate = 2000.0
    val totalRemainingDues = (remainingMonthsCount * regularMonthlyRate) +
            (if (!isAdmissionPaid && !isAdmissionPending) 1000.0 else 0.0) +
            (if (!isDressPaid && !isDressPending) 1500.0 else 0.0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tab Selector Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = CardWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = CardWhite,
                contentColor = RoyalBlue,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = RoyalBlue
                    )
                },
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "📋 Fee Structure",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (selectedTab == 0) RoyalBlue else TextSlate
                        )
                    },
                    modifier = Modifier.testTag("fees_tab_structure")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "💳 Dues & Pay (${remainingMonthsCount} Due)",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (selectedTab == 1) CrimsonPrimary else TextSlate
                        )
                    },
                    modifier = Modifier.testTag("fees_tab_dues")
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            "📄 Receipts (${studentPayments.size})",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (selectedTab == 2) RoyalBlue else TextSlate
                        )
                    },
                    modifier = Modifier.testTag("fees_tab_history")
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            when (selectedTab) {
                0 -> {
                    // Tab 0: Fee Structure Breakdown
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Class Interlink Banner
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = CardWhite,
                            shadowElevation = 1.dp,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
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
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.School, contentDescription = null, tint = RoyalBlue)
                                        Text(
                                            "ACADEMY OFFICIAL FEE STRUCTURE",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = TextNavy
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = StatusSuccess.copy(alpha = 0.12f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccess.copy(alpha = 0.35f))
                                    ) {
                                        Text(
                                            "ACTIVE STUDENT",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = StatusSuccess,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Enrolled Batch:", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                                        Text(
                                            student.batchName.ifBlank { "Regular Evening Batch" },
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = TextNavy
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Current Belt Grade:", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                                        Text(
                                            student.currentBelt,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = RoyalBlue
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            "REGULAR TRAINING & ACADEMY ESSENTIALS",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextNavy
                        )

                        // Regular Monthly Karate Training Card
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = CardWhite,
                            shadowElevation = 1.dp,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Regular Karate Training Fees", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                    Text("Monthly dojo training, physical conditioning & kata curriculum", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                                    Text("Frequency: Monthly (₹2,000/mo) or Full Annual Package", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = RoyalBlue)
                                }

                                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("₹2,000 / mo", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = StatusSuccess)
                                    Button(
                                        onClick = {
                                            selectedFeeCategory = "Regular Karate Training Fees"
                                            selectedFeeAmount = 2000.0
                                            preselectedMonth = null
                                            preselectedMode = "MONTHLY"
                                            selectedTab = 1
                                            showPayDialog = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(32.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                                    ) {
                                        Text("Pay Monthly/Yearly", fontSize = 11.sp, color = Color.White)
                                    }
                                }
                            }
                        }

                        // One-time Admission Card
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = CardWhite,
                            shadowElevation = 1.dp,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Academy Admission & Registration Fee", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                    Text("One-time lifetime enrolment and federation registration", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                                    Text("Status: ${if (isAdmissionPaid) "✅ PAID" else "⚠️ DUE ON JOINING"}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = if (isAdmissionPaid) StatusSuccess else StatusWarningText)
                                }

                                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("₹1,000", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                    if (!isAdmissionPaid) {
                                        Button(
                                            onClick = {
                                                selectedFeeCategory = "Academy Admission & Registration Fee"
                                                selectedFeeAmount = 1000.0
                                                preselectedMonth = "2026 Enrolment"
                                                selectedTab = 1
                                                showPayDialog = true
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(32.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                                        ) {
                                            Text("Pay Admission", fontSize = 11.sp, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }

                        // Karate Gi Dress Card
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = CardWhite,
                            shadowElevation = 1.dp,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Karate Gi & Uniform Dress Fee", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                    Text("Heavyweight cotton karate uniform with official academy crest patch & belt", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                                    Text("Status: ${if (isDressPaid) "✅ PAID" else "⚠️ DUE"}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = if (isDressPaid) StatusSuccess else StatusWarningText)
                                }

                                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("₹1,500", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                    if (!isDressPaid) {
                                        Button(
                                            onClick = {
                                                selectedFeeCategory = "Karate Gi & Uniform Dress Fee"
                                                selectedFeeAmount = 1500.0
                                                preselectedMonth = "Official Uniform Kit"
                                                selectedTab = 1
                                                showPayDialog = true
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(32.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                                        ) {
                                            Text("Order Gi Uniform", fontSize = 11.sp, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }

                        // Other Training Programs & Special Camps
                        Text(
                            "SPECIAL KARATE CAMPS & INTENSIVE WORKSHOPS",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextNavy
                        )

                        // Special Camps (1-Day, 3-Days, 1-Week) - Interconnected with Admin Portal
                        val defaultCampsList = listOf(
                            Triple("1-Day Special Karate Training Camp", 4999.0, "6 Hours Intensive • 3 Sessions (Morning 6:00 AM – 8:00 AM, Afternoon 11:00 AM – 1:00 PM, Evening 6:00 PM – 8:00 PM) • Intensive Kata, Sparring & Weapons Drills"),
                            Triple("3-Days Special Karate Training Camp", 9999.0, "4 Hours/Day (12 Hours Total) • 2 Sessions/Day (Morning 6:00 AM – 8:00 AM & Evening 6:00 PM – 8:00 PM) • Bunkai Mastery, Kumite Drills & Conditioning"),
                            Triple("1-Week (7-Days) Special Karate Camp", 14999.0, "4 Hours/Day (28 Hours Total) • 2 Sessions/Day (Morning 6:00 AM – 8:00 AM & Evening 6:00 PM – 8:00 PM) • Masterclass, Weapon Flow & Black Belt Prep")
                        )

                        val dynamicCampsList: List<Triple<String, Double, String>> = if (trainingPrograms.isNotEmpty()) {
                            val campProgs = trainingPrograms.filter { it.programTitle.contains("Camp", ignoreCase = true) || it.programName.contains("Camp", ignoreCase = true) }
                            if (campProgs.isNotEmpty()) {
                                campProgs.map { prog ->
                                    val desc = prog.syllabusOverview.ifBlank {
                                        "${prog.durationText} • ${prog.scheduleSummary.ifBlank { prog.description }}"
                                    }
                                    Triple(prog.programTitle.ifBlank { prog.programName }, prog.feeAmount.takeIf { it > 0 } ?: prog.monthlyFee, desc)
                                }
                            } else {
                                defaultCampsList
                            }
                        } else {
                            defaultCampsList
                        }

                        dynamicCampsList.forEach { (campTitle, campFee, campDesc) ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                color = CardWhite,
                                shadowElevation = 1.dp,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(campTitle, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                        Text(campDesc, style = MaterialTheme.typography.bodySmall, color = TextSlate)
                                        Text("Schedule / Split: Editable in Admin Portal • Open to all batches", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = RoyalBlue)
                                    }

                                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("₹${campFee.toInt()}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = StatusSuccess)
                                        Button(
                                            onClick = {
                                                selectedFeeCategory = campTitle
                                                selectedFeeAmount = campFee
                                                preselectedMonth = "$campTitle Schedule"
                                                preselectedMode = "CUSTOM"
                                                selectedTab = 1
                                                showPayDialog = true
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(32.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                                        ) {
                                            Text("Enroll & Pay", fontSize = 11.sp, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }

                        Text(
                            "ADVANCED WEAPONS & SPECIALIZED COURSES",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextNavy
                        )

                        val allProgramsToDisplay = if (trainingPrograms.isNotEmpty()) {
                            trainingPrograms
                        } else {
                            listOf(
                                TrainingProgramEntity(
                                    programId = "DEF-1",
                                    programTitle = "Bo Staff & Kobudo Traditional Weapons",
                                    feeAmount = 1500.0,
                                    feeFrequency = "3-Month Term",
                                    targetAudience = "Orange Belt & Above",
                                    durationText = "3 Months",
                                    syllabusOverview = "Traditional Okinawan weapons: Bo staff, Sai, and Nunchaku katas",
                                    scheduleSummary = "Saturday Special Batch",
                                    isActive = true,
                                    programName = "Bo Staff & Kobudo Traditional Weapons",
                                    description = "Traditional Okinawan weapons: Bo staff, Sai, and Nunchaku katas",
                                    ageGroup = "Orange Belt & Above",
                                    monthlyFee = 1500.0,
                                    durationMonths = 3,
                                    daysPerWeek = "Saturday Special Batch",
                                    coachName = "Shihan A. Tatarao"
                                ),
                                TrainingProgramEntity(
                                    programId = "DEF-2",
                                    programTitle = "Tournament Sparring & Kumite Intensive",
                                    feeAmount = 2500.0,
                                    feeFrequency = "Camp Term",
                                    targetAudience = "State & National Contenders",
                                    durationText = "2 Months",
                                    syllabusOverview = "Olympic WKF point sparring, reaction drills & high-performance kumite",
                                    scheduleSummary = "Sun 7:00 AM - 9:30 AM",
                                    isActive = true,
                                    programName = "Tournament Sparring & Kumite Intensive",
                                    description = "Olympic WKF point sparring, reaction drills & high-performance kumite",
                                    ageGroup = "State & National Contenders",
                                    monthlyFee = 2500.0,
                                    durationMonths = 2,
                                    daysPerWeek = "Sun 7:00 AM - 9:30 AM",
                                    coachName = "Sensei A. Sombabu"
                                )
                            )
                        }

                        allProgramsToDisplay.forEach { prog ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                color = CardWhite,
                                shadowElevation = 1.dp,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(prog.programName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                        Text(prog.description, style = MaterialTheme.typography.bodySmall, color = TextSlate)
                                        Text("Duration: ${prog.daysPerWeek} • ${prog.durationMonths} Months", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = RoyalBlue)
                                    }

                                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("₹${prog.monthlyFee.toInt()}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                        OutlinedButton(
                                            onClick = {
                                                selectedFeeCategory = prog.programName
                                                selectedFeeAmount = prog.monthlyFee
                                                preselectedMonth = "${prog.programName} (Term)"
                                                selectedTab = 1
                                                showPayDialog = true
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                                            modifier = Modifier.height(32.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                                        ) {
                                            Text("Enroll & Pay", fontSize = 11.sp, color = RoyalBlue)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Tab 1: Dues & Online Payment Centre
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Summary Hero Metric Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = CardWhite,
                                shadowElevation = 1.dp,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text("TOTAL PAID", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                                    Text("₹${totalPaidSum.toInt()}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = StatusSuccess)
                                    Text("$paidMonthsCount Months Cleared", fontSize = 10.sp, color = StatusSuccessText)
                                }
                            }

                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = CardWhite,
                                shadowElevation = 1.dp,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (remainingMonthsCount > 0) StatusErrorBorder else StatusSuccessBorder)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text("REMAINING DUES", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                                    Text("₹${totalRemainingDues.toInt()}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = if (totalRemainingDues > 0) StatusError else StatusSuccess)
                                    Text("$remainingMonthsCount Unpaid Months", fontSize = 10.sp, color = if (remainingMonthsCount > 0) StatusErrorText else StatusSuccessText)
                                }
                            }
                        }

                        // Regular Training Monthly Tracker Card (2026 Academic Year)
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = CardWhite,
                            shadowElevation = 1.dp,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonPrimary.copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("🥋", fontSize = 18.sp)
                                        Column {
                                            Text("REGULAR KARATE TRAINING (2026)", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                            Text("Monthly Fee: ₹2,000/mo • Complete 12-Month Year Tracker", fontSize = 11.sp, color = TextSlate)
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (remainingMonthsCount == 0) StatusSuccessBg else StatusWarningBg
                                    ) {
                                        Text(
                                            if (remainingMonthsCount == 0) "ALL PAID ✅" else "$remainingMonthsCount MONTHS DUE",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (remainingMonthsCount == 0) StatusSuccessText else StatusWarningText,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                HorizontalDivider(color = BorderLight)

                                // Quick Pay Options Strip
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            selectedFeeCategory = "Regular Karate Training Fees"
                                            selectedFeeAmount = 2000.0
                                            preselectedMonth = allMonths.firstOrNull { getMonthStatus(it) == "DUE" } ?: "August 2026"
                                            preselectedMode = "MONTHLY"
                                            showPayDialog = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).height(38.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                                    ) {
                                        Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Pay Next Month (₹2,000)", fontSize = 11.sp)
                                    }

                                    Button(
                                        onClick = {
                                            selectedFeeCategory = "Regular Karate Training - Full Annual Year 2026 Package"
                                            val yearAmt = if (remainingMonthsCount > 0) remainingMonthsCount * 2000.0 else 24000.0
                                            selectedFeeAmount = yearAmt
                                            preselectedMonth = "Full Year 2026 (All Remaining Months)"
                                            preselectedMode = "YEARLY"
                                            showPayDialog = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).height(38.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                                    ) {
                                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Pay Full Year (2026)", fontSize = 11.sp)
                                    }
                                }

                                // 12-Month Grid (Jan to Dec 2026)
                                Text("Select Any Month to Pay or View Status:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextNavy)

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    val rows = allMonths.chunked(3)
                                    rows.forEach { rowMonths ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            rowMonths.forEach { monthName ->
                                                val status = getMonthStatus(monthName)
                                                val shortMonth = monthName.split(" ").first()

                                                val (bgColor, borderColor, textColor, badgeText) = when (status) {
                                                    "PAID" -> Quadruple(StatusSuccessBg, StatusSuccessBorder, StatusSuccessText, "✅ Paid")
                                                    "PENDING" -> Quadruple(StatusWarningBg, StatusWarningBorder, StatusWarningText, "⏳ Pending")
                                                    else -> Quadruple(Color(0xFFFFF1F2), CrimsonPrimary.copy(alpha = 0.4f), CrimsonPrimary, "⚠️ Due ₹2k")
                                                }

                                                Surface(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clickable {
                                                            selectedFeeCategory = "Regular Karate Training Fees"
                                                            selectedFeeAmount = 2000.0
                                                            preselectedMonth = monthName
                                                            preselectedMode = "MONTHLY"
                                                            showPayDialog = true
                                                        },
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = bgColor,
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
                                                ) {
                                                    Column(
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                                    ) {
                                                        Text(shortMonth, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextNavy)
                                                        Text(badgeText, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = textColor)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // One-Time Admission & Uniform Fees Section
                        Text(
                            "ADMISSION & UNIFORM DRESS STATUS",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextNavy
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Admission Fee Card
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = CardWhite,
                                shadowElevation = 1.dp,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isAdmissionPaid) StatusSuccessBorder else BorderLight)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("📝 Admission Fee", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                    Text("One-Time: ₹1,000", fontSize = 11.sp, color = TextSlate)
                                    if (isAdmissionPaid) {
                                        Text("✅ PAID", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusSuccess)
                                    } else if (isAdmissionPending) {
                                        Text("⏳ VERIFY PENDING", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusWarningText)
                                    } else {
                                        Button(
                                            onClick = {
                                                selectedFeeCategory = "Academy Admission & Registration Fee"
                                                selectedFeeAmount = 1000.0
                                                preselectedMonth = "One-Time Enrolment (2026)"
                                                preselectedMode = "ONE_TIME"
                                                showPayDialog = true
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.fillMaxWidth().height(28.dp)
                                        ) {
                                            Text("Pay ₹1,000", fontSize = 10.sp)
                                        }
                                    }
                                }
                            }

                            // Uniform Fee Card
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = CardWhite,
                                shadowElevation = 1.dp,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isDressPaid) StatusSuccessBorder else BorderLight)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("🥋 Karate Gi Dress", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                    Text("Uniform Kit: ₹1,500", fontSize = 11.sp, color = TextSlate)
                                    if (isDressPaid) {
                                        Text("✅ PAID", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusSuccess)
                                    } else if (isDressPending) {
                                        Text("⏳ VERIFY PENDING", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusWarningText)
                                    } else {
                                        Button(
                                            onClick = {
                                                selectedFeeCategory = "Karate Gi & Uniform Dress Fee"
                                                selectedFeeAmount = 1500.0
                                                preselectedMonth = "Official Uniform Kit"
                                                preselectedMode = "ONE_TIME"
                                                showPayDialog = true
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.fillMaxWidth().height(28.dp)
                                        ) {
                                            Text("Pay ₹1,500", fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }

                        // QR Code Photo Payment Card
                        Text(
                            "ONLINE UPI PAYMENT CENTRE",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextNavy
                        )

                        UpiQrCodePhotoCard(
                            upiId = adminSettings?.upiId ?: "bromaacademy@upi",
                            academyName = adminSettings?.academyName ?: "BROMA Martial Arts Academy",
                            qrCodeUri = adminSettings?.qrCodeUri,
                            paymentPhone = adminSettings?.paymentPhone ?: "+91 98765 43210",
                            paymentReceiverName = adminSettings?.paymentReceiverName ?: "BROMA Martial Arts"
                        )

                        Button(
                            onClick = {
                                selectedFeeCategory = "Regular Karate Training Fees"
                                selectedFeeAmount = 2000.0
                                preselectedMonth = null
                                showPayDialog = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("pay_regular_karate_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
                        ) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Submit Online Transaction Reference",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                }

                2 -> {
                    // Tab 2: Payment History & Receipts
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "PAYMENT HISTORY & DIGITAL RECEIPT LOCKER",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextNavy
                        )

                        if (studentPayments.isEmpty()) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                color = CardWhite,
                                shadowElevation = 1.dp,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                            ) {
                                Text("No past payment receipts found for this student.", modifier = Modifier.padding(16.dp), color = TextSlate)
                            }
                        } else {
                            studentPayments.forEach { item ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedReceiptForModal = item }
                                        .testTag("receipt_item_${item.receiptNo}"),
                                    shape = RoundedCornerShape(14.dp),
                                    color = CardWhite,
                                    shadowElevation = 1.dp,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(item.feeCategory, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                            Text("Receipt No: ${item.receiptNo}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = RoyalBlue)
                                            Text("${item.month} • ${item.paymentDate}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                                        }

                                        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text("₹${item.amount.toInt()}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = if (item.status == "PAID") StatusSuccess.copy(alpha = 0.12f) else StatusWarning.copy(alpha = 0.12f),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, if (item.status == "PAID") StatusSuccess.copy(alpha = 0.35f) else StatusWarning.copy(alpha = 0.35f))
                                            ) {
                                                Text(
                                                    text = item.status,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = if (item.status == "PAID") StatusSuccess else StatusWarning,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
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
    }

    // Payment Reference Submission Dialog
    if (showPayDialog) {
        var txRef by remember { mutableStateOf("") }
        var chosenProgramCategory by remember { mutableStateOf(selectedFeeCategory) }
        var paymentOptionType by remember { mutableStateOf(if (preselectedMode == "YEARLY") "YEARLY" else "MONTHLY") } // "MONTHLY", "YEARLY", "CUSTOM"
        var selectedSingleMonth by remember {
            mutableStateOf(
                preselectedMonth ?: allMonths.firstOrNull { getMonthStatus(it) == "DUE" } ?: "August 2026"
            )
        }
        var customAmountStr by remember { mutableStateOf(selectedFeeAmount.toInt().toString()) }
        var paymentMode by remember { mutableStateOf("UPI / QR Code Scan") }

        val isRegularTraining = chosenProgramCategory.contains("Regular", ignoreCase = true)

        val baseFeeOptions = listOf(
            "Regular Karate Training Fees" to 2000.0,
            "1-Day Special Karate Training Camp" to 4999.0,
            "3-Days Special Karate Training Camp" to 9999.0,
            "1-Week (7-Days) Special Karate Camp" to 14999.0,
            "Academy Admission & Registration Fee" to 1000.0,
            "Karate Gi & Uniform Dress Fee" to 1500.0,
            "Kyu Belt Grading Exam Fees" to 1500.0,
            "Bo Staff & Kobudo Weapons Fees" to 1500.0
        )

        val feeOptions = (baseFeeOptions + trainingPrograms.map { (it.programTitle.ifBlank { it.programName }) to (it.feeAmount.takeIf { f -> f > 0 } ?: it.monthlyFee) } + feeItemsList.map { it.feeName to it.amount }).distinctBy { it.first }

        AlertDialog(
            onDismissRequest = { showPayDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Payment, contentDescription = null, tint = CrimsonPrimary)
                    Column {
                        Text(
                            "Submit Fee Payment Reference",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextNavy
                        )
                        Text(
                            "Student: ${student.fullName} (${student.userId})",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSlate
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Fee Category & Training Type Selector
                    Text("1. Select Fee / Training Head:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        feeOptions.forEach { (catName, catAmt) ->
                            val isChosen = chosenProgramCategory.contains(catName.take(12), ignoreCase = true)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isChosen) CrimsonPrimary else Color(0xFFF1F5F9),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isChosen) CrimsonPrimary else BorderLight),
                                modifier = Modifier.clickable {
                                    chosenProgramCategory = catName
                                    if (catName.contains("Regular", ignoreCase = true)) {
                                        paymentOptionType = "MONTHLY"
                                        customAmountStr = "2000"
                                    } else {
                                        paymentOptionType = "CUSTOM"
                                        customAmountStr = catAmt.toInt().toString()
                                    }
                                }
                            ) {
                                Text(
                                    text = catName,
                                    fontSize = 11.sp,
                                    fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isChosen) Color.White else TextNavy,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = BorderLight)

                    // Program-Specific Tenure / Frequency Selector
                    if (isRegularTraining) {
                        Text("2. Regular Training Fee Billing Option:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = paymentOptionType == "MONTHLY",
                                onClick = {
                                    paymentOptionType = "MONTHLY"
                                    customAmountStr = "2000"
                                },
                                label = { Text("Monthly Wise (Select Month)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CrimsonPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )

                            FilterChip(
                                selected = paymentOptionType == "YEARLY",
                                onClick = {
                                    paymentOptionType = "YEARLY"
                                    val yearAmt = if (remainingMonthsCount > 0) remainingMonthsCount * 2000.0 else 24000.0
                                    customAmountStr = yearAmt.toInt().toString()
                                },
                                label = { Text("⭐ Full Annual Year 2026", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = RoyalBlue,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }

                        if (paymentOptionType == "MONTHLY") {
                            Text("Select Training Month (All 12 Months Accessible):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextNavy)

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                val monthChunks = allMonths.chunked(4)
                                monthChunks.forEach { rowMonths ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        rowMonths.forEach { m ->
                                            val isSel = selectedSingleMonth == m
                                            val status = getMonthStatus(m)
                                            val shortMonth = m.split(" ").first()

                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (isSel) CrimsonPrimary else if (status == "PAID") StatusSuccessBg else Color(0xFFF1F5F9),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) CrimsonPrimary else if (status == "PAID") StatusSuccessBorder else BorderLight),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clickable {
                                                        selectedSingleMonth = m
                                                        customAmountStr = "2000"
                                                    }
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(vertical = 4.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Text(
                                                        text = shortMonth,
                                                        fontSize = 11.sp,
                                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                                        color = if (isSel) Color.White else TextNavy
                                                    )
                                                    Text(
                                                        text = if (status == "PAID") "✓ Paid" else if (status == "PENDING") "⏳ Verify" else "₹2k Due",
                                                        fontSize = 8.sp,
                                                        color = if (isSel) Color.White.copy(alpha = 0.9f) else if (status == "PAID") StatusSuccessText else TextSlate
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                color = StatusInfoBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, StatusInfoBorder)
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text("⭐ Full Annual Package (Year 2026)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = StatusInfoText)
                                    Text("Covers all 12 training months for 2026. Remaining $remainingMonthsCount months @ ₹2,000 = ₹${customAmountStr}.", fontSize = 11.sp, color = TextNavy)
                                }
                            }
                        }
                    } else {
                        // Other Program Specific Options
                        Text("2. Program Term & Session Details:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)

                        val programSessionOptions = when {
                            chosenProgramCategory.contains("Admission", ignoreCase = true) -> listOf("Academic Year 2026 Enrolment", "Lifetime Academy Membership")
                            chosenProgramCategory.contains("Dress", ignoreCase = true) || chosenProgramCategory.contains("Uniform", ignoreCase = true) -> listOf("Official Heavyweight Gi Uniform + Belt", "Replacement Academy Gi Set")
                            chosenProgramCategory.contains("Grading", ignoreCase = true) || chosenProgramCategory.contains("Exam", ignoreCase = true) -> listOf("Next Kyu Belt Grading Exam (2026)", "Dan Black Belt Pre-Test Examination")
                            chosenProgramCategory.contains("Weapons", ignoreCase = true) || chosenProgramCategory.contains("Bo Staff", ignoreCase = true) -> listOf("Quarterly Weapons Term (3 Months)", "6-Month Advanced Kobudo Module")
                            chosenProgramCategory.contains("Tournament", ignoreCase = true) -> listOf("State Championship Intensive Camp", "National Championship Special Training")
                            else -> listOf("Current 2026 Session", "Special 3-Month Module")
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            programSessionOptions.forEach { opt ->
                                val isSel = selectedSingleMonth == opt
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) RoyalBlue else Color(0xFFF1F5F9),
                                    modifier = Modifier.clickable { selectedSingleMonth = opt }
                                ) {
                                    Text(
                                        text = opt,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSel) Color.White else TextNavy,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Amount Field
                    OutlinedTextField(
                        value = customAmountStr,
                        onValueChange = { customAmountStr = it },
                        label = { Text("Payment Amount (₹) *") },
                        leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, color = CrimsonPrimary, modifier = Modifier.padding(start = 12.dp)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    // Payment Mode Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("UPI / QR Code Scan", "Google Pay / PhonePe", "Net Banking / NEFT", "Cash at Dojo Desk").forEach { mode ->
                            val isSel = paymentMode == mode
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSel) RoyalBlue else Color(0xFFF1F5F9),
                                modifier = Modifier.clickable { paymentMode = mode }
                            ) {
                                Text(
                                    text = mode,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) Color.White else TextNavy,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Transaction Reference UTR
                    OutlinedTextField(
                        value = txRef,
                        onValueChange = { txRef = it },
                        label = { Text("Transaction Reference ID / UTR *") },
                        placeholder = { Text("e.g. UPI/202608191234") },
                        leadingIcon = { Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = RoyalBlue) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("payment_ref_input"),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = StatusSuccessBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccessBorder)
                    ) {
                        val sessionDesc = if (isRegularTraining && paymentOptionType == "YEARLY") "Full Year 2026 Package" else selectedSingleMonth
                        Text(
                            "💡 Paying: $chosenProgramCategory for $sessionDesc — Amount: ₹$customAmountStr",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = StatusSuccessText,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (txRef.isNotBlank()) {
                            val parsedAmt = customAmountStr.toDoubleOrNull() ?: selectedFeeAmount
                            val finalMonth = if (isRegularTraining && paymentOptionType == "YEARLY") "Full Year 2026 Annual" else selectedSingleMonth
                            onSubmitPaymentRef(chosenProgramCategory, finalMonth, parsedAmt, paymentMode, txRef.trim())
                            showPayDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                    shape = RoundedCornerShape(8.dp),
                    enabled = txRef.isNotBlank() && customAmountStr.isNotBlank(),
                    modifier = Modifier.testTag("submit_payment_ref_dialog_button")
                ) {
                    Text("Submit For Verification", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPayDialog = false }) { Text("Cancel", color = TextSlate) }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Receipt View Modal
    if (selectedReceiptForModal != null) {
        val rec = selectedReceiptForModal!!
        AlertDialog(
            onDismissRequest = { selectedReceiptForModal = null },
            title = {
                Text(
                    "OFFICIAL DIGITAL RECEIPT",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextNavy
                )
            },
            text = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = SecondaryBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Receipt No: ${rec.receiptNo}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue)
                        HorizontalDivider(color = BorderLight)
                        Text("Student Name: ${rec.studentName}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = TextNavy)
                        Text("Fee Type: ${rec.feeCategory}", style = MaterialTheme.typography.bodyMedium, color = TextNavy)
                        Text("Month / Session: ${rec.month}", style = MaterialTheme.typography.bodyMedium, color = TextSlate)
                        Text("Amount: ₹${rec.amount.toInt()}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = StatusSuccess)
                        Text("Payment Method: ${rec.paymentMethod}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                        Text("Transaction Ref: ${rec.transactionRef}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                        Text("Payment Date: ${rec.paymentDate}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (rec.status == "PAID") StatusSuccess.copy(alpha = 0.12f) else StatusWarning.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (rec.status == "PAID") StatusSuccess.copy(alpha = 0.35f) else StatusWarning.copy(alpha = 0.35f))
                        ) {
                            Text(
                                "STATUS: ${rec.status}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (rec.status == "PAID") StatusSuccess else StatusWarning,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedReceiptForModal = null },
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

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun UpiQrCodePhotoCard(
    upiId: String,
    academyName: String,
    qrCodeUri: String? = null,
    paymentPhone: String? = null,
    paymentReceiverName: String? = null
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CardWhite,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.QrCode, contentDescription = null, tint = RoyalBlue)
                    Text("ACADEMY OFFICIAL QR CODE", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = StatusSuccess.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccess.copy(alpha = 0.35f))
                ) {
                    Text("SCAN TO PAY ⚡", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = StatusSuccess, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                }
            }

            Surface(
                modifier = Modifier.size(190.dp),
                shape = RoundedCornerShape(12.dp),
                color = CardWhite,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, BorderLight)
            ) {
                val isCustomUrl = !qrCodeUri.isNullOrBlank() && (qrCodeUri.startsWith("http") || qrCodeUri.startsWith("content") || qrCodeUri.startsWith("file") || qrCodeUri.startsWith("data"))

                if (isCustomUrl) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(8.dp)) {
                        AsyncImage(
                            model = qrCodeUri,
                            contentDescription = "Custom Payment QR Code Photo",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    val themeColor = RoyalBlue

                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(14.dp)) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val cellSize = size.width / 13f
                            val darkColor = Color(0xFF0F172A)

                            fun androidx.compose.ui.graphics.drawscope.DrawScope.drawMarker(x: Float, y: Float) {
                                drawRect(color = darkColor, topLeft = Offset(x, y), size = Size(cellSize * 3.5f, cellSize * 3.5f))
                                drawRect(color = Color.White, topLeft = Offset(x + cellSize * 0.6f, y + cellSize * 0.6f), size = Size(cellSize * 2.3f, cellSize * 2.3f))
                                drawRect(color = darkColor, topLeft = Offset(x + cellSize * 1.2f, y + cellSize * 1.2f), size = Size(cellSize * 1.1f, cellSize * 1.1f))
                            }

                            drawMarker(0f, 0f)
                            drawMarker(cellSize * 9.5f, 0f)
                            drawMarker(0f, cellSize * 9.5f)

                            val matrix = arrayOf(
                                intArrayOf(0,0,0,0,0,1,0,1,0,0,0,0,0),
                                intArrayOf(0,0,0,0,0,0,1,0,1,0,0,0,0),
                                intArrayOf(0,0,0,0,0,1,1,0,0,0,0,0,0),
                                intArrayOf(0,0,0,0,0,0,0,1,0,1,0,0,0),
                                intArrayOf(0,0,0,0,0,1,0,0,1,0,0,0,0),
                                intArrayOf(1,0,1,0,1,0,1,0,1,0,1,0,1),
                                intArrayOf(0,1,0,1,0,1,0,1,0,1,0,1,0),
                                intArrayOf(1,0,1,1,0,0,1,0,1,0,1,0,1),
                                intArrayOf(0,0,0,0,0,1,0,1,0,1,0,0,0),
                                intArrayOf(0,0,0,0,0,0,1,0,1,0,1,0,0),
                                intArrayOf(0,0,0,0,0,1,0,1,0,1,0,1,0),
                                intArrayOf(0,0,0,0,0,0,1,0,1,0,0,0,1),
                                intArrayOf(1,0,1,0,1,0,0,1,0,1,0,1,0)
                            )

                            for (r in 0 until 13) {
                                for (c in 0 until 13) {
                                    if (matrix[r][c] == 1) {
                                        val isThemeColored = (r + c) % 4 == 0
                                        drawRect(
                                            color = if (isThemeColored) themeColor else darkColor,
                                            topLeft = Offset(c * cellSize, r * cellSize),
                                            size = Size(cellSize * 0.88f, cellSize * 0.88f)
                                        )
                                    }
                                }
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = themeColor,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, CardWhite),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🥋", fontSize = 16.sp)
                            }
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = SecondaryBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("UPI ID: $upiId", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                    Text("PhonePe / GPay Number: ${paymentPhone ?: "+91 98765 43210"}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = RoyalBlue)
                    Text("Account: ${paymentReceiverName ?: academyName}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                }
            }
        }
    }
}
