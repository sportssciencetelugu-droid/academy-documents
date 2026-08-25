package com.example.ui.auth

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AcademyLeadershipEntity
import com.example.ui.common.BromaAcademyLogo
import com.example.ui.common.BromaPrimaryButton
import com.example.ui.common.BromaSecondaryButton
import com.example.ui.theme.*

@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onQuickDemoLogin: ((String, String) -> Unit)? = null,
    leadershipList: List<AcademyLeadershipEntity> = emptyList()
) {
    var showAcademyStandardsModal by remember { mutableStateOf(false) }

    if (showAcademyStandardsModal) {
        AlertDialog(
            onDismissRequest = { showAcademyStandardsModal = false },
            title = null,
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.85f)
                        .fillMaxWidth()
                ) {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .padding(vertical = 8.dp)
                    ) {
                        AcademyLeadershipHierarchyCard(leadershipList = leadershipList)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAcademyStandardsModal = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed, contentColor = TextOnRed)
                ) {
                    Text("Close", color = TextOnRed, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = CardBackground,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Brand Header Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BromaAcademyLogo(size = 100.dp, showBorder = true, borderColor = DefaultBorder)

                Text(
                    text = "BROMA ACADEMY",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    ),
                    color = DeepMartialRed,
                    textAlign = TextAlign.Center
                )

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = ActiveNavBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryRed.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "Train  •  Discipline  •  Achieve",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.2.sp
                        ),
                        color = DeepMartialRed,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                Text(
                    text = "Complete Martial Arts & Dojo Management Portal",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // ACADEMY LEADERSHIP HIERARCHY - EMBEDDED DIRECTLY ON AUTH SCREEN
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = CardBackground,
                shadowElevation = 2.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, DefaultBorder)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(ActiveNavBg, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🥋", fontSize = 16.sp)
                        }
                        Column {
                            Text(
                                "ACADEMY LEADERSHIP HIERARCHY",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                                color = DeepMartialRed
                            )
                            Text(
                                "Official Executive Board & Governing Officials",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }

                    HorizontalDivider(color = DefaultBorder)

                    val defaultOfficials = listOf(
                        Triple("Director & Chairman", "Ch. Sujatamutyalu", "🏛️"),
                        Triple("Founder & President", "A. Tatarao", "🥋"),
                        Triple("General Secretary", "A. Sombabu", "📜"),
                        Triple("Treasurer", "A. Sailjaraj", "💰")
                    )

                    val displayOfficials: List<Triple<String, String, String>> = if (leadershipList.isNotEmpty()) {
                        leadershipList.map { leader ->
                            val icon = when {
                                leader.designation.contains("Director", ignoreCase = true) || leader.designation.contains("Chairman", ignoreCase = true) -> "🏛️"
                                leader.designation.contains("President", ignoreCase = true) || leader.designation.contains("Founder", ignoreCase = true) -> "🥋"
                                leader.designation.contains("Secretary", ignoreCase = true) -> "📜"
                                leader.designation.contains("Treasurer", ignoreCase = true) -> "💰"
                                else -> "🥋"
                            }
                            Triple(leader.designation, leader.name, icon)
                        }
                    } else {
                        defaultOfficials
                    }

                    displayOfficials.forEach { (post, name, icon) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SecondaryBg, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(icon, fontSize = 18.sp)
                                Column {
                                    Text(
                                        text = post,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = DeepMartialRed
                                    )
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = ActiveNavBg,
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, PrimaryRed.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    "OFFICIAL",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = DeepMartialRed,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onNavigateToLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("welcome_login_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryRed,
                        contentColor = TextOnRed
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "LOGIN TO PORTAL",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextOnRed)
                    )
                }

                OutlinedButton(
                    onClick = onNavigateToRegister,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("welcome_create_account_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = CardBackground,
                        contentColor = DeepMartialRed
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, PrimaryRed)
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = DeepMartialRed
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Create Student Account",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepMartialRed)
                    )
                }

                OutlinedButton(
                    onClick = { showAcademyStandardsModal = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("welcome_instructors_standards_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = CardBackground,
                        contentColor = TextPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DefaultBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.MilitaryTech,
                        contentDescription = null,
                        tint = DeepMartialRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🥋 Academy Instructors & Standards",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                    )
                }

                if (onQuickDemoLogin != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = CardBackground,
                        shadowElevation = 1.dp,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DefaultBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "⚡ DEV DEMO PORTAL ACCESS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = DeepMartialRed
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Button(
                                    onClick = { onQuickDemoLogin("yogindra01", "student123") },
                                    modifier = Modifier.weight(1f).testTag("demo_student_btn"),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed, contentColor = TextOnRed)
                                ) {
                                    Text("Student", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { onQuickDemoLogin("sensei01", "coach123") },
                                    modifier = Modifier.weight(1f).testTag("demo_coach_btn"),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = DeepMartialRed, contentColor = TextOnRed)
                                ) {
                                    Text("Coach", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { onQuickDemoLogin("admin", "Yogindra@123") },
                                    modifier = Modifier.weight(1f).testTag("demo_admin_btn"),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkRed, contentColor = TextOnRed)
                                ) {
                                    Text("Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                TextButton(
                    onClick = onForgotPasswordClick,
                    modifier = Modifier.testTag("welcome_forgot_password_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Forgot Password?",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
