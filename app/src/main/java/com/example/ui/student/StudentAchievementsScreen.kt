package com.example.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserAccountEntity
import com.example.ui.common.StatusBadge
import com.example.ui.theme.*

@Composable
fun StudentAchievementsScreen(
    student: UserAccountEntity
) {
    val scrollState = rememberScrollState()

    val medalsList = listOf(
        Triple("🥇 Gold Medal", "Telangana State Karate Championship 2026", "Individual Kumite (Under-18)"),
        Triple("🥈 Silver Medal", "South India Open Martial Arts Cup 2025", "Kata Performance Category"),
        Triple("🏆 Special Award", "BROMA Dojo Excellence Trophy 2025", "100% Attendance & Exceptional Discipline")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "ACHIEVEMENTS & HONORS",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
            color = RoyalBlue
        )

        // LEADERBOARD / DOJO RANK CARD
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = CardWhite,
            shadowElevation = 1.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(ActiveNavBg)
                            .border(1.5.dp, RoyalBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏆", fontSize = 24.sp)
                    }

                    Column {
                        Text("DOJO RANKING", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                        Text("Rank #3 in Batch", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                    }
                }

                StatusBadge(status = "TOP 5%")
            }
        }

        // MEDAL TALLY LIST
        medalsList.forEach { (medal, title, desc) ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = CardWhite,
                shadowElevation = 1.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(medal.split(" ").firstOrNull() ?: "🏅", fontSize = 28.sp)

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(medal, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue)
                        Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = TextNavy)
                        Text(desc, style = MaterialTheme.typography.bodySmall, color = TextSlate)
                    }
                }
            }
        }
    }
}
