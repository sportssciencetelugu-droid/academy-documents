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
import com.example.data.UserAccountEntity
import com.example.data.UserRole
import com.example.ui.common.PhotoPickerSelector
import com.example.ui.theme.*

@Composable
fun AdminCoachesView(
    allUsersList: List<UserAccountEntity> = emptyList(),
    coachesList: List<UserAccountEntity>,
    onSaveCoach: (UserAccountEntity) -> Unit,
    onApproveCoach: (String) -> Unit = {},
    onDeleteCoach: (String) -> Unit = {}
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCoach by remember { mutableStateOf<UserAccountEntity?>(null) }
    var coachToDelete by remember { mutableStateOf<UserAccountEntity?>(null) }
    var activeCoachTab by remember { mutableStateOf(0) } // 0: All, 1: Pending Applications

    var addMode by remember { mutableStateOf(0) } // 0: App Account, 1: Offline / Direct Coach
    var selectedAppUser by remember { mutableStateOf<UserAccountEntity?>(null) }
    var nameVal by remember { mutableStateOf("") }
    var usernameVal by remember { mutableStateOf("") }
    var emailVal by remember { mutableStateOf("") }
    var phoneVal by remember { mutableStateOf("") }
    var beltVal by remember { mutableStateOf("Black Belt 4th Dan") }
    var designationVal by remember { mutableStateOf("Karate Sensei") }
    var specVal by remember { mutableStateOf("Kumite & Kata Specialist") }
    var dojoVal by remember { mutableStateOf("Main Dojang - Central Branch") }
    var bioVal by remember { mutableStateOf("") }
    var photoUriVal by remember { mutableStateOf<String?>(null) }
    var showHierarchyPreview by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val officialPresets = listOf(
        "SPECIAL TRAINING COACH & INSTRUCTOR",
        "CHIEF COACH & CHIEF INSTRUCTOR",
        "SENIOR MEN COACH & SENIOR MEN INSTRUCTOR",
        "SENIOR WOMEN COACH & SENIOR WOMEN INSTRUCTOR",
        "MEN COACH & MEN INSTRUCTOR",
        "WOMEN COACH & WOMEN INSTRUCTOR",
        "KATA ASST.COACH & ASST.INSTRUCTOR",
        "KUMITE ASST.COACH & ASST.INSTRUCTOR",
        "KATA WOMEN ASST.COACH & ASST.INSTRUCTOR",
        "KUMITE WOMEN ASST.COACH & WOMEN ASST.INSTRUCTOR"
    )

    val activeCoaches = coachesList.filter { it.status != "PENDING_APPROVAL" }
    val pendingCoaches = coachesList.filter { it.status == "PENDING_APPROVAL" }

    // Delete Confirmation Dialog
    if (coachToDelete != null) {
        val coach = coachToDelete!!
        AlertDialog(
            onDismissRequest = { coachToDelete = null },
            title = { Text("Confirm Delete Coach", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = StatusDanger) },
            text = {
                Text(
                    "Are you sure you want to remove Sensei ${coach.fullName} (${coach.userId})? This will delete their profile from the academy database.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextNavy
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteCoach(coach.userId)
                        coachToDelete = null
                        editingCoach = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusDanger),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Delete Coach", color = TextOnAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { coachToDelete = null }) {
                    Text("Cancel", color = TextSlate)
                }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showHierarchyPreview) {
        AlertDialog(
            onDismissRequest = { showHierarchyPreview = false },
            title = null,
            text = {
                Box(modifier = Modifier.fillMaxHeight(0.85f).fillMaxWidth()) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        com.example.ui.common.AcademyLeadershipSection(coachesList = coachesList)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showHierarchyPreview = false },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Close", color = TextOnAccent)
                }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
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
            Column(modifier = Modifier.weight(1f)) {
                Text("COACH DIRECTORY & MANAGEMENT", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                Text("Manage sensei profiles, hierarchy & register offline instructors", style = MaterialTheme.typography.bodySmall, color = TextSlate)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(
                    onClick = { showHierarchyPreview = true },
                    border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("🥋 Hierarchy", fontSize = 11.sp, color = RoyalBlue, fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = {
                        selectedAppUser = null
                        addMode = 0
                        nameVal = ""
                        usernameVal = ""
                        emailVal = ""
                        phoneVal = ""
                        beltVal = "Black Belt 4th Dan"
                        designationVal = "Karate Sensei"
                        specVal = "Kumite & Kata Specialist"
                        dojoVal = "Main Dojang - Central Branch"
                        bioVal = ""
                        photoUriVal = null
                        showAddDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("admin_add_coach_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Coach", color = TextOnAccent)
                }
            }
        }

        // Sub-tabs for Active Coaches vs Coach Applications
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = activeCoachTab == 0,
                onClick = { activeCoachTab = 0 },
                label = {
                    Text(
                        "🥋 Active Coaches (${activeCoaches.size})",
                        fontWeight = if (activeCoachTab == 0) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = RoyalBlue,
                    selectedLabelColor = TextOnAccent
                ),
                shape = RoundedCornerShape(8.dp)
            )

            FilterChip(
                selected = activeCoachTab == 1,
                onClick = { activeCoachTab = 1 },
                label = {
                    Text(
                        "📝 Applications & Verification (${pendingCoaches.size})",
                        fontWeight = if (activeCoachTab == 1) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = StatusWarning,
                    selectedLabelColor = TextNavy
                ),
                shape = RoundedCornerShape(8.dp)
            )
        }

        val displayedCoaches = if (activeCoachTab == 0) activeCoaches else pendingCoaches

        if (displayedCoaches.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = CardWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        if (activeCoachTab == 0) Icons.Default.Sports else Icons.Default.PendingActions,
                        contentDescription = null,
                        tint = TextSlate,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        if (activeCoachTab == 0) "No active coaches registered yet" else "No pending coach applications",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextNavy
                    )
                    Text(
                        if (activeCoachTab == 0) "Add a coach using the 'Add Coach' button above." else "New coach signup requests will appear here for verification and approval.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSlate
                    )
                }
            }
        } else {
            displayedCoaches.forEach { coach ->
                val isPending = coach.status == "PENDING_APPROVAL"
                val isOffline = coach.userId.contains("OFFLINE") || coach.username.startsWith("offline_")

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = CardWhite,
                    shadowElevation = 1.dp,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isPending) StatusWarning else BorderLight)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(ActiveNavBg)
                                        .border(1.5.dp, RoyalBlue, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!coach.profilePhotoUri.isNullOrBlank()) {
                                        Image(
                                            painter = rememberAsyncImagePainter(coach.profilePhotoUri),
                                            contentDescription = coach.fullName,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(28.dp))
                                    }
                                }

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(coach.fullName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                        if (isOffline) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = StatusWarning.copy(alpha = 0.15f),
                                                border = androidx.compose.foundation.BorderStroke(0.5.dp, StatusWarning.copy(alpha = 0.5f))
                                            ) {
                                                Text("OFFLINE SENSEI", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp), color = StatusWarning, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                            }
                                        }
                                    }
                                    Text("ID: ${coach.userId} • Belt: ${coach.currentBelt}", style = MaterialTheme.typography.bodySmall, color = RoyalBlue)
                                }
                            }

                            if (isPending) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = StatusWarning.copy(alpha = 0.12f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusWarning.copy(alpha = 0.4f))
                                ) {
                                    Text("⏳ PENDING VERIFICATION", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = StatusWarning, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = StatusSuccess.copy(alpha = 0.12f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccess.copy(alpha = 0.4f))
                                ) {
                                    Text("🟢 ACTIVE COACH", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = StatusSuccess, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }

                        Text("🥋 Designation: ${coach.designation}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = RoyalBlue)
                        Text("🌟 Belt/Dan: ${coach.currentBelt} ${if (coach.specializations != null) "• ${coach.specializations}" else ""}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                        if (coach.dojoCenter.isNotBlank()) {
                            Text("📍 Centre: ${coach.dojoCenter}", style = MaterialTheme.typography.bodySmall, color = TextNavy)
                        }

                        // Coach Availability & Slots Section
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = ActiveNavBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue.copy(alpha = 0.25f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🕒 AVAILABILITY & TRAINING SLOTS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue)
                                    Text("🟢 Available", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = StatusSuccess)
                                }
                                Text("• Morning Batch Slot: 06:00 AM - 07:30 AM (Mon, Wed, Fri)", style = MaterialTheme.typography.bodySmall, color = TextNavy)
                                Text("• Evening Batch Slot: 05:30 PM - 07:00 PM (Daily)", style = MaterialTheme.typography.bodySmall, color = TextNavy)
                                Text("• Weekend Special Training: 07:00 AM - 09:30 AM (Sat, Sun)", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                            }
                        }

                        HorizontalDivider(color = BorderLight)
                        Text("✉️ Email: ${coach.email}", style = MaterialTheme.typography.bodySmall, color = TextNavy)
                        Text("👤 Username: @${coach.username} ${if (coach.phone.isNotBlank()) "• 📱 ${coach.phone}" else ""}", style = MaterialTheme.typography.bodySmall, color = TextSlate)

                        if (!coach.bio.isNullOrBlank()) {
                            Text("📝 Bio: ${coach.bio}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                        }

                        // Action Buttons: Edit, Approve, Delete
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { coachToDelete = coach },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusDanger),
                                border = androidx.compose.foundation.BorderStroke(1.dp, StatusDanger.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("delete_coach_${coach.userId}")
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp), tint = StatusDanger)
                                Spacer(Modifier.width(4.dp))
                                Text(if (isPending) "Reject Application" else "Delete Coach", color = StatusDanger, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Button(
                                onClick = { editingCoach = coach },
                                colors = ButtonDefaults.buttonColors(containerColor = SecondaryBg),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("edit_coach_${coach.userId}")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = TextNavy)
                                Spacer(Modifier.width(4.dp))
                                Text("Edit Details", color = TextNavy, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }

                            if (isPending) {
                                Button(
                                    onClick = { onApproveCoach(coach.userId) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                                    modifier = Modifier.testTag("approve_coach_${coach.userId}")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = TextOnAccent, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Verify & Approve", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextOnAccent)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal: Edit Coach Details
    if (editingCoach != null) {
        val coach = editingCoach!!
        var editName by remember { mutableStateOf(coach.fullName) }
        var editUsername by remember { mutableStateOf(coach.username) }
        var editEmail by remember { mutableStateOf(coach.email) }
        var editPhone by remember { mutableStateOf(coach.phone) }
        var editBelt by remember { mutableStateOf(coach.currentBelt) }
        var editDesignation by remember { mutableStateOf(coach.designation) }
        var editSpec by remember { mutableStateOf(coach.specializations ?: "") }
        var editDojo by remember { mutableStateOf(coach.dojoCenter) }
        var editBio by remember { mutableStateOf(coach.bio ?: "") }
        var editStatus by remember { mutableStateOf(coach.status) }
        var editPhotoUri by remember { mutableStateOf(coach.profilePhotoUri) }

        AlertDialog(
            onDismissRequest = { editingCoach = null },
            title = { Text("Edit Sensei / Coach Profile", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Coach ID: ${coach.userId}", style = MaterialTheme.typography.labelSmall, color = RoyalBlue)

                    PhotoPickerSelector(
                        selectedImageUri = editPhotoUri,
                        onImageSelected = { editPhotoUri = it },
                        label = "COACH / SENSEI PROFILE PHOTO",
                        categoryHint = "Sensei Photo"
                    )

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Sensei Full Name *", color = TextSlate) },
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
                        label = { Text("App Username", color = TextSlate) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("Email Address", color = TextSlate) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Phone Number", color = TextSlate) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    HorizontalDivider(color = BorderLight)
                    Text("QUALIFICATIONS & DESIGNATION", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue)

                    OutlinedTextField(
                        value = editBelt,
                        onValueChange = { editBelt = it },
                        label = { Text("Dan / Belt Grade", color = TextSlate) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("SELECT OFFICIAL DESIGNATION:", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        officialPresets.take(5).forEach { preset ->
                            SuggestionChip(
                                onClick = { editDesignation = preset },
                                label = { Text(preset, fontSize = 10.sp) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (editDesignation == preset) ActiveNavBg else SecondaryBg,
                                    labelColor = if (editDesignation == preset) RoyalBlue else TextNavy
                                ),
                                border = SuggestionChipDefaults.suggestionChipBorder(
                                    enabled = true,
                                    borderColor = if (editDesignation == preset) RoyalBlue else BorderLight
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = editDesignation,
                        onValueChange = { editDesignation = it },
                        label = { Text("Coach Designation", color = TextSlate) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editSpec,
                        onValueChange = { editSpec = it },
                        label = { Text("Specializations", color = TextSlate) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editDojo,
                        onValueChange = { editDojo = it },
                        label = { Text("Primary Dojo Branch", color = TextSlate) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Biography & Teaching Experience", color = TextSlate) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = coach.copy(
                            fullName = editName,
                            username = editUsername,
                            email = editEmail,
                            phone = editPhone,
                            currentBelt = editBelt,
                            designation = editDesignation,
                            specializations = editSpec,
                            dojoCenter = editDojo,
                            bio = editBio,
                            status = editStatus,
                            profilePhotoUri = editPhotoUri
                        )
                        onSaveCoach(updated)
                        editingCoach = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("save_coach_changes_button")
                ) { Text("Save Changes", color = TextOnAccent) }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = {
                            coachToDelete = coach
                        }
                    ) {
                        Text("Delete Coach", color = StatusDanger)
                    }
                    TextButton(onClick = { editingCoach = null }) {
                        Text("Cancel", color = TextSlate)
                    }
                }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Modal: Add New Coach (With 2 explicit modes: Registered App User vs Direct Offline Sensei Entry)
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
            title = { Text("Add Coach / Sensei to Academy", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
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
                            text = { Text("📝 Offline Sensei Entry", fontSize = 12.sp, fontWeight = if (addMode == 1) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }

                    if (addMode == 0) {
                        // Quick import from registered app accounts
                        if (allUsersList.isNotEmpty()) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                color = SecondaryBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("⚡ REGISTERED APP ACCOUNTS", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = RoyalBlue)
                                    Text("Select an app account to promote/assign as Coach:", style = MaterialTheme.typography.bodySmall, color = TextSlate)

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
                                                    photoUriVal = user.profilePhotoUri
                                                    if (user.currentBelt.isNotBlank()) beltVal = user.currentBelt
                                                    if (user.designation.isNotBlank()) designationVal = user.designation
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

                        Text("COACH ACCOUNT INFORMATION", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue)
                        OutlinedTextField(
                            value = nameVal,
                            onValueChange = { nameVal = it },
                            label = { Text("Sensei Full Name *", color = TextSlate) },
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
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = emailVal,
                            onValueChange = { emailVal = it },
                            label = { Text("Email Address *", color = TextSlate) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = phoneVal,
                            onValueChange = { phoneVal = it },
                            label = { Text("Phone Number", color = TextSlate) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        // MODE 2: Direct Offline Sensei Entry
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
                                    "For offline coaches or guest masters without smartphone app accounts. Directly register their details below.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextNavy
                                )
                            }
                        }

                        PhotoPickerSelector(
                            selectedImageUri = photoUriVal,
                            onImageSelected = { photoUriVal = it },
                            label = "SENSEI PHOTO (OPTIONAL)",
                            categoryHint = "Sensei Photo"
                        )

                        Text("OFFLINE COACH DETAILS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue)
                        OutlinedTextField(
                            value = nameVal,
                            onValueChange = { nameVal = it },
                            label = { Text("Sensei / Instructor Full Name *", color = TextSlate) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = phoneVal,
                            onValueChange = { phoneVal = it },
                            label = { Text("Contact Phone Number", color = TextSlate) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = dojoVal,
                            onValueChange = { dojoVal = it },
                            label = { Text("Primary Dojo Branch Location", color = TextSlate) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    HorizontalDivider(color = BorderLight)
                    Text("QUALIFICATIONS & DESIGNATION", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue)
                    OutlinedTextField(
                        value = beltVal,
                        onValueChange = { beltVal = it },
                        label = { Text("Dan / Belt Grade", color = TextSlate) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("QUICK ACADEMY HIERARCHY DESIGNATION PRESETS:", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        officialPresets.take(5).forEach { preset ->
                            SuggestionChip(
                                onClick = { designationVal = preset },
                                label = { Text(preset, fontSize = 10.sp) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (designationVal == preset) ActiveNavBg else SecondaryBg,
                                    labelColor = if (designationVal == preset) RoyalBlue else TextNavy
                                ),
                                border = SuggestionChipDefaults.suggestionChipBorder(
                                    enabled = true,
                                    borderColor = if (designationVal == preset) RoyalBlue else BorderLight
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = designationVal,
                        onValueChange = { designationVal = it },
                        label = { Text("Coach Designation", color = TextSlate) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = specVal,
                        onValueChange = { specVal = it },
                        label = { Text("Specializations / Focus Area", color = TextSlate) },
                        shape = RoundedCornerShape(10.dp),
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
                            val finalUserId = if (isManualOffline) "COACH-OFFLINE-" + (100..999).random() else (targetUser?.userId ?: ("COACH-" + (10..99).random()))
                            val cleanName = nameVal.trim()
                            val generatedUsername = if (isManualOffline) "offline_coach_${cleanName.lowercase().replace(" ", "_")}" else (usernameVal.ifBlank { nameVal.lowercase().replace(" ", "") })
                            val generatedEmail = if (isManualOffline) "offline_coach_${cleanName.lowercase().replace(" ", "_")}@broma.local" else (emailVal.ifBlank { "coach_${usernameVal}@broma.com" })

                            val newCoach = (targetUser ?: UserAccountEntity(
                                userId = finalUserId,
                                email = generatedEmail,
                                username = generatedUsername,
                                password = "coach123",
                                role = UserRole.COACH,
                                fullName = cleanName
                            )).copy(
                                userId = finalUserId,
                                fullName = cleanName,
                                username = generatedUsername,
                                email = generatedEmail,
                                phone = phoneVal,
                                role = UserRole.COACH,
                                currentBelt = beltVal,
                                designation = designationVal,
                                specializations = specVal,
                                dojoCenter = dojoVal,
                                bio = bioVal,
                                status = "ACTIVE",
                                profilePhotoUri = photoUriVal
                            )
                            onSaveCoach(newCoach)
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("submit_new_coach_button")
                ) { Text(if (addMode == 1) "Register Offline Coach" else "Save Coach", color = TextOnAccent) }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel", color = TextSlate) }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
