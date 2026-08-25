package com.example.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Approval
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CertificateEntity
import com.example.data.UserAccountEntity
import com.example.ui.theme.*

@Composable
fun StudentCertificatesScreen(
    student: UserAccountEntity,
    certificatesList: List<CertificateEntity>,
    onSubmitCertificate: (String, String, String, String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("ALL") }
    var showAddCertModal by remember { mutableStateOf(false) }

    val defaultCategories = listOf("ALL", "Belt Certificates", "Tournament Certificates", "Seminar Certificates", "Achievement Certificates", "Course Certificates")
    val dynamicCategories = (defaultCategories + certificatesList.map { it.category }).distinct()
    val scrollState = rememberScrollState()

    val filteredList = if (selectedCategory == "ALL") certificatesList else certificatesList.filter { it.category == selectedCategory }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header Row with + Add Certificate Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "CERTIFICATE LOCKER",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextNavy
                )
                Text(
                    "Official BROMA & External Verified Credentials",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSlate
                )
            }

            Button(
                onClick = { showAddCertModal = true },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("add_certificate_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add", color = TextOnAccent)
            }
        }

        // Category Filter Chips
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = CardWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            ScrollableTabRow(
                selectedTabIndex = dynamicCategories.indexOf(selectedCategory).coerceAtLeast(0),
                containerColor = CardWhite,
                contentColor = RoyalBlue,
                edgePadding = 8.dp,
                indicator = { tabPositions ->
                    val idx = dynamicCategories.indexOf(selectedCategory).coerceAtLeast(0)
                    if (idx < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[idx]),
                            color = RoyalBlue
                        )
                    }
                },
                divider = {}
            ) {
                dynamicCategories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Tab(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        text = {
                            Text(
                                cat,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium),
                                color = if (isSelected) RoyalBlue else TextSlate
                            )
                        },
                        modifier = Modifier.testTag("cert_cat_tab_$cat")
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (filteredList.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = CardWhite,
                        shadowElevation = 1.dp,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                    ) {
                        Text(
                            text = "No certificates found in '$selectedCategory'. Tap '+ Add' to submit a new certificate for verification.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSlate,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    filteredList.forEach { cert ->
                        var showPhotoModal by remember { mutableStateOf(false) }

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
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(
                                            imageVector = if (cert.isOfficialBroma) Icons.Default.Verified else Icons.Default.Approval,
                                            contentDescription = null,
                                            tint = if (cert.isOfficialBroma) RoyalBlue else TextNavy
                                        )
                                        Text(
                                            text = if (cert.isOfficialBroma) "OFFICIAL BROMA DIPLOMA" else cert.issuingOrg,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (cert.isOfficialBroma) RoyalBlue else TextNavy
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = when (cert.status) {
                                            "VERIFIED" -> StatusSuccess.copy(alpha = 0.12f)
                                            "REJECTED" -> StatusError.copy(alpha = 0.12f)
                                            else -> StatusWarning.copy(alpha = 0.12f)
                                        },
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            when (cert.status) {
                                                "VERIFIED" -> StatusSuccess.copy(alpha = 0.35f)
                                                "REJECTED" -> StatusError.copy(alpha = 0.35f)
                                                else -> StatusWarning.copy(alpha = 0.35f)
                                            }
                                        )
                                    ) {
                                        Text(
                                            text = cert.status,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = when (cert.status) {
                                                "VERIFIED" -> StatusSuccess
                                                "REJECTED" -> StatusError
                                                else -> StatusWarning
                                            },
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text(
                                    cert.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextNavy
                                )
                                Text(
                                    "Student: ${cert.studentName} (${cert.studentId})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSlate
                                )
                                Text(
                                    "Category: ${cert.category} • Issued: ${cert.issueDate}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSlate
                                )

                                // VISUAL CERTIFICATE DOCUMENT PHOTO CARD PREVIEW
                                CertificatePhotoCard(cert = cert, onExpandPhoto = { showPhotoModal = true })
                            }
                        }

                        // Full-screen Certificate Photo Modal
                        if (showPhotoModal) {
                            CertificatePhotoModal(cert = cert, onDismiss = { showPhotoModal = false })
                        }
                    }
                }
            }
        }
    }

    // Submit Certificate Dialog Modal
    if (showAddCertModal) {
        var certTitle by remember { mutableStateOf("") }
        var certCategory by remember { mutableStateOf("Tournament") }
        var isCustomCategory by remember { mutableStateOf(false) }
        var customCategoryInput by remember { mutableStateOf("") }
        var certDate by remember { mutableStateOf("2026-08-10") }
        var certLocation by remember { mutableStateOf("Hyderabad Main Dojo") }
        var certPresentOfficials by remember { mutableStateOf("Shihan Brucelee Raj, Sensei Rajesh Kumar") }
        var certOrg by remember { mutableStateOf("BROMA Martial Arts Academy") }

        val certCategoryOptions = listOf(
            "Tournament",
            "Seminar",
            "Kata Junior",
            "Kumite Cadet",
            "Belt Promotion",
            "Workshop",
            "State Championship",
            "National Championship",
            "Custom Category"
        )

        AlertDialog(
            onDismissRequest = { showAddCertModal = false },
            title = {
                Text(
                    "Submit Certificate For Verification",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextNavy
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = certTitle,
                        onValueChange = { certTitle = it },
                        label = { Text("Certificate Name / Title", color = TextSlate) },
                        placeholder = { Text("e.g. 1st Place Kata Gold Medal", color = TextSlate) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cert_title_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CardWhite,
                            unfocusedContainerColor = CardWhite,
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        )
                    )

                    Text("Select Category:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        certCategoryOptions.chunked(2).forEach { rowOpts ->
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                rowOpts.forEach { opt ->
                                    val isSel = if (opt == "Custom Category") isCustomCategory else (!isCustomCategory && certCategory == opt)
                                    Surface(
                                        modifier = Modifier.clickable {
                                            if (opt == "Custom Category") {
                                                isCustomCategory = true
                                            } else {
                                                isCustomCategory = false
                                                certCategory = opt
                                            }
                                        },
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSel) RoyalBlue else SecondaryBg,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) RoyalBlue else BorderLight)
                                    ) {
                                        Text(
                                            text = if (opt == "Custom Category") "✨ Custom Category" else opt,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal),
                                            color = if (isSel) TextOnAccent else TextNavy,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (isCustomCategory) {
                        OutlinedTextField(
                            value = customCategoryInput,
                            onValueChange = { customCategoryInput = it },
                            label = { Text("Enter Custom Category Name *", color = RoyalBlue) },
                            placeholder = { Text("e.g. Weapon Kata, Self Defence, Inter-School", color = TextSlate) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("cert_custom_category_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CardWhite,
                                unfocusedContainerColor = CardWhite,
                                focusedBorderColor = RoyalBlue,
                                unfocusedBorderColor = BorderLight,
                                focusedTextColor = TextNavy,
                                unfocusedTextColor = TextNavy
                            )
                        )
                    }

                    OutlinedTextField(
                        value = certDate,
                        onValueChange = { certDate = it },
                        label = { Text("Event / Issue Date (YYYY-MM-DD)", color = TextSlate) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cert_date_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CardWhite,
                            unfocusedContainerColor = CardWhite,
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        )
                    )

                    OutlinedTextField(
                        value = certLocation,
                        onValueChange = { certLocation = it },
                        label = { Text("Location / Venue", color = TextSlate) },
                        placeholder = { Text("e.g. Saroornagar Indoor Stadium, Hyderabad", color = TextSlate) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cert_location_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CardWhite,
                            unfocusedContainerColor = CardWhite,
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        )
                    )

                    OutlinedTextField(
                        value = certPresentOfficials,
                        onValueChange = { certPresentOfficials = it },
                        label = { Text("Officials / Dignitaries Present", color = TextSlate) },
                        placeholder = { Text("e.g. Shihan Brucelee Raj, Master K. Ramakrishna", color = TextSlate) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cert_officials_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CardWhite,
                            unfocusedContainerColor = CardWhite,
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        )
                    )

                    OutlinedTextField(
                        value = certOrg,
                        onValueChange = { certOrg = it },
                        label = { Text("Issuing Organization / Federation", color = TextSlate) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cert_org_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CardWhite,
                            unfocusedContainerColor = CardWhite,
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        )
                    )
                }
            },
            confirmButton = {
                val finalCat = if (isCustomCategory) {
                    if (customCategoryInput.isNotBlank()) customCategoryInput.trim() else "Special Certificate"
                } else {
                    certCategory
                }
                Button(
                    onClick = {
                        if (certTitle.isNotBlank()) {
                            val fullOrg = if (certLocation.isNotBlank() || certPresentOfficials.isNotBlank()) {
                                "$certOrg • Loc: $certLocation • Dignitaries: $certPresentOfficials"
                            } else {
                                certOrg
                            }
                            onSubmitCertificate(certTitle, finalCat, certDate, fullOrg)
                            showAddCertModal = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("submit_cert_dialog_button"),
                    enabled = certTitle.isNotBlank() && (!isCustomCategory || customCategoryInput.isNotBlank())
                ) {
                    Text("Submit For Verification", color = TextOnAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCertModal = false }) { Text("Cancel", color = TextSlate) }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun CertificatePhotoCard(
    cert: CertificateEntity,
    onExpandPhoto: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExpandPhoto() },
        shape = RoundedCornerShape(12.dp),
        color = SecondaryBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Certificate Banner Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "🥋 CERTIFICATE DOCUMENT",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextNavy
                )
                Text(
                    "TAP TO EXPAND 🔍",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = RoyalBlue
                )
            }

            // Visual Frame
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(8.dp),
                color = CardWhite,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, BorderLight)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            "📜 BROMA DIPLOMA OF MERIT 📜",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = RoyalBlue
                        )
                        Text("This certifies that", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                        Text(
                            cert.studentName.uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextNavy
                        )
                        Text("has successfully achieved", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                        Text(
                            cert.title,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = StatusSuccess
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("ID: ${cert.certId}", style = MaterialTheme.typography.labelSmall, color = TextSlate, fontSize = 9.sp)
                            Text(
                                "Seal Verified ✓",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = StatusSuccess,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CertificatePhotoModal(
    cert: CertificateEntity,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "CERTIFICATE DOCUMENT",
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(12.dp),
                    color = CardWhite,
                    border = androidx.compose.foundation.BorderStroke(2.dp, BorderLight)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            "🥋 B.R.O.M.A. MARTIAL ARTS ACADEMY 🥋",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = RoyalBlue
                        )
                        Text(
                            "FOUNDED 2020 • RECOGNIZED MARTIAL ARTS FEDERATION",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSlate,
                            fontSize = 9.sp
                        )

                        HorizontalDivider(color = BorderLight, thickness = 1.dp)

                        Text(
                            "OFFICIAL CERTIFICATE OF MERIT",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextNavy
                        )

                        Text("This is officially awarded to", style = MaterialTheme.typography.bodySmall, color = TextSlate)

                        Text(
                            text = cert.studentName.uppercase(),
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = TextNavy
                        )

                        Text("For outstanding completion & excellence in", style = MaterialTheme.typography.bodySmall, color = TextSlate)

                        Text(
                            text = cert.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = RoyalBlue
                        )

                        Text(
                            "Category: ${cert.category} | Issued On: ${cert.issueDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSlate
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column(horizontalAlignment = Alignment.Start) {
                                Text("Certificate ID:", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                                Text(cert.certId, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                Text("Status: ${cert.status}", style = MaterialTheme.typography.labelSmall, color = StatusSuccess)
                            }

                            // Official Seal Stamp
                            Surface(
                                shape = CircleShape,
                                color = ActiveNavBg,
                                border = androidx.compose.foundation.BorderStroke(2.dp, RoyalBlue),
                                modifier = Modifier.size(54.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("BROMA", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RoyalBlue, fontSize = 8.sp)
                                        Text("SEAL", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy, fontSize = 8.sp)
                                    }
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("Sensei Rajesh Kumar", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                Text("Chief Examiner & Director", style = MaterialTheme.typography.labelSmall, color = TextSlate, fontSize = 9.sp)
                            }
                        }
                    }
                }

                Text("✓ Verified Document in BROMA Central Registry", style = MaterialTheme.typography.bodySmall, color = StatusSuccess)
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Close Viewer", color = TextOnAccent)
            }
        },
        containerColor = CardWhite,
        shape = RoundedCornerShape(16.dp)
    )
}
