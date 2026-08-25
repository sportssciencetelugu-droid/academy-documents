package com.example.ui.admin

import androidx.compose.foundation.background
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
import com.example.data.AdminSettingsEntity
import com.example.ui.theme.*

@Composable
fun AdminContactSettingsView(
    adminSettings: AdminSettingsEntity?,
    onSaveContact: (String, String, String, String, String, String, String, String, String, String, String, String) -> Unit
) {
    var academyName by remember { mutableStateOf(adminSettings?.academyName ?: "BROMA Academy of Martial Arts") }
    var shortName by remember { mutableStateOf(adminSettings?.academyShortName ?: "BROMA") }
    var regdNo by remember { mutableStateOf(adminSettings?.registrationNumber ?: "HYD/ACT/2026/8941") }
    var affiliation by remember { mutableStateOf(adminSettings?.affiliation ?: "World Karate Federation (WKF) & Karate India Org (KIO)") }
    var phone1 by remember { mutableStateOf(adminSettings?.academyPhone ?: "+91 98480 22338") }
    var phone2 by remember { mutableStateOf(adminSettings?.academyPhone2 ?: "+91 98480 99887") }
    var email by remember { mutableStateOf(adminSettings?.academyEmail ?: "contact@broma-academy.com") }
    var address by remember { mutableStateOf(adminSettings?.academyAddress ?: "Plot No. 42, Road No. 12, Banjara Hills, Hyderabad, Telangana - 500034") }
    var website by remember { mutableStateOf(adminSettings?.website ?: "https://broma.academy") }
    var tagline1 by remember { mutableStateOf(adminSettings?.tagline1 ?: "Discipline • Honor • Excellence") }
    var tagline2 by remember { mutableStateOf(adminSettings?.tagline2 ?: "Traditional Martial Arts Training Since 2010") }
    var admissionsNote by remember { mutableStateOf(adminSettings?.admissionsNote ?: "Admissions open for batch 2026-2027. Contact sensei at dojo front desk.") }

    var saveMessage by remember { mutableStateOf<String?>(null) }

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
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Contacts, contentDescription = null, tint = RoyalBlue)
                    Text(
                        text = "ACADEMY CONTACT & OVERVIEW SETTINGS",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextNavy
                    )
                }
                Text(
                    text = "Update official academy headquarters address, contact numbers, official email, registration & affiliation info. Real-time updates automatically push to all student and coach portals.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSlate
                )
            }
        }

        if (saveMessage != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = StatusSuccessBg,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccessBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusSuccess)
                    Text(saveMessage!!, style = MaterialTheme.typography.bodySmall, color = StatusSuccessText)
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = CardWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Official Academy Identity", fontWeight = FontWeight.Bold, color = TextNavy)

                OutlinedTextField(
                    value = academyName,
                    onValueChange = { academyName = it },
                    label = { Text("Full Academy Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = shortName,
                        onValueChange = { shortName = it },
                        label = { Text("Short Name") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = regdNo,
                        onValueChange = { regdNo = it },
                        label = { Text("Govt. Regd No.") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                OutlinedTextField(
                    value = affiliation,
                    onValueChange = { affiliation = it },
                    label = { Text("Federation / Affiliation") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                HorizontalDivider(color = BorderLight)
                Text("Contact Channels", fontWeight = FontWeight.Bold, color = TextNavy)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = phone1,
                        onValueChange = { phone1 = it },
                        label = { Text("Primary Phone *") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = phone2,
                        onValueChange = { phone2 = it },
                        label = { Text("Secondary Phone / Helpline") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Official Email Address *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = website,
                    onValueChange = { website = it },
                    label = { Text("Official Website URL") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Main Dojo & Academy Address *") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                HorizontalDivider(color = BorderLight)
                Text("Branding & Notice", fontWeight = FontWeight.Bold, color = TextNavy)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = tagline1,
                        onValueChange = { tagline1 = it },
                        label = { Text("Primary Tagline") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = tagline2,
                        onValueChange = { tagline2 = it },
                        label = { Text("Secondary Tagline") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                OutlinedTextField(
                    value = admissionsNote,
                    onValueChange = { admissionsNote = it },
                    label = { Text("Admissions Notice / Dojo Note") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Button(
                    onClick = {
                        onSaveContact(
                            academyName, shortName, regdNo, affiliation,
                            phone1, phone2, email, address, website,
                            tagline1, tagline2, admissionsNote
                        )
                        saveMessage = "Academy contact details and overview updated! Synced to all student and coach portals."
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_academy_contact_button")
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save & Sync Academy Contact Details")
                }
            }
        }
    }
}
