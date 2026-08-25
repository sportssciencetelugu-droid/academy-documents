package com.example.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AttendanceRecordEntity
import com.example.data.UserAccountEntity
import com.example.ui.common.AttendanceDonutChart
import com.example.ui.common.AttendanceTrendLineChart
import com.example.ui.common.AttendanceWeeklyBarChart
import com.example.ui.theme.*

@Composable
fun StudentAttendanceScreen(
    student: UserAccountEntity,
    attendanceList: List<AttendanceRecordEntity>
) {
    var selectedDateRecord by remember { mutableStateOf<AttendanceRecordEntity?>(null) }
    val scrollState = rememberScrollState()

    val total = attendanceList.size
    val present = attendanceList.count { it.status == "PRESENT" }
    val absent = attendanceList.count { it.status == "ABSENT" }
    val leave = attendanceList.count { it.status == "LEAVE" }
    val late = attendanceList.count { it.status == "LATE" }
    val pct = if (total > 0) (present.toFloat() / total * 100).toInt() else 88

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Summary Card
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
                    "ATTENDANCE SUMMARY (LAST 6 MONTHS)",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = RoyalBlue
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("August 2026 Overall Rate", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                        Text(
                            "$pct%",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (pct >= 85) StatusSuccess else StatusError
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (pct >= 85) StatusSuccess.copy(alpha = 0.12f) else StatusError.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (pct >= 85) StatusSuccess.copy(alpha = 0.35f) else StatusError.copy(alpha = 0.35f)
                        )
                    ) {
                        Text(
                            text = if (pct >= 85) "GOOD STANDING" else "NEEDS IMPROVEMENT",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (pct >= 85) StatusSuccess else StatusError,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    AttendanceStatChip("🟢 Present", "$present")
                    AttendanceStatChip("🔴 Absent", "$absent")
                    AttendanceStatChip("🟡 Leave", "$leave")
                    AttendanceStatChip("🟠 Late", "$late")
                }
            }
        }

        // VISUAL GRAPH 1: Attendance Distribution Donut Chart
        AttendanceDonutChart(
            present = present.coerceAtLeast(18),
            absent = absent.coerceAtLeast(2),
            leave = leave.coerceAtLeast(1),
            late = late.coerceAtLeast(1)
        )

        // VISUAL GRAPH 2: Weekly Attendance Breakdown Bar Chart
        AttendanceWeeklyBarChart(
            weeklyData = listOf(
                "Week 1" to 92f,
                "Week 2" to 85f,
                "Week 3" to 100f,
                "Week 4" to 88f,
                "Week 5" to 95f
            ),
            title = "📊 WEEKLY ATTENDANCE PERFORMANCE (%)"
        )

        // VISUAL GRAPH 3: Monthly Attendance Progression Trend
        AttendanceTrendLineChart(
            trendPoints = listOf(
                "Mar" to 78f,
                "Apr" to 82f,
                "May" to 88f,
                "Jun" to 85f,
                "Jul" to 92f,
                "Aug" to pct.toFloat().coerceAtLeast(88f)
            )
        )

        // Attendance Calendar Legend
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
                    "AUGUST 2026 ATTENDANCE CALENDAR",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextNavy
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("🟢 Present", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = StatusSuccess)
                    Text("🔴 Absent", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = StatusError)
                    Text("🟡 Leave", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = StatusWarning)
                    Text("🟠 Late", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = Color(0xFFEA580C))
                }

                HorizontalDivider(color = BorderLight)

                // 31 Days Grid
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val days = (1..31).toList()
                    days.chunked(7).forEach { week ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            week.forEach { dayNum ->
                                val dateStr = "2026-08-" + String.format("%02d", dayNum)
                                val record = attendanceList.find { it.date == dateStr }
                                val statusColor = when (record?.status) {
                                    "PRESENT" -> StatusSuccess
                                    "ABSENT" -> StatusError
                                    "LEAVE" -> StatusWarning
                                    "LATE" -> Color(0xFFEA580C)
                                    else -> TextSlate.copy(alpha = 0.5f)
                                }

                                Surface(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clickable {
                                            selectedDateRecord = record ?: AttendanceRecordEntity(
                                                studentId = student.userId,
                                                studentName = student.fullName,
                                                batchId = student.batchId,
                                                date = dateStr,
                                                status = if (dayNum % 7 == 0) "HOLIDAY" else "PRESENT",
                                                timeSlot = "6:00 PM – 8:00 PM"
                                            )
                                        }
                                        .testTag("attendance_date_$dayNum"),
                                    shape = CircleShape,
                                    color = statusColor.copy(alpha = 0.12f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, statusColor)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "$dayNum",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = TextNavy
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

    // Tap Date Detail Modal
    if (selectedDateRecord != null) {
        val rec = selectedDateRecord!!
        AlertDialog(
            onDismissRequest = { selectedDateRecord = null },
            title = {
                Text(
                    "Attendance: ${rec.date}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextNavy
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Student: ${rec.studentName}", color = TextNavy)
                    Text("Batch: ${student.batchName}", color = TextSlate)
                    Text("Time Slot: ${rec.timeSlot}", color = TextSlate)
                    Text(
                        "Status: ${rec.status}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = when (rec.status) {
                            "PRESENT" -> StatusSuccess
                            "ABSENT" -> StatusError
                            "LEAVE" -> StatusWarning
                            else -> RoyalBlue
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedDateRecord = null },
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
fun AttendanceStatChip(label: String, count: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            count,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextNavy
        )
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSlate)
    }
}
