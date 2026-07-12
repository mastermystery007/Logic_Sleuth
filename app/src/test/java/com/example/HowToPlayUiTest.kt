package com.example

import android.content.Context
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class HowToPlayUiTest {
    @get:Rule
    val composeRule = createEmptyComposeRule()

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private var scenario: ActivityScenario<MainActivity>? = null

    @Before
    fun clearPreferences() {
        context.getSharedPreferences(HowToPlayPreferences.PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    @After
    fun closeScenario() {
        scenario?.close()
    }

    @Test
    fun firstLaunchDisplaysTutorialAndCompletingStoresSeenPreference() {
        launchActivity()

        composeRule.onNodeWithTag("how_to_play_root").assertIsDisplayed()
        composeRule.onNodeWithTag("open_case_files_button").performClick()

        composeRule.onNodeWithTag("dashboard_root").assertIsDisplayed()
        assertTrue(HowToPlayPreferences.hasSeenHowToPlay(context))
    }

    @Test
    fun laterLaunchSkipsAutomaticOnboardingAndShowsHelpButton() {
        HowToPlayPreferences.markHowToPlaySeen(context)
        launchActivity()

        composeRule.onNodeWithTag("how_to_play_root").assertDoesNotExist()
        composeRule.onNodeWithTag("dashboard_root").assertIsDisplayed()
        composeRule.onNodeWithTag("how_to_play_button").assertIsDisplayed()
    }

    @Test
    fun dashboardHelpButtonOpensTutorialAndCloseReturnsToDashboardWithoutRewritingPreference() {
        HowToPlayPreferences.markHowToPlaySeen(context)
        launchActivity()

        composeRule.onNodeWithTag("dashboard_root").assertIsDisplayed()
        composeRule.onNodeWithTag("how_to_play_button").performClick()
        composeRule.onNodeWithTag("how_to_play_root").assertIsDisplayed()
        composeRule.onNodeWithTag("close_how_to_play_button").performClick()

        composeRule.onNodeWithTag("dashboard_root").assertIsDisplayed()
        composeRule.onNodeWithTag("case_card_1").assertIsDisplayed()
        assertTrue(HowToPlayPreferences.hasSeenHowToPlay(context))
    }

    @Test
    fun manualHelpDoesNotMarkFirstLaunchPreferenceWhenItWasUnseen() {
        launchActivity()
        composeRule.onNodeWithTag("open_case_files_button").performClick()
        context.getSharedPreferences(HowToPlayPreferences.PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(HowToPlayPreferences.KEY_HAS_SEEN_HOW_TO_PLAY, false)
            .commit()

        composeRule.onNodeWithTag("how_to_play_button").performClick()
        composeRule.onNodeWithTag("close_how_to_play_button").performClick()

        assertFalse(HowToPlayPreferences.hasSeenHowToPlay(context))
    }

    private fun launchActivity() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }
}
