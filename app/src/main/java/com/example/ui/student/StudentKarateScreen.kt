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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
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
import com.example.data.UserAccountEntity
import com.example.ui.common.StatusBadge
import com.example.ui.theme.*

@Composable
fun StudentKarateScreen(
    student: UserAccountEntity,
    allStudentsList: List<UserAccountEntity> = emptyList()
) {
    var searchQuery by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val (currentBeltColor, nextBeltName, progressPct) = when (student.currentBelt.uppercase()) {
        "WHITE BELT" -> Triple(BeltWhite, "Yellow Belt", 0.75f)
        "YELLOW BELT" -> Triple(BeltYellow, "Orange Belt", 0.70f)
        "ORANGE BELT" -> Triple(BeltOrange, "Green Belt", 0.60f)
        "GREEN BELT" -> Triple(BeltGreen, "Blue Belt", 0.50f)
        "BLUE BELT" -> Triple(BeltBlue, "Brown Belt", 0.40f)
        "BROWN BELT" -> Triple(BeltBrown, "Black Belt (1st Dan)", 0.30f)
        "BLACK BELT" -> Triple(BeltBlack, "Master (2nd Dan)", 0.90f)
        else -> Triple(BeltGreen, "Next Belt Grade", 0.65f)
    }

    val filteredAthletes = allStudentsList.filter {
        it.fullName.contains(searchQuery, ignoreCase = true) ||
        it.userId.contains(searchQuery, ignoreCase = true) ||
        it.currentBelt.contains(searchQuery, ignoreCase = true) ||
        it.fatherName.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // BELT RANK & PROGRESS HERO CARD
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("karate_belt_card"),
            shape = RoundedCornerShape(16.dp),
            color = CardWhite,
            shadowElevation = 1.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "MY MARTIAL ARTS LEVEL & BELT GRADE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                            color = RoyalBlue
                        )
                        Text(
                            student.currentBelt,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextNavy
                        )
                        Text(
                            "Belt Level Number: ${student.beltLevel} • Reg No: ${student.userId}",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSlate
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(ActiveNavBg)
                            .border(2.dp, RoyalBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🥋", fontSize = 30.sp)
                    }
                }

                HorizontalDivider(color = BorderLight)

                // BELT PROGRESS BAR
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Target Next Belt: $nextBeltName", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                        Text("${(progressPct * 100).toInt()}%", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue)
                    }

                    LinearProgressIndicator(
                        progress = { progressPct },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = RoyalBlue,
                        trackColor = BorderLight
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Joined: ${student.joiningDate}", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                    Text("Dojo: ${student.dojoCenter}", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                }
            }
        }

        // ACADEMY ATHLETES & FELLOW KARATEKAS ROSTER
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Groups, contentDescription = null, tint = RoyalBlue)
                        Column {
                            Text(
                                "ACADEMY ATHLETES & ROSTER",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextNavy
                            )
                            Text(
                                "Explore fellow martial artists & rank progression",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSlate
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ActiveNavBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue.copy(alpha = 0.35f))
                    ) {
                        Text(
                            "${allStudentsList.size} Athletes",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = RoyalBlue,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search athlete name, belt rank, reg no...", color = TextSlate) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSlate) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("athlete_roster_search_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoyalBlue,
                        unfocusedBorderColor = BorderLight,
                        focusedTextColor = TextNavy,
                        unfocusedTextColor = TextNavy
                    )
                )

                if (filteredAthletes.isEmpty()) {
                    Text("No athletes found matching search query.", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        filteredAthletes.forEach { athlete ->
                            val (bColor, _) = when (athlete.currentBelt.uppercase()) {
                                "WHITE BELT" -> Pair(BeltWhite, Color.Black)
                                "YELLOW BELT" -> Pair(BeltYellow, Color.Black)
                                "ORANGE BELT" -> Pair(BeltOrange, Color.White)
                                "GREEN BELT" -> Pair(BeltGreen, Color.White)
                                "BLUE BELT" -> Pair(BeltBlue, Color.White)
                                "BROWN BELT" -> Pair(BeltBrown, Color.White)
                                "BLACK BELT" -> Pair(BeltBlack, Color.White)
                                else -> Pair(BeltGreen, Color.White)
                            }

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = CardWhite,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (athlete.userId == student.userId) RoyalBlue else BorderLight)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Surface(
                                            modifier = Modifier.size(46.dp),
                                            shape = CircleShape,
                                            color = SecondaryBg,
                                            border = androidx.compose.foundation.BorderStroke(1.5.dp, bColor)
                                        ) {
                                            if (!athlete.profilePhotoUri.isNull_or_blank()) {
                                                Image(
                                                    painter = rememberAsyncImagePainter(athlete.profilePhotoUri),
                                                    contentDescription = athlete.fullName,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            } else {
                                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                                    Text("🥋", fontSize = 20.sp)
                                                }
                                            }
                                        }

                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text(
                                                    text = athlete.fullName,
                                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = TextNavy
                                                )
                                                if (athlete.userId == student.userId) {
                                                    Surface(shape = RoundedCornerShape(4.dp), color = ActiveNavBg) {
                                                        Text("YOU", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = RoyalBlue, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                                    }
                                                }
                                            }
                                            Text("ID: ${athlete.userId} • Batch: ${athlete.batchName.take(15)}", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                                            if (athlete.fatherName.isNotBlank()) {
                                                Text("S/o, D/o: ${athlete.fatherName}", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                                            }
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = bColor.copy(alpha = 0.15f),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, bColor)
                                        ) {
                                            Text(
                                                text = athlete.currentBelt,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = if (athlete.currentBelt.uppercase() == "WHITE BELT") TextNavy else bColor,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                        Text("Level ${athlete.beltLevel}", style = MaterialTheme.typography.labelSmall, color = TextSlate, modifier = Modifier.padding(top = 2.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // MY KARATE TRAINING & CODE CARD
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = RoyalBlue)
                    Text(
                        "MY KARATE PILLARS & DOJO DISCIPLINE",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextNavy
                    )
                }

                HorizontalDivider(color = BorderLight)

                val pillars = listOf(
                    "🥋 Kihon (Fundamentals)" to "Mastery of basic stances, punches, kicks, and defense blocks.",
                    "🥋 Kata (Forms & Bunkai)" to "Traditional sequences demonstrating focus, speed, power, and rhythm.",
                    "🥋 Kumite (Controlled Sparring)" to "Dynamic application of tactical reflexes with protective equipment.",
                    "🥋 Kobudo & Weapon Training" to "Bo staff, Nunchaku, and traditional Okinawan martial arts weapons."
                )

                pillars.forEach { (title, desc) ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SecondaryBg, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                        Text(desc, style = MaterialTheme.typography.bodySmall, color = TextSlate)
                    }
                }
            }
        }
    }
}

private fun String?.isNull_or_blank(): Boolean {
    return this == null || this.trim().isEmpty()
}
