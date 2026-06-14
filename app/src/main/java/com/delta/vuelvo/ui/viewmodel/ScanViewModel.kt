package com.delta.vuelvo.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.delta.vuelvo.data.VuelvoData
import com.delta.vuelvo.data.repository.VuelvoRepository
import com.delta.vuelvo.domain.ScanResult
import com.delta.vuelvo.nfc.StampPayload
import com.delta.vuelvo.nfc.VuelvoUri
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val repository: VuelvoRepository,
) : ViewModel() {

    /** Applies a stamp from a real scanned payload. */
    suspend fun scan(payload: StampPayload): ScanResult = repository.applyStamp(payload)

    /**
     * Emulator / no-NFC fallback: applies the hardcoded test tag so the scan UI can be
     * exercised by tapping the target.
     */
    suspend fun simulateScan(): ScanResult {
        val payload = VuelvoUri.parse(VuelvoData.FALLBACK_TAG_URI)
            ?: error("Invalid fallback tag URI")
        return repository.applyStamp(payload)
    }
}
