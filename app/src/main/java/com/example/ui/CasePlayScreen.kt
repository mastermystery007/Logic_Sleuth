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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.RewardedAdManager
import com.example.ads.RewardedAdPurpose
import com.example.data.Case
import com.example.ui.theme.*
import com.example.viewmodel.AccusationResult
import com.example.viewmodel.DetectiveViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CasePlayScreen(
    viewModel: DetectiveViewModel,
    onNavigateBack: () -> Unit,
    rewardedAdManager: RewardedAdManager,
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

    var activeTab by remember { mutableStateOf("Cast") }
    var focusedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var showExplanationDialog by remember { mutableStateOf(false) }


    LaunchedEffect(accusationResult) {
        if (accusationResult == AccusationResult.Success) {
            showExplanationDialog = true
        }
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
            val tabs = listOf("Cast", "Dossier", "Logic Grid", "Deduce")
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
                "Cast" -> {
                    CastTab(case = activeCase)
                }
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
                        rewardedAdManager = rewardedAdManager,
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
                Text(
                    text = "CASE CLOSED",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = NoirAmber
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Your deduction matches the evidence.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GridWhite
                    )

                    Card(
                        colors = CardDefaults.cardColors(containerColor = SlateCard),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            SolutionRow(label = "Culprit:", value = activeCase.solutionSuspect)
                            SolutionRow(label = "Weapon:", value = activeCase.solutionWeapon)
                            SolutionRow(label = "Location:", value = activeCase.solutionLocation)
                            if (activeCase.hasLiar && activeCase.solutionLiar != null) {
                                SolutionRow(label = "False witness:", value = activeCase.solutionLiar)
                            }
                        }
                    }

                    Divider(color = Color(0x33B0BEC5))

                    Text(
                        text = "CASE EXPLANATION",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = NoirAmber,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = activeCase.murderExplanation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateGrey,
                        lineHeight = 20.sp
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExplanationDialog = false
                        viewModel.selectCase(null)
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

@Composable
private fun SolutionRow(label: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            color = NoirAmber,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.width(105.dp)
        )
        Text(
            text = value,
            color = GridWhite,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
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
    val horizontalScrollState = rememberScrollState()
    var fitToScreen by remember { mutableStateOf(true) }
    val rowHeaderWidth = if (fitToScreen) 92.dp else 140.dp
    val cellWidth = if (fitToScreen) 52.dp else 120.dp
    val cellHeight = if (fitToScreen) 48.dp else 56.dp
    val headerFontSize = if (fitToScreen) 7.sp else 9.sp
    val markFontSize = if (fitToScreen) 15.sp else 18.sp
    val disabledCellBackground = MutedGrey.copy(alpha = 0.12f)

    val columnHeaders = case.suspects + case.weapons
    val rowHeaders = case.locations + case.weapons

    fun isLocationSuspectCell(row: Int, col: Int): Boolean =
        row in 0..2 && col in 0..2

    fun isLocationWeaponCell(row: Int, col: Int): Boolean =
        row in 0..2 && col in 3..5

    fun isWeaponSuspectCell(row: Int, col: Int): Boolean =
        row in 3..5 && col in 0..2

    fun isPlayableCell(row: Int, col: Int): Boolean =
        isLocationSuspectCell(row, col) ||
            isLocationWeaponCell(row, col) ||
            isWeaponSuspectCell(row, col)

    fun isDisabledCell(row: Int, col: Int): Boolean =
        row in 3..5 && col in 3..5

    @Composable
    fun HeaderCell(header: String, isWeaponGroup: Boolean) {
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
                fontSize = headerFontSize,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 6.dp)
            )
        }
    }

    @Composable
    fun RowHeaderCell(rowLabel: String, rowIndex: Int) {
        val isWeaponRow = rowIndex >= 3
        Box(
            modifier = Modifier
                .size(width = rowHeaderWidth, height = cellHeight)
                .border(0.5.dp, Color(0x33B0BEC5))
                .background(if (isWeaponRow) Color(0x22388E3C) else Color(0x22FFB300)),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = rowLabel,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = GridWhite,
                fontFamily = FontFamily.Monospace,
                fontSize = headerFontSize,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }

    @Composable
    fun DisabledCell() {
        Box(
            modifier = Modifier
                .size(width = cellWidth, height = cellHeight)
                .border(0.5.dp, Color(0x33B0BEC5))
                .background(disabledCellBackground)
                .testTag("disabled_grid_cell"),
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
    }

    @Composable
    fun ActiveGridCell(row: Int, col: Int) {
        val isFocused = focusedCell?.let { it.first == row && it.second == col } == true
        val currentMark = gridState[Pair(row, col)] ?: ""
        Box(
            modifier = Modifier
                .size(width = cellWidth, height = cellHeight)
                .border(
                    width = if (isFocused) 1.5.dp else 0.5.dp,
                    color = if (isFocused) NoirAmber else Color(0x33B0BEC5)
                )
                .background(if (isFocused) SelectedBox else CharcoalSurface)
                .clickable(enabled = isPlayableCell(row, col)) { onCellClick(row, col) }
                .testTag("grid_cell_${row}_${col}"),
            contentAlignment = Alignment.Center
        ) {
            when (currentMark) {
                "X" -> Text("✕", color = BloodRed, fontWeight = FontWeight.Bold, fontSize = markFontSize)
                "O" -> Text("⬤", color = ClueGreen, fontWeight = FontWeight.Bold, fontSize = markFontSize)
            }
        }
    }

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
                            "Location: ${case.locations[rowIdx]}"
                        } else {
                            "Weapon: ${case.weapons[rowIdx - 3]}"
                        }
                        val colLabel = if (colIdx in 0..2) {
                            "Suspect: ${case.suspects[colIdx]}"
                        } else {
                            "Weapon: ${case.weapons[colIdx - 3]}"
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
            Row {
                Box(
                    modifier = Modifier
                        .size(width = rowHeaderWidth, height = cellHeight)
                        .border(0.5.dp, Color(0x33B0BEC5))
                        .background(SlateCard)
                )

                Row(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
                    columnHeaders.forEachIndexed { index, header ->
                        HeaderCell(header = header, isWeaponGroup = index >= 3)
                    }
                }
            }

            rowHeaders.forEachIndexed { r, rowLabel ->
                Row {
                    RowHeaderCell(rowLabel = rowLabel, rowIndex = r)

                    Row(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
                        columnHeaders.forEachIndexed { c, _ ->
                            if (isDisabledCell(r, c)) {
                                DisabledCell()
                            } else {
                                ActiveGridCell(row = r, col = c)
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { fitToScreen = !fitToScreen },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, NoirAmber),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NoirAmber)
            ) {
                Text(
                    text = if (fitToScreen) "LARGE VIEW" else "FIT TO SCREEN",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }

            OutlinedButton(
                onClick = onResetGrid,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, BloodRed),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BloodRed)
            ) {
                Text(
                    text = "CLEAR GRID",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
        }

        Text(
            text = "Toggles: Unknown ➔ ✕ (Cross) ➔ ⬤ (Match)",
            style = MaterialTheme.typography.labelSmall,
            color = MutedGrey,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}

// Cast reference tab
@Composable
fun CastTab(case: Case) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CastSectionCard(
            title = "SUSPECTS",
            icon = Icons.Default.Person,
            items = case.suspects.map { suspect ->
                suspect to (case.suspectDescriptions[suspect] ?: "")
            }
        )

        CastSectionCard(
            title = "WEAPONS",
            icon = Icons.Default.Build,
            items = case.weapons.map { weapon ->
                weapon to (case.weaponDescriptions[weapon] ?: "")
            }
        )

        CastSectionCard(
            title = "LOCATIONS",
            icon = Icons.Default.Place,
            items = case.locations.map { location ->
                location to (case.locationDescriptions[location] ?: "")
            }
        )

        if (case.statements.isNotEmpty()) {
            WitnessStatementsCard(case = case)
        }
    }
}

@Composable
fun CastSectionCard(
    title: String,
    icon: ImageVector,
    items: List<Pair<String, String>>
) {
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
                Icon(icon, contentDescription = "$title icon", tint = NoirAmber)
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = NoirAmber
                )
            }

            Divider(color = Color(0x33B0BEC5))

            items.forEach { (name, description) ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SlateCard.copy(alpha = 0.45f))
                        .border(1.dp, Color(0x22B0BEC5), RoundedCornerShape(8.dp))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = GridWhite
                    )

                    if (description.isNotBlank()) {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateGrey,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WitnessStatementsCard(case: Case) {
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
                    imageVector = Icons.Default.RecordVoiceOver,
                    contentDescription = "Witness statements icon",
                    tint = NoirAmber
                )
                Text(
                    text = "WITNESS STATEMENTS",
                    style = MaterialTheme.typography.labelLarge,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = NoirAmber
                )
            }

            Divider(color = Color(0x33B0BEC5))

            if (case.hasLiar) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(BloodRed.copy(alpha = 0.12f))
                        .border(1.dp, BloodRed.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Exactly one witness may be lying. Use these statements with the physical clues.",
                        style = MaterialTheme.typography.bodySmall,
                        color = BloodRed,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            case.statements.forEach { statement ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SlateCard.copy(alpha = 0.55f))
                        .border(1.dp, Color(0x22B0BEC5), RoundedCornerShape(8.dp))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = statement.speaker,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = NoirAmber
                    )
                    Text(
                        text = "“${statement.text}”",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateGrey,
                        lineHeight = 18.sp
                    )
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

                if (case.hasLiar) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(BloodRed.copy(alpha = 0.12f))
                            .border(1.dp, BloodRed.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "This file includes one false witness statement. Check the Cast tab before deducing.",
                            style = MaterialTheme.typography.bodySmall,
                            color = BloodRed,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

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
    rewardedAdManager: RewardedAdManager,
    onShowExplanation: () -> Unit
) {
    var isSuspectExpanded by remember { mutableStateOf(false) }
    var isWeaponExpanded by remember { mutableStateOf(false) }
    var isLocationExpanded by remember { mutableStateOf(false) }
    var isLiarExpanded by remember { mutableStateOf(false) }
    var showRevealDialog by remember { mutableStateOf(false) }

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
                                     text = "CASE CLOSED. Outstanding work, Detective. Tap the button below to review the case file.",
                                     color = ClueGreen,
                                     style = MaterialTheme.typography.bodySmall,
                                     fontWeight = FontWeight.Bold
                                 )
                             }
                         }
                     }
                     is AccusationResult.Failure -> {
                         Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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

                             Button(
                                 onClick = {
                                     rewardedAdManager.showRewardedAd(
                                         purpose = RewardedAdPurpose.REVEAL_SOLUTION,
                                         onRewardEarned = { showRevealDialog = true }
                                     )
                                 },
                                 colors = ButtonDefaults.buttonColors(containerColor = NoirAmber),
                                 modifier = Modifier
                                     .fillMaxWidth()
                                     .testTag("reveal_solution_button"),
                                 shape = RoundedCornerShape(8.dp)
                             ) {
                                 Icon(Icons.Default.Visibility, contentDescription = "Reveal solution icon", tint = Color.Black)
                                 Spacer(modifier = Modifier.width(8.dp))
                                 Text(
                                     text = "WATCH AD TO SEE CORRECT ANSWER",
                                     color = Color.Black,
                                     fontWeight = FontWeight.Bold,
                                     fontFamily = FontFamily.Monospace
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
                        onClick = {
                            rewardedAdManager.showRewardedAd(
                                purpose = RewardedAdPurpose.CHECK_ANSWER,
                                onRewardEarned = { viewModel.makeAccusation() }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BloodRed),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("accuse_button"),
                        shape = RoundedCornerShape(8.dp),
                        enabled = chosenSuspect.isNotEmpty() && chosenWeapon.isNotEmpty() && chosenLocation.isNotEmpty() && (!case.hasLiar || chosenLiar.isNotEmpty())
                    ) {
                        Icon(Icons.Default.Gavel, contentDescription = "Gavel symbol", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("WATCH AD TO CHECK ANSWER", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
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

    if (showRevealDialog) {
        AlertDialog(
            onDismissRequest = { showRevealDialog = false },
            title = {
                Text(
                    text = "SOLUTION REVEALED",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = NoirAmber
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SlateCard),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            SolutionRow(label = "Culprit:", value = case.solutionSuspect)
                            SolutionRow(label = "Weapon:", value = case.solutionWeapon)
                            SolutionRow(label = "Location:", value = case.solutionLocation)
                            if (case.hasLiar && case.solutionLiar != null) {
                                SolutionRow(label = "False witness:", value = case.solutionLiar)
                            }
                        }
                    }

                    Divider(color = Color(0x33B0BEC5))

                    Text(
                        text = "CASE EXPLANATION",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = NoirAmber,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = case.murderExplanation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateGrey,
                        lineHeight = 20.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showRevealDialog = false }) {
                    Text("CLOSE", color = NoirAmber, fontFamily = FontFamily.Monospace)
                }
            },
            containerColor = CharcoalSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
