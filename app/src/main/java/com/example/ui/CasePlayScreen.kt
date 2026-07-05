package com.example.ui

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Case
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
    val checkedClues by viewModel.checkedClues.collectAsState()
    val isCompleted by viewModel.isActiveCaseCompleted.collectAsState()
    val chosenSuspect by viewModel.chosenSuspect.collectAsState()
    val chosenWeapon by viewModel.chosenWeapon.collectAsState()
    val chosenLocation by viewModel.chosenLocation.collectAsState()
    val chosenLiar by viewModel.chosenLiar.collectAsState()
    val accusationResult by viewModel.accusationResult.collectAsState()

    // Return early if no case selected
    val activeCase = case ?: return

    var activeTab by remember { mutableStateOf("Dossier") }
    var focusedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var showExplanationDialog by remember { mutableStateOf(false) }


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
                                text = "ONE WITNESS MAY BE LYING",
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
            val tabs = listOf("Dossier", "Logic Grid", "Deduce")
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
                "Logic Grid" -> {
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
                "Dossier" -> {
                    DossierTab(
                        case = activeCase,
                        checkedClues = checkedClues,
                        onToggleClue = { index -> viewModel.toggleClueChecked(index) }
                    )
                }
                "Deduce" -> {
                    AccusationTab(
                        case = activeCase,
                        viewModel = viewModel,
                        chosenSuspect = chosenSuspect,
                        chosenWeapon = chosenWeapon,
                        chosenLocation = chosenLocation,
                        chosenLiar = chosenLiar,
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
                        text = "CASE SOLVED",
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
                    onClick = {
                        showExplanationDialog = false
                        onNavigateBack()
                    }
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
    val rowHeaders = listOf("W1", "W2", "W3", "L1", "L2", "L3")
    val colHeaders = listOf("S1", "S2", "S3", "L1", "L2", "L3")

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

                    if (focusedCell != null && isPlayableCell(focusedCell.first, focusedCell.second)) {
                        val rowIdx = focusedCell.first
                        val colIdx = focusedCell.second
                        val rowLabel = if (rowIdx in 0..2) {
                            "Location: " + case.locations[rowIdx]
                        } else {
                            "Weapon: " + case.weapons[rowIdx - 3]
                        }
                        val colLabel = if (colIdx in 0..2) {
                            "Suspect: " + case.suspects[colIdx]
                        } else {
                            "Weapon: " + case.weapons[colIdx - 3]
                        }
                        val currentMark = gridState[focusedCell] ?: ""
                        Text(
                            text = "$rowLabel  ✕  $colLabel",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = GridWhite
                        )
                        Text(
                            text = "Current State: " + if (currentMark.isEmpty()) "Unknown (Tap to toggle)" else if (currentMark == "O") "CONFIRMED (O)" else "ELIMINATED (X)",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = if (currentMark == "O") ClueGreen else if (currentMark == "X") BloodRed else MutedGrey
                        )
                    } else {
                        Text(
                            text = "Tap any active grid cell below to investigate relationships",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateGrey
                        )
                    }
                }
            }
        }

        // Logic Grid Render with frozen row headers and one shared horizontal scroll for headers/body.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .border(2.dp, Color(0x33B0BEC5), RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(CharcoalSurface)
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

                    // Column headers representing Suspects (S1-S3) and Locations (L1-L3)
                    colHeaders.forEachIndexed { index, header ->
                        val isLocationGroup = index >= 3
                        Box(
                            modifier = Modifier
                                .size(width = columnGroupWidth, height = 28.dp)
                                .border(0.5.dp, Color(0x33B0BEC5))
                                .background(if (isWeaponGroup) Color(0x22388E3C) else Color(0x22FFB300)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = GridWhite,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            Row {
                Box(
                    modifier = Modifier
                        .size(width = rowHeaderWidth, height = cellHeight)
                        .border(0.5.dp, Color(0x33B0BEC5))
                        .background(SlateCard),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ROWS",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = MutedGrey
                    )
                }
                Row(
                    modifier = Modifier.horizontalScroll(horizontalScrollState, reverseScrolling = false)
                ) {
                    columnHeaders.forEachIndexed { index, header ->
                        val isWeaponGroup = index >= 3
                        Box(
                            modifier = Modifier
                                .size(width = cellWidth, height = cellHeight)
                                .border(0.5.dp, Color(0x33B0BEC5))
                                .background(if (isWeaponGroup) Color(0x22388E3C) else Color(0x22FFB300)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = header,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = GridWhite,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            for (r in 0..5) {
                Row {
                    val isWeaponRow = r >= 3
                    val groupLabel = if (r == 0) rowGroupLabels[0] else if (r == 3) rowGroupLabels[1] else ""
                    Box(
                        modifier = Modifier
                            .size(width = rowHeaderWidth, height = cellHeight)
                            .border(0.5.dp, Color(0x33B0BEC5))
                            .background(if (isWeaponRow) Color(0x22388E3C) else Color(0x22FFB300)),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (groupLabel.isNotEmpty()) {
                                Text(
                                    text = groupLabel.take(3).uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MutedGrey,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            } else {
                                Spacer(modifier = Modifier.width(20.dp))
                            }
                            Text(
                                text = rowHeaders[r],
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = GridWhite,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.horizontalScroll(horizontalScrollState, reverseScrolling = false)
                    ) {
                        for (c in 0..5) {
                            val isFocused = focusedCell != null && focusedCell.first == r && focusedCell.second == c
                            if (isDisabledCell(r, c)) {
                                Box(
                                    modifier = Modifier
                                        .size(width = cellWidth, height = cellHeight)
                                        .border(0.5.dp, Color(0x33B0BEC5))
                                        .background(disabledCellBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "–",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MutedGrey.copy(alpha = 0.45f),
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            } else {
                                val currentMark = gridState[Pair(r, c)] ?: ""
                                Box(
                                    modifier = Modifier
                                        .size(width = cellWidth, height = cellHeight)
                                        .border(
                                            width = if (isFocused) 1.5.dp else 0.5.dp,
                                            color = if (isFocused) NoirAmber else Color(0x33B0BEC5)
                                        )
                                        .background(if (isFocused) SelectedBox else CharcoalSurface)
                                        .clickable(enabled = isPlayableCell(r, c)) { onCellClick(r, c) }
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
                text = "Toggles: Unknown ➔ ✕ (Cross) ➔ ⬤ (Match)",
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
                        val code = "S${idx + 1}"
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
                        val code = "W${idx + 1}"
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
                        val code = "L${idx + 1}"
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

// Dossier tab
@Composable
fun DossierTab(
    case: Case,
    checkedClues: Set<Int>,
    onToggleClue: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                        text = "CASE BRIEF",
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
                    val isChecked = checkedClues.contains(index)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { onToggleClue(index) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = NoirAmber,
                                uncheckedColor = MutedGrey,
                                checkmarkColor = Color.Black
                            )
                        )
                        Text(
                            text = clue,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isChecked) MutedGrey.copy(alpha = 0.55f) else SlateGrey,
                            lineHeight = 18.sp,
                            textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
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
    chosenLiar: String,
    accusationResult: AccusationResult,
    isCaseCompleted: Boolean,
    onShowExplanation: () -> Unit
) {
    var isSuspectExpanded by remember { mutableStateOf(false) }
    var isWeaponExpanded by remember { mutableStateOf(false) }
    var isLocationExpanded by remember { mutableStateOf(false) }
    var isLiarExpanded by remember { mutableStateOf(false) }

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
                        text = "FINAL ACCUSATION",
                        style = MaterialTheme.typography.labelLarge,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = BloodRed
                    )
                }

                Text(
                    text = if (case.hasLiar) {
                        "This case includes one liar. Identify the killer, weapon, location, and lying suspect."
                    } else {
                        "Specify who committed the crime, with which weapon, and where."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateGrey
                )

                Divider(color = Color(0x33B0BEC5))

                // Suspect select dropdown
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "SUSPECT:",
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
                        text = "WEAPON:",
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
                        text = "LOCATION:",
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

                if (case.hasLiar) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Who is lying?",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = MutedGrey
                        )
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = { isLiarExpanded = true },
                                colors = ButtonDefaults.buttonColors(containerColor = SlateCard),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("select_liar_button")
                            ) {
                                Text(
                                    text = if (chosenLiar.isEmpty()) "SELECT LYING SUSPECT..." else chosenLiar,
                                    color = if (chosenLiar.isEmpty()) MutedGrey else GridWhite,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Start
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown icon", tint = GridWhite)
                            }
                            DropdownMenu(
                                expanded = isLiarExpanded,
                                onDismissRequest = { isLiarExpanded = false },
                                modifier = Modifier.background(SlateCard)
                            ) {
                                case.suspects.forEach { suspect ->
                                    DropdownMenuItem(
                                        text = { Text(suspect, color = GridWhite, fontWeight = FontWeight.Bold) },
                                        onClick = {
                                            viewModel.chooseLiar(suspect)
                                            isLiarExpanded = false
                                        }
                                    )
                                }
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
                                     text = "CASE SOLVED! Outstanding work, Detective. Tap the button below to review the case file.",
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
                                     text = "ACCUSATION NOT PROVEN. That combination does not match the clues. Keep searching the grid!",
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
                        enabled = chosenSuspect.isNotEmpty() && chosenWeapon.isNotEmpty() && chosenLocation.isNotEmpty() && (!case.hasLiar || chosenLiar.isNotEmpty())
                    ) {
                        Icon(Icons.Default.Gavel, contentDescription = "Gavel symbol", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("MAKE ACCUSATION", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
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
