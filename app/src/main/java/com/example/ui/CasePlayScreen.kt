package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Case
import com.example.data.Statement
import com.example.ui.theme.*
import com.example.viewmodel.AccusationResult
import com.example.viewmodel.DetectiveViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CasePlayScreen(
    viewModel: DetectiveViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val case by viewModel.activeCase.collectAsState()
    val gridState by viewModel.activeGrid.collectAsState()
    val notesText by viewModel.activeNotes.collectAsState()
    val isCompleted by viewModel.isActiveCaseCompleted.collectAsState()
    val chosenSuspect by viewModel.chosenSuspect.collectAsState()
    val chosenWeapon by viewModel.chosenWeapon.collectAsState()
    val chosenLocation by viewModel.chosenLocation.collectAsState()
    val accusationResult by viewModel.accusationResult.collectAsState()

    // Return early if no case selected
    val activeCase = case ?: return

    var activeTab by remember { mutableStateOf("Grid") }
    var focusedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var showExplanationDialog by remember { mutableStateOf(false) }

    // Synchronize local notes state with database
    var localNotesText by remember(notesText) { mutableStateOf(notesText) }

    LaunchedEffect(notesText) {
        localNotesText = notesText
    }

    if (accusationResult == AccusationResult.Success) {
        showExplanationDialog = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "CASE #${activeCase.id}",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = NoirAmber,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = activeCase.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GridWhite
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back back arrow",
                            tint = GridWhite
                        )
                    }
                },
                actions = {
                    if (isCompleted) {
                        IconButton(onClick = { viewModel.clearCaseCompletion() }) {
                            Icon(
                                imageVector = Icons.Default.LockOpen,
                                contentDescription = "Reset completion status",
                                tint = ClueGreen
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CarbonDark
                )
            )
        },
        containerColor = CarbonDark,
        modifier = modifier.testTag("case_play_root")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Adaptive Header showing difficulty and metadata
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val badgeColor = when (activeCase.difficulty) {
                        "Easy" -> ClueGreen
                        "Medium" -> NoirAmber
                        "Hard" -> BloodRed
                        else -> GridWhite
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(badgeColor.copy(alpha = 0.15f))
                            .border(1.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${activeCase.difficulty.uppercase()} DIFFICULTY",
                            color = badgeColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    if (activeCase.hasLiar) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(BloodRed.copy(alpha = 0.15f))
                                .border(1.dp, BloodRed.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "LIAR SUSPECTS",
                                color = BloodRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                if (isCompleted) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Case Solved Checked",
                            tint = ClueGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "SOLVED",
                            color = ClueGreen,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Tab bar for gameplay modes
            val tabs = listOf("Grid", "Case File", "Interrogations", "Accuse")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SlateCard)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                tabs.forEach { tabName ->
                    val isSelected = tabName == activeTab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) NoirAmber else Color.Transparent)
                            .clickable { activeTab = tabName }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tabName.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.Black else MutedGrey,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Switcher Content
            when (activeTab) {
                "Grid" -> {
                    LogicGridTab(
                        case = activeCase,
                        gridState = gridState,
                        focusedCell = focusedCell,
                        onCellClick = { r, c ->
                            focusedCell = Pair(r, c)
                            viewModel.toggleGridCell(r, c)
                        },
                        onResetGrid = { viewModel.resetGrid() }
                    )
                }
                "Case File" -> {
                    CaseFileTab(
                        case = activeCase,
                        notesText = localNotesText,
                        onNotesChange = { text ->
                            localNotesText = text
                            viewModel.saveNotes(text)
                        }
                    )
                }
                "Interrogations" -> {
                    InterrogationTab(case = activeCase)
                }
                "Accuse" -> {
                    AccusationTab(
                        case = activeCase,
                        viewModel = viewModel,
                        chosenSuspect = chosenSuspect,
                        chosenWeapon = chosenWeapon,
                        chosenLocation = chosenLocation,
                        accusationResult = accusationResult,
                        isCaseCompleted = isCompleted,
                        onShowExplanation = { showExplanationDialog = true }
                    )
                }
            }
        }
    }

    // Success dialog or resolution summary
    if (showExplanationDialog) {
        AlertDialog(
            onDismissRequest = { showExplanationDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success Star",
                        tint = ClueGreen,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "CASE DECLASSIFIED!",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = ClueGreen
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Congratulations, Detective! You solved it correctly.",
                        style = MaterialTheme.typography.titleMedium,
                        color = GridWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Divider(color = Color(0x33B0BEC5))
                    Text(
                        text = activeCase.murderExplanation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateGrey
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showExplanationDialog = false }
                ) {
                    Text("CLOSE FILE", color = NoirAmber, fontFamily = FontFamily.Monospace)
                }
            },
            containerColor = CharcoalSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

// Logic Grid Sub-component
@Composable
fun LogicGridTab(
    case: Case,
    gridState: Map<Pair<Int, Int>, String>,
    focusedCell: Pair<Int, Int>?,
    onCellClick: (row: Int, col: Int) -> Unit,
    onResetGrid: () -> Unit
) {
    val rowHeaders = listOf("🔪X", "🔪Y", "🔪Z", "🏛️D", "🏛️E", "🏛️F")
    val colHeaders = listOf("👤A", "👤B", "👤C", "🏛️D", "🏛️E", "🏛️F")

    Column(modifier = Modifier.fillMaxWidth()) {
        // Focus Cell Inspector Panel
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(containerColor = CharcoalSurface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x1AFFB300))
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Focus info icon",
                        tint = NoirAmber,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = "CELL INSPECTOR",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = MutedGrey
                    )

                    if (focusedCell != null) {
                        val rowIdx = focusedCell.first
                        val colIdx = focusedCell.second
                        val rowLabel = if (rowIdx in 0..2) "Weapon: " + case.weapons[rowIdx] else "Location: " + case.locations[rowIdx - 3]
                        val colLabel = if (colIdx in 0..2) "Suspect: " + case.suspects[colIdx] else "Location: " + case.locations[colIdx - 3]
                        val currentMark = gridState[focusedCell] ?: ""
                        Text(
                            text = "$rowLabel  ✕  $colLabel",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = GridWhite
                        )
                        Text(
                            text = "Current State: " + if (currentMark.isEmpty()) "Empty (Tap to toggle)" else if (currentMark == "O") "CONFIRMED (O)" else "ELIMINATED (X)",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = if (currentMark == "O") ClueGreen else if (currentMark == "X") BloodRed else MutedGrey
                        )
                    } else {
                        Text(
                            text = "Tap any grid cell below to investigate relationships",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateGrey
                        )
                    }
                }
            }
        }

        // Logic Grid Render
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .border(2.dp, Color(0x33B0BEC5), RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .background(CharcoalSurface)
            ) {
                // Header Row (Top label spaces + headers)
                Row {
                    // Empty corner
                    Box(
                        modifier = Modifier
                            .size(width = 60.dp, height = 44.dp)
                            .border(0.5.dp, Color(0x33B0BEC5))
                            .background(SlateCard),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "GRID",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = NoirAmber
                        )
                    }

                    // Column headers representing Suspects (A-C) and Locations (D-F)
                    colHeaders.forEachIndexed { index, header ->
                        val isLocationGroup = index >= 3
                        Box(
                            modifier = Modifier
                                .size(width = 44.dp, height = 44.dp)
                                .border(
                                    width = 0.5.dp,
                                    color = Color(0x33B0BEC5)
                                )
                                .background(if (isLocationGroup) Color(0x22388E3C) else Color(0x22FFB300)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = header,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = GridWhite,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Grid Body rows
                for (r in 0..5) {
                    Row {
                        val isRowLocationGroup = r >= 3
                        // Row Header
                        Box(
                            modifier = Modifier
                                .size(width = 60.dp, height = 44.dp)
                                .border(0.5.dp, Color(0x33B0BEC5))
                                .background(if (isRowLocationGroup) Color(0x22388E3C) else Color(0x22FFB300)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = rowHeaders[r],
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = GridWhite,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Grid cells
                        for (c in 0..5) {
                            val isBottomRight = r >= 3 && c >= 3
                            val isFocused = focusedCell != null && focusedCell.first == r && focusedCell.second == c

                            if (isBottomRight) {
                                // Redundant bottom right quadrant (Location vs Location) - blocked out
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .border(0.5.dp, Color(0x33B0BEC5))
                                        .background(SlateCard),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Block,
                                        contentDescription = "Redundant block marker",
                                        tint = Color(0x1AB0BEC5),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else {
                                val currentMark = gridState[Pair(r, c)] ?: ""
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .border(
                                            width = if (isFocused) 1.5.dp else 0.5.dp,
                                            color = if (isFocused) NoirAmber else Color(0x33B0BEC5)
                                        )
                                        .background(if (isFocused) SelectedBox else CharcoalSurface)
                                        .clickable { onCellClick(r, c) }
                                        .testTag("grid_cell_${r}_${c}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    when (currentMark) {
                                        "X" -> {
                                            Text(
                                                text = "✕",
                                                color = BloodRed,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp
                                            )
                                        }
                                        "O" -> {
                                            Text(
                                                text = "⬤",
                                                color = ClueGreen,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Toggles: Empty ➔ ✕ (Cross) ➔ ⬤ (Match)",
                style = MaterialTheme.typography.labelSmall,
                color = MutedGrey,
                fontStyle = FontStyle.Italic
            )
            Button(
                onClick = onResetGrid,
                colors = ButtonDefaults.buttonColors(containerColor = BloodRed.copy(alpha = 0.15f)),
                border = BorderStroke(1.dp, BloodRed.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.testTag("reset_grid_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Restart icons",
                    tint = BloodRed,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "CLEAR GRID",
                    color = BloodRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Legend keys cards
        Text(
            text = "CASE INDEX",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = NoirAmber,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CharcoalSurface)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Suspect list
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "👤 SUSPECT KEY:",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = NoirAmber
                    )
                    case.suspects.forEachIndexed { idx, s ->
                        val code = "A,B,C".split(",")[idx]
                        Text(
                            text = "[$code] $s — ${case.suspectDescriptions[s] ?: ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateGrey
                        )
                    }
                }

                Divider(color = Color(0x19B0BEC5))

                // Weapon list
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "🔪 WEAPON KEY:",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = NoirAmber
                    )
                    case.weapons.forEachIndexed { idx, w ->
                        val code = "X,Y,Z".split(",")[idx]
                        Text(
                            text = "[$code] $w — ${case.weaponDescriptions[w] ?: ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateGrey
                        )
                    }
                }

                Divider(color = Color(0x19B0BEC5))

                // Location list
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "🏛️ LOCATION KEY:",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = NoirAmber
                    )
                    case.locations.forEachIndexed { idx, l ->
                        val code = "D,E,F".split(",")[idx]
                        Text(
                            text = "[$code] $l — ${case.locationDescriptions[l] ?: ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateGrey
                        )
                    }
                }
            }
        }
    }
}

// Case Files (Backstory & Clues) Tab Component
@Composable
fun CaseFileTab(
    case: Case,
    notesText: String,
    onNotesChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Backstory Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CharcoalSurface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Story icon",
                        tint = NoirAmber
                    )
                    Text(
                        text = "THE CHRONICLES",
                        style = MaterialTheme.typography.labelLarge,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = NoirAmber
                    )
                }
                Text(
                    text = case.story,
                    style = MaterialTheme.typography.bodyMedium,
                    color = GridWhite,
                    lineHeight = 20.sp
                )
            }
        }

        // Clues Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CharcoalSurface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FindInPage,
                        contentDescription = "Clues Icon",
                        tint = NoirAmber
                    )
                    Text(
                        text = "COLLECTED EVIDENCE",
                        style = MaterialTheme.typography.labelLarge,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = NoirAmber
                    )
                }

                Divider(color = Color(0x33B0BEC5))

                case.clues.forEachIndexed { index, clue ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "•",
                            color = NoirAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = clue,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SlateGrey,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // Notepad Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CharcoalSurface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notes,
                        contentDescription = "Notepad icon",
                        tint = NoirAmber
                    )
                    Text(
                        text = "DETECTIVE'S JOURNAL",
                        style = MaterialTheme.typography.labelLarge,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = NoirAmber
                    )
                }

                Text(
                    text = "Type down custom mental links, deductions, or lists to reference later. Journal saves automatically.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedGrey
                )

                Spacer(modifier = Modifier.height(4.dp))

                TextField(
                    value = notesText,
                    onValueChange = onNotesChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("case_notes_input"),
                    placeholder = {
                        Text(
                            "e.g., Lord Crimson must have been in the Conservatory because...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedGrey
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SlateCard,
                        unfocusedContainerColor = CharcoalSurface,
                        focusedTextColor = GridWhite,
                        unfocusedTextColor = GridWhite,
                        cursorColor = NoirAmber,
                        focusedIndicatorColor = NoirAmber,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    }
}

// Interrogation transcripts tab
@Composable
fun InterrogationTab(
    case: Case
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (case.hasLiar) {
            // Police caution warning element
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE65100))
                    .border(2.dp, Color(0xFFFFB300), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning caution tape",
                        tint = Color.White
                    )
                    Column {
                        Text(
                            text = "CRITICAL TWIST ALERT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White
                        )
                        Text(
                            text = "Exactly ONE of the suspects below is lying. The other two are telling the absolute truth. Spot the logical contradiction to expose the murderer!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Text(
            text = "TRANSCRIPTS: INTERROGATIONS",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = MutedGrey
        )

        if (case.statements.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No statements recorded. Review the evidence directly.",
                    color = MutedGrey,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace
                )
            }
        } else {
            case.statements.forEach { statement ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CharcoalSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(NoirAmber.copy(alpha = 0.2f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Avatar icon",
                                    tint = NoirAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = statement.speaker.uppercase(),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = GridWhite
                                )
                                Text(
                                    text = case.suspectDescriptions[statement.speaker] ?: "No catalog description available.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MutedGrey
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SlateCard)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "“${statement.text}”",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GridWhite,
                                fontStyle = FontStyle.Italic,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// Accusation/Charge suite Tab Component
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AccusationTab(
    case: Case,
    viewModel: DetectiveViewModel,
    chosenSuspect: String,
    chosenWeapon: String,
    chosenLocation: String,
    accusationResult: AccusationResult,
    isCaseCompleted: Boolean,
    onShowExplanation: () -> Unit
) {
    var isSuspectExpanded by remember { mutableStateOf(false) }
    var isWeaponExpanded by remember { mutableStateOf(false) }
    var isLocationExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
            border = BorderStroke(
                width = 1.dp,
                color = when (accusationResult) {
                    AccusationResult.Success -> ClueGreen
                    AccusationResult.Failure -> BloodRed
                    else -> Color.Transparent
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = "Accusation Gavel",
                        tint = BloodRed
                    )
                    Text(
                        text = "THE POLICE CHARGES",
                        style = MaterialTheme.typography.labelLarge,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = BloodRed
                    )
                }

                Text(
                    text = "Specify who committed the murder, with which weapon, and at what location. If they are logically correct, you claim the solved Case Badge!",
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateGrey
                )

                Divider(color = Color(0x33B0BEC5))

                // Suspect select dropdown
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "THE PRISONER CHOSEN:",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = MutedGrey
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { isSuspectExpanded = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SlateCard),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("select_suspect_button")
                        ) {
                            Text(
                                text = if (chosenSuspect.isEmpty()) "SELECT SUSPECT..." else chosenSuspect,
                                color = if (chosenSuspect.isEmpty()) MutedGrey else GridWhite,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Start
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown icon", tint = GridWhite)
                        }
                        DropdownMenu(
                            expanded = isSuspectExpanded,
                            onDismissRequest = { isSuspectExpanded = false },
                            modifier = Modifier.background(SlateCard)
                        ) {
                            case.suspects.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(s, color = GridWhite, fontWeight = FontWeight.Bold) },
                                    onClick = {
                                        viewModel.selectAccusationSuspect(s)
                                        isSuspectExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Weapon select dropdown
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "THE MURDER DEVICE USED:",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = MutedGrey
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { isWeaponExpanded = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SlateCard),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("select_weapon_button")
                        ) {
                            Text(
                                text = if (chosenWeapon.isEmpty()) "SELECT WEAPON..." else chosenWeapon,
                                color = if (chosenWeapon.isEmpty()) MutedGrey else GridWhite,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Start
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown icon", tint = GridWhite)
                        }
                        DropdownMenu(
                            expanded = isWeaponExpanded,
                            onDismissRequest = { isWeaponExpanded = false },
                            modifier = Modifier.background(SlateCard)
                        ) {
                            case.weapons.forEach { w ->
                                DropdownMenuItem(
                                    text = { Text(w, color = GridWhite, fontWeight = FontWeight.Bold) },
                                    onClick = {
                                        viewModel.selectAccusationWeapon(w)
                                        isWeaponExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Location select dropdown
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "THE CRIME LOCATION:",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = MutedGrey
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { isLocationExpanded = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SlateCard),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("select_location_button")
                        ) {
                            Text(
                                text = if (chosenLocation.isEmpty()) "SELECT LOCATION..." else chosenLocation,
                                color = if (chosenLocation.isEmpty()) MutedGrey else GridWhite,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Start
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown icon", tint = GridWhite)
                        }
                        DropdownMenu(
                            expanded = isLocationExpanded,
                            onDismissRequest = { isLocationExpanded = false },
                            modifier = Modifier.background(SlateCard)
                        ) {
                            case.locations.forEach { l ->
                                DropdownMenuItem(
                                    text = { Text(l, color = GridWhite, fontWeight = FontWeight.Bold) },
                                    onClick = {
                                        viewModel.selectAccusationLocation(l)
                                        isLocationExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Results banner
                when (accusationResult) {
                     is AccusationResult.Success -> {
                         Box(
                             modifier = Modifier
                                 .fillMaxWidth()
                                 .clip(RoundedCornerShape(8.dp))
                                 .background(ClueGreen.copy(alpha = 0.15f))
                                 .border(1.dp, ClueGreen, RoundedCornerShape(8.dp))
                                 .padding(12.dp)
                         ) {
                             Row(
                                 verticalAlignment = Alignment.CenterVertically,
                                 horizontalArrangement = Arrangement.spacedBy(8.dp)
                             ) {
                                 Icon(Icons.Default.CheckCircle, contentDescription = "Success check", tint = ClueGreen)
                                 Text(
                                     text = "CASE SOLVED! Outstanding work, Detective. Tap the button below to review the declassified files.",
                                     color = ClueGreen,
                                     style = MaterialTheme.typography.bodySmall,
                                     fontWeight = FontWeight.Bold
                                 )
                             }
                         }
                     }
                     is AccusationResult.Failure -> {
                         Box(
                             modifier = Modifier
                                 .fillMaxWidth()
                                 .clip(RoundedCornerShape(8.dp))
                                 .background(BloodRed.copy(alpha = 0.15f))
                                 .border(1.dp, BloodRed, RoundedCornerShape(8.dp))
                                 .padding(12.dp)
                         ) {
                             Row(
                                 verticalAlignment = Alignment.CenterVertically,
                                 horizontalArrangement = Arrangement.spacedBy(8.dp)
                             ) {
                                 Icon(Icons.Default.Cancel, contentDescription = "Failure cross icon", tint = BloodRed)
                                 Text(
                                     text = "ACQUISITION CHARGES DISMISSED! That combination doesn't match our logic. Keep searching the grid!",
                                     color = BloodRed,
                                     style = MaterialTheme.typography.bodySmall,
                                     fontWeight = FontWeight.Bold
                                 )
                             }
                         }
                     }
                     else -> {}
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.makeAccusation() },
                        colors = ButtonDefaults.buttonColors(containerColor = BloodRed),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("accuse_button"),
                        shape = RoundedCornerShape(8.dp),
                        enabled = chosenSuspect.isNotEmpty() && chosenWeapon.isNotEmpty() && chosenLocation.isNotEmpty()
                    ) {
                        Icon(Icons.Default.Gavel, contentDescription = "Gavel symbol", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ARRANGE CHARGES", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }

                    if (isCaseCompleted) {
                        Button(
                            onClick = onShowExplanation,
                            colors = ButtonDefaults.buttonColors(containerColor = ClueGreen),
                            modifier = Modifier.testTag("view_details_button"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = "Info icon details", tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("DETAILS", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}
