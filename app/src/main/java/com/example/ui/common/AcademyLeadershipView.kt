package com.example.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.AcademyInstructorsData
import com.example.data.AcademyLeadershipEntity
import com.example.data.AcademyStandardEntity
import com.example.data.UserAccountEntity
import com.example.ui.theme.*

@Composable
fun AcademyLeadershipSection(
    leadershipList: List<AcademyLeadershipEntity> = emptyList(),
    standardsList: List<AcademyStandardEntity> = emptyList(),
    coachesList: List<UserAccountEntity> = emptyList(),
    modifier: Modifier = Modifier,
    showAcademyRules: Boolean = true
) {
    var selectedTab by remember { mutableStateOf("OFFICIALS") } // "OFFICIALS", "COACHES", "STANDARDS"

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Header Banner
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.AccountBalance, contentDescription = null, tint = RoyalBlue)
                    Text(
                        text = "ACADEMY OFFICIALS & HIERARCHY",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                        color = TextNavy
                    )
                }
                Text(
                    text = "Official Governance Board, Executive Leadership, Grandmasters & Black Belt Coaching Faculty",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSlate
                )

                // Category Filter Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedTab == "OFFICIALS",
                        onClick = { selectedTab = "OFFICIALS" },
                        label = { Text("Executive Officials (${leadershipList.size})", fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RoyalBlue,
                            selectedLabelColor = TextOnAccent
                        )
                    )
                    FilterChip(
                        selected = selectedTab == "COACHES",
                        onClick = { selectedTab = "COACHES" },
                        label = { Text("Coaching Faculty (${coachesList.ifEmpty { AcademyInstructorsData.OFFICIAL_INSTRUCTORS }.size})", fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RoyalBlue,
                            selectedLabelColor = TextOnAccent
                        )
                    )
                    if (showAcademyRules) {
                        FilterChip(
                            selected = selectedTab == "STANDARDS",
                            onClick = { selectedTab = "STANDARDS" },
                            label = { Text("Dojo Standards", fontWeight = FontWeight.SemiBold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RoyalBlue,
                                selectedLabelColor = TextOnAccent
                            )
                        )
                    }
                }
            }
        }

        when (selectedTab) {
            "OFFICIALS" -> {
                if (leadershipList.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = CardWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Groups, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(36.dp))
                            Text("Official Leadership Hierarchy", fontWeight = FontWeight.Bold, color = TextNavy)
                            Text("Director, Chairman, President, General Secretary & Treasurer posts appear here in real-time.", color = TextSlate, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                } else {
                    leadershipList.sortedBy { it.displayOrder }.forEachIndexed { index, leader ->
                        AcademyOfficialCard(leader = leader, index = index + 1)
                    }
                }
            }
            "COACHES" -> {
                val instructorsToShow = coachesList.ifEmpty { AcademyInstructorsData.OFFICIAL_INSTRUCTORS }
                instructorsToShow.forEachIndexed { index, coach ->
                    CoachHierarchyCard(coach = coach, index = index + 1)
                }
            }
            "STANDARDS" -> {
                // Standards list
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = CardWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = StatusSuccess)
                            Text(
                                text = "OFFICIAL ACADEMY STANDARDS & CODE",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextNavy
                            )
                        }

                        if (standardsList.isNotEmpty()) {
                            standardsList.sortedBy { it.orderNumber }.forEach { std ->
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text("🥋", fontSize = 14.sp)
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(std.title, fontWeight = FontWeight.Bold, color = TextNavy, style = MaterialTheme.typography.bodyMedium)
                                        Text(std.description, color = TextSlate, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        } else {
                            AcademyInstructorsData.DOJO_STANDARDS_RULES.forEach { rule ->
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text("🥋", fontSize = 14.sp)
                                    Text(
                                        text = rule,
                                        style = MaterialTheme.typography.bodySmall,
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

@Composable
fun AcademyOfficialCard(leader: AcademyLeadershipEntity, index: Int) {
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("official_card_${leader.leadershipId}"),
        shape = RoundedCornerShape(12.dp),
        color = CardWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
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
                // Post Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = StatusInfoBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusInfoBorder)
                ) {
                    Text(
                        text = leader.postTitle.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                        color = StatusInfoText
                    )
                }

                if (leader.isExecutiveBoard) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = StatusSuccessBg
                    ) {
                        Text(
                            text = "Executive Board",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = StatusSuccessText
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(StatusInfoBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AccountBalance, contentDescription = null, tint = RoyalBlue)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = leader.fullName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextNavy
                    )
                    Text(
                        text = leader.rankOrBelt,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = RoyalBlue
                    )
                }
            }

            if (leader.messageOrBio.isNotBlank()) {
                Text(
                    text = leader.messageOrBio,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSlate
                )
            }

            if (isExpanded) {
                HorizontalDivider(color = BorderLight)
                if (leader.contactPhone.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(16.dp))
                        Text("Phone: ${leader.contactPhone}", style = MaterialTheme.typography.bodySmall, color = TextNavy)
                    }
                }
                if (leader.contactEmail.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Email, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(16.dp))
                        Text("Email: ${leader.contactEmail}", style = MaterialTheme.typography.bodySmall, color = TextNavy)
                    }
                }
            }

            TextButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = if (isExpanded) "Show Less ▲" else "View Contact & Details ▼",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoyalBlue
                )
            }
        }
    }
}

@Composable
fun CoachHierarchyCard(coach: UserAccountEntity, index: Int) {
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("coach_card_${coach.userId}"),
        shape = RoundedCornerShape(12.dp),
        color = CardWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = StatusInfoBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, StatusInfoBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "#$index",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = StatusInfoText
                    )
                    Text(
                        text = coach.designation.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                        color = StatusInfoText
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = coach.fullName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextNavy
                    )
                    Text(
                        text = "${coach.currentBelt} • ${coach.experienceYears ?: 5}+ Years Experience",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = RoyalBlue
                    )
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(StatusInfoBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = RoyalBlue)
                }
            }

            if (!coach.specializations.isNullOrEmpty()) {
                Text(
                    text = "Specialization: ${coach.specializations}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSlate
                )
            }

            if (isExpanded) {
                HorizontalDivider(color = BorderLight)
                if (!coach.bio.isNullOrEmpty()) {
                    Text(
                        text = coach.bio!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSlate
                    )
                }
                if (coach.phone.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(16.dp))
                        Text("Contact: ${coach.phone}", style = MaterialTheme.typography.bodySmall, color = TextNavy)
                    }
                }
            }

            TextButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = if (isExpanded) "Show Less ▲" else "View Bio & Details ▼",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoyalBlue
                )
            }
        }
    }
}
