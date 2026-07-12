package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun HowToPlayScreen(
    onComplete: () -> Unit,
    isFirstLaunch: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CarbonDark)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .testTag("how_to_play_root"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isFirstLaunch) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onComplete,
                    modifier = Modifier.testTag("close_how_to_play_icon")
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Close how to play", tint = NoirAmber)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 680.dp)
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = if (isFirstLaunch) 24.dp else 8.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            TutorialHeader()
            TutorialStep(
                title = "1. READ THE CASE FILE",
                body = "Use the Cast and Dossier tabs to review the suspects, evidence, witness statements, and verified clues.",
                icon = Icons.Filled.Description
            ) { CaseFileVisual() }
            TutorialStep(
                title = "2. MARK THE LOGIC GRID",
                body = "Tap a playable cell to cycle through its marks.",
                icon = Icons.Filled.GridOn
            ) { GridMarkDemo() }
            TutorialStep(
                title = "3. COMPLETE THE MATCHES",
                body = "Each case has three suspects, three weapons, and three locations. Every suspect must match exactly one weapon and one location.\n\nThe grid compares Location × Suspect, Location × Weapon, and Weapon × Suspect.\n\nConfirming a match automatically rules out conflicting cells in that grid section.",
                icon = Icons.Filled.Search
            ) { MatchDiagram() }
            TutorialStep(
                title = "4. MAKE YOUR ACCUSATION",
                body = "Open the Deduce tab and select the culprit, weapon, and location. In liar cases, also identify the suspect who made the false statement.",
                icon = Icons.Filled.Gavel
            ) { AccusationDemo() }
            TipCard()
            Button(
                onClick = onComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag(if (isFirstLaunch) "open_case_files_button" else "close_how_to_play_button"),
                colors = ButtonDefaults.buttonColors(containerColor = NoirAmber, contentColor = CarbonDark),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (isFirstLaunch) "OPEN THE CASE FILES" else "BACK TO CASE FILES",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TutorialHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(CircleShape)
                .background(NoirAmber.copy(alpha = 0.12f))
                .border(1.dp, NoirAmber.copy(alpha = 0.65f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.HelpOutline, contentDescription = null, tint = NoirAmber, modifier = Modifier.size(34.dp))
        }
        Text(
            text = "DETECTIVE TRAINING FILE",
            color = MutedGrey,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = "HOW TO PLAY",
            color = NoirAmber,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Match every suspect to one weapon and one location, then identify the culprit.",
            color = SlateGrey,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        )
    }
}

@Composable
private fun TutorialStep(
    title: String,
    body: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
        border = BorderStroke(1.dp, NoirAmber.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(NoirAmber.copy(alpha = 0.14f))
                        .border(1.dp, NoirAmber.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = NoirAmber)
                }
                Text(
                    text = title,
                    color = GridWhite,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }
            Text(body, color = SlateGrey, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.fillMaxWidth())
            content()
        }
    }
}

@Composable
private fun CaseFileVisual() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            MiniFileCard("CAST", Icons.Filled.Person, Modifier.weight(1f))
            MiniFileCard("DOSSIER", Icons.Filled.Folder, Modifier.weight(1f))
        }
        Text("People → Clues → Deductions", color = NoirAmber, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}

@Composable
private fun MiniFileCard(label: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SlateCard)
            .border(1.dp, NoirAmber.copy(alpha = 0.55f), RoundedCornerShape(12.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = NoirAmber, modifier = Modifier.size(28.dp))
        Text(label, color = GridWhite, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun GridMarkDemo() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            GridStateCell("", "BLANK", "UNKNOWN")
            Text("↓", color = NoirAmber, fontSize = 22.sp)
            GridStateCell("X", "X", "IMPOSSIBLE", BloodRed)
            Text("↓", color = NoirAmber, fontSize = 22.sp)
            GridStateCell("O", "O", "CONFIRMED", ClueGreen)
        }
        Text("X means the pairing is impossible. O means the pairing is confirmed.", color = MutedGrey, fontFamily = FontFamily.Monospace, fontSize = 12.sp, textAlign = TextAlign.Center)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            PairingExample("STUDY × VICTOR", "X", BloodRed, Modifier.weight(1f))
            PairingExample("STUDY × ELEANOR", "O", ClueGreen, Modifier.weight(1f))
        }
    }
}

@Composable
private fun GridStateCell(mark: String, title: String, label: String, markColor: Color = MutedGrey) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(SelectedBox)
                .border(1.dp, NoirAmber.copy(alpha = 0.65f), RoundedCornerShape(9.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(if (mark.isBlank()) "·" else mark, color = markColor, fontWeight = FontWeight.Bold, fontSize = 28.sp)
        }
        Text(title, color = GridWhite, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center)
        Text(label, color = MutedGrey, fontFamily = FontFamily.Monospace, fontSize = 11.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun PairingExample(label: String, mark: String, markColor: Color, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(SlateCard)
            .border(1.dp, NoirAmber.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier.size(34.dp).clip(RoundedCornerShape(6.dp)).background(SelectedBox),
            contentAlignment = Alignment.Center
        ) {
            Text(mark, color = markColor, fontWeight = FontWeight.Bold)
        }
        Text(label, color = GridWhite, fontFamily = FontFamily.Monospace, fontSize = 11.sp, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun MatchDiagram() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            RelationshipCard("SUSPECT", Icons.Filled.Person, Modifier.weight(1f))
            RelationshipCard("WEAPON", Icons.Filled.Search, Modifier.weight(1f))
            RelationshipCard("LOCATION", Icons.Filled.Home, Modifier.weight(1f))
        }
        Text("1 suspect = 1 weapon = 1 location", color = NoirAmber, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MiniCell("O", ClueGreen)
            Text("confirmed", color = SlateGrey, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
            Text("→", color = NoirAmber, fontSize = 20.sp)
            MiniCell("X", BloodRed)
            MiniCell("X", BloodRed)
        }
        Text("Other cells in that row or column become X.", color = MutedGrey, fontFamily = FontFamily.Monospace, fontSize = 12.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun RelationshipCard(label: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SlateCard)
            .border(1.dp, NoirAmber.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(icon, contentDescription = null, tint = NoirAmber, modifier = Modifier.size(24.dp))
        Text(label, color = GridWhite, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun MiniCell(mark: String, color: Color) {
    Box(
        modifier = Modifier.size(32.dp).clip(RoundedCornerShape(6.dp)).background(SelectedBox).border(1.dp, color.copy(alpha = 0.65f), RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(mark, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun AccusationDemo() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            AccusationPanel("NORMAL CASE", listOf("CULPRIT", "WEAPON", "LOCATION"), Modifier.weight(1f))
            AccusationPanel("LIAR CASE", listOf("CULPRIT", "WEAPON", "LOCATION", "LIAR"), Modifier.weight(1f))
        }
    }
}

@Composable
private fun AccusationPanel(title: String, rows: List<String>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SlateCard)
            .border(1.dp, NoirAmber.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Text(title, color = NoirAmber, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center)
        rows.forEach { label ->
            Text(
                text = label,
                color = GridWhite,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CarbonDark)
                    .border(1.dp, MutedGrey.copy(alpha = 0.28f), RoundedCornerShape(8.dp))
                    .padding(vertical = 7.dp, horizontal = 4.dp)
            )
        }
        Icon(Icons.Filled.Gavel, contentDescription = null, tint = ClueGreen, modifier = Modifier.size(24.dp))
    }
}

@Composable
private fun TipCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = BorderStroke(1.dp, NoirAmber.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Filled.Info, contentDescription = null, tint = NoirAmber)
            Text("CASE NOTE", color = NoirAmber, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text("Checking off clues only tracks your progress. It does not change the solution.", color = SlateGrey, textAlign = TextAlign.Center)
        }
    }
}
