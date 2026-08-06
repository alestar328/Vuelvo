package com.delta.vuelvo.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.delta.vuelvo.data.repository.VuelvoRepository
import com.delta.vuelvo.domain.ScanResult
import com.delta.vuelvo.nfc.StampPayload
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val repository: VuelvoRepository,
) : ViewModel() {

    /** Applies a stamp from a real scanned payload. */
    suspend fun scan(payload: StampPayload): ScanResult = repository.applyStamp(payload)
}
