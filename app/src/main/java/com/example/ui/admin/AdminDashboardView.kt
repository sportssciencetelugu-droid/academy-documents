package com.example.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PaymentRecordEntity
import com.example.ui.theme.*

@Composable
fun AdminDashboardView(
    studentsCount: Int,
    coachesCount: Int,
    batchesCount: Int,
    paymentsList: List<PaymentRecordEntity>
) {
    val totalCollected = paymentsList.filter { it.status == "PAID" }.sumOf { it.amount }
    val totalPending = paymentsList.filter { it.status == "VERIFICATION_PENDING" || it.status == "DUE" }.sumOf { it.amount }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = CardWhite,
            shadowElevation = 1.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("BROMA ACADEMY CONTROL CENTER", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = RoyalBlue)
                Text("Overall Academy KPI Metrics & Operational Status", style = MaterialTheme.typography.bodySmall, color = TextSlate)
            }
        }

        // 3 Metric Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = CardWhite,
                shadowElevation = 1.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Total Students", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                    Text("$studentsCount", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                }
            }

            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = CardWhite,
                shadowElevation = 1.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Coaches", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                    Text("$coachesCount", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = RoyalBlue)
                }
            }

            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = CardWhite,
                shadowElevation = 1.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Active Batches", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                    Text("$batchesCount", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = StatusSuccess)
                }
            }
        }

        // Fee Summary
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = CardWhite,
            shadowElevation = 1.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("FINANCIAL COLLECTION SUMMARY", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Collected", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                        Text("₹${totalCollected.toInt()}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = StatusSuccess)
                    }

                    Column {
                        Text("Pending Verification", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                        Text("₹${totalPending.toInt()}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = StatusWarning)
                    }
                }
            }
        }
    }
}
