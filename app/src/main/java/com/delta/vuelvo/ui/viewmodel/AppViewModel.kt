package com.delta.vuelvo.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delta.vuelvo.data.Reward
import com.delta.vuelvo.data.repository.VuelvoRepository
import com.delta.vuelvo.domain.ScanResult
import com.delta.vuelvo.nfc.StampPayload
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class Tab { CARDS, SCAN, REWARDS }

/** A scan pushed in from NFC / deep link. [id] makes each event distinct so the UI replays it. */
data class ExternalScan(val id: Long, val result: ScanResult)

/**
 * Activity-scoped coordinator: owns navigation/dialog state and bridges externally
 * triggered stamps (NFC tags, deep links) into the scan UI via [externalScans].
 */
@HiltViewModel
class AppViewModel @Inject constructor(
    private val repository: VuelvoRepository,
) : ViewModel() {

    var tab by mutableStateOf(Tab.CARDS)
        private set
    var openCardId by mutableStateOf<String?>(null)
        private set
    var redeemReward by mutableStateOf<Reward?>(null)
        private set

    /** Full-screen "recompensa conseguida" celebration, shown above the tab bar. */
    var rewardCelebration by mutableStateOf<ScanResult?>(null)
        private set

    /** Set when a stamp is applied from outside the scan screen (NFC / deep link). */
    var pendingExternalScan by mutableStateOf<ExternalScan?>(null)
        private set
    private var scanCounter = 0L

    fun selectTab(value: Tab) { tab = value }
    fun openCard(id: String) { openCardId = id }
    fun closeCard() { openCardId = null }
    fun showRedeem(reward: Reward) { redeemReward = reward }
    fun dismissRedeem() { redeemReward = null }
    fun showRewardCelebration(result: ScanResult) { rewardCelebration = result }
    fun dismissRewardCelebration() { rewardCelebration = null }
    fun consumeExternalScan() { pendingExternalScan = null }

    /** Single entry point for NFC tags and `vuelvo://` deep links. */
    fun onStampPayload(payload: StampPayload) {
        viewModelScope.launch {
            val result = repository.applyStamp(payload)
            openCardId = null
            redeemReward = null
            tab = Tab.SCAN
            pendingExternalScan = ExternalScan(scanCounter++, result)
        }
    }
}
