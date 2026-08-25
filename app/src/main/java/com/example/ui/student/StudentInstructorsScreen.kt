package com.example.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AcademyLeadershipEntity
import com.example.data.AcademyStandardEntity
import com.example.data.UserAccountEntity
import com.example.ui.common.AcademyLeadershipSection
import com.example.ui.theme.*

@Composable
fun StudentInstructorsScreen(
    leadershipList: List<AcademyLeadershipEntity> = emptyList(),
    standardsList: List<AcademyStandardEntity> = emptyList(),
    coachesList: List<UserAccountEntity> = emptyList()
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "ACADEMY LEADERSHIP & DOJO HIERARCHY",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                color = TextNavy
            )
            Text(
                text = "Official Governance Board, Black Belt Coaches, and Academy Standards (Real-time updates)",
                style = MaterialTheme.typography.bodySmall,
                color = TextSlate
            )
        }

        AcademyLeadershipSection(
            leadershipList = leadershipList,
            standardsList = standardsList,
            coachesList = coachesList,
            showAcademyRules = true
        )
    }
}
