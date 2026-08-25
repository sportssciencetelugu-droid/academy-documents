package com.example.ui.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.AdminSettingsEntity
import com.example.data.BatchEntity
import com.example.data.FeeItemEntity
import com.example.data.UserAccountEntity
import com.example.ui.common.BromaAcademyLogo
import com.example.ui.common.BromaCard
import com.example.ui.common.PhotoPickerSelector
import com.example.ui.student.UpiQrCodePhotoCard
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Academy Overview & Branding Management Screen for Admin Portal
 * Allows the Admin to manage academy logo (cloud/database sync), contact details,
 * address, affiliation, batch schedules, and payment QR settings directly from mobile.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingsView(
    adminSettings: AdminSettingsEntity?,
    batchesList: List<BatchEntity> = emptyList(),
    feeItemsList: List<FeeItemEntity> = emptyList(),
    currentAdmin: UserAccountEntity? = null,
    onSaveSettings: (AdminSettingsEntity) -> Unit,
    onSaveLogo: (String?, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    // Branding & Identity States
    var academyName by remember(adminSettings) {
        mutableStateOf(adminSettings?.academyName ?: "BRUCELEE RAJ OLYMPIC MARTIALARTS ACADEMY (BROMAA)")
    }
    var academyShortName by remember(adminSettings) {
        mutableStateOf(adminSettings?.academyShortName ?: "BROMAA")
    }
    var registrationNumber by remember(adminSettings) {
        mutableStateOf(adminSettings?.registrationNumber ?: "Regd.MP-23570")
    }
    var affiliation by remember(adminSettings) {
        mutableStateOf(adminSettings?.affiliation ?: "AFFILIATED TO MODERN SHITO-RYU KARATE DO ASSOCIATION (MSKA)")
    }
    var tagline1 by remember(adminSettings) {
        mutableStateOf(adminSettings?.tagline1 ?: "SELF DEFENCE")
    }
    var tagline2 by remember(adminSettings) {
        mutableStateOf(adminSettings?.tagline2 ?: "FITNESS")
    }
    var admissionsNote by remember(adminSettings) {
        mutableStateOf(adminSettings?.admissionsNote ?: "ADMISSIONS OPEN FOR BOYS & GIRLS (Age 3 Years & Above)")
    }

    // Contact & Location States
    var academyPhone by remember(adminSettings) {
        mutableStateOf(adminSettings?.academyPhone ?: "8374632364")
    }
    var academyPhone2 by remember(adminSettings) {
        mutableStateOf(adminSettings?.academyPhone2 ?: "6309735840")
    }
    var academyEmail by remember(adminSettings) {
        mutableStateOf(adminSettings?.academyEmail ?: "info@bromaacademy.com")
    }
    var academyAddress by remember(adminSettings) {
        mutableStateOf(adminSettings?.academyAddress ?: "2ND FLOOR, GEETHA HOSPITAL, NEETU ENUGU BOMMA, KAKATEEYA ITI JUNCTION, BC ROAD, GAJUWAKA")
    }
    var website by remember(adminSettings) {
        mutableStateOf(adminSettings?.website ?: "www.bromaacademy.org")
    }

    // Logo Management States
    var currentLogoUri by remember(adminSettings) {
        mutableStateOf(adminSettings?.logoUri)
    }
    var pendingLogoUri by remember { mutableStateOf<String?>(null) }
    var showCropFitDialog by remember { mutableStateOf(false) }
    var selectedScaleMode by remember { mutableStateOf(ContentScale.Crop) }
    var logoSaveMessage by remember { mutableStateOf<String?>(null) }
    var showRemoveLogoConfirm by remember { mutableStateOf(false) }

    // Payment & UPI States
    var upiId by remember(adminSettings) {
        mutableStateOf(adminSettings?.upiId ?: "bromaacademy@upi")
    }
    var paymentPhone by remember(adminSettings) {
        mutableStateOf(adminSettings?.paymentPhone ?: "8374632364")
    }
    var paymentReceiverName by remember(adminSettings) {
        mutableStateOf(adminSettings?.paymentReceiverName ?: "BROMA Martial Arts")
    }
    var qrCodeUri by remember(adminSettings) {
        mutableStateOf(adminSettings?.qrCodeUri ?: "")
    }
    var paymentInstructions by remember(adminSettings) {
        mutableStateOf(adminSettings?.paymentInstructions ?: "Scan QR Code or pay via UPI ID. Enter transaction reference number after payment for verification.")
    }

    var savedSuccess by remember { mutableStateOf(false) }
    var activeTabSection by remember { mutableStateOf("BRANDING") } // "BRANDING", "LOGO", "CONTACT", "SCHEDULE", "PAYMENT"

    // Image Picker Launcher for Academy Logo
    val logoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            pendingLogoUri = uri.toString()
            showCropFitDialog = true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SecondaryBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Title Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = PrimaryNavy,
            shadowElevation = 3.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                BromaAcademyLogo(
                    size = 56.dp,
                    showBorder = true,
                    borderColor = GoldSecondary,
                    logoUri = currentLogoUri
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ACADEMY OVERVIEW & BRANDING",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Manage global logo, branding, address, batches & official identity across all 3 portals",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSlate.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // Section Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = activeTabSection == "LOGO",
                onClick = { activeTabSection = "LOGO" },
                label = { Text("🖼️ Academy Logo") },
                leadingIcon = { Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CrimsonPrimary,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = activeTabSection == "BRANDING",
                onClick = { activeTabSection = "BRANDING" },
                label = { Text("🥋 Identity & Affiliation") },
                leadingIcon = { Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(16.dp)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CrimsonPrimary,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = activeTabSection == "CONTACT",
                onClick = { activeTabSection = "CONTACT" },
                label = { Text("📍 Dojo & Contact") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CrimsonPrimary,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = activeTabSection == "SCHEDULE",
                onClick = { activeTabSection = "SCHEDULE" },
                label = { Text("🕒 6 Batches & Fees") },
                leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CrimsonPrimary,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = activeTabSection == "PAYMENT",
                onClick = { activeTabSection = "PAYMENT" },
                label = { Text("💳 UPI & QR Code") },
                leadingIcon = { Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CrimsonPrimary,
                    selectedLabelColor = Color.White
                )
            )
        }

        // ==========================================
        // TAB 1: ACADEMY LOGO MANAGEMENT (CLOUD SYNC)
        // ==========================================
        if (activeTabSection == "LOGO") {
            BromaCard(
                borderColor = CrimsonPrimary.copy(alpha = 0.5f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = CrimsonPrimary)
                    Text(
                        text = "ACADEMY LOGO (CLOUD / DATABASE SYNC)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextNavy
                    )
                }
                Text(
                    text = "The logo uploaded here is stored in Supabase / Cloud storage and updates automatically across all portals (Admin, Student, Coach, Login, and Certificates).",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSlate
                )

                HorizontalDivider(color = BorderLight, modifier = Modifier.padding(vertical = 4.dp))

                // Current Logo Preview Area
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        color = CardWhite,
                        border = BorderStroke(2.dp, CrimsonPrimary),
                        shadowElevation = 4.dp
                    ) {
                        if (!currentLogoUri.isNullOrBlank()) {
                            AsyncImage(
                                model = currentLogoUri,
                                contentDescription = "Current Academy Logo",
                                placeholder = painterResource(id = R.drawable.app_logo),
                                error = painterResource(id = R.drawable.app_logo),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.app_logo),
                                contentDescription = "Default BROMA Official Emblem",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (currentLogoUri.isNullOrBlank()) GoldSecondary.copy(alpha = 0.15f) else BeltGreen.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, if (currentLogoUri.isNullOrBlank()) GoldSecondary else BeltGreen)
                        ) {
                            Text(
                                text = if (currentLogoUri.isNullOrBlank()) "DEFAULT OFFICIAL EMBLEM" else "CUSTOM CLOUD LOGO ACTIVE",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (currentLogoUri.isNullOrBlank()) Color(0xFFB8860B) else BeltGreen
                            )
                        }

                        Text(
                            text = "Last Updated: ${adminSettings?.logoUpdatedTimestamp ?: "Initial Setup"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSlate
                        )
                        Text(
                            text = "Updated By: ${adminSettings?.logoUpdatedBy ?: (currentAdmin?.fullName ?: "Chief Admin")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSlate
                        )
                    }
                }

                // Action Buttons for Logo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { logoPickerLauncher.launch("image/*") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_upload_logo_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (currentLogoUri.isNullOrBlank()) "Upload Logo" else "Change Logo")
                    }

                    if (!currentLogoUri.isNullOrBlank()) {
                        OutlinedButton(
                            onClick = { showRemoveLogoConfirm = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Remove")
                        }
                    }
                }

                // Or Direct URI / Cloud Storage URL input
                var directUrlInput by remember { mutableStateOf("") }
                var showDirectUrlInput by remember { mutableStateOf(false) }

                TextButton(
                    onClick = { showDirectUrlInput = !showDirectUrlInput }
                ) {
                    Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (showDirectUrlInput) "Hide URL Input" else "Or enter Supabase/Web Image URL directly", fontSize = 12.sp)
                }

                if (showDirectUrlInput) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = directUrlInput,
                            onValueChange = { directUrlInput = it },
                            label = { Text("Supabase/Cloud Logo URL") },
                            placeholder = { Text("https://...") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Button(
                            onClick = {
                                if (directUrlInput.isNotBlank()) {
                                    pendingLogoUri = directUrlInput.trim()
                                    showCropFitDialog = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Preview")
                        }
                    }
                }

                if (logoSaveMessage != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = BeltGreen.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, BeltGreen)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BeltGreen)
                            Text(logoSaveMessage ?: "", color = BeltGreen, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }

        // ==========================================
        // TAB 2: IDENTITY & AFFILIATION
        // ==========================================
        if (activeTabSection == "BRANDING") {
            BromaCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = CrimsonPrimary)
                    Text(
                        text = "ACADEMY IDENTITY & RECOGNITION",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextNavy
                    )
                }

                OutlinedTextField(
                    value = academyName,
                    onValueChange = { academyName = it },
                    label = { Text("Full Academy Name") },
                    placeholder = { Text("BRUCELEE RAJ OLYMPIC MARTIALARTS ACADEMY (BROMAA)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = academyShortName,
                        onValueChange = { academyShortName = it },
                        label = { Text("Short Name / Acronym") },
                        placeholder = { Text("BROMAA") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = registrationNumber,
                        onValueChange = { registrationNumber = it },
                        label = { Text("Government Regd. No.") },
                        placeholder = { Text("Regd.MP-23570") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = affiliation,
                    onValueChange = { affiliation = it },
                    label = { Text("Official Affiliation") },
                    placeholder = { Text("AFFILIATED TO MODERN SHITO-RYU KARATE DO ASSOCIATION (MSKA)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = tagline1,
                        onValueChange = { tagline1 = it },
                        label = { Text("Pillar 1 (Left)") },
                        placeholder = { Text("SELF DEFENCE") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = tagline2,
                        onValueChange = { tagline2 = it },
                        label = { Text("Pillar 2 (Right)") },
                        placeholder = { Text("FITNESS") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = admissionsNote,
                    onValueChange = { admissionsNote = it },
                    label = { Text("Admissions Banner Note") },
                    placeholder = { Text("ADMISSIONS OPEN FOR BOYS & GIRLS (Age 3 Years & Above)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // ==========================================
        // TAB 3: CONTACT & DOJO ADDRESS
        // ==========================================
        if (activeTabSection == "CONTACT") {
            BromaCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.ContactPhone, contentDescription = null, tint = CrimsonPrimary)
                    Text(
                        text = "DOJO LOCATION & OFFICIAL CONTACTS",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextNavy
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = academyPhone,
                        onValueChange = { academyPhone = it },
                        label = { Text("Contact Phone 1") },
                        placeholder = { Text("8374632364") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = academyPhone2,
                        onValueChange = { academyPhone2 = it },
                        label = { Text("Contact Phone 2") },
                        placeholder = { Text("6309735840") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = academyEmail,
                    onValueChange = { academyEmail = it },
                    label = { Text("Official Email Address") },
                    placeholder = { Text("info@bromaacademy.com") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = academyAddress,
                    onValueChange = { academyAddress = it },
                    label = { Text("Main Dojo Address") },
                    placeholder = { Text("2ND FLOOR, GEETHA HOSPITAL, NEETU ENUGU BOMMA, KAKATEEYA ITI JUNCTION, BC ROAD, GAJUWAKA") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                OutlinedTextField(
                    value = website,
                    onValueChange = { website = it },
                    label = { Text("Website / Social Links") },
                    placeholder = { Text("www.bromaacademy.org") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // ==========================================
        // TAB 4: 6 BATCHES & FEES OVERVIEW
        // ==========================================
        if (activeTabSection == "SCHEDULE") {
            BromaCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = CrimsonPrimary)
                    Text(
                        text = "OFFICIAL 6-BATCH MASTER SCHEDULE",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextNavy
                    )
                }

                val officialBatches = listOf(
                    Triple("BATCH 1", "05:00 AM – 08:00 AM", "Black Belts • Kata Training"),
                    Triple("BATCH 2", "05:00 AM – 06:30 AM", "Brown Belts • Kata Training"),
                    Triple("BATCH 3", "06:30 AM – 08:00 AM", "Brown Belts • Kumite Sparring"),
                    Triple("BATCH 4", "05:00 PM – 08:00 PM", "Black Belts • Kumite Sparring"),
                    Triple("BATCH 5", "06:00 PM – 07:00 PM", "Colour Belts • Kata Training"),
                    Triple("BATCH 6", "07:00 PM – 08:00 PM", "Colour Belts • Kumite Sparring")
                )

                officialBatches.forEach { (bName, time, desc) ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = SecondaryBg,
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = CrimsonPrimary,
                                modifier = Modifier.padding(2.dp)
                            ) {
                                Text(
                                    text = bName,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(time, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                                Text(desc, style = MaterialTheme.typography.bodySmall, color = TextSlate)
                            }
                        }
                    }
                }

                HorizontalDivider(color = BorderLight, modifier = Modifier.padding(vertical = 4.dp))

                Text(
                    text = "ACADEMY FEE STRUCTURE",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextNavy
                )

                val feeSchedule = listOf(
                    Pair("Admission Fee (One-Time)", "₹2,000"),
                    Pair("Basic Karate Dress / Gi (One-Time)", "₹1,500"),
                    Pair("Regular Training (Self Defence, Fitness & Weapon Training Included)", "₹2,000 / mo"),
                    Pair("Special Training Camp (1 Day - 6 Hours)", "₹5,000"),
                    Pair("Special Training Camp (3 Days - 4 Hours/Day)", "₹10,000"),
                    Pair("Special Training Masterclass (1 Week - 4 Hours/Day)", "₹15,000")
                )

                feeSchedule.forEach { (feeTitle, feeAmt) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(feeTitle, style = MaterialTheme.typography.bodySmall, color = TextNavy, modifier = Modifier.weight(1f))
                        Text(feeAmt, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = CrimsonPrimary)
                    }
                }
            }
        }

        // ==========================================
        // TAB 5: UPI & PAYMENT QR CODE
        // ==========================================
        if (activeTabSection == "PAYMENT") {
            BromaCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = CrimsonPrimary)
                    Text(
                        text = "PAYMENT & QR CODE SETTINGS",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextNavy
                    )
                }

                OutlinedTextField(
                    value = upiId,
                    onValueChange = { upiId = it },
                    label = { Text("Official Academy UPI ID") },
                    placeholder = { Text("bromaacademy@upi") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = paymentPhone,
                        onValueChange = { paymentPhone = it },
                        label = { Text("PhonePe / UPI Number") },
                        placeholder = { Text("8374632364") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = paymentReceiverName,
                        onValueChange = { paymentReceiverName = it },
                        label = { Text("Beneficiary / Receiver Name") },
                        placeholder = { Text("BROMA Martial Arts") },
                        modifier = Modifier.weight(1f)
                    )
                }

                CustomQrUploadSection(
                    currentQrUri = qrCodeUri,
                    onQrUriChange = {
                        qrCodeUri = it
                        savedSuccess = false
                    }
                )

                OutlinedTextField(
                    value = paymentInstructions,
                    onValueChange = { paymentInstructions = it },
                    label = { Text("Payment Instructions for Parents & Students") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Text("LIVE PAYMENT QR CARD PREVIEW", style = MaterialTheme.typography.labelSmall, color = TextSlate)
                UpiQrCodePhotoCard(
                    upiId = upiId,
                    academyName = academyName,
                    qrCodeUri = qrCodeUri,
                    paymentPhone = paymentPhone,
                    paymentReceiverName = paymentReceiverName
                )
            }
        }

        // Global Save / Update Button
        if (savedSuccess) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = BeltGreen.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, BeltGreen)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BeltGreen)
                    Text(
                        text = "✓ Academy Overview & Branding settings saved and synchronized globally!",
                        color = BeltGreen,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        Button(
            onClick = {
                val timestamp = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
                val updated = (adminSettings ?: AdminSettingsEntity()).copy(
                    academyName = academyName,
                    academyShortName = academyShortName,
                    registrationNumber = registrationNumber,
                    affiliation = affiliation,
                    academyPhone = academyPhone,
                    academyPhone2 = academyPhone2,
                    academyEmail = academyEmail,
                    academyAddress = academyAddress,
                    website = website,
                    tagline1 = tagline1,
                    tagline2 = tagline2,
                    admissionsNote = admissionsNote,
                    upiId = upiId,
                    paymentPhone = paymentPhone,
                    paymentReceiverName = paymentReceiverName,
                    qrCodeUri = qrCodeUri,
                    paymentInstructions = paymentInstructions,
                    logoUri = currentLogoUri,
                    logoUpdatedTimestamp = timestamp,
                    logoUpdatedBy = currentAdmin?.fullName ?: "Chief Admin"
                )
                onSaveSettings(updated)
                savedSuccess = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("admin_save_academy_settings_button"),
            colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SAVE & SYNC ACADEMY BRANDING",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }

    // ====================================================
    // CROP / FIT / PREVIEW DIALOG BEFORE SAVING NEW LOGO
    // ====================================================
    if (showCropFitDialog && pendingLogoUri != null) {
        AlertDialog(
            onDismissRequest = { showCropFitDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Crop, contentDescription = null, tint = CrimsonPrimary)
                    Text("Crop / Fit Academy Logo Preview")
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Preview how the new logo will look in headers, badges, certificates, and student portals.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSlate
                    )

                    // Aspect ratio / Scale switcher
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = selectedScaleMode == ContentScale.Crop,
                            onClick = { selectedScaleMode = ContentScale.Crop },
                            label = { Text("Circular Crop", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = selectedScaleMode == ContentScale.Fit,
                            onClick = { selectedScaleMode = ContentScale.Fit },
                            label = { Text("Fit Aspect", fontSize = 11.sp) }
                        )
                    }

                    // Live Interactive Preview Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardWhite)
                            .border(1.dp, BorderLight, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = pendingLogoUri,
                            contentDescription = "New Logo Preview",
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .border(2.dp, CrimsonPrimary, CircleShape),
                            contentScale = selectedScaleMode
                        )
                    }

                    Text(
                        text = "Note: The old logo is preserved until you confirm this new upload.",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSlate
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val timestamp = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
                        currentLogoUri = pendingLogoUri
                        onSaveLogo(pendingLogoUri, currentAdmin?.fullName ?: "Chief Admin")
                        val updated = (adminSettings ?: AdminSettingsEntity()).copy(
                            logoUri = pendingLogoUri,
                            logoUpdatedTimestamp = timestamp,
                            logoUpdatedBy = currentAdmin?.fullName ?: "Chief Admin"
                        )
                        onSaveSettings(updated)
                        showCropFitDialog = false
                        logoSaveMessage = "✓ New Academy logo saved and synchronized to Supabase & all portals!"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
                ) {
                    Text("Apply & Sync Everywhere")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCropFitDialog = false }) {
                    Text("Cancel (Keep Old)")
                }
            }
        )
    }

    // Remove Logo Confirmation Dialog
    if (showRemoveLogoConfirm) {
        AlertDialog(
            onDismissRequest = { showRemoveLogoConfirm = false },
            title = { Text("Reset to Official Emblem?") },
            text = { Text("This will remove the custom logo and revert all portals to the default official BROMA vectorized emblem.") },
            confirmButton = {
                Button(
                    onClick = {
                        val timestamp = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
                        currentLogoUri = null
                        onSaveLogo(null, currentAdmin?.fullName ?: "Chief Admin")
                        val updated = (adminSettings ?: AdminSettingsEntity()).copy(
                            logoUri = null,
                            logoUpdatedTimestamp = timestamp,
                            logoUpdatedBy = currentAdmin?.fullName ?: "Chief Admin"
                        )
                        onSaveSettings(updated)
                        showRemoveLogoConfirm = false
                        logoSaveMessage = "✓ Custom logo removed. Reverted to official default emblem."
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
                ) {
                    Text("Reset to Emblem")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveLogoConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
