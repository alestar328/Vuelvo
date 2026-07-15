package com.delta.vuelvo.ui.components

import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.delta.vuelvo.data.CommerceIcon
import com.delta.vuelvo.data.StampCard
import com.delta.vuelvo.data.vector

/**
 * Renders the comercio's logo image if the scanned tag included one, falling back to the
 * commerce [icon] otherwise. Used anywhere a card's representative icon tile is shown
 * (card list, card detail, rewards, redeem pass, stamp-added modal).
 *
 * Owns the tile frame ([shape] + [background] + optional [elevation]): logos with an alpha channel
 * (circular marks, wordmarks) render frameless and uncropped so their transparency shows through,
 * while opaque photos and the fallback icon keep the classic rounded tile.
 */
@Composable
fun CardLogoOrSymbol(
    logoUrl: String?,
    icon: CommerceIcon,
    size: Dp,
    symbolSize: Dp,
    shape: Shape,
    background: Color,
    symbolTint: Color,
    elevation: Dp = 0.dp,
) {
    var logoHasAlpha by remember(logoUrl) { mutableStateOf(false) }
    Box(
        Modifier
            .size(size)
            .then(
                if (logoHasAlpha) Modifier
                else Modifier.shadow(elevation, shape).clip(shape).background(background)
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (logoUrl != null) {
            SubcomposeAsyncImage(
                model = logoUrl,
                contentDescription = null,
                contentScale = if (logoHasAlpha) ContentScale.Fit else ContentScale.Crop,
                modifier = Modifier.size(size),
                loading = { Fallback(icon, symbolSize, symbolTint) },
                error = { Fallback(icon, symbolSize, symbolTint) },
                onSuccess = { state ->
                    logoHasAlpha =
                        (state.result.drawable as? BitmapDrawable)?.bitmap?.hasAlpha() == true
                },
            )
        } else {
            Fallback(icon, symbolSize, symbolTint)
        }
    }
}

@Composable
fun CardLogoOrSymbol(
    card: StampCard,
    size: Dp,
    symbolSize: Dp,
    shape: Shape,
    background: Color,
    elevation: Dp = 0.dp,
) = CardLogoOrSymbol(
    logoUrl = card.logoUrl,
    icon = card.icon,
    size = size,
    symbolSize = symbolSize,
    shape = shape,
    background = background,
    symbolTint = card.ink,
    elevation = elevation,
)

@Composable
private fun Fallback(icon: CommerceIcon, symbolSize: Dp, tint: Color) {
    Icon(icon.vector, null, Modifier.size(symbolSize), tint = tint)
}
