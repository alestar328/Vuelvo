package com.delta.vuelvo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delta.vuelvo.data.RewardStatus
import com.delta.vuelvo.data.StampCard
import com.delta.vuelvo.data.repository.VuelvoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CardsViewModel @Inject constructor(
    repository: VuelvoRepository,
) : ViewModel() {

    val cards: StateFlow<List<StampCard>> = repository.cards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Cards completed plus rewards already waiting to be redeemed. */
    val readyCount: StateFlow<Int> = combine(repository.cards, repository.rewards) { cards, rewards ->
        cards.count { it.ready } + rewards.count { it.status == RewardStatus.AVAILABLE }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
}
