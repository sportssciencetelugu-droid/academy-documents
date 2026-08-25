package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// BROMA Academy Official UI Color System (Deep Navy & Royal Blue Academy Identity)

// 1. MAIN COLOR SYSTEM
val DeepNavy = Color(0xFF0F172A)      // Primary Brand / Dark Navy
val SlateBlue = Color(0xFF334155)     // Secondary / Slate Navy
val RoyalBlue = Color(0xFF2563EB)     // Accent / Royal Blue Action
val LightGrayBg = Color(0xFFF8FAFC)   // Main Background
val CardWhite = Color(0xFFFFFFFF)     // Card / Container Background
val SecondaryBg = Color(0xFFF1F5F9)   // Subtle Secondary Container

// TEXT COLORS
val TextNavy = Color(0xFF0F172A)      // Primary Text (High Contrast Dark Navy)
val TextSlate = Color(0xFF64748B)     // Secondary Text (Slate Gray)
val TextMuted = Color(0xFF94A3B8)     // Muted/Caption Text
val TextOnAccent = Color(0xFFFFFFFF)  // Text on Royal Blue or Dark Navy

// BORDERS & DIVIDERS
val BorderLight = Color(0xFFE2E8F0)   // Light Gray Border
val DividerColor = Color(0xFFF1F5F9)  // Subtle Divider

// NAVIGATION
val ActiveNavBg = Color(0xFFEFF6FF)   // Light Blue Background
val ActiveNavText = RoyalBlue         // Royal Blue Icon & Text
val InactiveNavText = Color(0xFF64748B) // Slate Gray

// 8. STATUS COLORS & CONTAINERS (Maximum Readability & Contrast)
val StatusSuccess = Color(0xFF16A34A) // Green: Success / Completed / Paid / Present
val StatusSuccessBg = Color(0xFFDCFCE7)
val StatusSuccessBorder = Color(0xFF86EFAC)
val StatusSuccessText = Color(0xFF14532D)

val StatusWarning = Color(0xFFD97706) // Amber: Warning / Pending / Due / Late
val StatusWarningBg = Color(0xFFFEF3C7)
val StatusWarningBorder = Color(0xFFFCD34D)
val StatusWarningText = Color(0xFF78350F)

val StatusError = Color(0xFFDC2626)   // Red: Error / Overdue / Absent / Failed
val StatusErrorBg = Color(0xFFFEE2E2)
val StatusErrorBorder = Color(0xFFFCA5A5)
val StatusErrorText = Color(0xFF7F1D1D)
val StatusDanger = StatusError

val StatusInfo = Color(0xFF2563EB)    // Royal Blue: Information / Active / Upcoming / Leave
val StatusInfoBg = Color(0xFFEFF6FF)
val StatusInfoBorder = Color(0xFFBFDBFE)
val StatusInfoText = Color(0xFF1E40AF)

val StatusPaid = StatusSuccess
val StatusDue = StatusWarning
val StatusOverdue = StatusError
val StatusPending = StatusWarning

// BELT COLORS
val BeltWhite = Color(0xFFE2E8F0)
val BeltYellow = Color(0xFFEAB308)
val BeltOrange = Color(0xFFF97316)
val BeltGreen = Color(0xFF16A34A)
val BeltBlue = Color(0xFF2563EB)
val BeltPurple = Color(0xFF9333EA)
val BeltBrown = Color(0xFF78350F)
val BeltBlack = Color(0xFF0F172A)

// Compatibility aliases for clean transition
val PrimaryNavy = DeepNavy
val NavyPrimary = DeepNavy
val PrimaryRed = RoyalBlue
val DeepMartialRed = DeepNavy
val DarkRed = SlateBlue
val CrimsonPrimary = RoyalBlue
val CrimsonLight = Color(0xFF60A5FA)
val CrimsonDark = DeepNavy
val GoldSecondary = RoyalBlue
val GoldDark = SlateBlue

val MainBackground = LightGrayBg
val CardBackground = CardWhite
val SecondaryBackground = SecondaryBg
val DrawerBackground = CardWhite

val DarkBackground = LightGrayBg
val DarkSurface = CardWhite
val DarkSurfaceVariant = SecondaryBg

val LightBackground = LightGrayBg
val LightSurface = CardWhite

val TextPrimary = TextNavy
val TextSecondary = TextSlate
val TextOnRed = TextOnAccent
val DefaultBorder = BorderLight
