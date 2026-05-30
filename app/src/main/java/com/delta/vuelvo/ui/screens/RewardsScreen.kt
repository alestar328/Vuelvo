package com.delta.vuelvo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.delta.vuelvo.data.Reward
import com.delta.vuelvo.data.RewardStatus
import com.delta.vuelvo.data.vector
import com.delta.vuelvo.ui.VuelvoState
import com.delta.vuelvo.ui.components.Header
import com.delta.vuelvo.ui.icons.VuelvoIcons
import com.delta.vuelvo.ui.theme.VuAccent
import com.delta.vuelvo.ui.theme.VuAccentDeep
import com.delta.vuelvo.ui.theme.VuAccentSoft
import com.delta.vuelvo.ui.theme.VuBg
import com.delta.vuelvo.ui.theme.VuCard
import com.delta.vuelvo.ui.theme.VuInk
import com.delta.vuelvo.ui.theme.VuInk2
import com.delta.vuelvo.ui.theme.VuInk3
import com.delta.vuelvo.ui.theme.VuLine

@Composable
fun RewardsScreen(
    state: VuelvoState,
    onRedeem: (Reward) -> Unit,
    bottomInset: androidx.compose.ui.unit.Dp,
) {
    val available = state.rewards.filter { it.status == RewardStatus.AVAILABLE }
    val history = state.rewards.filter { it.status == RewardStatus.REDEEMED }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(VuBg),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = bottomInset),
    ) {
        item {
            Header(
                "Recompensas",
                "${available.size} ${if (available.size != 1) "disponibles" else "disponible"} para canjear",
            )
        }

        if (available.isNotEmpty()) {
            item {
                Column(
                    Modifier.padding(bottom = 30.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    available.forEach { RewardCard(it, onRedeem) }
                }
            }
        } else {
            item { EmptyRewards() }
        }

        if (history.isNotEmpty()) {
            item {
                Text(
                    "HISTORIAL",
                    modifier = Modifier.padding(start = 4.dp, bottom = 6.dp),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = VuInk3,
                )
                Column(
                    Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(22.dp), spotColor = VuInk)
                        .clip(RoundedCornerShape(22.dp))
                        .background(VuCard)
                        .padding(horizontal = 16.dp),
                ) {
                    history.forEachIndexed { i, r -> HistoryRow(r, last = i == history.lastIndex) }
                }
            }
        }
    }
}

@Composable
private fun RewardCard(r: Reward, onRedeem: (Reward) -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(22.dp), spotColor = VuInk)
            .clip(RoundedCornerShape(22.dp))
            .background(VuCard),
    ) {
        Box(
            Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(Brush.verticalGradient(listOf(VuAccent, VuAccentDeep))),
        )
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                Modifier.size(50.dp).clip(RoundedCornerShape(15.dp)).background(r.tile),
                contentAlignment = Alignment.Center,
            ) {
                Icon(r.icon.vector, null, Modifier.size(26.dp), tint = r.ink)
            }
            Column(Modifier.weight(1f)) {
                Text(r.name, fontSize = 16.5.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.3).sp, color = VuInk)
                Text(r.commerce, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold, color = VuInk2)
            }
            Text(
                "Canjear",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .clip(RoundedCornerShape(13.dp))
                    .background(VuInk)
                    .clickable { onRedeem(r) }
                    .padding(horizontal = 18.dp, vertical = 11.dp),
            )
        }
    }
}

@Composable
private fun HistoryRow(r: Reward, last: Boolean) {
    Column {
        Row(
            Modifier.fillMaxWidth().padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            Box(
                Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(VuBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(r.icon.vector, null, Modifier.size(22.dp), tint = VuInk3)
            }
            Column(Modifier.weight(1f)) {
                Text(r.name, fontSize = 15.5.sp, fontWeight = FontWeight.Bold, color = VuInk)
                Text(r.commerce, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = VuInk3)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(r.date, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VuInk3)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(VuelvoIcons.Check, null, Modifier.size(12.dp), tint = VuInk3)
                    Text("Canjeada", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VuInk3)
                }
            }
        }
        if (!last) Box(Modifier.fillMaxWidth().height(1.dp).background(VuLine))
    }
}

@Composable
private fun EmptyRewards() {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 30.dp)
            .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = VuInk)
            .clip(RoundedCornerShape(24.dp))
            .background(VuCard)
            .padding(horizontal = 24.dp, vertical = 34.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier.size(56.dp).clip(RoundedCornerShape(17.dp)).background(VuAccentSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(VuelvoIcons.Sparkle, null, Modifier.size(28.dp), tint = VuAccentDeep)
        }
        Text(
            "Aún no hay recompensas listas",
            modifier = Modifier.padding(top = 14.dp),
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            color = VuInk,
        )
        Text(
            "Sigue sumando sellos y aquí aparecerán tus premios.",
            modifier = Modifier.padding(top = 6.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = VuInk2,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}
