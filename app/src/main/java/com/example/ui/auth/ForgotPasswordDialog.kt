package com.example.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun ForgotPasswordDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.LockReset, contentDescription = "Forgot Password") },
        title = { Text("Reset Password") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (!submitted) {
                    Text("Enter your registered email address or username. We will send password reset instructions to your email.")
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email or Username") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("forgot_password_email_input"),
                        singleLine = true
                    )
                } else {
                    Text("If an account exists for '$email', password reset instructions have been sent! Please check your inbox.")
                }
            }
        },
        confirmButton = {
            if (!submitted) {
                Button(
                    onClick = {
                        if (email.isNotBlank()) {
                            submitted = true
                            onSubmit(email)
                        }
                    },
                    modifier = Modifier.testTag("send_reset_button")
                ) {
                    Text("Send Reset Link")
                }
            } else {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_reset_button")
                ) {
                    Text("Close")
                }
            }
        },
        dismissButton = {
            if (!submitted) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("cancel_reset_button")
                ) {
                    Text("Cancel")
                }
            }
        }
    )
}
