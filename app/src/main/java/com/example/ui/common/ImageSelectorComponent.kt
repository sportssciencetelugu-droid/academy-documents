package com.example.ui.common

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.ui.theme.*

data class PresetPhoto(
    val title: String,
    val category: String,
    val url: String
)

val GALLERY_PRESET_PHOTOS = listOf(
    PresetPhoto("Official Black Belt Certificate", "Certificates", "https://images.unsplash.com/photo-1555597673-b21d5c935865?w=800&auto=format&fit=crop&q=60"),
    PresetPhoto("Tournament Gold Medal", "Achievements", "https://images.unsplash.com/photo-1569517282132-25d22f4573e6?w=800&auto=format&fit=crop&q=60"),
    PresetPhoto("Martial Arts Kata Action", "Profile", "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=800&auto=format&fit=crop&q=60"),
    PresetPhoto("Sensei Master Profile", "Profile", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800&auto=format&fit=crop&q=60"),
    PresetPhoto("National Kumite Championship Poster", "Tournaments", "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=800&auto=format&fit=crop&q=60"),
    PresetPhoto("Kata Masterclass & Seminar Poster", "Calendar", "https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=800&auto=format&fit=crop&q=60"),
    PresetPhoto("Belt Grading Examination Poster", "Calendar", "https://images.unsplash.com/photo-1517649763962-0c623266ddc0?w=800&auto=format&fit=crop&q=60"),
    PresetPhoto("Official Academy Banner", "Announcements", "https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91?w=800&auto=format&fit=crop&q=60"),
    PresetPhoto("Special Boot Camp Poster", "Announcements", "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=800&auto=format&fit=crop&q=60"),
    PresetPhoto("State Level Trophy", "Achievements", "https://images.unsplash.com/photo-1579952363873-27f3bade9f55?w=800&auto=format&fit=crop&q=60"),
    PresetPhoto("Payment Transaction Receipt Sample", "Payments", "https://images.unsplash.com/photo-1554224155-6726b3ff858f?w=800&auto=format&fit=crop&q=60")
)

@Composable
fun PhotoPickerSelector(
    selectedImageUri: String?,
    onImageSelected: (String) -> Unit,
    label: String = "ATTACH PHOTO / DOCUMENT",
    categoryHint: String = "All"
) {
    var showDialog by remember { mutableStateOf(false) }

    // Direct Gallery Picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onImageSelected(it.toString())
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
            color = RoyalBlue
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardWhite)
                .border(1.dp, if (!selectedImageUri.isNullOrBlank()) RoyalBlue else BorderLight, RoundedCornerShape(12.dp))
                .clickable { showDialog = true }
                .testTag("photo_picker_trigger_button"),
            color = CardWhite,
            shadowElevation = 1.dp
        ) {
            if (!selectedImageUri.isNullOrBlank()) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(selectedImageUri),
                            contentDescription = "Selected Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, BorderLight, RoundedCornerShape(8.dp))
                        )
                        Column {
                            Text(
                                "✓ Photo Attached",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = TextNavy
                            )
                            Text(
                                selectedImageUri.take(35) + if (selectedImageUri.length > 35) "..." else "",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSlate
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = "Change Photo", tint = RoyalBlue)
                        }
                        IconButton(
                            onClick = { onImageSelected("") },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Remove Photo", tint = StatusError)
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(22.dp))
                        Text(
                            "Choose Photo (Phone Gallery or Presets)",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = RoyalBlue
                        )
                    }

                    Button(
                        onClick = { galleryLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryBg),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("direct_phone_gallery_button")
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = TextNavy, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Gallery", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                    }
                }
            }
        }
    }

    if (showDialog) {
        var customUriText by remember { mutableStateOf(selectedImageUri ?: "") }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "📷 SELECT / ATTACH PHOTO",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextNavy
                    )
                    IconButton(onClick = { showDialog = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSlate)
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 480.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Option 1: Phone Gallery Picker
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                galleryLauncher.launch("image/*")
                                showDialog = false
                            },
                        shape = RoundedCornerShape(10.dp),
                        color = ActiveNavBg,
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, RoyalBlue)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(RoyalBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = TextOnAccent, modifier = Modifier.size(22.dp))
                            }
                            Column {
                                Text(
                                    "Open Phone Gallery",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = RoyalBlue
                                )
                                Text(
                                    "Choose any photo, certificate or receipt from your device",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSlate
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = BorderLight)

                    // Option 2: Enter Custom File URL or content URI
                    Text("Or Enter Image URL / File Path:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)
                    OutlinedTextField(
                        value = customUriText,
                        onValueChange = { customUriText = it },
                        placeholder = { Text("https://... or content://media/...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_photo_url_input"),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalBlue,
                            unfocusedBorderColor = BorderLight,
                            focusedTextColor = TextNavy,
                            unfocusedTextColor = TextNavy
                        ),
                        trailingIcon = {
                            if (customUriText.isNotBlank()) {
                                IconButton(onClick = {
                                    onImageSelected(customUriText)
                                    showDialog = false
                                }) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Apply", tint = RoyalBlue)
                                }
                            }
                        }
                    )

                    HorizontalDivider(color = BorderLight)

                    // Option 3: Academy High-Res Presets
                    Text("Or Choose From Academy Presets:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextNavy)

                    val filteredPresets = if (categoryHint != "All") {
                        GALLERY_PRESET_PHOTOS.filter { it.category.contains(categoryHint, ignoreCase = true) || categoryHint == "All" }
                    } else GALLERY_PRESET_PHOTOS

                    val presetsToDisplay = if (filteredPresets.isEmpty()) GALLERY_PRESET_PHOTOS else filteredPresets

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        presetsToDisplay.chunked(2).forEach { rowPresets ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowPresets.forEach { preset ->
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                onImageSelected(preset.url)
                                                showDialog = false
                                            },
                                        color = SecondaryBg,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                                    ) {
                                        Column(modifier = Modifier.padding(6.dp)) {
                                            Image(
                                                painter = rememberAsyncImagePainter(preset.url),
                                                contentDescription = preset.title,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(75.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                preset.title,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                                color = TextNavy,
                                                maxLines = 1
                                            )
                                            Text(
                                                preset.category,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = RoyalBlue,
                                                fontSize = 9.sp
                                            )
                                        }
                                    }
                                }
                                if (rowPresets.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customUriText.isNotBlank()) {
                            onImageSelected(customUriText)
                        }
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Done", color = TextOnAccent)
                }
            }
        )
    }
}
