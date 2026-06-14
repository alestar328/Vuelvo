package com.delta.vuelvo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delta.vuelvo.data.Reward
import com.delta.vuelvo.data.RewardStatus
import com.delta.vuelvo.data.repository.VuelvoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RewardsViewModel @Inject constructor(
    private val repository: VuelvoRepository,
) : ViewModel() {

    val available: StateFlow<List<Reward>> = repository.rewards
        .map { list -> list.filter { it.status == RewardStatus.AVAILABLE } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val history: StateFlow<List<Reward>> = repository.rewards
        .map { list -> list.filter { it.status == RewardStatus.REDEEMED } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun confirmRedeem(reward: Reward) {
        viewModelScope.launch { repository.confirmRedeem(reward) }
    }
}
