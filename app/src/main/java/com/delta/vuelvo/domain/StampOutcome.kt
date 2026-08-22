package com.delta.vuelvo.domain

/** Result of trying to apply a stamp — either it went through, or the comercio isn't active. */
sealed class StampOutcome {
    data class Applied(val result: ScanResult) : StampOutcome()
    object Blocked : StampOutcome()
}
