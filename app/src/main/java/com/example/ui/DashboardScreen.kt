package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Case
import com.example.ui.theme.*
import com.example.viewmodel.DetectiveViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DetectiveViewModel,
    onNavigateToCase: (caseId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val completedIds by viewModel.completedCaseIds.collectAsState()
    val rawCases = viewModel.cases
    val activeFilter by viewModel.difficultyFilter.collectAsState()

    // Filtered cases
    val filteredCases = remember(activeFilter, completedIds, rawCases) {
        rawCases.filter { case ->
            when (activeFilter) {
                "All" -> true
                "Easy" -> case.difficulty == "Easy"
                "Medium" -> case.difficulty == "Medium"
                "Hard" -> case.difficulty == "Hard"
                "Completed" -> completedIds.contains(case.id)
                "In Progress" -> !completedIds.contains(case.id)
                else -> true
            }
        }
    }

    // Stats
    val totalSolved = completedIds.size
    val totalProgress = if (rawCases.isNotEmpty()) (totalSolved.toFloat() / rawCases.size * 100).toInt() else 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search icon",
                            tint = NoirAmber
                        )
                        Text(
                            text = "NOIR LOGIC FILES",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = GridWhite,
                            letterSpacing = 2.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CarbonDark
                )
            )
        },
        containerColor = CarbonDark,
        modifier = modifier.testTag("dashboard_root")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Atmospheric Header / Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                CharcoalSurface,
                                SlateCard
                            )
                        )
                    )
                    .border(1.dp, Color(0x33B0BEC5), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "OFFICER DOSSIER",
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedGrey,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Special Investigator",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = NoirAmber
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = "Investigator star badge",
                            tint = NoirAmber,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Unmask culprits, decode testimony, and solve grid riddles using cold, hard logical subtraction.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateGrey
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Progress Bar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "SOLVED MYSTERIES",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MutedGrey,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "$totalSolved / ${rawCases.size} completed",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NoirAmber,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { totalProgress / 100f },
                                color = NoirAmber,
                                trackColor = Color(0x33FFB300),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                            )
                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x1AFFB300))
                        ) {
                            Text(
                                text = "$totalProgress%",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = NoirAmber,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Difficulty/Completion Filter Row
            val filters = listOf("All", "Easy", "Medium", "Hard", "Completed")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                filters.forEach { filter ->
                    val isSelected = filter == activeFilter
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) NoirAmber else SlateCard)
                            .clickable { viewModel.setDifficultyFilter(filter) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = filter,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.Black else GridWhite,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Cases list
            Text(
                text = "ACTIVE CASES ($activeFilter)",
                style = MaterialTheme.typography.labelLarge,
                color = MutedGrey,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (filteredCases.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Empty file icon",
                            tint = MutedGrey,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "No cases found in this archive.",
                            color = MutedGrey,
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(filteredCases) { case ->
                        val isSolved = completedIds.contains(case.id)
                        CaseCard(
                            case = case,
                            isSolved = isSolved,
                            onClick = {
                                viewModel.selectCase(case.id)
                                onNavigateToCase(case.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CaseCard(
    case: Case,
    isSolved: Boolean,
    onClick: () -> Unit
) {
    val difficultyColor = when (case.difficulty) {
        "Easy" -> Color(0xFF4CAF50)
        "Medium" -> NoirAmber
        "Hard" -> BloodRed
        else -> GridWhite
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = if (isSolved) 1.dp else 0.dp,
                color = if (isSolved) ClueGreen else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .testTag("case_card_${case.id}"),
        colors = CardDefaults.cardColors(
            containerColor = CharcoalSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Difficulty Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(difficultyColor.copy(alpha = 0.15f))
                            .border(1.dp, difficultyColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = case.difficulty.uppercase(),
                            color = difficultyColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    if (case.hasLiar) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(BloodRed.copy(alpha = 0.15f))
                                .border(1.dp, BloodRed.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "LIAR TWIST",
                                color = BloodRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Case #${case.id}: ${case.title}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GridWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = case.story,
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedGrey,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Solve Indicator
            if (isSolved) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ClueGreen.copy(alpha = 0.15f))
                        .border(1.dp, ClueGreen.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Solved Case",
                        tint = ClueGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x1AFFB300))
                        .border(1.dp, Color(0x33FFB300), RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play Case",
                        tint = NoirAmber,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
