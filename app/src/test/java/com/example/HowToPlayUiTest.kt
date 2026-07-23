package com.example

import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import org.junit.After
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
    fun firstLaunchDisplaysNavigationGuideAndDismissStoresSeenPreference() {
        launchActivity()

        composeRule.onNodeWithTag("dashboard_root").assertIsDisplayed()
        composeRule.onNodeWithTag("first_launch_guide").assertIsDisplayed()
        composeRule.onNodeWithTag("dismiss_first_launch_guide")
            .assertIsDisplayed()
            .performClick()
        composeRule.waitForIdle()

        composeRule.onAllNodesWithTag("first_launch_guide").assertCountEquals(0)
        composeRule.onNodeWithTag("case_card_1").assertIsDisplayed()
        assertTrue(HowToPlayPreferences.hasSeenNavigationGuide(context))
    }

    @Test
    fun laterLaunchSkipsNavigationGuideAndShowsHeaderButtons() {
        HowToPlayPreferences.markNavigationGuideSeen(context)
        launchActivity()

        composeRule.onAllNodesWithTag("first_launch_guide").assertCountEquals(0)
        composeRule.onNodeWithTag("dashboard_root").assertIsDisplayed()
        composeRule.onNodeWithTag("how_to_play_button").assertIsDisplayed()
        composeRule.onNodeWithTag("privacy_policy_button").assertIsDisplayed()
    }

    @Test
    fun dashboardHelpButtonOpensTutorialAndCloseReturnsToDashboard() {
        HowToPlayPreferences.markNavigationGuideSeen(context)
        launchActivity()

        composeRule.onNodeWithTag("dashboard_root").assertIsDisplayed()
        composeRule.onNodeWithTag("how_to_play_button").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("how_to_play_root").assertIsDisplayed()
        composeRule.onNodeWithTag("close_how_to_play_button")
            .performScrollTo()
            .assertIsDisplayed()
            .performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("dashboard_root").assertIsDisplayed()
        composeRule.onNodeWithTag("case_card_1").assertIsDisplayed()
    }

    @Test
    fun systemBackFromHowToPlayReturnsToDashboard() {
        HowToPlayPreferences.markNavigationGuideSeen(context)
        launchActivity()

        composeRule.onNodeWithTag("how_to_play_button").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("how_to_play_root").assertIsDisplayed()

        scenario?.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("dashboard_root").assertIsDisplayed()
        composeRule.onNodeWithTag("case_card_1").assertIsDisplayed()
    }

    private fun launchActivity() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        composeRule.waitForIdle()
    }
}
