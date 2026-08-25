package com.example.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.AnnouncementEntity
import com.example.ui.theme.*

@Composable
fun StudentAnnouncementsScreen(
    announcementsList: List<AnnouncementEntity>
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text("ACADEMY ANNOUNCEMENTS", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = TextNavy)
            Text("Official notices, holiday updates & tournament news", style = MaterialTheme.typography.bodySmall, color = TextSlate)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (announcementsList.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = CardWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                    ) {
                        Text("No active announcements at this time.", style = MaterialTheme.typography.bodyMedium, color = TextSlate, modifier = Modifier.padding(16.dp))
                    }
                } else {
                    announcementsList.forEach { ann ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = CardWhite,
                            shadowElevation = 1.dp,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (ann.priority == "Urgent") StatusError.copy(alpha = 0.5f) else BorderLight)
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
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (ann.priority == "Urgent") StatusError.copy(alpha = 0.12f) else ActiveNavBg
                                    ) {
                                        Text(
                                            text = ann.priority.uppercase(),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (ann.priority == "Urgent") StatusError else RoyalBlue,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }

                                    Text("Published: ${ann.publishDate}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                                }

                                Text(ann.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                Text(ann.message, style = MaterialTheme.typography.bodyMedium, color = TextSlate)

                                Text("Audience: ${ann.audience}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium), color = RoyalBlue)
                            }
                        }
                    }
                }
            }
        }
    }
}
