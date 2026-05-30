package com.delta.vuelvo.ui

import androidx.compose.runtime.mutableStateListOf
import com.delta.vuelvo.data.Reward
import com.delta.vuelvo.data.RewardStatus
import com.delta.vuelvo.data.StampCard
import com.delta.vuelvo.data.VuelvoData

data class ScanResult(
    val gotReward: Boolean,
    val cardName: String,
    val reward: String?,
    val newCount: Int,
    val max: Int,
)

/** In-memory app state mirroring the prototype's state machine (vuelvo-app.jsx). */
class VuelvoState {
    val cards = mutableStateListOf<StampCard>().also { it.addAll(VuelvoData.cards) }
    val rewards = mutableStateListOf<Reward>().also { it.addAll(VuelvoData.rewards) }

    val readyCount: Int
        get() = cards.count { it.ready } + rewards.count { it.status == RewardStatus.AVAILABLE }

    /** Simulates an NFC tap that adds a stamp to the "cafe" card. */
    fun scan(): ScanResult {
        val i = cards.indexOfFirst { it.id == "cafe" }
        val c = cards[i]
        val next = c.stamps + 1
        return if (next >= c.max) {
            cards[i] = c.copy(stamps = 0)
            rewards.add(
                0,
                Reward(
                    id = "rn${System.currentTimeMillis()}",
                    cardId = c.id,
                    name = c.reward,
                    commerce = c.name,
                    icon = c.icon,
                    tile = c.tile,
                    ink = c.ink,
                    status = RewardStatus.AVAILABLE,
                    date = "Hoy",
                ),
            )
            ScanResult(true, c.name, c.reward, c.max, c.max)
        } else {
            cards[i] = c.copy(stamps = next)
            ScanResult(false, c.name, null, next, c.max)
        }
    }

    fun redeem(reward: Reward) {
        val ri = rewards.indexOfFirst { it.id == reward.id }
        if (ri >= 0) rewards[ri] = rewards[ri].copy(status = RewardStatus.REDEEMED, date = "Hoy")
        val ci = cards.indexOfFirst { it.id == reward.cardId }
        if (ci >= 0) cards[ci] = cards[ci].copy(stamps = 0)
    }
}
