package com.example.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TournamentEntity
import com.example.ui.theme.*
import java.util.UUID

@Composable
fun AdminTournamentsManagementView(
    tournamentsList: List<TournamentEntity>,
    onSaveTournament: (TournamentEntity) -> Unit,
    onDeleteTournament: (String) -> Unit,
    onTogglePublishTournament: (String) -> Unit
) {
    var showAddEditDialog by remember { mutableStateOf(false) }
    var selectedTournament by remember { mutableStateOf<TournamentEntity?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    if (showAddEditDialog) {
        AddEditTournamentDialog(
            tournament = selectedTournament,
            onDismiss = {
                showAddEditDialog = false
                selectedTournament = null
            },
            onSave = { updated ->
                onSaveTournament(updated)
                successMessage = "Tournament '${updated.title}' saved and published! Real-time notifications sent to all students & coaches."
                showAddEditDialog = false
                selectedTournament = null
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = CardWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = RoyalBlue)
                        Text(
                            text = "TOURNAMENT DETAILS & UPLOADS",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextNavy
                        )
                    }
                    Text(
                        text = "Upload State, National & District Championship details, rules, entry fees, PDF circulars, and publish them to Student & Coach portals in real-time.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSlate
                    )
                }

                Button(
                    onClick = {
                        selectedTournament = null
                        showAddEditDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("add_tournament_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Tournament")
                }
            }
        }

        if (successMessage != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = StatusSuccessBg,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccessBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusSuccess)
                        Text(successMessage!!, style = MaterialTheme.typography.bodySmall, color = StatusSuccessText)
                    }
                    IconButton(onClick = { successMessage = null }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = StatusSuccessText, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        var activeTournamentTab by remember { mutableStateOf(0) }

        // Tab Selector
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = CardWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                Button(
                    onClick = { activeTournamentTab = 0 },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeTournamentTab == 0) RoyalBlue else CardWhite,
                        contentColor = if (activeTournamentTab == 0) TextOnAccent else TextNavy
                    ),
                    elevation = null
                ) {
                    Text("TOURNAMENTS (${tournamentsList.size})", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }

                Button(
                    onClick = { activeTournamentTab = 1 },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeTournamentTab == 1) RoyalBlue else CardWhite,
                        contentColor = if (activeTournamentTab == 1) TextOnAccent else TextNavy
                    ),
                    elevation = null
                ) {
                    Text("REGISTERED ATHLETES", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }

        if (activeTournamentTab == 1) {
            // Registered Participants & Officials List
            var roleFilter by remember { mutableStateOf("ALL") }
            var searchQuery by remember { mutableStateOf("") }

            val allRegistrations = remember {
                mutableStateListOf(
                    TournamentAthleteReg(
                        name = "Rahul Sharma",
                        aadhar = "6789-2341-9876",
                        age = "14 Yrs",
                        category = "Kata Individual",
                        weight = "48 kg",
                        phone = "+91 98765 43210",
                        tournament = "All-India Karate Championship 2026",
                        status = "VERIFIED",
                        role = "STUDENT",
                        beltOrGrade = "Brown Belt (1st Kyu)",
                        assignedCoachOrDojo = "Sensei Rajesh Kumar • Main Dojo"
                    ),
                    TournamentAthleteReg(
                        name = "Sensei Rajesh Kumar",
                        aadhar = "4123-5566-7788",
                        age = "38 Yrs",
                        category = "Chief Team Coach & Official",
                        weight = "78 kg",
                        phone = "+91 98765 11111",
                        tournament = "All-India Karate Championship 2026",
                        status = "VERIFIED",
                        role = "COACH",
                        beltOrGrade = "Black Belt 4th Dan",
                        assignedCoachOrDojo = "Head Coach • Team Telangana"
                    ),
                    TournamentAthleteReg(
                        name = "Ananya Reddy",
                        aadhar = "4532-8765-1092",
                        age = "12 Yrs",
                        category = "Kumite Cadet",
                        weight = "42 kg",
                        phone = "+91 98450 12345",
                        tournament = "Telangana State Karate Open Cup",
                        status = "VERIFIED",
                        role = "STUDENT",
                        beltOrGrade = "Green Belt (4th Kyu)",
                        assignedCoachOrDojo = "Sensei Vikram Varma • Secunderabad"
                    ),
                    TournamentAthleteReg(
                        name = "Sensei Vikram Varma",
                        aadhar = "8899-1122-3344",
                        age = "34 Yrs",
                        category = "Accredited Referee / Coach",
                        weight = "72 kg",
                        phone = "+91 98765 22222",
                        tournament = "Telangana State Karate Open Cup",
                        status = "VERIFIED",
                        role = "COACH",
                        beltOrGrade = "Black Belt 3rd Dan",
                        assignedCoachOrDojo = "Senior Coach • BROMA North"
                    ),
                    TournamentAthleteReg(
                        name = "Vikram Patel",
                        aadhar = "9812-3456-7890",
                        age = "16 Yrs",
                        category = "Team Kata",
                        weight = "58 kg",
                        phone = "+91 97654 32109",
                        tournament = "All-India Karate Championship 2026",
                        status = "PENDING",
                        role = "STUDENT",
                        beltOrGrade = "Blue Belt (5th Kyu)",
                        assignedCoachOrDojo = "Sensei Rajesh Kumar • Main Dojo"
                    ),
                    TournamentAthleteReg(
                        name = "Shihan Brucelee Raj",
                        aadhar = "1122-3344-5566",
                        age = "52 Yrs",
                        category = "Tournament Director & Grandmaster",
                        weight = "82 kg",
                        phone = "+91 98765 00001",
                        tournament = "All-India Karate Championship 2026",
                        status = "VERIFIED",
                        role = "COACH",
                        beltOrGrade = "Grandmaster Black Belt 8th Dan",
                        assignedCoachOrDojo = "President & Chief Technical Director"
                    )
                )
            }

            val filteredRegs = allRegistrations.filter { reg ->
                val matchesRole = when (roleFilter) {
                    "STUDENT" -> reg.role == "STUDENT"
                    "COACH" -> reg.role == "COACH"
                    else -> true
                }
                val matchesSearch = searchQuery.isBlank() ||
                        reg.name.contains(searchQuery, ignoreCase = true) ||
                        reg.tournament.contains(searchQuery, ignoreCase = true) ||
                        reg.category.contains(searchQuery, ignoreCase = true) ||
                        reg.beltOrGrade.contains(searchQuery, ignoreCase = true)
                matchesRole && matchesSearch
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Search Box & Role Filter Segmented Control
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search participants, tournament, belt...", color = TextSlate) },
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CardWhite,
                        unfocusedContainerColor = CardWhite,
                        focusedBorderColor = RoyalBlue,
                        unfocusedBorderColor = BorderLight,
                        focusedTextColor = TextNavy,
                        unfocusedTextColor = TextNavy
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("tournament_participant_search_input")
                )

                // Role Filter Tabs: All, Students, Coaches
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val studentCount = allRegistrations.count { it.role == "STUDENT" }
                    val coachCount = allRegistrations.count { it.role == "COACH" }

                    listOf(
                        Triple("ALL", "All (${allRegistrations.size})", "all_tab"),
                        Triple("STUDENT", "🥋 Students ($studentCount)", "students_tab"),
                        Triple("COACH", "🥋 Coaches & Officials ($coachCount)", "coaches_tab")
                    ).forEach { (rKey, rLabel, testTag) ->
                        val isSelected = roleFilter == rKey
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { roleFilter = rKey }
                                .testTag("filter_$testTag"),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) RoyalBlue else CardWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) RoyalBlue else BorderLight)
                        ) {
                            Text(
                                text = rLabel,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium),
                                color = if (isSelected) TextOnAccent else TextNavy,
                                modifier = Modifier.padding(vertical = 10.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                // Table / Card Columns Header
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = SecondaryBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ROLE & PARTICIPANT DETAILS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextSlate)
                        Text("EVENT & CATEGORY", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextSlate)
                    }
                }

                if (filteredRegs.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = CardWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                    ) {
                        Text("No participants found matching current filter.", style = MaterialTheme.typography.bodyMedium, color = TextSlate, modifier = Modifier.padding(16.dp))
                    }
                } else {
                    filteredRegs.forEach { reg ->
                        val isCoach = reg.role == "COACH"
                        Surface(
                            modifier = Modifier.fillMaxWidth().testTag("participant_card_${reg.name}"),
                            shape = RoundedCornerShape(12.dp),
                            color = CardWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isCoach) RoyalBlue.copy(alpha = 0.35f) else BorderLight),
                            shadowElevation = 1.dp
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                // Header: Role Badge + Status
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (isCoach) RoyalBlue else ActiveNavBg,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isCoach) RoyalBlue else RoyalBlue.copy(alpha = 0.5f))
                                        ) {
                                            Text(
                                                text = if (isCoach) "🥋 COACH / SENSEI" else "🥋 STUDENT ATHLETE",
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = if (isCoach) TextOnAccent else RoyalBlue
                                            )
                                        }

                                        Text(reg.beltOrGrade, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = TextNavy)
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (reg.status == "VERIFIED") StatusSuccessBg else StatusWarningBg
                                    ) {
                                        Text(
                                            text = reg.status,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (reg.status == "VERIFIED") StatusSuccessText else StatusWarningText
                                        )
                                    }
                                }

                                // Name & Assigned Dojo
                                Column {
                                    Text(reg.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                    Text("🏢 ${reg.assignedCoachOrDojo}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                                }

                                // Tournament info
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(6.dp),
                                    color = SecondaryBg
                                ) {
                                    Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text("🏆 Tournament: ${reg.tournament}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue)
                                        Text("🎯 Division / Role: ${reg.category}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = TextNavy)
                                    }
                                }

                                // Demographics / Metadata Columns
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Age: ${reg.age}", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                                        if (!isCoach) {
                                            Text("Weight: ${reg.weight}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                        }
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("📞 Phone: ${reg.phone}", style = MaterialTheme.typography.labelSmall, color = TextNavy)
                                        Text("ID: ${reg.aadhar}", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (tournamentsList.isEmpty()) {
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
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(48.dp))
                    Text("No Tournaments Uploaded", fontWeight = FontWeight.Bold, color = TextNavy)
                    Text("Click 'Add Tournament' above to upload championship circulars and categories.", color = TextSlate, style = MaterialTheme.typography.bodySmall)
                }
            }
        } else {
            tournamentsList.forEach { tournament ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_tournament_${tournament.tournamentId}"),
                    shape = RoundedCornerShape(12.dp),
                    color = CardWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (tournament.isPublished) StatusSuccessBorder else BorderLight),
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (tournament.isPublished) StatusSuccessBg else StatusWarningBg
                                ) {
                                    Text(
                                        text = if (tournament.isPublished) "PUBLISHED (LIVE)" else "DRAFT",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (tournament.isPublished) StatusSuccessText else StatusWarningText
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = StatusInfoBg
                                ) {
                                    Text(
                                        text = "${tournament.categoryType.uppercase()} LEVEL",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = StatusInfoText
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = { onTogglePublishTournament(tournament.tournamentId) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        if (tournament.isPublished) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle Visibility",
                                        tint = if (tournament.isPublished) StatusWarning else StatusSuccess,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        selectedTournament = tournament
                                        showAddEditDialog = true
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = RoyalBlue, modifier = Modifier.size(18.dp))
                                }
                                IconButton(
                                    onClick = {
                                        onDeleteTournament(tournament.tournamentId)
                                        successMessage = "Tournament '${tournament.title}' deleted."
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusError, modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        Text(tournament.title, fontWeight = FontWeight.Bold, color = TextNavy, style = MaterialTheme.typography.titleMedium)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(14.dp))
                                Text("${tournament.startDate} – ${tournament.endDate}", style = MaterialTheme.typography.bodySmall, color = TextNavy)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = StatusError, modifier = Modifier.size(14.dp))
                                Text("${tournament.venue}, ${tournament.city}", style = MaterialTheme.typography.bodySmall, color = TextNavy)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Payments, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(14.dp))
                                Text("Entry Fee: ₹${tournament.registrationFee.toInt()}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = StatusSuccess)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Timer, contentDescription = null, tint = StatusWarning, modifier = Modifier.size(14.dp))
                                Text("Deadline: ${tournament.registrationDeadline}", style = MaterialTheme.typography.bodySmall, color = TextSlate)
                            }
                        }

                        Text("Categories: ${tournament.eventsCategories}", color = TextSlate, style = MaterialTheme.typography.bodySmall)

                        if (tournament.description.isNotBlank()) {
                            Text(tournament.description, color = TextSlate, style = MaterialTheme.typography.bodySmall)
                        }

                        if (tournament.circularPdfUrl != null && tournament.circularPdfUrl.isNotBlank()) {
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
                                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = StatusError, modifier = Modifier.size(16.dp))
                                    Text("Official Circular / Guidelines PDF Attached", style = MaterialTheme.typography.labelSmall, color = StatusInfoText)
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
fun AddEditTournamentDialog(
    tournament: TournamentEntity?,
    onDismiss: () -> Unit,
    onSave: (TournamentEntity) -> Unit
) {
    var title by remember { mutableStateOf(tournament?.title ?: "All-India Karate Championship 2026") }
    var startDate by remember { mutableStateOf(tournament?.startDate ?: "2026-09-10") }
    var endDate by remember { mutableStateOf(tournament?.endDate ?: "2026-09-12") }
    var venue by remember { mutableStateOf(tournament?.venue ?: "G.M.C. Balayogi Indoor Stadium") }
    var city by remember { mutableStateOf(tournament?.city ?: "Hyderabad") }
    var organizer by remember { mutableStateOf(tournament?.organizer ?: "National Karate Federation & BROMA Academy") }
    var categoryType by remember { mutableStateOf(tournament?.categoryType ?: "National") }
    var eventsCategories by remember { mutableStateOf(tournament?.eventsCategories ?: "Sub-Junior, Cadet, Junior, Senior (Individual Kata, Team Kata, Kumite)") }
    var registrationFee by remember { mutableStateOf((tournament?.registrationFee ?: 1500.0).toInt().toString()) }
    var registrationDeadline by remember { mutableStateOf(tournament?.registrationDeadline ?: "2026-08-25") }
    var eligibility by remember { mutableStateOf(tournament?.eligibility ?: "Yellow Belt and above with valid federation ID") }
    var prizeDetails by remember { mutableStateOf(tournament?.prizeDetails ?: "Gold, Silver, Bronze Medals + Official Certificate + Cash Prizes for Open Division") }
    var circularPdfUrl by remember { mutableStateOf(tournament?.circularPdfUrl ?: "https://broma.academy/tournaments/circular_2026.pdf") }
    var description by remember { mutableStateOf(tournament?.description ?: "Official WKF-rules championship. Medical insurance and safety gear mandatory.") }
    var isPublished by remember { mutableStateOf(tournament?.isPublished ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = RoyalBlue)
                Text(
                    text = if (tournament == null) "Upload New Tournament" else "Edit Tournament Details",
                    fontWeight = FontWeight.Bold,
                    color = TextNavy
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tournament Title *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = categoryType,
                        onValueChange = { categoryType = it },
                        label = { Text("Level (District/State/National)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = registrationFee,
                        onValueChange = { registrationFee = it },
                        label = { Text("Entry Fee (₹)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        label = { Text("Start Date (YYYY-MM-DD)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { endDate = it },
                        label = { Text("End Date (YYYY-MM-DD)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                OutlinedTextField(
                    value = venue,
                    onValueChange = { venue = it },
                    label = { Text("Venue & Stadium *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = registrationDeadline,
                        onValueChange = { registrationDeadline = it },
                        label = { Text("Reg. Deadline") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                OutlinedTextField(
                    value = eventsCategories,
                    onValueChange = { eventsCategories = it },
                    label = { Text("Categories (Kata / Kumite / Age groups)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = circularPdfUrl,
                    onValueChange = { circularPdfUrl = it },
                    label = { Text("Official Circular / Brochure PDF URL") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Tournament Rules & Description") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = isPublished,
                        onCheckedChange = { isPublished = it }
                    )
                    Text("Publish to Student & Coach Portals Immediately", style = MaterialTheme.typography.bodyMedium, color = TextNavy)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && venue.isNotBlank()) {
                        val trn = TournamentEntity(
                            tournamentId = tournament?.tournamentId ?: "TRN-${UUID.randomUUID().toString().take(8)}",
                            title = title.trim(),
                            startDate = startDate.trim(),
                            endDate = endDate.trim(),
                            venue = venue.trim(),
                            city = city.trim(),
                            organizer = organizer.trim(),
                            categoryType = categoryType.trim(),
                            eventsCategories = eventsCategories.trim(),
                            registrationFee = registrationFee.toDoubleOrNull() ?: 1000.0,
                            registrationDeadline = registrationDeadline.trim(),
                            eligibility = eligibility.trim(),
                            prizeDetails = prizeDetails.trim(),
                            circularPdfUrl = circularPdfUrl.trim(),
                            description = description.trim(),
                            isPublished = isPublished
                        )
                        onSave(trn)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                shape = RoundedCornerShape(8.dp),
                enabled = title.isNotBlank() && venue.isNotBlank()
            ) {
                Text(if (tournament == null) "Publish Tournament" else "Save Changes")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                Text("Cancel")
            }
        }
    )
}

data class TournamentAthleteReg(
    val name: String,
    val aadhar: String,
    val age: String,
    val category: String,
    val weight: String,
    val phone: String,
    val tournament: String,
    val status: String,
    val role: String = "STUDENT",
    val beltOrGrade: String = "Brown Belt (1st Kyu)",
    val assignedCoachOrDojo: String = "Main Dojo"
)
