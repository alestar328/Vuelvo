package com.delta.vuelvo.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.delta.vuelvo.data.Reward
import com.delta.vuelvo.data.StampCard
import com.delta.vuelvo.ui.icons.VuelvoIcons
import com.delta.vuelvo.ui.screens.CardDetail
import com.delta.vuelvo.ui.screens.CardsScreen
import com.delta.vuelvo.ui.screens.RedeemSheet
import com.delta.vuelvo.ui.screens.RewardsScreen
import com.delta.vuelvo.ui.screens.ScanScreen
import com.delta.vuelvo.ui.theme.VuAccent
import com.delta.vuelvo.ui.theme.VuAccentDeep
import com.delta.vuelvo.ui.theme.VuBg
import com.delta.vuelvo.ui.theme.VuCard
import com.delta.vuelvo.ui.theme.VuInk
import com.delta.vuelvo.ui.theme.VuInk2
import com.delta.vuelvo.ui.theme.VuInk3
import com.delta.vuelvo.ui.theme.VuLine

private enum class Tab { CARDS, SCAN, REWARDS }

@Composable
fun VuelvoApp() {
    val state = remember { VuelvoState() }
    var tab by remember { mutableStateOf(Tab.CARDS) }
    var openCard by remember { mutableStateOf<StampCard?>(null) }
    var redeem by remember { mutableStateOf<Reward?>(null) }

    val navInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val contentBottom = 104.dp + navInset

    Box(Modifier.fillMaxSize().background(VuBg)) {
        when (tab) {
            Tab.CARDS -> CardsScreen(state, onOpen = { openCard = it }, bottomInset = contentBottom)
            Tab.SCAN -> ScanScreen(onScan = { state.scan() }, bottomInset = contentBottom)
            Tab.REWARDS -> RewardsScreen(state, onRedeem = { redeem = it }, bottomInset = contentBottom)
        }

        TabBar(
            tab = tab,
            onSelect = { tab = it },
            modifier = Modifier.align(Alignment.BottomCenter),
        )

        val current = openCard?.let { oc -> state.cards.find { it.id == oc.id } ?: oc }
        if (current != null) {
            CardDetail(
                card = current,
                onClose = { openCard = null },
                onGoScan = { openCard = null; tab = Tab.SCAN },
            )
        }

        redeem?.let { r ->
            RedeemSheet(
                reward = r,
                onConfirm = { state.redeem(r); redeem = null },
                onClose = { redeem = null },
            )
        }
    }

    BackHandler(enabled = openCard != null || redeem != null || tab != Tab.CARDS) {
        when {
            redeem != null -> redeem = null
            openCard != null -> openCard = null
            else -> tab = Tab.CARDS
        }
    }
}

@Composable
private fun TabBar(tab: Tab, onSelect: (Tab) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .background(VuCard)
            .drawBehind {
                drawLine(VuLine, Offset(0f, 0f), Offset(size.width, 0f), strokeWidth = 1.dp.toPx())
            }
            .navigationBarsPadding()
            .padding(top = 10.dp, bottom = 14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        TabItem(Modifier.weight(1f), "Tarjetas", VuelvoIcons.StampCard, tab == Tab.CARDS) { onSelect(Tab.CARDS) }

        Column(
            Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                Modifier
                    .offset(y = (-22).dp)
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier
                        .size(62.dp)
                        .shadow(10.dp, CircleShape, spotColor = VuAccentDeep)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(VuAccent, VuAccentDeep)))
                        .clickable { onSelect(Tab.SCAN) },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(VuelvoIcons.Nfc, null, Modifier.size(32.dp), tint = Color.White)
                }
            }
            Text(
                "Escanear",
                modifier = Modifier.offset(y = (-14).dp),
                fontSize = 11.sp,
                fontWeight = if (tab == Tab.SCAN) FontWeight.ExtraBold else FontWeight.Bold,
                color = if (tab == Tab.SCAN) VuAccentDeep else VuInk2,
            )
        }

        TabItem(Modifier.weight(1f), "Recompensas", VuelvoIcons.Gift, tab == Tab.REWARDS) { onSelect(Tab.REWARDS) }
    }
}

@Composable
private fun TabItem(modifier: Modifier, label: String, icon: ImageVector, active: Boolean, onClick: () -> Unit) {
    Column(
        modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
        ) { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(icon, null, Modifier.size(26.dp), tint = if (active) VuAccentDeep else VuInk3)
        Text(
            label,
            fontSize = 11.sp,
            fontWeight = if (active) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = if (active) VuAccentDeep else VuInk3,
        )
    }
}
