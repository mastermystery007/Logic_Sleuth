package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ads.AdMobRewardedAdManager
import com.example.ads.FakeRewardedAdManager
import com.example.ads.RewardedAdPurpose
import com.example.ui.CasePlayScreen
import com.example.ui.DashboardScreen
import com.example.ui.HowToPlayScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.DetectiveViewModel
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: DetectiveViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MobileAds.initialize(this)
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val activity = this@MainActivity
                val snackbarHostState = remember { SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()
                val rewardedAdManager = remember {
                    if (BuildConfig.DEBUG) {
                        FakeRewardedAdManager()
                    } else {
                        AdMobRewardedAdManager(activity)
                    }
                }
                var showHowToPlay by remember {
                    mutableStateOf(!HowToPlayPreferences.hasSeenHowToPlay(activity))
                }
                var isFirstLaunchHowToPlay by remember {
                    mutableStateOf(showHowToPlay)
                }

                if (showHowToPlay) {
                    HowToPlayScreen(
                        onComplete = {
                            if (isFirstLaunchHowToPlay) {
                                HowToPlayPreferences.markHowToPlaySeen(activity)
                            }
                            showHowToPlay = false
                        },
                        isFirstLaunch = isFirstLaunchHowToPlay
                    )
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHost = { SnackbarHost(snackbarHostState) }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "dashboard",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("dashboard") {
                                DashboardScreen(
                                    viewModel = viewModel,
                                    onNavigateToCase = { caseId ->
                                        navController.navigate("case_play/$caseId")
                                    },
                                    onOpenHowToPlay = {
                                        isFirstLaunchHowToPlay = false
                                        showHowToPlay = true
                                    }
                                )
                            }
                            composable(
                                route = "case_play/{caseId}",
                                arguments = listOf(navArgument("caseId") { type = NavType.IntType })
                            ) { backStackEntry ->
                                val caseId = backStackEntry.arguments?.getInt("caseId")
                                val activeCase by viewModel.activeCase.collectAsState()
                                var revealedLiar by remember(caseId) { mutableStateOf<String?>(null) }
                                var showLiarDialog by remember(caseId) { mutableStateOf(false) }

                                LaunchedEffect(caseId) {
                                    viewModel.selectCase(caseId)
                                }

                                val matchingCase = activeCase?.takeIf { it.id == caseId }
                                val liar = matchingCase?.solutionLiar

                                Column(modifier = Modifier.fillMaxSize()) {
                                    if (matchingCase?.hasLiar == true && liar != null) {
                                        Button(
                                            onClick = {
                                                if (revealedLiar != null) {
                                                    showLiarDialog = true
                                                } else {
                                                    rewardedAdManager.showRewardedAd(
                                                        purpose = RewardedAdPurpose.REVEAL_LIAR,
                                                        onRewardEarned = {
                                                            revealedLiar = liar
                                                            showLiarDialog = true
                                                        },
                                                        onAdUnavailable = {
                                                            coroutineScope.launch {
                                                                snackbarHostState.showSnackbar(
                                                                    "A rewarded ad is not available right now. Check your connection and try again."
                                                                )
                                                            }
                                                        }
                                                    )
                                                }
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                                .testTag("reveal_liar_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Visibility,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.size(8.dp))
                                            Text(
                                                if (revealedLiar == null) {
                                                    "WATCH AD TO REVEAL THE LIAR"
                                                } else {
                                                    "VIEW REVEALED LIAR"
                                                }
                                            )
                                        }
                                    }

                                    CasePlayScreen(
                                        viewModel = viewModel,
                                        onNavigateBack = {
                                            navController.popBackStack()
                                            viewModel.selectCase(null)
                                        },
                                        rewardedAdManager = rewardedAdManager,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                if (showLiarDialog && revealedLiar != null) {
                                    AlertDialog(
                                        onDismissRequest = { showLiarDialog = false },
                                        title = { Text("LIAR REVEALED") },
                                        text = { Text("The false witness is $revealedLiar.") },
                                        confirmButton = {
                                            TextButton(onClick = { showLiarDialog = false }) {
                                                Text("CLOSE")
                                            }
                                        }
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
