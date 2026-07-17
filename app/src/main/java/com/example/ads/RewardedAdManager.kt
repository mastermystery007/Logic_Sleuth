package com.example.ads

import android.app.Activity
import com.example.BuildConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

enum class RewardedAdPurpose {
    CHECK_ANSWER,
    REVEAL_LIAR,
    REVEAL_SOLUTION
}

interface RewardedAdManager {
    fun showRewardedAd(
        purpose: RewardedAdPurpose,
        onRewardEarned: () -> Unit,
        onAdUnavailable: () -> Unit = {}
    )
}

class FakeRewardedAdManager : RewardedAdManager {
    override fun showRewardedAd(
        purpose: RewardedAdPurpose,
        onRewardEarned: () -> Unit,
        onAdUnavailable: () -> Unit
    ) {
        onRewardEarned()
    }
}

class AdMobRewardedAdManager(
    private val activity: Activity
) : RewardedAdManager {

    private val rewardedAds = mutableMapOf<RewardedAdPurpose, RewardedAd?>()
    private val loadingPurposes = mutableSetOf<RewardedAdPurpose>()

    init {
        RewardedAdPurpose.entries.forEach(::load)
    }

    override fun showRewardedAd(
        purpose: RewardedAdPurpose,
        onRewardEarned: () -> Unit,
        onAdUnavailable: () -> Unit
    ) {
        val ad = rewardedAds[purpose]
        if (ad == null) {
            onAdUnavailable()
            load(purpose)
            return
        }

        // Remove the cached instance before showing it so a rapid second tap cannot reuse it.
        rewardedAds[purpose] = null
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                load(purpose)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                load(purpose)
                onAdUnavailable()
            }
        }

        ad.show(activity) {
            onRewardEarned()
        }
    }

    private fun load(purpose: RewardedAdPurpose) {
        if (rewardedAds[purpose] != null || !loadingPurposes.add(purpose)) return

        val adUnitId = when (purpose) {
            // With two production units, answer checks share the full-solution placement.
            RewardedAdPurpose.CHECK_ANSWER -> BuildConfig.ADMOB_REWARDED_CHECK_ANSWER_ID
            RewardedAdPurpose.REVEAL_LIAR -> BuildConfig.ADMOB_REWARDED_REVEAL_LIAR_ID
            RewardedAdPurpose.REVEAL_SOLUTION -> BuildConfig.ADMOB_REWARDED_REVEAL_SOLUTION_ID
        }

        RewardedAd.load(
            activity,
            adUnitId,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    loadingPurposes.remove(purpose)
                    rewardedAds[purpose] = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    loadingPurposes.remove(purpose)
                    rewardedAds[purpose] = null
                }
            }
        )
    }
}
