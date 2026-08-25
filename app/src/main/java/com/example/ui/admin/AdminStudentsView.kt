package com.example.ui.admin

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
import com.example.data.BatchEntity
import com.example.data.UserAccountEntity
import com.example.data.UserRole
import com.example.ui.common.PhotoPickerSelector
import com.example.ui.theme.*

@Composable
fun AdminStudentsView(
    allUsersList: List<UserAccountEntity> = emptyList(),
    studentsList: List<UserAccountEntity>,
    batchesList: List<BatchEntity>,
    onSaveStudent: (UserAccountEntity) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingStudent by remember { mutableStateOf<UserAccountEntity?>(null) }
    var viewingIdCardStudent by remember { mutableStateOf<UserAccountEntity?>(null) }

    var searchQuery by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf("ALL") } // ALL, WITH_ID, OFFLINE

    var addMode by remember { mutableStateOf(0) } // 0: App Account Link, 1: Offline / Direct Entry
    var selectedAppUser by remember { mutableStateOf<UserAccountEntity?>(null) }
    var customUserIdVal by remember { mutableStateOf("") }
    var nameVal by remember { mutableStateOf("") }
    var usernameVal by remember { mutableStateOf("") }
    var emailVal by remember { mutableStateOf("") }
    var phoneVal by remember { mutableStateOf("") }
    var parentEmailVal by remember { mutableStateOf("") }
    var batchVal by remember { mutableStateOf("Morning Champions") }
    var dojoVal by remember { mutableStateOf("Main Dojang - Central Branch") }
    var coachVal by remember { mutableStateOf("Sensei Rajesh Sharma") }
    var beltVal by remember { mutableStateOf("Yellow Belt (8th Kyu)") }
    var photoUriVal by remember { mutableStateOf<String?>(null) }
    var trainingProgVal by remember { mutableStateOf("Regular Karate Training") }
    var dobVal by remember { mutableStateOf("2012-05-15") }
    var genderVal by remember { mutableStateOf("Male") }
    var emergencyVal by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    val filteredStudents = remember(studentsList, searchQuery, filterType) {
        studentsList.filter { st ->
            val matchesSearch = searchQuery.isBlank() ||
                    st.fullName.contains(searchQuery, ignoreCase = true) ||
                    st.userId.contains(searchQuery, ignoreCase = true) ||
                    st.username.contains(searchQuery, ignoreCase = true) ||
                    st.currentBelt.contains(searchQuery, ignoreCase = true) ||
                    st.batchName.contains(searchQuery, ignoreCase = true) ||
                    st.dojoCenter.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (filterType) {
                "OFFLINE" -> st.userId.contains("OFFLINE", ignoreCase = true) || st.username.startsWith("offline_")
                "WITH_ID" -> st.userId.isNotBlank() && !st.userId.contains("OFFLINE", ignoreCase = true)
                else -> true
            }

            matchesSearch && matchesFilter
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("STUDENT DIRECTORY & ID CARDS", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                Text("Manage student records, customize Student IDs, view official ID cards & assign batches", style = MaterialTheme.typography.bodySmall, color = TextSlate)
            }

            Button(
                onClick = {
                    selectedAppUser = null
                    addMode = 0
                    customUserIdVal = "BROMA-" + (1000..9999).random()
                    nameVal = ""
                    usernameVal = ""
                    emailVal = ""
                    phoneVal = ""
                    parentEmailVal = ""
                    photoUriVal = null
                    trainingProgVal = "Regular Karate Training"
                    batchVal = batchesList.firstOrNull()?.batchName ?: "Morning Champions"
                    dojoVal = batchesList.firstOrNull()?.location ?: "Main Dojang - Central Branch"
                    coachVal = batchesList.firstOrNull()?.coachName ?: "Sensei Rajesh Sharma"
                    beltVal = "Yellow Belt (8th Kyu)"
                    showAddDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("admin_add_student_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Student", color = TextOnAccent)
            }
        }

        // Search Bar & Filter Chips
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = CardWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by Student ID (e.g. BROMA-0001), Name, Belt, Batch...", fontSize = 12.sp, color = TextSlate) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = RoyalBlue) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSlate)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoyalBlue,
                        unfocusedBorderColor = BorderLight
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "ALL" to "All (${studentsList.size})",
                        "WITH_ID" to "With Student ID (${studentsList.count { !it.userId.contains("OFFLINE") }})",
                        "OFFLINE" to "Offline (${studentsList.count { it.userId.contains("OFFLINE") || it.username.startsWith("offline_") }})"
                    ).forEach { (fKey, fLabel) ->
                        val isSelected = filterType == fKey
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) RoyalBlue else SecondaryBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) RoyalBlue else BorderLight),
                            modifier = Modifier.clickable { filterType = fKey }
                        ) {
                            Text(
                                text = fLabel,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) TextOnAccent else TextNavy,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }

        if (filteredStudents.isEmpty()) {
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
                    Icon(Icons.Default.SearchOff, contentDescription = null, tint = TextSlate, modifier = Modifier.size(36.dp))
                    Text("No students found matching '$searchQuery'", fontWeight = FontWeight.Bold, color = TextNavy)
                }
            }
        }

        filteredStudents.forEach { student ->
            val isOffline = student.userId.contains("OFFLINE") || student.username.startsWith("offline_")

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = CardWhite,
                shadowElevation = 1.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(ActiveNavBg)
                                    .border(1.5.dp, RoyalBlue, CircleShape),
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
                                    Icon(Icons.Default.Person, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(26.dp))
                                }
                            }

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(student.fullName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                    if (isOffline) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = StatusWarning.copy(alpha = 0.15f),
                                            border = androidx.compose.foundation.BorderStroke(0.5.dp, StatusWarning.copy(alpha = 0.5f))
                                        ) {
                                            Text("OFFLINE STUDENT", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp), color = StatusWarning, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                        }
                                    }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = ActiveNavBg,
                                        border = androidx.compose.foundation.BorderStroke(0.5.dp, RoyalBlue.copy(alpha = 0.5f)),
                                        modifier = Modifier.clickable { viewingIdCardStudent = student }
                                    ) {
                                        Text("🆔 ID: ${student.userId}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                    Text("• ${student.currentBelt}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ActiveNavBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue.copy(alpha = 0.35f))
                        ) {
                            Text(student.currentBelt, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                        }
                    }

                    HorizontalDivider(color = BorderLight)

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("✉️ Email: ${student.email}", style = MaterialTheme.typography.bodySmall, color = TextNavy)
                        Text("👤 Username: @${student.username} ${if (student.phone.isNotBlank()) "• 📱 ${student.phone}" else ""}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                        if (!student.parentEmail.isNullOrBlank()) {
                            Text("👨‍👩‍👧 Parent Email: ${student.parentEmail}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                        }
                        if (student.trainingPrograms.isNotBlank()) {
                            Text("🥋 Program: ${student.trainingPrograms}", style = MaterialTheme.typography.bodySmall, color = RoyalBlue)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("📍 Centre: ${student.dojoCenter}", style = MaterialTheme.typography.bodySmall, color = TextNavy)
                            Text("⏰ Batch: ${student.batchId} (${student.batchName})", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                            Text("🥋 Coach: ${student.coachName}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = { viewingIdCardStudent = student },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("view_student_id_card_${student.userId}")
                            ) {
                                Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.size(14.dp), tint = RoyalBlue)
                                Spacer(Modifier.width(4.dp))
                                Text("ID Card", color = RoyalBlue, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }

                            Button(
                                onClick = { editingStudent = student },
                                colors = ButtonDefaults.buttonColors(containerColor = SecondaryBg),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("edit_student_batch_${student.userId}")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = TextNavy)
                                Spacer(Modifier.width(4.dp))
                                Text("Edit", color = TextNavy, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal: Official BROMAA Student ID Card
    if (viewingIdCardStudent != null) {
        val st = viewingIdCardStudent!!
        AlertDialog(
            onDismissRequest = { viewingIdCardStudent = null },
            title = null,
            text = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = CardWhite,
                    border = androidx.compose.foundation.BorderStroke(2.dp, RoyalBlue)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Card Header
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = DeepNavy
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "BRUCELEE RAJ OLYMPIC MARTIALARTS ACADEMY",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Text(
                                    "OFFICIAL STUDENT IDENTITY CARD",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CrimsonPrimary,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        // Photo & Core Identity
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(ActiveNavBg)
                                .border(2.dp, RoyalBlue, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!st.profilePhotoUri.isNullOrBlank()) {
                                Image(
                                    painter = rememberAsyncImagePainter(st.profilePhotoUri),
                                    contentDescription = st.fullName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(Icons.Default.Person, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(48.dp))
                            }
                        }

                        Text(
                            text = st.fullName.uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextNavy
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = RoyalBlue
                        ) {
                            Text(
                                text = "STUDENT ID: ${st.userId}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }

                        HorizontalDivider(color = BorderLight)

                        // Key Details Table
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Belt Rank:", fontSize = 11.sp, color = TextSlate, fontWeight = FontWeight.Bold)
                                Text(st.currentBelt, fontSize = 11.sp, color = CrimsonPrimary, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Batch & Timing:", fontSize = 11.sp, color = TextSlate, fontWeight = FontWeight.Bold)
                                Text(st.batchName, fontSize = 11.sp, color = TextNavy, fontWeight = FontWeight.SemiBold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Dojo Centre:", fontSize = 11.sp, color = TextSlate, fontWeight = FontWeight.Bold)
                                Text(st.dojoCenter, fontSize = 11.sp, color = TextNavy, fontWeight = FontWeight.SemiBold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Assigned Sensei:", fontSize = 11.sp, color = TextSlate, fontWeight = FontWeight.Bold)
                                Text(st.coachName, fontSize = 11.sp, color = TextNavy, fontWeight = FontWeight.SemiBold)
                            }
                            if (st.phone.isNotBlank()) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Emergency Contact:", fontSize = 11.sp, color = TextSlate, fontWeight = FontWeight.Bold)
                                    Text(st.phone, fontSize = 11.sp, color = TextNavy, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        HorizontalDivider(color = BorderLight)

                        // Barcode / QR Simulation & Validity
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("VALID THRU: 2026-2027", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSlate)
                                Text("AFFILIATED TO MSKA INDIA", fontSize = 8.sp, color = TextSlate)
                            }
                            Icon(Icons.Default.QrCode2, contentDescription = null, tint = TextNavy, modifier = Modifier.size(36.dp))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewingIdCardStudent = null },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Close ID Card", color = TextOnAccent)
                }
            },
            containerColor = Color.Transparent,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Modal: Edit Student Details, Batch & Centre
    if (editingStudent != null) {
        val st = editingStudent!!
        var editUserId by remember { mutableStateOf(st.userId) }
        var editName by remember { mutableStateOf(st.fullName) }
        var editUsername by remember { mutableStateOf(st.username) }
        var editEmail by remember { mutableStateOf(st.email) }
        var editPhone by remember { mutableStateOf(st.phone) }
        var editParentEmail by remember { mutableStateOf(st.parentEmail ?: "") }
        var editBatch by remember { mutableStateOf(st.batchId) }
        var editBatchName by remember { mutableStateOf(st.batchName) }
        var editDojo by remember { mutableStateOf(st.dojoCenter) }
        var editCoach by remember { mutableStateOf(st.coachName) }
        var editBelt by remember { mutableStateOf(st.currentBelt) }
        var editBeltLevel by remember { mutableIntStateOf(st.beltLevel) }
        var editBeltHistory by remember { mutableStateOf(st.beltHistory) }
        var editPrograms by remember { mutableStateOf(st.trainingPrograms) }
        var editEmergency by remember { mutableStateOf(st.emergencyContact) }
        var editStatus by remember { mutableStateOf(st.status) }
        var editPhotoUri by remember { mutableStateOf(st.profilePhotoUri) }

        var newBeltEntryName by remember { mutableStateOf("Yellow Belt") }
        var newBeltEntryDate by remember { mutableStateOf(java.text.SimpleDateFormat("MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date())) }
        var newBeltEntryExaminer by remember { mutableStateOf("Shihan Brucelee Raj") }
        var showAddBeltMilestone by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { editingStudent = null },
            title = { Text("Edit Student Profile & Assignment", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = editUserId,
                            onValueChange = { editUserId = it },
                            label = { Text("Student ID (Registration No.) *", color = TextSlate) },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalBlue,
                                unfocusedBorderColor = BorderLight,
                                focusedTextColor = TextNavy,
                                unfocusedTextColor = TextNavy
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedButton(
                            onClick = { editUserId = "BROMA-" + (1000..9999).random() },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                        ) {
                            Text("⚡ Auto", fontSize = 11.sp, color = RoyalBlue, fontWeight = FontWeight.Bold)
                        }
                    }

                    PhotoPickerSelector(
                        selectedImageUri = editPhotoUri,
                        onImageSelected = { editPhotoUri = it },
                        label = "STUDENT PROFILE PHOTO",
                        categoryHint = "Student Photo"
                    )

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name *", color = TextSlate) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editUsername,
                        onValueChange = { editUsername = it },
                        label = { Text("Username", color = TextSlate) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("Email Address", color = TextSlate) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Phone Number", color = TextSlate) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editParentEmail,
                        onValueChange = { editParentEmail = it },
                        label = { Text("Parent Email", color = TextSlate) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    HorizontalDivider(color = BorderLight)
                    Text("BATCH & TIMETABLE PRESETS:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue)
                    if (batchesList.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            batchesList.forEach { b ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (editBatch == b.batchId || editBatch == b.batchName) ActiveNavBg else SecondaryBg,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (editBatch == b.batchId || editBatch == b.batchName) RoyalBlue else BorderLight),
                                    onClick = {
                                        editBatch = b.batchId
                                        editBatchName = b.batchName
                                        editDojo = b.location
                                        editCoach = b.coachName
                                    }
                                ) {
                                    Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("${b.batchName} (${b.scheduleTiming})", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                        Text(b.location, style = MaterialTheme.typography.labelSmall, color = RoyalBlue)
                                    }
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = editBatch,
                        onValueChange = { editBatch = it },
                        label = { Text("Batch ID / Schedule Name", color = TextSlate) },
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
                        value = editDojo,
                        onValueChange = { editDojo = it },
                        label = { Text("Dojo Training Centre Location", color = TextSlate) },
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
                        value = editCoach,
                        onValueChange = { editCoach = it },
                        label = { Text("Assigned Sensei / Coach", color = TextSlate) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    HorizontalDivider(color = BorderLight)
                    Text("🥋 BELT GRADE & PROMOTION HISTORY", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = editBelt,
                            onValueChange = { editBelt = it },
                            label = { Text("Current Belt", color = TextSlate) },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalBlue,
                                unfocusedBorderColor = BorderLight,
                                focusedTextColor = TextNavy,
                                unfocusedTextColor = TextNavy
                            ),
                            modifier = Modifier.weight(1.4f)
                        )

                        OutlinedTextField(
                            value = editBeltLevel.toString(),
                            onValueChange = { editBeltLevel = it.toIntOrNull() ?: editBeltLevel },
                            label = { Text("Level (1-10)", color = TextSlate) },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalBlue,
                                unfocusedBorderColor = BorderLight,
                                focusedTextColor = TextNavy,
                                unfocusedTextColor = TextNavy
                            ),
                            modifier = Modifier.weight(0.8f)
                        )
                    }

                    // Existing Belt History Milestones List
                    val historyList = editBeltHistory.split("|").filter { it.isNotBlank() }
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = SecondaryBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Belt Exam Milestones (${historyList.size})", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                TextButton(
                                    onClick = { showAddBeltMilestone = !showAddBeltMilestone },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = RoyalBlue)
                                    Spacer(Modifier.width(2.dp))
                                    Text("Add Milestone", fontSize = 11.sp, color = RoyalBlue, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (showAddBeltMilestone) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(6.dp),
                                    color = CardWhite,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue.copy(alpha = 0.5f))
                                ) {
                                    Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        OutlinedTextField(
                                            value = newBeltEntryName,
                                            onValueChange = { newBeltEntryName = it },
                                            label = { Text("Belt Color / Rank", fontSize = 10.sp) },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            OutlinedTextField(
                                                value = newBeltEntryDate,
                                                onValueChange = { newBeltEntryDate = it },
                                                label = { Text("Date (e.g. Oct 2025)", fontSize = 10.sp) },
                                                modifier = Modifier.weight(1f)
                                            )
                                            OutlinedTextField(
                                                value = newBeltEntryExaminer,
                                                onValueChange = { newBeltEntryExaminer = it },
                                                label = { Text("Examiner / Dojo", fontSize = 10.sp) },
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                        Button(
                                            onClick = {
                                                if (newBeltEntryName.isNotBlank()) {
                                                    val entry = "$newBeltEntryName ($newBeltEntryDate - $newBeltEntryExaminer)"
                                                    editBeltHistory = if (editBeltHistory.isBlank()) entry else "$editBeltHistory|$entry"
                                                    showAddBeltMilestone = false
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Save Belt Milestone", fontSize = 11.sp, color = TextOnAccent)
                                        }
                                    }
                                }
                            }

                            if (historyList.isEmpty()) {
                                Text("No belt milestones recorded yet. Add from above.", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                            } else {
                                historyList.forEachIndexed { idx, item ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("🥋 $item", style = MaterialTheme.typography.bodySmall, color = TextNavy, modifier = Modifier.weight(1f))
                                        IconButton(
                                            onClick = {
                                                val remaining = historyList.toMutableList().apply { removeAt(idx) }
                                                editBeltHistory = remaining.joinToString("|")
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = "Delete", tint = StatusError, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = editBeltHistory,
                        onValueChange = { editBeltHistory = it },
                        label = { Text("Raw Belt History (Separated by |)", color = TextSlate) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    OutlinedTextField(
                        value = editPrograms,
                        onValueChange = { editPrograms = it },
                        label = { Text("Training Programs & Status", color = TextSlate) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = st.copy(
                            userId = editUserId.ifBlank { st.userId },
                            fullName = editName,
                            username = editUsername,
                            email = editEmail,
                            phone = editPhone,
                            parentEmail = editParentEmail.ifBlank { null },
                            batchId = editBatch,
                            batchName = editBatchName.ifBlank { editBatch },
                            dojoCenter = editDojo,
                            coachName = editCoach,
                            currentBelt = editBelt,
                            beltLevel = editBeltLevel,
                            beltHistory = editBeltHistory,
                            trainingPrograms = editPrograms,
                            emergencyContact = editEmergency,
                            status = editStatus,
                            profilePhotoUri = editPhotoUri
                        )
                        onSaveStudent(updated)
                        editingStudent = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("save_student_changes_button")
                ) { Text("Save Changes", color = TextOnAccent) }
            },
            dismissButton = {
                TextButton(onClick = { editingStudent = null }) { Text("Cancel", color = TextSlate) }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Modal: Add New Student (With 2 explicit modes: Registered App User vs Offline Student)
    if (showAddDialog) {
        val matchingAccount = remember(usernameVal, emailVal, allUsersList) {
            if (usernameVal.length >= 2 || emailVal.length >= 3) {
                allUsersList.firstOrNull {
                    (emailVal.isNotBlank() && it.email.equals(emailVal.trim(), ignoreCase = true)) ||
                    (usernameVal.isNotBlank() && it.username.equals(usernameVal.trim(), ignoreCase = true))
                }
            } else null
        }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Student to Academy", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Option Selector: 1. Link Registered App Account, 2. Manual Offline Entry
                    TabRow(
                        selectedTabIndex = addMode,
                        containerColor = SecondaryBg,
                        contentColor = RoyalBlue
                    ) {
                        Tab(
                            selected = addMode == 0,
                            onClick = { addMode = 0 },
                            text = { Text("📱 Registered App User", fontSize = 12.sp, fontWeight = if (addMode == 0) FontWeight.Bold else FontWeight.Normal) }
                        )
                        Tab(
                            selected = addMode == 1,
                            onClick = {
                                addMode = 1
                                selectedAppUser = null
                            },
                            text = { Text("📝 Offline Student Entry", fontSize = 12.sp, fontWeight = if (addMode == 1) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }

                    if (addMode == 0) {
                        // MODE 1: Quick import from registered app accounts
                        if (allUsersList.isNotEmpty()) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                color = SecondaryBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("⚡ REGISTERED APP ACCOUNTS", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = RoyalBlue)
                                    Text("Select an app account to auto-fill email, username & info:", style = MaterialTheme.typography.bodySmall, color = TextSlate)

                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        allUsersList.take(6).forEach { user ->
                                            Surface(
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (selectedAppUser?.userId == user.userId) ActiveNavBg else CardWhite,
                                                border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedAppUser?.userId == user.userId) RoyalBlue else BorderLight),
                                                onClick = {
                                                    selectedAppUser = user
                                                    nameVal = user.fullName
                                                    usernameVal = user.username
                                                    emailVal = user.email
                                                    phoneVal = user.phone
                                                    parentEmailVal = user.parentEmail ?: ""
                                                    photoUriVal = user.profilePhotoUri
                                                    if (user.dojoCenter.isNotBlank()) dojoVal = user.dojoCenter
                                                    if (user.currentBelt.isNotBlank()) beltVal = user.currentBelt
                                                }
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(8.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(user.fullName, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                                        Text("@${user.username} • ${user.email}", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                                                    }
                                                    Text("Tap to Select", style = MaterialTheme.typography.labelSmall, color = RoyalBlue)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (selectedAppUser != null) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                color = StatusSuccess.copy(alpha = 0.12f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccess.copy(alpha = 0.35f))
                            ) {
                                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Linked App Account: ${selectedAppUser!!.fullName} (@${selectedAppUser!!.username})", style = MaterialTheme.typography.bodySmall, color = StatusSuccess)
                                }
                            }
                        } else if (matchingAccount != null) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                color = ActiveNavBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue.copy(alpha = 0.35f)),
                                onClick = {
                                    selectedAppUser = matchingAccount
                                    nameVal = matchingAccount.fullName
                                    usernameVal = matchingAccount.username
                                    emailVal = matchingAccount.email
                                    phoneVal = matchingAccount.phone
                                    parentEmailVal = matchingAccount.parentEmail ?: ""
                                    photoUriVal = matchingAccount.profilePhotoUri
                                }
                            ) {
                                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Found App User: ${matchingAccount.fullName} (${matchingAccount.email}). Tap to auto-fill!", style = MaterialTheme.typography.bodySmall, color = RoyalBlue)
                                }
                            }
                        }

                        Text("STUDENT ACCOUNT INFORMATION", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = customUserIdVal,
                                onValueChange = { customUserIdVal = it },
                                label = { Text("Student ID (Permanent Registration No.)", color = TextSlate) },
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = RoyalBlue,
                                    unfocusedBorderColor = BorderLight,
                                    focusedTextColor = TextNavy,
                                    unfocusedTextColor = TextNavy
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedButton(
                                onClick = { customUserIdVal = "BROMA-" + (1000..9999).random() },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                            ) {
                                Text("⚡ Auto", fontSize = 11.sp, color = RoyalBlue, fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedTextField(
                            value = nameVal,
                            onValueChange = { nameVal = it },
                            label = { Text("Full Name *", color = TextSlate) },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalBlue,
                                unfocusedBorderColor = BorderLight,
                                focusedTextColor = TextNavy,
                                unfocusedTextColor = TextNavy
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = usernameVal,
                            onValueChange = { usernameVal = it },
                            label = { Text("App Username *", color = TextSlate) },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalBlue,
                                unfocusedBorderColor = BorderLight,
                                focusedTextColor = TextNavy,
                                unfocusedTextColor = TextNavy
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = emailVal,
                            onValueChange = { emailVal = it },
                            label = { Text("Email Address *", color = TextSlate) },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalBlue,
                                unfocusedBorderColor = BorderLight,
                                focusedTextColor = TextNavy,
                                unfocusedTextColor = TextNavy
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = phoneVal,
                            onValueChange = { phoneVal = it },
                            label = { Text("Phone Number", color = TextSlate) },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalBlue,
                                unfocusedBorderColor = BorderLight,
                                focusedTextColor = TextNavy,
                                unfocusedTextColor = TextNavy
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = parentEmailVal,
                            onValueChange = { parentEmailVal = it },
                            label = { Text("Parent / Guardian Email (If Minor)", color = TextSlate) },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalBlue,
                                unfocusedBorderColor = BorderLight,
                                focusedTextColor = TextNavy,
                                unfocusedTextColor = TextNavy
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        // MODE 2: DIRECT / OFFLINE STUDENT ENTRY (No Phone / App Account needed)
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = ActiveNavBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue.copy(alpha = 0.3f))
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "For offline students without a smartphone or app account. Enter their basic details below to register them in the academy student list.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextNavy
                                )
                            }
                        }

                        PhotoPickerSelector(
                            selectedImageUri = photoUriVal,
                            onImageSelected = { photoUriVal = it },
                            label = "STUDENT PROFILE PHOTO (OPTIONAL)",
                            categoryHint = "Student Photo"
                        )

                        Text("OFFLINE STUDENT BASIC DETAILS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue)
                        OutlinedTextField(
                            value = nameVal,
                            onValueChange = { nameVal = it },
                            label = { Text("Student Full Name *", color = TextSlate) },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalBlue,
                                unfocusedBorderColor = BorderLight,
                                focusedTextColor = TextNavy,
                                unfocusedTextColor = TextNavy
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = phoneVal,
                            onValueChange = { phoneVal = it },
                            label = { Text("Parent / Student Contact Phone", color = TextSlate) },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalBlue,
                                unfocusedBorderColor = BorderLight,
                                focusedTextColor = TextNavy,
                                unfocusedTextColor = TextNavy
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = emergencyVal,
                            onValueChange = { emergencyVal = it },
                            label = { Text("Emergency Contact / Guardian Name", color = TextSlate) },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalBlue,
                                unfocusedBorderColor = BorderLight,
                                focusedTextColor = TextNavy,
                                unfocusedTextColor = TextNavy
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = dobVal,
                                onValueChange = { dobVal = it },
                                label = { Text("Date of Birth", color = TextSlate) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = genderVal,
                                onValueChange = { genderVal = it },
                                label = { Text("Gender", color = TextSlate) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    HorizontalDivider(color = BorderLight)
                    Text("BATCH & ACADEMY ASSIGNMENT", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue)

                    if (batchesList.isNotEmpty()) {
                        Text("SELECT BATCH / TIMETABLE:", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            batchesList.forEach { b ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (batchVal == b.batchName || batchVal == b.batchId) ActiveNavBg else SecondaryBg,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (batchVal == b.batchName || batchVal == b.batchId) RoyalBlue else BorderLight),
                                    onClick = {
                                        batchVal = b.batchName
                                        dojoVal = b.location
                                        coachVal = b.coachName
                                    }
                                ) {
                                    Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("${b.batchName} (${b.scheduleTiming})", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                        Text(b.location, style = MaterialTheme.typography.labelSmall, color = RoyalBlue)
                                    }
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = batchVal,
                        onValueChange = { batchVal = it },
                        label = { Text("Batch Name / Timetable", color = TextSlate) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = dojoVal,
                        onValueChange = { dojoVal = it },
                        label = { Text("Dojo Centre Location", color = TextSlate) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = coachVal,
                        onValueChange = { coachVal = it },
                        label = { Text("Assigned Coach", color = TextSlate) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = beltVal,
                        onValueChange = { beltVal = it },
                        label = { Text("Belt Grade", color = TextSlate) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nameVal.isNotBlank()) {
                            val isManualOffline = addMode == 1
                            val targetUser = if (!isManualOffline) (selectedAppUser ?: matchingAccount) else null
                            val finalUserId = customUserIdVal.ifBlank {
                                if (isManualOffline) "BROMA-OFFLINE-" + (1000..9999).random() else (targetUser?.userId ?: ("BROMA-" + (1000..9999).random()))
                            }
                            val cleanName = nameVal.trim()
                            val generatedUsername = if (isManualOffline) "offline_${cleanName.lowercase().replace(" ", "_")}" else (usernameVal.ifBlank { emailVal.substringBefore("@") })
                            val generatedEmail = if (isManualOffline) "offline_${cleanName.lowercase().replace(" ", "_")}@broma.local" else (emailVal.ifBlank { "$usernameVal@broma.com" })

                            val newStudent = (targetUser ?: UserAccountEntity(
                                userId = finalUserId,
                                email = generatedEmail,
                                username = generatedUsername,
                                password = "offline123",
                                role = UserRole.STUDENT,
                                fullName = cleanName
                            )).copy(
                                userId = finalUserId,
                                fullName = cleanName,
                                username = generatedUsername,
                                email = generatedEmail,
                                phone = phoneVal,
                                parentEmail = if (isManualOffline) null else (parentEmailVal.ifBlank { emailVal }),
                                role = UserRole.STUDENT,
                                batchId = batchVal,
                                batchName = batchVal,
                                dojoCenter = dojoVal,
                                coachName = coachVal,
                                currentBelt = beltVal,
                                trainingPrograms = trainingProgVal,
                                dob = dobVal,
                                gender = genderVal,
                                emergencyContact = emergencyVal,
                                profilePhotoUri = photoUriVal
                            )
                            onSaveStudent(newStudent)
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("submit_new_student_button")
                ) { Text(if (addMode == 1) "Register Offline Student" else "Save Student", color = TextOnAccent) }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel", color = TextSlate) }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
