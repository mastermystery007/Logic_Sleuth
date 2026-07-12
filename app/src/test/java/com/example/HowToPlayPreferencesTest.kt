package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class HowToPlayPreferencesTest {
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences(HowToPlayPreferences.PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    @Test
    fun unseenStateDefaultsToFalse() {
        assertFalse(HowToPlayPreferences.hasSeenHowToPlay(context))
    }

    @Test
    fun completingOnboardingPersistsFlag() {
        HowToPlayPreferences.markHowToPlaySeen(context)
        assertTrue(HowToPlayPreferences.hasSeenHowToPlay(context))
    }

    @Test
    fun seenStateCanBeReadOnNextLaunch() {
        context.getSharedPreferences(HowToPlayPreferences.PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(HowToPlayPreferences.KEY_HAS_SEEN_HOW_TO_PLAY, true)
            .commit()

        assertTrue(HowToPlayPreferences.hasSeenHowToPlay(context))
    }
}
