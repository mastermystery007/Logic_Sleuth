package com.example.ads

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
