package com.example.ui.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter

/**
 * Official BROMA Academy Logo Component
 * Dynamically displays the custom academy logo stored in cloud/database,
 * falling back safely to the official vectorized emblem.
 */
@Composable
fun BromaAcademyLogo(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    showBorder: Boolean = true,
    borderColor: Color = BorderLight,
    logoUri: String? = null
) {
    Surface(
        modifier = modifier.size(size),
        shape = CircleShape,
        color = CardWhite,
        border = if (showBorder) androidx.compose.foundation.BorderStroke(1.dp, borderColor) else null
    ) {
        if (!logoUri.isNullOrBlank()) {
            AsyncImage(
                model = logoUri,
                contentDescription = "Bruce Lee Raj Olympic Martial Arts Academy Logo",
                placeholder = painterResource(id = R.drawable.app_logo),
                error = painterResource(id = R.drawable.app_logo),
                fallback = painterResource(id = R.drawable.app_logo),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "Bruce Lee Raj Olympic Martial Arts Academy Logo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}

/**
 * Standard BROMA Premium Clean Card Container
 */
@Composable
fun BromaCard(
    modifier: Modifier = Modifier,
    borderColor: Color = BorderLight,
    backgroundColor: Color = CardWhite,
    shape: RoundedCornerShape = RoundedCornerShape(14.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        color = backgroundColor,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content
        )
    }
}

/**
 * Standard BROMA Primary Action Button (Royal Blue #2563EB, White Text)
 */
@Composable
fun BromaPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    testTagStr: String? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .then(if (testTagStr != null) Modifier.testTag(testTagStr) else Modifier),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = RoyalBlue,
            contentColor = TextOnAccent,
            disabledContainerColor = Color(0xFFE2E8F0),
            disabledContentColor = Color(0xFF94A3B8)
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = TextOnAccent)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text.uppercase(),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = TextOnAccent
                )
            )
        }
    }
}

/**
 * Standard BROMA Secondary Outlined Button (White Bg, Navy/Slate Text, Light Gray Border)
 */
@Composable
fun BromaSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    testTagStr: String? = null
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .then(if (testTagStr != null) Modifier.testTag(testTagStr) else Modifier),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = CardWhite,
            contentColor = TextNavy
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = RoyalBlue)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextNavy
                )
            )
        }
    }
}

/**
 * Danger Button (Red #DC2626 for destructive actions)
 */
@Composable
fun BromaDangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.defaultMinSize(minHeight = 48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = StatusError,
            contentColor = TextOnAccent,
            disabledContainerColor = Color(0xFFE2E8F0),
            disabledContentColor = Color(0xFF94A3B8)
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = TextOnAccent)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text.uppercase(),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = TextOnAccent
                )
            )
        }
    }
}

/**
 * BROMA Status Badge with Semantic Status Colors
 */
@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, fgColor, icon) = when (status.uppercase()) {
        "PAID", "ACTIVE", "APPROVED", "VERIFIED", "PRESENT", "REGISTERED", "SUCCESS" ->
            Triple(StatusSuccess.copy(alpha = 0.12f), StatusSuccess, "🟢")
        "DUE", "PENDING", "PENDING_APPROVAL", "LATE", "OPEN", "WARNING" ->
            Triple(StatusWarning.copy(alpha = 0.15f), StatusWarning, "⏳")
        "OVERDUE", "REJECTED", "DEACTIVATED", "ABSENT", "FAILED", "ERROR" ->
            Triple(StatusError.copy(alpha = 0.12f), StatusError, "🔴")
        "LEAVE", "INFO", "UPCOMING" ->
            Triple(StatusInfo.copy(alpha = 0.12f), StatusInfo, "🔵")
        else ->
            Triple(SecondaryBg, TextSlate, "ℹ️")
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, fgColor.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(icon, fontSize = 10.sp)
            Text(
                text = status.replace("_", " ").uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                color = fgColor
            )
        }
    }
}

/**
 * Universal Empty State Card
 */
@Composable
fun EmptyStateCard(
    title: String,
    description: String,
    icon: ImageVector = Icons.Default.Info,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = CardWhite,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(ActiveNavBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(28.dp))
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextNavy,
                textAlign = TextAlign.Center
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSlate,
                textAlign = TextAlign.Center
            )

            if (actionLabel != null && onActionClick != null) {
                Spacer(modifier = Modifier.height(4.dp))
                BromaSecondaryButton(text = actionLabel, onClick = onActionClick)
            }
        }
    }
}

/**
 * Universal Skeleton Loader for Lists/Cards
 */
@Composable
fun SkeletonLoader(
    modifier: Modifier = Modifier,
    height: Dp = 80.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "skeletonAlpha"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        shape = RoundedCornerShape(14.dp),
        color = SecondaryBg.copy(alpha = alpha),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {}
}

/**
 * Universal Error State Card with Retry Action
 */
@Composable
fun ErrorStateCard(
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = StatusError.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, StatusError.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = StatusError)
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextNavy
                )
            }
            IconButton(onClick = onRetry) {
                Icon(Icons.Default.Refresh, contentDescription = "Retry", tint = RoyalBlue)
            }
        }
    }
}

/**
 * Confirmation Modal for Destructive or Critical Actions
 */
@Composable
fun ConfirmationModal(
    title: String,
    message: String,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    isDanger: Boolean = true,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextNavy
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSlate
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDanger) StatusError else RoyalBlue,
                    contentColor = TextOnAccent
                )
            ) {
                Text(confirmText.uppercase(), fontWeight = FontWeight.Bold, color = TextOnAccent)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText, color = TextSlate, fontWeight = FontWeight.Medium)
            }
        },
        containerColor = CardWhite,
        shape = RoundedCornerShape(16.dp)
    )
}
