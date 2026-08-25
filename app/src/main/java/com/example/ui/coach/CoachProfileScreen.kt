package com.example.ui.coach

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.ui.common.PhotoPickerSelector
import com.example.ui.theme.*

@Composable
fun CoachProfileScreen(
    coach: UserAccountEntity,
    onSaveCoachProfile: (UserAccountEntity) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var showEditDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Coach Profile Banner Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = CardWhite,
            shadowElevation = 1.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(ActiveNavBg)
                        .border(2.5.dp, RoyalBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (!coach.avatarUrl.isNullOrBlank()) {
                        Image(
                            painter = rememberAsyncImagePainter(coach.avatarUrl),
                            contentDescription = coach.fullName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = RoyalBlue,
                            modifier = Modifier.size(54.dp)
                        )
                    }
                }

                Text(
                    text = coach.fullName,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextNavy
                )
                Text(
                    text = coach.designation,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = RoyalBlue
                )

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = ActiveNavBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "🥋 ${coach.currentBelt}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = RoyalBlue,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                Button(
                    onClick = { showEditDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("edit_coach_profile_button")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit Coach Profile & Photo", color = TextOnAccent)
                }
            }
        }

        // Contact & Academy Assignment Card
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
                Text(
                    "CONTACT & DOJO ASSIGNMENTS",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                    color = RoyalBlue
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("✉️ Email: ${coach.email}", style = MaterialTheme.typography.bodyMedium, color = TextNavy)
                    Text("📱 Phone: ${if (coach.phone.isNotBlank()) coach.phone else "+91 98765 43210"}", style = MaterialTheme.typography.bodyMedium, color = TextNavy)
                    Text("👤 Username: @${coach.username}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                    Text("📍 Dojo Center: ${coach.dojoCenter.ifBlank { "Main Dojang - Central Branch" }}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                    Text("⏰ Primary Batch: ${coach.batchId}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                }
            }
        }

        // Professional Biography Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = CardWhite,
            shadowElevation = 1.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "BIOGRAPHY & TEACHING PHILOSOPHY",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                    color = RoyalBlue
                )
                Text(
                    text = coach.bio ?: "Sensei ${coach.fullName} is an esteemed martial arts practitioner dedicated to coaching karate discipline, Kata perfection, and high-performance Kumite fighting strategy.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextNavy
                )
                Text("Coaching Experience: ${coach.experienceYears ?: 14} Years Active Teaching", style = MaterialTheme.typography.labelMedium, color = RoyalBlue)
                Text("Specializations: ${coach.specializations ?: "Shotokan Kata, Sport Kumite, Self Defense, Bunkai Applications"}", style = MaterialTheme.typography.labelMedium, color = StatusSuccess)
            }
        }

        // Key Achievements List
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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = RoyalBlue)
                    Text(
                        "HONORS & CERTIFICATIONS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                        color = RoyalBlue
                    )
                }

                val achievements = listOf(
                    "🏆 National Karate Championship Medalist (Kumite Senior Division)",
                    "🥇 State Martial Arts Gold Medalist & Lead Seminar Clinician",
                    "🎓 Certified National WKF Karate Referee & Examiner",
                    "📜 Black Belt Dan Certification from All India Karate Federation"
                )

                achievements.forEach { ach ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = SecondaryBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                    ) {
                        Text(
                            ach,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = TextNavy,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }

    // Modal: Edit Coach Profile & Photo
    if (showEditDialog) {
        var editName by remember { mutableStateOf(coach.fullName) }
        var editPhone by remember { mutableStateOf(coach.phone) }
        var editEmail by remember { mutableStateOf(coach.email) }
        var editBelt by remember { mutableStateOf(coach.currentBelt) }
        var editDesignation by remember { mutableStateOf(coach.designation) }
        var editSpecializations by remember { mutableStateOf(coach.specializations ?: "Shotokan Kata, Kumite, Self Defense") }
        var editBio by remember { mutableStateOf(coach.bio ?: "") }
        var editExp by remember { mutableStateOf((coach.experienceYears ?: 14).toString()) }
        var editAvatarUrl by remember { mutableStateOf(coach.avatarUrl ?: "") }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text(
                    "Edit Sensei Profile & Photo",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextNavy
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 480.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PhotoPickerSelector(
                        selectedImageUri = editAvatarUrl,
                        onImageSelected = { editAvatarUrl = it },
                        label = "COACH PROFILE PHOTO (GALLERY OR PRESETS)",
                        categoryHint = "Profile"
                    )

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name", color = TextSlate) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        )
                    )

                    OutlinedTextField(
                        value = editDesignation,
                        onValueChange = { editDesignation = it },
                        label = { Text("Designation (e.g. Chief Instructor / Sensei)", color = TextSlate) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        )
                    )

                    OutlinedTextField(
                        value = editBelt,
                        onValueChange = { editBelt = it },
                        label = { Text("Belt / Dan Rank", color = TextSlate) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        )
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editPhone,
                            onValueChange = { editPhone = it },
                            label = { Text("Contact Phone", color = TextSlate) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalBlue,
                                unfocusedBorderColor = BorderLight,
                                focusedTextColor = TextNavy,
                                unfocusedTextColor = TextNavy
                            )
                        )
                        OutlinedTextField(
                            value = editExp,
                            onValueChange = { editExp = it },
                            label = { Text("Years Exp.", color = TextSlate) },
                            modifier = Modifier.weight(0.7f),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalBlue,
                                unfocusedBorderColor = BorderLight,
                                focusedTextColor = TextNavy,
                                unfocusedTextColor = TextNavy
                            )
                        )
                    }

                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("Email Address", color = TextSlate) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        )
                    )

                    OutlinedTextField(
                        value = editSpecializations,
                        onValueChange = { editSpecializations = it },
                        label = { Text("Specializations", color = TextSlate) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        )
                    )

                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Coach Bio & Philosophy", color = TextSlate) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = coach.copy(
                            fullName = editName,
                            phone = editPhone,
                            email = editEmail,
                            currentBelt = editBelt,
                            designation = editDesignation,
                            specializations = editSpecializations,
                            bio = editBio,
                            experienceYears = editExp.toIntOrNull() ?: 14,
                            profilePhotoUri = editAvatarUrl
                        )
                        onSaveCoachProfile(updated)
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("save_coach_profile_dialog_button")
                ) {
                    Text("Save Changes", color = TextOnAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = TextSlate)
                }
            }
        )
    }
}

