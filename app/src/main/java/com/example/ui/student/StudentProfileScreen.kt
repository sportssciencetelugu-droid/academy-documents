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
import com.example.data.StudentRequestEntity
import com.example.data.UserAccountEntity
import com.example.ui.common.PhotoPickerSelector
import com.example.ui.theme.*

@Composable
fun StudentProfileScreen(
    student: UserAccountEntity,
    siblingList: List<UserAccountEntity>,
    requestsList: List<StudentRequestEntity>,
    onChildSwitch: (UserAccountEntity) -> Unit,
    onSavePersonalDetails: (String, String, String, String) -> Unit,
    onRequestChangeSubmit: (String, String, String, String) -> Unit,
    onUpdateClassAndTraining: (String, String) -> Unit = { _, _ -> },
    onSaveStudentProfile: ((UserAccountEntity) -> Unit)? = null
) {
    var showPersonalEditDialog by remember { mutableStateOf(false) }
    var showOfficialRequestDialog by remember { mutableStateOf(false) }
    var showClassEditDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Family Switcher Header if multiple children exist
        if (siblingList.size > 1) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = CardWhite,
                shadowElevation = 1.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "👨‍👩‍👧 FAMILY ACCOUNT — MY CHILDREN",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = RoyalBlue
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        siblingList.forEach { child ->
                            FilterChip(
                                selected = child.userId == student.userId,
                                onClick = { onChildSwitch(child) },
                                label = { Text(child.fullName) },
                                leadingIcon = {
                                    if (child.userId == student.userId) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ActiveNavBg,
                                    selectedLabelColor = RoyalBlue
                                ),
                                modifier = Modifier.testTag("switch_child_${child.userId}")
                            )
                        }
                    }
                }
            }
        }

        // Student Card Header
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
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(ActiveNavBg)
                        .border(2.5.dp, RoyalBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (!student.profilePhotoUri.isNullOrBlank()) {
                        Image(
                            painter = rememberAsyncImagePainter(student.profilePhotoUri),
                            contentDescription = student.fullName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = RoyalBlue,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }

                Text(
                    text = student.fullName.uppercase(),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextNavy
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = SecondaryBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                    ) {
                        Text(
                            text = "ID: ${student.userId}",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                            color = TextNavy,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = StatusSuccess.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccess.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = "🥋 ${student.currentBelt}",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = StatusSuccess,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }

                Button(
                    onClick = { showPersonalEditDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("edit_student_profile_button")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit Profile & Photo", color = TextOnAccent)
                }
            }
        }

        // Personal Information
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
                    Text(
                        "PERSONAL DETAILS",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextNavy
                    )
                    IconButton(
                        onClick = { showPersonalEditDialog = true },
                        modifier = Modifier.testTag("edit_personal_info_button")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Personal Info", tint = RoyalBlue)
                    }
                }

                ProfileDetailRow(label = "Username", value = student.username)
                ProfileDetailRow(label = "Date of Birth", value = student.dob)
                ProfileDetailRow(label = "Gender", value = student.gender)
                ProfileDetailRow(label = "Blood Group", value = student.bloodGroup)
                ProfileDetailRow(label = "Phone", value = student.phone.ifBlank { "Not specified" })
                ProfileDetailRow(label = "Email", value = student.email)
                ProfileDetailRow(label = "Address", value = student.address.ifBlank { "Main Dojo Quarter" })
                ProfileDetailRow(label = "Emergency Contact", value = student.emergencyContact.ifBlank { "+91 98765 33333" })
                ProfileDetailRow(label = "Joining Date", value = student.joiningDate)
            }
        }

        // Academy Details (Protected Fields)
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
                    Text(
                        "ACADEMY RECORD",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextNavy
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = { showClassEditDialog = true },
                            modifier = Modifier.testTag("edit_my_class_button")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit My Class", tint = RoyalBlue)
                        }
                        TextButton(
                            onClick = { showOfficialRequestDialog = true },
                            modifier = Modifier.testTag("request_change_button")
                        ) {
                            Text("Request Change 🔒", color = RoyalBlue, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }

                ProfileDetailRow(label = "Current Belt", value = student.currentBelt)
                ProfileDetailRow(label = "Belt Level", value = "Level ${student.beltLevel}")
                ProfileDetailRow(label = "Assigned Batch", value = student.batchName)
                ProfileDetailRow(label = "Assigned Coach", value = student.coachName)
                ProfileDetailRow(label = "Training Programs", value = student.trainingPrograms)
            }
        }

        // Parent Information Section
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
                Text(
                    "PARENT / GUARDIAN",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextNavy
                )

                ProfileDetailRow(label = "Father / Guardian Name", value = student.fatherName.ifBlank { "Ch. Ramesh" })
                ProfileDetailRow(label = "Mother / Guardian Name", value = student.motherName.ifBlank { "Ch. Sunita" })
                ProfileDetailRow(label = "Parent Email", value = student.parentEmail ?: student.email)
                ProfileDetailRow(label = "Parent Phone", value = student.phone.ifBlank { "+91 98765 22222" })
                ProfileDetailRow(label = "Occupation", value = student.occupation.ifBlank { "Software Professional" })
            }
        }

        // Pending Change Requests List
        if (requestsList.isNotEmpty()) {
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
                        "MY CHANGE REQUESTS",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextNavy
                    )
                    requestsList.forEach { req ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = SecondaryBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("${req.requestType}: ${req.requestedValue}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                    Text("Reason: ${req.reason}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = when (req.status) {
                                        "APPROVED" -> StatusSuccess.copy(alpha = 0.12f)
                                        "REJECTED" -> StatusError.copy(alpha = 0.12f)
                                        else -> StatusWarning.copy(alpha = 0.12f)
                                    },
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        when (req.status) {
                                            "APPROVED" -> StatusSuccess.copy(alpha = 0.35f)
                                            "REJECTED" -> StatusError.copy(alpha = 0.35f)
                                            else -> StatusWarning.copy(alpha = 0.35f)
                                        }
                                    )
                                ) {
                                    Text(
                                        text = req.status,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = when (req.status) {
                                            "APPROVED" -> StatusSuccess
                                            "REJECTED" -> StatusError
                                            else -> StatusWarning
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Personal Details Dialog
    if (showPersonalEditDialog) {
        var phoneVal by remember { mutableStateOf(student.phone) }
        var emailVal by remember { mutableStateOf(student.email) }
        var addressVal by remember { mutableStateOf(student.address) }
        var emergencyVal by remember { mutableStateOf(student.emergencyContact) }
        var photoUriVal by remember { mutableStateOf(student.profilePhotoUri) }

        AlertDialog(
            onDismissRequest = { showPersonalEditDialog = false },
            title = {
                Text(
                    "Edit Profile & Personal Info",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextNavy
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PhotoPickerSelector(
                        selectedImageUri = photoUriVal,
                        onImageSelected = { photoUriVal = it },
                        label = "STUDENT PROFILE PHOTO",
                        categoryHint = "Student Profile"
                    )

                    OutlinedTextField(
                        value = phoneVal,
                        onValueChange = { phoneVal = it },
                        label = { Text("Phone Number", color = TextSlate) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        )
                    )
                    OutlinedTextField(
                        value = emailVal,
                        onValueChange = { emailVal = it },
                        label = { Text("Email Address", color = TextSlate) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        )
                    )
                    OutlinedTextField(
                        value = addressVal,
                        onValueChange = { addressVal = it },
                        label = { Text("Residential Address", color = TextSlate) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        )
                    )
                    OutlinedTextField(
                        value = emergencyVal,
                        onValueChange = { emergencyVal = it },
                        label = { Text("Emergency Contact", color = TextSlate) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
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
                        if (onSaveStudentProfile != null) {
                            val updated = student.copy(
                                phone = phoneVal,
                                email = emailVal,
                                address = addressVal,
                                emergencyContact = emergencyVal,
                                profilePhotoUri = photoUriVal
                            )
                            onSaveStudentProfile(updated)
                        } else {
                            onSavePersonalDetails(phoneVal, emailVal, addressVal, emergencyVal)
                        }
                        showPersonalEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("save_personal_info_dialog_button")
                ) {
                    Text("Save Changes", color = TextOnAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPersonalEditDialog = false }) {
                    Text("Cancel", color = TextSlate)
                }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Submit Official Change Request Dialog
    if (showOfficialRequestDialog) {
        var reqType by remember { mutableStateOf("Belt Update") }
        var targetVal by remember { mutableStateOf("") }
        var reasonVal by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showOfficialRequestDialog = false },
            title = {
                Text(
                    "Submit Record Change Request",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextNavy
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Official academy records (Belt, Batch, Coach) require Coach/Admin approval.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSlate
                    )

                    OutlinedTextField(
                        value = reqType,
                        onValueChange = { reqType = it },
                        label = { Text("Request Type (e.g. Belt Update, Batch Transfer)", color = TextSlate) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        )
                    )
                    OutlinedTextField(
                        value = targetVal,
                        onValueChange = { targetVal = it },
                        label = { Text("Requested Value (e.g. Blue Belt)", color = TextSlate) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        )
                    )
                    OutlinedTextField(
                        value = reasonVal,
                        onValueChange = { reasonVal = it },
                        label = { Text("Reason (e.g. Passed grading examination)", color = TextSlate) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
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
                        if (targetVal.isNotBlank() && reasonVal.isNotBlank()) {
                            onRequestChangeSubmit(reqType, student.currentBelt, targetVal, reasonVal)
                            showOfficialRequestDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("submit_request_dialog_button")
                ) {
                    Text("Submit Request", color = TextOnAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showOfficialRequestDialog = false }) {
                    Text("Cancel", color = TextSlate)
                }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Edit My Class & Special Intense Training Dialog
    if (showClassEditDialog) {
        var batchVal by remember { mutableStateOf(student.batchName.ifBlank { "Batch 2 (6:00 PM – 8:00 PM)" }) }
        var isWeaponChecked by remember { mutableStateOf(student.trainingPrograms.contains("Weapon", ignoreCase = true)) }
        var isFitnessChecked by remember { mutableStateOf(student.trainingPrograms.contains("Fitness", ignoreCase = true) || student.trainingPrograms.contains("Self Defence", ignoreCase = true)) }
        var isRegularChecked by remember { mutableStateOf(student.trainingPrograms.contains("Regular", ignoreCase = true) || student.trainingPrograms.isBlank()) }
        var isSpecialBootCampChecked by remember { mutableStateOf(student.trainingPrograms.contains("Boot Camp", ignoreCase = true) || student.trainingPrograms.contains("Special", ignoreCase = true)) }

        AlertDialog(
            onDismissRequest = { showClassEditDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.School, contentDescription = null, tint = RoyalBlue)
                    Text(
                        "Edit My Class & Special Training",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextNavy
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Select your enrolled class batch and special intense training programs:", style = MaterialTheme.typography.bodySmall, color = TextSlate)

                    OutlinedTextField(
                        value = batchVal,
                        onValueChange = { batchVal = it },
                        label = { Text("My Class / Batch Name", color = TextSlate) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        )
                    )

                    Text(
                        "SPECIAL INTENSIVE TRAINING & COURSES",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = RoyalBlue
                    )

                    // Weapon Training Checkbox
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isWeaponChecked = !isWeaponChecked },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = isWeaponChecked,
                            onCheckedChange = { isWeaponChecked = it },
                            colors = CheckboxDefaults.colors(checkedColor = RoyalBlue)
                        )
                        Column {
                            Text("Weapon Training (Bo, Nunchaku, Sai)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                            Text("Included in Regular Training (₹2,000/mo)", style = MaterialTheme.typography.bodySmall, color = StatusSuccess)
                        }
                    }

                    // Fitness & Self Defence Checkbox
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isFitnessChecked = !isFitnessChecked },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = isFitnessChecked,
                            onCheckedChange = { isFitnessChecked = it },
                            colors = CheckboxDefaults.colors(checkedColor = RoyalBlue)
                        )
                        Column {
                            Text("Fitness & Self Defence", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                            Text("Included in Regular Training (₹2,000/mo)", style = MaterialTheme.typography.bodySmall, color = StatusSuccess)
                        }
                    }

                    // Regular Karate Training Checkbox
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isRegularChecked = !isRegularChecked },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = isRegularChecked,
                            onCheckedChange = { isRegularChecked = it },
                            colors = CheckboxDefaults.colors(checkedColor = RoyalBlue)
                        )
                        Column {
                            Text("Regular Training (Karate, Fitness, Self Defence & Weapons)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                            Text("Standard Fee: ₹2,000 / month (All Disciplines Included)", style = MaterialTheme.typography.bodySmall, color = StatusSuccess)
                        }
                    }

                    // Special Boot Camp Checkbox
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isSpecialBootCampChecked = !isSpecialBootCampChecked },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = isSpecialBootCampChecked,
                            onCheckedChange = { isSpecialBootCampChecked = it },
                            colors = CheckboxDefaults.colors(checkedColor = RoyalBlue)
                        )
                        Column {
                            Text("Special Boot Camp / Masterclass", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                            Text("1-Day (₹5,000) / 3-Days (₹10,000)", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val selectedProgs = mutableListOf<String>()
                        if (isRegularChecked) selectedProgs.add("Regular Karate Training")
                        if (isWeaponChecked) selectedProgs.add("Weapon Training")
                        if (isFitnessChecked) selectedProgs.add("Fitness & Self Defence")
                        if (isSpecialBootCampChecked) selectedProgs.add("Special Training Boot Camp")

                        val progStr = if (selectedProgs.isEmpty()) "Regular Karate Training" else selectedProgs.joinToString(", ")
                        onUpdateClassAndTraining(batchVal, progStr)
                        showClassEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("save_edited_class_button")
                ) {
                    Text("Save My Class & Programs", color = TextOnAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClassEditDialog = false }) {
                    Text("Cancel", color = TextSlate)
                }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextSlate)
        Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = TextNavy)
    }
}
