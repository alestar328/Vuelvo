package com.delta.vuelvo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delta.vuelvo.data.StampCard
import com.delta.vuelvo.data.repository.VuelvoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CardDetailViewModel @Inject constructor(
    private val repository: VuelvoRepository,
) : ViewModel() {

    private val cardId = MutableStateFlow<String?>(null)

    val card: StateFlow<StampCard?> = cardId
        .flatMapLatest { id -> if (id == null) flowOf(null) else repository.card(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun setCardId(id: String) { cardId.value = id }
}
