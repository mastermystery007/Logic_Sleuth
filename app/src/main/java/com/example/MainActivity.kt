package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ads.AdMobRewardedAdManager
import com.example.ads.FakeRewardedAdManager
import com.example.ui.DashboardScreen
import com.example.ui.CasePlayScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.DetectiveViewModel
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {
    private val viewModel: DetectiveViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MobileAds.initialize(this)
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val rewardedAdManager = remember {
                    if (BuildConfig.DEBUG) {
                        FakeRewardedAdManager()
                    } else {
                        AdMobRewardedAdManager(this)
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
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
                                }
                            )
                        }
                        composable(
                            route = "case_play/{caseId}",
                            arguments = listOf(navArgument("caseId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val caseId = backStackEntry.arguments?.getInt("caseId")
                            LaunchedEffect(caseId) {
                                viewModel.selectCase(caseId)
                            }
                            CasePlayScreen(
                                viewModel = viewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                    viewModel.selectCase(null)
                                },
                                rewardedAdManager = rewardedAdManager
                            )
                        }
                    }
                }
            }
        }
    }
}
