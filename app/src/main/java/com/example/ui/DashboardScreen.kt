package com.example.ui

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
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
    val cases = viewModel.cases
    val totalSolved = completedIds.size

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "DEDUCE IT",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = NoirAmber,
                        letterSpacing = 2.sp
                    )
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Open a file. Read the clues. Solve the case.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateGrey,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "$totalSolved / ${cases.size} files solved",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedGrey,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )
            }

            Text(
                text = "CASE FILES",
                style = MaterialTheme.typography.labelLarge,
                color = MutedGrey,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(cases) { case ->
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
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSolved) ClueGreen.copy(alpha = 0.15f) else Color(0x1AFFB300))
                    .border(
                        1.dp,
                        if (isSolved) ClueGreen.copy(alpha = 0.4f) else Color(0x33FFB300),
                        RoundedCornerShape(8.dp)
                    )
            ) {
                Icon(
                    imageVector = if (isSolved) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                    contentDescription = if (isSolved) "Solved Case" else "Open Case",
                    tint = if (isSolved) ClueGreen else NoirAmber,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
