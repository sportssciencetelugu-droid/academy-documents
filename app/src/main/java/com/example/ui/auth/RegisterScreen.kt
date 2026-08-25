package com.example.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AcademyLeadershipEntity
import com.example.ui.theme.*

@Composable
fun RegisterScreen(
    onRegisterSubmit: (String, String, String, String, String, String) -> Unit,
    onRegisterCoachSubmit: (String, String, String, String, String, String, Int) -> Unit,
    onBackClick: () -> Unit,
    leadershipList: List<AcademyLeadershipEntity> = emptyList()
) {
    var selectedRole by remember { mutableStateOf("STUDENT") } // STUDENT, COACH, ADMIN

    var fullName by remember { mutableStateOf("") }
    var parentEmail by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var specialization by remember { mutableStateOf("Kumite & Kata Training") }
    var experienceYears by remember { mutableStateOf("5") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successInfoMessage by remember { mutableStateOf<String?>(null) }

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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("register_back_button")
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextNavy)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when(selectedRole) {
                        "COACH" -> "Apply for Coach / Sensei"
                        "ADMIN" -> "Admin Portal Provisioning"
                        else -> "Create Student Account"
                    },
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextNavy
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ROLE SELECTOR TAB ROW
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
                                color = if (selectedRole == "STUDENT") ActiveNavBg else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedRole = "STUDENT"; errorMessage = null; successInfoMessage = null }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Student / Parent",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (selectedRole == "STUDENT") RoyalBlue else TextSlate
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (selectedRole == "COACH") ActiveNavBg else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedRole = "COACH"; errorMessage = null; successInfoMessage = null }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Coach / Sensei",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (selectedRole == "COACH") RoyalBlue else TextSlate
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (selectedRole == "ADMIN") ActiveNavBg else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedRole = "ADMIN"; errorMessage = null; successInfoMessage = null }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Admin Staff",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (selectedRole == "ADMIN") RoyalBlue else TextSlate
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ROLE DESCRIPTIVE BANNER
            when (selectedRole) {
                "STUDENT" -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = CardWhite,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("🥋", fontSize = 20.sp)
                            Column {
                                Text(
                                    "Student & Family Registration",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextNavy
                                )
                                Text(
                                    "Parents can register multiple child usernames (e.g. yogindra01, rahul02) under one Parent Email.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSlate
                                )
                            }
                        }
                    }
                }
                "COACH" -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = CardWhite,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldSecondary.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = DeepMartialRed, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "ADMIN APPROVAL PROCESS",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = DeepMartialRed
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Coach applications are verified by the Academy Executive Board. You can sign in as Sensei once the Chief Admin approves your credentials.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSlate
                            )
                        }
                    }
                }
                "ADMIN" -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = CardWhite,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryRed.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = PrimaryRed, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "CHIEF ADMIN RESTRICTED ACCESS",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = PrimaryRed
                                )
                            }
                            Text(
                                text = "Master Admin access is strictly configured for modernshitoryukaratedo@gmail.com. Public creation of Admin accounts is disabled to protect academy governance.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextNavy
                            )
                            HorizontalDivider(color = BorderLight)
                            Text(
                                text = "To log in as Chief Admin, please return to the Sign In screen and select the Admin Portal.",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSlate
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CardWhite,
                unfocusedContainerColor = CardWhite,
                focusedBorderColor = PrimaryRed,
                unfocusedBorderColor = BorderLight,
                focusedLabelColor = PrimaryRed,
                unfocusedLabelColor = TextSlate,
                focusedTextColor = TextNavy,
                unfocusedTextColor = TextNavy,
                cursorColor = PrimaryRed
            )

            if (selectedRole != "ADMIN") {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text(if (selectedRole == "COACH") "Sensei / Coach Full Name" else "Student Full Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryRed) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("register_fullname_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = textFieldColors
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (selectedRole == "STUDENT") {
                    OutlinedTextField(
                        value = parentEmail,
                        onValueChange = { parentEmail = it },
                        label = { Text("Parent Email (Optional)") },
                        leadingIcon = { Icon(Icons.Default.SupervisorAccount, contentDescription = null, tint = RoyalBlue) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_parent_email_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = textFieldColors
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (selectedRole == "COACH") {
                    OutlinedTextField(
                        value = specialization,
                        onValueChange = { specialization = it },
                        label = { Text("Specialization (e.g. Kumite, Bo Staff, Weapons)") },
                        leadingIcon = { Icon(Icons.Default.SportsMartialArts, contentDescription = null, tint = PrimaryRed) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = textFieldColors
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = experienceYears,
                        onValueChange = { experienceYears = it },
                        label = { Text("Years of Martial Arts Experience") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = PrimaryRed) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = textFieldColors
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Unique Username (e.g., ${if (selectedRole == "COACH") "sensei_raj" else "yogindra01"})") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryRed) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("register_username_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = textFieldColors
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = PrimaryRed) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("register_email_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = textFieldColors
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = PrimaryRed) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("register_phone_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = textFieldColors
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryRed) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("register_password_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = textFieldColors
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryRed) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("register_confirm_password_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = textFieldColors
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = StatusErrorBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusErrorBorder)
                ) {
                    Text(errorMessage!!, color = StatusErrorText, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(10.dp))
                }
            }

            if (successInfoMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = StatusSuccessBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccessBorder)
                ) {
                    Text(successInfoMessage!!, color = StatusSuccessText, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(10.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (selectedRole == "ADMIN") {
                Button(
                    onClick = onBackClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed, contentColor = TextOnRed)
                ) {
                    Text("GO TO ADMIN SIGN IN", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextOnRed)
                }
            } else {
                Button(
                    onClick = {
                        if (fullName.isBlank() || username.isBlank() || email.isBlank() || password.isBlank()) {
                            errorMessage = "Please fill in all mandatory fields."
                        } else if (password != confirmPassword) {
                            errorMessage = "Passwords do not match."
                        } else {
                            if (selectedRole == "STUDENT") {
                                onRegisterSubmit(email.trim(), username.trim(), password.trim(), fullName.trim(), parentEmail.trim(), phone.trim())
                            } else if (selectedRole == "COACH") {
                                val expInt = experienceYears.toIntOrNull() ?: 5
                                onRegisterCoachSubmit(email.trim(), username.trim(), password.trim(), fullName.trim(), phone.trim(), specialization.trim(), expInt)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("register_submit_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed, contentColor = TextOnRed)
                ) {
                    Text(
                        text = if (selectedRole == "COACH") "SUBMIT COACH APPLICATION" else "CREATE STUDENT ACCOUNT",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextOnRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ACADEMY LEADERSHIP HIERARCHY IN AUTH
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = CardWhite,
                shadowElevation = 1.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
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

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

