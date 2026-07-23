package com.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CarbonDark
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.GridWhite
import com.example.ui.theme.NoirAmber
import com.example.ui.theme.SlateGrey

@Composable
fun FirstLaunchGuideDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CharcoalSurface,
        titleContentColor = NoirAmber,
        textContentColor = GridWhite,
        title = {
            Text(
                text = "QUICK GUIDE",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "You can always find these controls in the top-right corner of the case-files screen.",
                    color = SlateGrey,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                GuideItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = null,
                            tint = NoirAmber,
                            modifier = Modifier.size(30.dp)
                        )
                    },
                    title = "How to Play",
                    description = "Open the detective training guide whenever you need a reminder."
                )

                GuideItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Policy,
                            contentDescription = null,
                            tint = NoirAmber,
                            modifier = Modifier.size(30.dp)
                        )
                    },
                    title = "Privacy Policy",
                    description = "Review the app's privacy information at any time."
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NoirAmber,
                    contentColor = CarbonDark
                )
            ) {
                Text(
                    text = "GOT IT",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

@Composable
private fun GuideItem(
    icon: @Composable () -> Unit,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        icon()
        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = GridWhite,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                color = SlateGrey,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
