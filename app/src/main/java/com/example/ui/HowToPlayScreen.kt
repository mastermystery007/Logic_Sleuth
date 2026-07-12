package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun HowToPlayScreen(onComplete: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CarbonDark)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .testTag("how_to_play_root"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("HOW TO PLAY", color = NoirAmber, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 28.sp, letterSpacing = 2.sp)
        Text("Match every suspect to one weapon and one location, then identify the culprit.", color = SlateGrey)
        TutorialStep("1. READ THE CASE FILE", "Use the Cast and Dossier tabs to review the suspects, evidence, witness statements, and verified clues.", Icons.Filled.Description)
        TutorialStep("2. MARK THE LOGIC GRID", "Tap a playable cell to cycle through its marks.", Icons.Filled.GridOn) { GridMarkDemo() }
        TutorialStep("3. COMPLETE THE MATCHES", "Each case has three suspects, three weapons, and three locations. Every suspect must match exactly one weapon and one location.\n\nThe grid compares Location × Suspect, Location × Weapon, and Weapon × Suspect.\n\nConfirming a match automatically rules out conflicting cells in that grid section.", Icons.Filled.Search)
        TutorialStep("4. MAKE YOUR ACCUSATION", "Open the Deduce tab and select the culprit, weapon, and location. In liar cases, also identify the suspect who made the false statement.", Icons.Filled.Gavel) { AccusationDemo() }
        Card(colors = CardDefaults.cardColors(containerColor = SlateCard), shape = RoundedCornerShape(14.dp)) {
            Text("Checking off clues only tracks your progress. It does not change the solution.", color = NoirAmber, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, modifier = Modifier.padding(14.dp))
        }
        Button(
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth().height(54.dp).testTag("open_case_files_button"),
            colors = ButtonDefaults.buttonColors(containerColor = NoirAmber, contentColor = CarbonDark)
        ) { Text("OPEN THE CASE FILES", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun TutorialStep(title: String, body: String, icon: androidx.compose.ui.graphics.vector.ImageVector, content: @Composable ColumnScope.() -> Unit = {}) {
    Card(colors = CardDefaults.cardColors(containerColor = CharcoalSurface), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(icon, contentDescription = null, tint = NoirAmber)
                Text(title, color = GridWhite, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            }
            Text(body, color = SlateGrey, style = MaterialTheme.typography.bodyMedium)
            content()
        }
    }
}

@Composable
private fun GridMarkDemo() {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
        SampleCell("", "UNKNOWN")
        Text("→", color = NoirAmber, fontSize = 22.sp)
        SampleCell("X", "RULED OUT", BloodRed)
        Text("→", color = NoirAmber, fontSize = 22.sp)
        SampleCell("O", "MATCH", ClueGreen)
    }
    Text("X means the pairing is impossible.\nO means the pairing is confirmed.", color = MutedGrey, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
}

@Composable
private fun SampleCell(mark: String, label: String, markColor: Color = MutedGrey) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(Modifier.size(42.dp).clip(RoundedCornerShape(6.dp)).background(SelectedBox).border(1.dp, NoirAmber.copy(alpha = 0.6f), RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) {
            Text(mark, color = markColor, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        Text(label, color = MutedGrey, fontSize = 9.sp, fontFamily = FontFamily.Monospace, textAlign = TextAlign.Center)
    }
}

@Composable
private fun AccusationDemo() {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        AccusationCard("NORMAL CASE", "Culprit + Weapon + Location", Modifier.weight(1f))
        AccusationCard("LIAR CASE", "Culprit + Weapon + Location + Liar", Modifier.weight(1f))
    }
}

@Composable
private fun AccusationCard(title: String, body: String, modifier: Modifier) {
    Column(modifier.clip(RoundedCornerShape(10.dp)).background(SlateCard).border(1.dp, NoirAmber.copy(alpha = 0.35f), RoundedCornerShape(10.dp)).padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, color = NoirAmber, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        Text(body, color = GridWhite, fontSize = 12.sp)
    }
}
