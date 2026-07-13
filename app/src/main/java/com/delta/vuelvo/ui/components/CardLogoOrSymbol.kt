package com.delta.vuelvo.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil.compose.SubcomposeAsyncImage
import com.delta.vuelvo.data.StampCard
import com.delta.vuelvo.data.vector

/**
 * Renders the comercio's logo image if the scanned tag included one, falling back to the icon
 * otherwise. Used anywhere a [StampCard]'s icon tile is shown (card list, card detail).
 */
@Composable
fun CardLogoOrSymbol(card: StampCard, size: Dp, symbolSize: Dp) {
    val url = card.logoUrl
    if (url != null) {
        SubcomposeAsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(size),
            loading = { Fallback(card, symbolSize) },
            error = { Fallback(card, symbolSize) },
        )
    } else {
        Fallback(card, symbolSize)
    }
}

@Composable
private fun Fallback(card: StampCard, symbolSize: Dp) {
    Icon(card.icon.vector, null, Modifier.size(symbolSize), tint = card.ink)
}
