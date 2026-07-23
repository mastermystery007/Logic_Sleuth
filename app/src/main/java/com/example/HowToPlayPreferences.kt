package com.example

import android.content.Context

object HowToPlayPreferences {
    const val PREFS_NAME = "deduce_it_preferences"
    const val KEY_HAS_SEEN_HOW_TO_PLAY = "has_seen_how_to_play"
    const val KEY_HAS_SEEN_NAVIGATION_GUIDE = "has_seen_navigation_guide"

    fun hasSeenHowToPlay(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_HAS_SEEN_HOW_TO_PLAY, false)

    fun markHowToPlaySeen(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_HAS_SEEN_HOW_TO_PLAY, true)
            .apply()
    }

    fun hasSeenNavigationGuide(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_HAS_SEEN_NAVIGATION_GUIDE, false)

    fun markNavigationGuideSeen(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_HAS_SEEN_NAVIGATION_GUIDE, true)
            .apply()
    }
}
