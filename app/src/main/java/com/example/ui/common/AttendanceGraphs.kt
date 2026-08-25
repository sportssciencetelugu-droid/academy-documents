package com.example.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * Visual Donut Chart showing distribution of Present, Absent, Leave, and Late attendance status.
 */
@Composable
fun AttendanceDonutChart(
    present: Int,
    absent: Int,
    leave: Int,
    late: Int,
    modifier: Modifier = Modifier
) {
    val total = (present + absent + leave + late).coerceAtLeast(1)
    val presentPct = (present.toFloat() / total * 100).toInt()
    val absentPct = (absent.toFloat() / total * 100).toInt()
    val leavePct = (leave.toFloat() / total * 100).toInt()
    val latePct = (late.toFloat() / total * 100).toInt()

    val presentAngle = (present.toFloat() / total) * 360f
    val absentAngle = (absent.toFloat() / total) * 360f
    val leaveAngle = (leave.toFloat() / total) * 360f
    val lateAngle = (late.toFloat() / total) * 360f

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CardWhite,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📊 ATTENDANCE DISTRIBUTION ANALYSIS",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = RoyalBlue
                )
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = StatusSuccessBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccessBorder)
                ) {
                    Text(
                        text = "$presentPct% Overall Rate",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = StatusSuccessText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Donut Canvas
                Box(
                    modifier = Modifier.size(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(130.dp)) {
                        val strokeWidth = 24f
                        val radius = (size.minDimension - strokeWidth) / 2
                        val center = Offset(size.width / 2, size.height / 2)
                        val arcSize = Size(radius * 2, radius * 2)
                        val arcTopLeft = Offset(center.x - radius, center.y - radius)

                        var startAngle = -90f

                        // Draw Background Ring
                        drawArc(
                            color = SecondaryBg,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = arcTopLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth)
                        )

                        // Draw Present Arc (Green)
                        if (presentAngle > 0) {
                            drawArc(
                                color = StatusSuccess,
                                startAngle = startAngle,
                                sweepAngle = presentAngle - 2f,
                                useCenter = false,
                                topLeft = arcTopLeft,
                                size = arcSize,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                            startAngle += presentAngle
                        }

                        // Draw Late Arc (Orange)
                        if (lateAngle > 0) {
                            drawArc(
                                color = StatusWarning,
                                startAngle = startAngle,
                                sweepAngle = lateAngle - 2f,
                                useCenter = false,
                                topLeft = arcTopLeft,
                                size = arcSize,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                            startAngle += lateAngle
                        }

                        // Draw Leave Arc (Yellow/Gold)
                        if (leaveAngle > 0) {
                            drawArc(
                                color = GoldSecondary,
                                startAngle = startAngle,
                                sweepAngle = leaveAngle - 2f,
                                useCenter = false,
                                topLeft = arcTopLeft,
                                size = arcSize,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                            startAngle += leaveAngle
                        }

                        // Draw Absent Arc (Red)
                        if (absentAngle > 0) {
                            drawArc(
                                color = StatusError,
                                startAngle = startAngle,
                                sweepAngle = absentAngle - 2f,
                                useCenter = false,
                                topLeft = arcTopLeft,
                                size = arcSize,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                    }

                    // Center Percentage Display
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$presentPct%",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = if (presentPct >= 80) StatusSuccess else StatusWarning
                        )
                        Text(
                            text = "PRESENT",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                            color = TextSlate
                        )
                    }
                }

                // Legend List
                Column(
                    modifier = Modifier.padding(start = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DonutLegendItem(color = StatusSuccess, label = "Present", count = present, percentage = presentPct)
                    DonutLegendItem(color = StatusWarning, label = "Late", count = late, percentage = latePct)
                    DonutLegendItem(color = GoldSecondary, label = "Leave", count = leave, percentage = leavePct)
                    DonutLegendItem(color = StatusError, label = "Absent", count = absent, percentage = absentPct)
                }
            }
        }
    }
}

@Composable
private fun DonutLegendItem(color: Color, label: String, count: Int, percentage: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = TextNavy
        )
        Text(
            text = "$count ($percentage%)",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}

/**
 * Visual Weekly / Daily Bar Chart showing attendance volume & rate.
 */
@Composable
fun AttendanceWeeklyBarChart(
    weeklyData: List<Pair<String, Float>>, // Label to Percentage (0..100)
    title: String = "📈 WEEKLY ATTENDANCE TREND",
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CardWhite,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = RoyalBlue
            )

            // Bar Chart Canvas Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                weeklyData.forEach { (label, value) ->
                    val barHeightFraction = (value / 100f).coerceIn(0.05f, 1f)
                    val barColor = when {
                        value >= 85f -> StatusSuccess
                        value >= 70f -> RoyalBlue
                        value >= 50f -> StatusWarning
                        else -> StatusError
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        // Value label on top of bar
                        Text(
                            text = "${value.toInt()}%",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                            color = barColor
                        )

                        Spacer(Modifier.height(4.dp))

                        // Scaled Bar Box
                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .fillMaxHeight(barHeightFraction * 0.8f)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(barColor, barColor.copy(alpha = 0.5f))
                                    ),
                                    shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = barColor,
                                    shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                                )
                        )

                        Spacer(Modifier.height(6.dp))

                        // Label under bar
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                            color = TextSlate
                        )
                    }
                }
            }
        }
    }
}

/**
 * Visual Line & Area Trend Graph showing monthly attendance percentage progression over time.
 */
@Composable
fun AttendanceTrendLineChart(
    trendPoints: List<Pair<String, Float>>, // Month/Week label to Attendance %
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
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
                Text(
                    text = "📈 ATTENDANCE PROGRESSION OVER TIME",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = RoyalBlue
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = StatusSuccessBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccessBorder)
                ) {
                    Text(
                        text = "LIVE SYNC",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                        color = StatusSuccessText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (trendPoints.isEmpty()) return@Canvas

                    val width = size.width
                    val height = size.height
                    val spacing = width / (trendPoints.size - 1).coerceAtLeast(1)

                    val maxVal = 100f
                    val minVal = 0f

                    val points = trendPoints.mapIndexed { index, pair ->
                        val x = index * spacing
                        val normalizedY = (pair.second - minVal) / (maxVal - minVal)
                        val y = height - (normalizedY * (height - 30f)) - 15f
                        Offset(x, y)
                    }

                    // Draw Area Gradient under path
                    val path = Path().apply {
                        moveTo(points.first().x, points.first().y)
                        for (i in 1 until points.size) {
                            val p1 = points[i - 1]
                            val p2 = points[i]
                            cubicTo(
                                (p1.x + p2.x) / 2, p1.y,
                                (p1.x + p2.x) / 2, p2.y,
                                p2.x, p2.y
                            )
                        }
                        lineTo(points.last().x, height)
                        lineTo(points.first().x, height)
                        close()
                    }

                    drawPath(
                        path = path,
                        brush = Brush.verticalGradient(
                            colors = listOf(RoyalBlue.copy(alpha = 0.25f), Color.Transparent)
                        )
                    )

                    // Draw Stroke Path
                    val strokePath = Path().apply {
                        moveTo(points.first().x, points.first().y)
                        for (i in 1 until points.size) {
                            val p1 = points[i - 1]
                            val p2 = points[i]
                            cubicTo(
                                (p1.x + p2.x) / 2, p1.y,
                                (p1.x + p2.x) / 2, p2.y,
                                p2.x, p2.y
                            )
                        }
                    }

                    drawPath(
                        path = strokePath,
                        color = RoyalBlue,
                        style = Stroke(width = 4f, cap = StrokeCap.Round)
                    )

                    // Draw Point Nodes
                    points.forEach { pt ->
                        drawCircle(
                            color = RoyalBlue,
                            radius = 6f,
                            center = pt
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 3f,
                            center = pt
                        )
                    }
                }
            }

            // X-Axis Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                trendPoints.forEach { (label, value) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${value.toInt()}%",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                            color = RoyalBlue
                        )
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = TextSlate
                        )
                    }
                }
            }
        }
    }
}

/**
 * Visual Batch Attendance Comparison Bar Chart.
 */
@Composable
fun BatchAttendanceComparisonChart(
    batchData: List<Triple<String, Int, Color>>, // Batch Name, Attendance %, Accent Color
    title: String = "📊 BATCH ATTENDANCE COMPARISON CHART",
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CardWhite,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = RoyalBlue
            )

            batchData.forEach { (batchName, percentage, accentColor) ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = batchName,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = TextNavy
                        )
                        Text(
                            text = "$percentage%",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = accentColor
                        )
                    }

                    // Progress bar track
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .background(SecondaryBg, RoundedCornerShape(5.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(percentage / 100f)
                                .fillMaxHeight()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(accentColor.copy(alpha = 0.7f), accentColor)
                                    ),
                                    shape = RoundedCornerShape(5.dp)
                                )
                        )
                    }
                }
            }
        }
    }
}
