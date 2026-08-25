package com.example.ui.student

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.AnnouncementEntity
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.GoldSecondary

@Composable
fun UrgentAnnouncementDialog(
    announcement: AnnouncementEntity,
    onDismiss: () -> Unit,
    onViewDetails: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Campaign,
                contentDescription = "Urgent Announcement",
                tint = CrimsonPrimary,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = "📢 ${announcement.title}",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    color = CrimsonPrimary.copy(alpha = 0.15f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "PRIORITY: ${announcement.priority.uppercase()}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = CrimsonPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = announcement.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onViewDetails,
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                modifier = Modifier.testTag("urgent_announcement_view_button")
            ) {
                Text("View Details")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("urgent_announcement_close_button")
            ) {
                Text("Dismiss", color = Color.Gray)
            }
        }
    )
}
