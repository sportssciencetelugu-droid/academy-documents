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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.data.*
import com.example.ui.common.BromaCard
import com.example.ui.common.BromaPrimaryButton
import com.example.ui.common.BromaSecondaryButton
import com.example.ui.common.StatusBadge
import com.example.ui.theme.*

@Composable
fun StudentDashboardScreen(
    student: UserAccountEntity,
    siblingList: List<UserAccountEntity> = emptyList(),
    attendanceList: List<AttendanceRecordEntity>,
    paymentsList: List<PaymentRecordEntity>,
    nextEvent: CalendarEventEntity?,
    announcement: AnnouncementEntity?,
    onChildSwitch: (UserAccountEntity) -> Unit = {},
    onNavigateToTab: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    // Calculate Attendance stats
    val totalClasses = attendanceList.size
    val presentCount = attendanceList.count { it.status == "PRESENT" }
    val absentCount = attendanceList.count { it.status == "ABSENT" }
    val leaveCount = attendanceList.count { it.status == "LEAVE" }
    val attendancePct = if (totalClasses > 0) (presentCount.toFloat() / totalClasses * 100).toInt() else 92

    // Calculate Fees status
    val pendingPayments = paymentsList.filter { it.status == "DUE" || it.status == "OVERDUE" }
    val totalDueAmount = pendingPayments.sumOf { it.amount }.let { if (it > 0) it else 0.0 }
    val isFeePaid = pendingPayments.isEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // MULTI-CHILD SIBLING QUICK SWITCH BAR (if parent has multiple registered students)
        if (siblingList.size > 1) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("multi_child_overview_bar"),
                shape = RoundedCornerShape(16.dp),
                color = CardWhite,
                shadowElevation = 2.dp,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, RoyalBlue.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
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
                            Text("👨‍👩‍👧", fontSize = 18.sp)
                            Column {
                                Text(
                                    "FAMILY ACCOUNT • SWITCH STUDENT",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp),
                                    color = RoyalBlue
                                )
                                Text(
                                    "${siblingList.size} children linked to your family account",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSlate,
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ActiveNavBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue.copy(alpha = 0.3f))
                        ) {
                            Text(
                                "Active: ${student.fullName.take(12)}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = RoyalBlue,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        siblingList.forEach { child ->
                            val isSelected = child.userId == student.userId
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onChildSwitch(child) }
                                    .testTag("overview_switch_child_${child.userId}"),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) ActiveNavBg else SecondaryBg,
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) RoyalBlue else BorderLight
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) RoyalBlue else BorderLight),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (!child.avatarUrl.isNullOrBlank()) {
                                            Image(
                                                painter = rememberAsyncImagePainter(child.avatarUrl),
                                                contentDescription = child.fullName,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            Text(
                                                child.fullName.take(1).uppercase(),
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                color = if (isSelected) TextOnAccent else TextNavy
                                            )
                                        }
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            child.fullName,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium),
                                            color = if (isSelected) RoyalBlue else TextNavy,
                                            maxLines = 1
                                        )
                                        Text(
                                            child.currentBelt,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSlate,
                                            fontSize = 9.sp,
                                            maxLines = 1
                                        )
                                    }

                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            tint = RoyalBlue,
                                            contentDescription = "Active",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // 1. WELCOME & PROFILE SUMMARY HEADER CARD
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = CardWhite,
            shadowElevation = 1.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(ActiveNavBg)
                                .border(1.dp, RoyalBlue.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = RoyalBlue,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Good day, ${student.fullName}",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = TextNavy
                            )
                            Text(
                                text = "Student ID: ${student.userId}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = TextSlate
                            )
                        }
                    }

                    StatusBadge(status = if (isFeePaid) "PAID" else "DUE")
                }

                HorizontalDivider(color = BorderLight)

                // Key metrics row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Belt Rank", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                        Text(
                            student.currentBelt,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextNavy
                        )
                    }
                    Column {
                        Text("Batch", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                        Text(
                            student.batchId,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextNavy
                        )
                    }
                    Column {
                        Text("Attendance Rate", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                        Text(
                            "$attendancePct%",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (attendancePct >= 85) StatusSuccess else StatusWarning
                        )
                    }
                }
            }
        }

        // 2. KEY METRICS: ATTENDANCE & FEES (Side-by-side cards)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ATTENDANCE CARD
            Surface(
                modifier = Modifier.weight(1f),
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
                        Text(
                            "ATTENDANCE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                            color = TextSlate
                        )
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(16.dp))
                    }

                    Text(
                        text = "$attendancePct%",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (attendancePct >= 85) StatusSuccess else StatusWarning
                    )

                    Text(
                        text = "${if (totalClasses > 0) presentCount else 18} of ${if (totalClasses > 0) totalClasses else 20} classes attended",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSlate
                    )

                    LinearProgressIndicator(
                        progress = { (attendancePct / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (attendancePct >= 85) StatusSuccess else StatusWarning,
                        trackColor = SecondaryBg
                    )

                    OutlinedButton(
                        onClick = { onNavigateToTab("attendance") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = CardWhite,
                            contentColor = RoyalBlue
                        )
                    ) {
                        Text("View Details", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = RoyalBlue)
                    }
                }
            }

            // FEES CARD
            Surface(
                modifier = Modifier.weight(1f),
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
                        Text(
                            "FEES STATUS",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                            color = TextSlate
                        )
                        Icon(Icons.Default.Payments, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(16.dp))
                    }

                    Text(
                        text = if (isFeePaid) "₹0" else "₹${totalDueAmount.toInt()}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isFeePaid) StatusSuccess else StatusError
                    )

                    Text(
                        text = if (isFeePaid) "All dues cleared" else "Payment pending",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSlate
                    )

                    StatusBadge(status = if (isFeePaid) "PAID" else "DUE")

                    Button(
                        onClick = { onNavigateToTab("fees") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RoyalBlue,
                            contentColor = TextOnAccent
                        )
                    ) {
                        Text("View Fees", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextOnAccent)
                    }
                }
            }
        }

        // 3. TRAINING & BATCH DETAILS CARD
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = CardWhite,
            shadowElevation = 1.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "TRAINING & BATCH DETAILS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                        color = TextSlate
                    )
                    TextButton(
                        onClick = { onNavigateToTab("class") },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("View Batch", color = RoyalBlue, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Current Belt", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                        Text(
                            student.currentBelt,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextNavy
                        )
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Batch Name", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                        Text(
                            student.batchName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextNavy
                        )
                    }
                }

                HorizontalDivider(color = BorderLight)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Sensei / Coach", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                        Text(
                            student.coachName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextNavy
                        )
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Schedule", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                        Text(
                            "Mon–Sat (6:00–8:00 PM)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextNavy
                        )
                    }
                }
            }
        }

        // 4. UPCOMING SESSIONS & EVENTS
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = CardWhite,
            shadowElevation = 1.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "UPCOMING SESSIONS & EVENTS",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                    color = TextSlate
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Next Training
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = SecondaryBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                "🥋 Next Session",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = RoyalBlue
                            )
                            Text(
                                "Today • 6:00 PM",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextNavy
                            )
                            Text(
                                student.batchName,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSlate
                            )
                            Text(
                                "📍 ${student.dojoCenter}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSlate
                            )
                        }
                    }

                    // Next Event Card
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToTab("calendar") },
                        shape = RoundedCornerShape(12.dp),
                        color = SecondaryBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                "🏆 Academy Event",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = StatusWarning
                            )
                            Text(
                                nextEvent?.title ?: "State Championship 2026",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextNavy,
                                maxLines = 1
                            )
                            Text(
                                nextEvent?.startDate ?: "24 Aug 2026",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = TextSlate
                            )
                            Text(
                                "Category: ${nextEvent?.category ?: "Tournament"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSlate
                            )
                        }
                    }
                }
            }
        }

        // 5. ANNOUNCEMENT CARD
        if (announcement != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToTab("announcements") },
                shape = RoundedCornerShape(16.dp),
                color = CardWhite,
                shadowElevation = 1.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(ActiveNavBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(24.dp))
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                "ANNOUNCEMENT",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = RoyalBlue
                            )
                            StatusBadge(status = announcement.priority)
                        }
                        Text(
                            announcement.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextNavy
                        )
                        Text(
                            announcement.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSlate,
                            maxLines = 2
                        )
                    }
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = TextSlate,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 6. QUICK ACTIONS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BromaSecondaryButton(
                text = "My Profile",
                onClick = { onNavigateToTab("profile") },
                icon = Icons.Default.Person,
                modifier = Modifier.weight(1f)
            )
            BromaSecondaryButton(
                text = "Contact Dojo",
                onClick = { onNavigateToTab("contact") },
                icon = Icons.Default.Call,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
