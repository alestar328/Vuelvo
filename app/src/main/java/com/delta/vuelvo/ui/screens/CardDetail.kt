package com.delta.vuelvo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.delta.vuelvo.data.StampCard
import com.delta.vuelvo.data.vector
import com.delta.vuelvo.ui.components.Stamps
import com.delta.vuelvo.ui.icons.VuelvoIcons
import com.delta.vuelvo.ui.theme.VuAccent
import com.delta.vuelvo.ui.theme.VuAccentDeep
import com.delta.vuelvo.ui.theme.VuAccentSoft
import com.delta.vuelvo.ui.theme.VuBg
import com.delta.vuelvo.ui.theme.VuCard
import com.delta.vuelvo.ui.theme.VuInk
import com.delta.vuelvo.ui.theme.VuInk2
import com.delta.vuelvo.ui.theme.VuInk3
import com.delta.vuelvo.ui.theme.VuStampEmpty

@Composable
fun CardDetail(card: StampCard, onClose: () -> Unit, onGoScan: () -> Unit) {
    val left = card.max - card.stamps
    val ready = card.ready
    val pct = if (card.max == 0) 0f else card.stamps.toFloat() / card.max

    Column(
        Modifier
            .fillMaxSize()
            .background(VuBg)
            .verticalScroll(rememberScrollState()),
    ) {
        // hero
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                .background(Brush.linearGradient(listOf(card.tile, Color.White)))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 26.dp),
        ) {
            Box(
                Modifier
                    .size(40.dp)
                    .shadow(4.dp, RoundedCornerShape(999.dp))
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.White.copy(alpha = 0.7f))
                    .clickable { onClose() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(VuelvoIcons.ChevronLeft, null, Modifier.size(20.dp), tint = VuInk)
            }
            Row(
                modifier = Modifier.padding(top = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(
                    Modifier.size(58.dp).shadow(6.dp, RoundedCornerShape(17.dp)).clip(RoundedCornerShape(17.dp)).background(Color.White),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(card.icon.vector, null, Modifier.size(30.dp), tint = card.ink)
                }
                Column {
                    Text(card.name, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp, color = VuInk)
                    Text(card.category, fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold, color = VuInk2)
                }
            }
        }

        Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 130.dp)) {
            // stamp card
            Column(
                Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(26.dp), spotColor = VuInk)
                    .clip(RoundedCornerShape(26.dp))
                    .background(VuCard)
                    .padding(horizontal = 22.dp, vertical = 24.dp),
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(bottom = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Text("TU TARJETA", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.4.sp, color = VuInk2)
                    Row {
                        Text("${card.stamps}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = VuInk)
                        Text("/${card.max}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = VuInk3)
                    }
                }
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Stamps(count = card.stamps, max = card.max, size = 40.dp, gap = 14.dp, accentEmpty = true)
                }
            }

            // reward callout
            Row(
                Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        if (ready) Brush.linearGradient(listOf(VuAccent, VuAccentDeep))
                        else Brush.linearGradient(listOf(VuAccentSoft, VuAccentSoft)),
                    )
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(
                    Modifier.size(44.dp).clip(RoundedCornerShape(13.dp))
                        .background(if (ready) Color.White.copy(alpha = 0.2f) else Color.White),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(VuelvoIcons.Gift, null, Modifier.size(24.dp), tint = if (ready) Color.White else VuAccentDeep)
                }
                Column(Modifier.weight(1f)) {
                    Text(card.reward, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = if (ready) Color.White else VuInk)
                    Text(
                        if (ready) "¡Lista para canjear ahora!"
                        else "Te ${if (left == 1) "falta" else "faltan"} $left ${if (left > 1) "sellos" else "sello"}",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (ready) Color.White.copy(alpha = 0.95f) else VuInk2,
                    )
                }
            }

            // progress bar
            if (!ready) {
                Box(
                    Modifier
                        .padding(top = 22.dp)
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(99.dp))
                        .background(VuStampEmpty),
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(pct)
                            .height(8.dp)
                            .clip(RoundedCornerShape(99.dp))
                            .background(Brush.linearGradient(listOf(VuAccent, VuAccentDeep))),
                    )
                }
            }

            // scan CTA
            Row(
                Modifier
                    .padding(top = 22.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(VuInk)
                    .clickable { onGoScan() }
                    .padding(vertical = 15.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(VuelvoIcons.Nfc, null, Modifier.size(21.dp), tint = Color.White)
                Spacer(Modifier.size(9.dp))
                Text("Escanear para sumar un sello", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
