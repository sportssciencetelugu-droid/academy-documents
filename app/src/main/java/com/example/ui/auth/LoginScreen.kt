package com.example.ui.auth

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AcademyLeadershipEntity
import com.example.ui.common.BromaAcademyLogo
import com.example.ui.common.BromaPrimaryButton
import com.example.ui.theme.*

@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onBackClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onNavigateToRegister: (() -> Unit)? = null,
    onQuickDemoLogin: ((String, String) -> Unit)? = null,
    leadershipList: List<AcademyLeadershipEntity> = emptyList()
) {
    var selectedPortal by remember { mutableStateOf("STUDENT") } // STUDENT, COACH, ADMIN
    var usernameOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("login_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextNavy
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "BROMA ACADEMY PORTAL",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = PrimaryRed
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Logo Header
            BromaAcademyLogo(size = 90.dp, showBorder = true, borderColor = BorderLight)

            Spacer(modifier = Modifier.height(16.dp))

            // 3 PORTAL TAB SELECTOR
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = CardWhite,
                shadowElevation = 1.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (selectedPortal == "STUDENT") ActiveNavBg else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                selectedPortal = "STUDENT"
                                errorMessage = null
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Student / Parent",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (selectedPortal == "STUDENT") RoyalBlue else TextSlate
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (selectedPortal == "COACH") ActiveNavBg else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                selectedPortal = "COACH"
                                errorMessage = null
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Coach Portal",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (selectedPortal == "COACH") RoyalBlue else TextSlate
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (selectedPortal == "ADMIN") ActiveNavBg else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                selectedPortal = "ADMIN"
                                errorMessage = null
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Admin Portal",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (selectedPortal == "ADMIN") RoyalBlue else TextSlate
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // PORTAL HEADINGS & DESCRIPTION
            val portalTitle = when (selectedPortal) {
                "COACH" -> "Sensei / Coach Portal"
                "ADMIN" -> "Chief Admin Portal"
                else -> "Student & Parent Portal"
            }
            val portalSubtitle = when (selectedPortal) {
                "COACH" -> "Access batch schedules & record daily attendance"
                "ADMIN" -> "Executive administration portal for academy management & governance"
                else -> "Track belt progress, schedules, attendance, fees & certificates"
            }

            Text(
                text = portalTitle,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = TextNavy
            )

            Text(
                text = portalSubtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSlate,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            // CREDENTIAL INPUT FORM
            OutlinedTextField(
                value = usernameOrEmail,
                onValueChange = {
                    usernameOrEmail = it
                    errorMessage = null
                },
                label = {
                    Text(
                        when (selectedPortal) {
                            "ADMIN" -> "Chief Admin Email or Username"
                            "COACH" -> "Coach Email or Username"
                            else -> "Student Email / Username"
                        }
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = when (selectedPortal) {
                            "COACH" -> Icons.Default.SportsMartialArts
                            "ADMIN" -> Icons.Default.Security
                            else -> Icons.Default.Person
                        },
                        contentDescription = null,
                        tint = DeepMartialRed
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_username_input"),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardWhite,
                    unfocusedContainerColor = CardWhite,
                    focusedBorderColor = PrimaryRed,
                    unfocusedBorderColor = BorderLight,
                    focusedLabelColor = PrimaryRed,
                    unfocusedLabelColor = TextSlate,
                    focusedTextColor = TextNavy,
                    unfocusedTextColor = TextNavy
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
                label = { Text("Password") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryRed)
                },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle Password Visibility",
                            tint = TextSlate
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_password_input"),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardWhite,
                    unfocusedContainerColor = CardWhite,
                    focusedBorderColor = PrimaryRed,
                    unfocusedBorderColor = BorderLight,
                    focusedLabelColor = PrimaryRed,
                    unfocusedLabelColor = TextSlate,
                    focusedTextColor = TextNavy,
                    unfocusedTextColor = TextNavy
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        modifier = Modifier.testTag("login_remember_me_checkbox"),
                        colors = CheckboxDefaults.colors(checkedColor = PrimaryRed)
                    )
                    Text("Remember me", style = MaterialTheme.typography.bodySmall, color = TextNavy)
                }

                TextButton(
                    onClick = onForgotPasswordClick,
                    modifier = Modifier.testTag("login_forgot_password_button")
                ) {
                    Text("Forgot Password?", color = PrimaryRed, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = StatusErrorBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusErrorBorder)
                ) {
                    Text(
                        text = errorMessage!!,
                        color = StatusErrorText,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            BromaPrimaryButton(
                text = "SIGN IN TO ${selectedPortal} PORTAL",
                onClick = {
                    if (usernameOrEmail.isBlank() || password.isBlank()) {
                        errorMessage = "Please enter both username/email and password."
                    } else {
                        onLoginClick(usernameOrEmail.trim(), password.trim())
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                testTagStr = "login_submit_button"
            )

            if (onQuickDemoLogin != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = CardWhite,
                    shadowElevation = 1.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "⚡ ONE-CLICK DEMO LOGIN",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = PrimaryRed
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = { onQuickDemoLogin("yogindra01", "student123") },
                                modifier = Modifier.weight(1f).testTag("quick_student_login"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed, contentColor = TextOnRed)
                            ) {
                                Text("Student", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { onQuickDemoLogin("sensei01", "coach123") },
                                modifier = Modifier.weight(1f).testTag("quick_coach_login"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DeepMartialRed, contentColor = TextOnRed)
                            ) {
                                Text("Coach", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { onQuickDemoLogin("modernshitoryukaratedo@gmail.com", "Bromaa@143") },
                                modifier = Modifier.weight(1f).testTag("quick_admin_login"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DarkRed, contentColor = TextOnRed)
                            ) {
                                Text("Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            if (onNavigateToRegister != null && selectedPortal != "ADMIN") {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (selectedPortal == "COACH") "Want to apply as Sensei?" else "Don't have an account?",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSlate
                    )
                    TextButton(onClick = onNavigateToRegister) {
                        Text(
                            text = if (selectedPortal == "COACH") "Apply as Coach" else "Create Account",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = RoyalBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ACADEMY LEADERSHIP HIERARCHY BANNER IN AUTH
            AcademyLeadershipHierarchyCard(leadershipList = leadershipList)
        }
    }
}

@Composable
fun AcademyLeadershipHierarchyCard(
    leadershipList: List<AcademyLeadershipEntity> = emptyList(),
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = CardWhite,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Security, contentDescription = null, tint = RoyalBlue)
                Column {
                    Text(
                        "ACADEMY LEADERSHIP HIERARCHY",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextNavy
                    )
                    Text(
                        "Governing Executive Committee & Officials",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSlate
                    )
                }
            }

            HorizontalDivider(color = BorderLight)

            val defaultHierarchy = listOf(
                Triple("Director & Chairman", "Ch. Sujatamutyalu", "🏛️"),
                Triple("Founder & President", "A. Tatarao", "🥋"),
                Triple("General Secretary", "A. Sombabu", "📜"),
                Triple("Treasurer", "A. Sailjaraj", "💰")
            )

            val hierarchyItems: List<Triple<String, String, String>> = if (leadershipList.isNotEmpty()) {
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
                defaultHierarchy
            }

            hierarchyItems.forEach { (post, name, icon) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SecondaryBg, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(icon, fontSize = 16.sp)
                        Column {
                            Text(
                                text = post,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = RoyalBlue
                            )
                            Text(
                                text = name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextNavy
                            )
                        }
                    }
                    Surface(shape = RoundedCornerShape(4.dp), color = ActiveNavBg) {
                        Text(
                            "OFFICIAL",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = RoyalBlue,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }
    }
}
