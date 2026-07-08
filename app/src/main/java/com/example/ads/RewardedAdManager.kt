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

    init {
        load(RewardedAdPurpose.CHECK_ANSWER)
        load(RewardedAdPurpose.REVEAL_SOLUTION)
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

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedAds[purpose] = null
                load(purpose)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                rewardedAds[purpose] = null
                load(purpose)
                onAdUnavailable()
            }
        }

        ad.show(activity) {
            onRewardEarned()
        }
    }

    private fun load(purpose: RewardedAdPurpose) {
        val adUnitId = when (purpose) {
            RewardedAdPurpose.CHECK_ANSWER -> BuildConfig.ADMOB_REWARDED_CHECK_ANSWER_ID
            RewardedAdPurpose.REVEAL_SOLUTION -> BuildConfig.ADMOB_REWARDED_REVEAL_SOLUTION_ID
        }

        RewardedAd.load(
            activity,
            adUnitId,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAds[purpose] = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAds[purpose] = null
                }
            }
        )
    }
}
