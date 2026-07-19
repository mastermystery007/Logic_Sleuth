package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ads.AdMobRewardedAdManager
import com.example.ads.FakeRewardedAdManager
import com.example.ads.RewardedAdManager
import com.example.ads.UnavailableRewardedAdManager
import com.example.privacy.AdConsentManager
import com.example.ui.CasePlayScreen
import com.example.ui.DashboardScreen
import com.example.ui.HowToPlayScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.DetectiveViewModel
import com.google.android.gms.ads.MobileAds
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {
    private val viewModel: DetectiveViewModel by viewModels()

    private lateinit var consentManager: AdConsentManager
    private val rewardedAdManagerState =
        MutableStateFlow<RewardedAdManager>(UnavailableRewardedAdManager)
    private val privacyOptionsRequiredState = MutableStateFlow(false)
    private val mobileAdsInitializationStarted = AtomicBoolean(false)
    private val mobileAdsInitializationComplete = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        consentManager = AdConsentManager.getInstance(applicationContext)
        if (BuildConfig.DEBUG) {
            // Debug builds never request live ads.
            rewardedAdManagerState.value = FakeRewardedAdManager()
        }

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val activity = this@MainActivity
                val rewardedAdManager by rewardedAdManagerState.collectAsState()
                val privacyOptionsRequired by privacyOptionsRequiredState.collectAsState()
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
                                    },
                                    onOpenHowToPlay = {
                                        isFirstLaunchHowToPlay = false
                                        showHowToPlay = true
                                    },
                                    onOpenPrivacyPolicy = {
                                        openPrivacyPolicy()
                                    },
                                    showPrivacyOptions = privacyOptionsRequired,
                                    onOpenPrivacyOptions = {
                                        consentManager.showPrivacyOptionsForm(activity) { formError ->
                                            if (formError != null) {
                                                Log.w(TAG, "Privacy options form error: ${formError.message}")
                                            }
                                            refreshPrivacyOptionsState()
                                            configureRewardedAdsForCurrentConsent()
                                        }
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

        requestConsentAndConfigureAds()
    }

    private fun openPrivacyPolicy() {
        val policyIntent = Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL)).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
        }

        runCatching {
            startActivity(policyIntent)
        }.onFailure { error ->
            Log.w(TAG, "Unable to open privacy policy", error)
        }
    }

    private fun requestConsentAndConfigureAds() {
        consentManager.gatherConsent(this) { error ->
            if (error != null) {
                Log.w(TAG, "Consent gathering error: ${error.message}")
            }
            refreshPrivacyOptionsState()
            configureRewardedAdsForCurrentConsent()
        }

        // Consent from a previous session may already be valid immediately after the update request.
        refreshPrivacyOptionsState()
        configureRewardedAdsForCurrentConsent()
    }

    private fun refreshPrivacyOptionsState() {
        privacyOptionsRequiredState.value = consentManager.isPrivacyOptionsRequired
    }

    private fun configureRewardedAdsForCurrentConsent() {
        if (BuildConfig.DEBUG) {
            rewardedAdManagerState.value = FakeRewardedAdManager()
            return
        }

        if (!consentManager.canRequestAds) {
            rewardedAdManagerState.value = UnavailableRewardedAdManager
            return
        }

        if (mobileAdsInitializationStarted.compareAndSet(false, true)) {
            MobileAds.initialize(this) {
                mobileAdsInitializationComplete.set(true)
                rewardedAdManagerState.value =
                    if (consentManager.canRequestAds) {
                        AdMobRewardedAdManager(this)
                    } else {
                        UnavailableRewardedAdManager
                    }
            }
            return
        }

        if (
            mobileAdsInitializationComplete.get() &&
                rewardedAdManagerState.value === UnavailableRewardedAdManager
        ) {
            rewardedAdManagerState.value = AdMobRewardedAdManager(this)
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val PRIVACY_POLICY_URL =
            "https://mastermystery007.github.io/privacy_policies/deduce-it/"
    }
}
