package com.delta.vuelvo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.delta.vuelvo.data.StampCard
import com.delta.vuelvo.data.vector
import com.delta.vuelvo.ui.components.Header
import com.delta.vuelvo.ui.components.Stamps
import com.delta.vuelvo.ui.icons.VuelvoIcons
import com.delta.vuelvo.ui.theme.VuAccentDeep
import com.delta.vuelvo.ui.theme.VuAccentSoft
import com.delta.vuelvo.ui.theme.VuBg
import com.delta.vuelvo.ui.theme.VuCard
import com.delta.vuelvo.ui.theme.VuInk
import com.delta.vuelvo.ui.theme.VuInk2
import com.delta.vuelvo.ui.theme.VuInk3
import androidx.compose.ui.graphics.Color

@Composable
fun CardsScreen(
    cards: List<StampCard>,
    readyCount: Int,
    onOpen: (StampCard) -> Unit,
    onDelete: (StampCard) -> Unit,
    bottomInset: androidx.compose.ui.unit.Dp,
) {
    val subtitle = "${cards.size} comercios · $readyCount ${if (readyCount != 1) "recompensas listas" else "recompensa lista"}"
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(VuBg),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = bottomInset),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { Header("Tarjetas", subtitle) }
        items(cards, key = { it.id }) { card ->
            SwipeableCardRow(
                card = card,
                onOpen = onOpen,
                onDelete = onDelete,
                modifier = Modifier.animateItem(),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableCardRow(
    card: StampCard,
    onOpen: (StampCard) -> Unit,
    onDelete: (StampCard) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showConfirm by remember { mutableStateOf(false) }
    val dismissState = rememberSwipeToDismissBoxState(
        // Swiping left arms the confirmation dialog but never dismisses on its own —
        // returning false keeps the row in place and lets the dialog decide.
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                showConfirm = true
            }
            false
        },
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        backgroundContent = { DeleteBackground() },
    ) {
        CardRow(card, onOpen)
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Eliminar tarjeta", fontWeight = FontWeight.Bold) },
            text = {
                Text("¿Seguro que quieres eliminar “${card.name}”? Perderás sus ${card.stamps} ${if (card.stamps == 1) "sello" else "sellos"}.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showConfirm = false
                    onDelete(card)
                }) { Text("Eliminar", color = DeleteRed, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) { Text("Cancelar", color = VuInk2) }
            },
        )
    }
}

@Composable
private fun DeleteBackground() {
    Row(
        Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(DeleteRed)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
    ) {
        Icon(VuelvoIcons.Trash, "Eliminar", Modifier.size(26.dp), tint = Color.White)
    }
}

private val DeleteRed = Color(0xFFE5484D)

@Composable
private fun CardRow(card: StampCard, onOpen: (StampCard) -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = VuInk)
            .clip(RoundedCornerShape(24.dp))
            .background(VuCard)
            .clickable { onOpen(card) }
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 18.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        ) {
            Box(
                Modifier.size(46.dp).clip(RoundedCornerShape(14.dp)).background(card.tile),
                contentAlignment = Alignment.Center,
            ) {
                Icon(card.icon.vector, null, Modifier.size(24.dp), tint = card.ink)
            }
            Column(Modifier.weight(1f)) {
                Text(card.name, fontSize = 17.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.2).sp, color = VuInk)
                Text(card.category, fontSize = 13.5.sp, fontWeight = FontWeight.Medium, color = VuInk2)
            }
            if (card.ready) {
                Text(
                    "Lista ✦",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = VuAccentDeep,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(VuAccentSoft)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                )
            } else {
                Icon(VuelvoIcons.Chevron, null, Modifier.size(18.dp), tint = VuInk3)
            }
        }

        Stamps(count = card.stamps, max = card.max, size = 26.dp, gap = 9.dp)

        val left = card.max - card.stamps
        Row(
            modifier = Modifier.padding(top = 15.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row {
                Text("${card.stamps}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VuInk)
                Text("/${card.max}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VuInk3)
            }
            Text(
                if (card.ready) "Canjea tu ${card.reward.lowercase()}"
                else "A $left ${if (left > 1) "sellos" else "sello"} de tu ${card.reward.lowercase()}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = VuInk2,
            )
        }
    }
}
