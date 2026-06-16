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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.delta.vuelvo.ui.icons.VuelvoIcons
import com.delta.vuelvo.ui.screens.CardDetail
import com.delta.vuelvo.ui.screens.CardsScreen
import com.delta.vuelvo.ui.screens.RedeemFlow
import com.delta.vuelvo.ui.screens.RewardsScreen
import com.delta.vuelvo.ui.screens.ScanScreen
import com.delta.vuelvo.ui.screens.StampAddedModal
import com.delta.vuelvo.ui.theme.VuAccent
import com.delta.vuelvo.ui.theme.VuAccentDeep
import com.delta.vuelvo.ui.theme.VuBg
import com.delta.vuelvo.ui.theme.VuCard
import com.delta.vuelvo.ui.theme.VuInk
import com.delta.vuelvo.ui.theme.VuInk2
import com.delta.vuelvo.ui.theme.VuInk3
import com.delta.vuelvo.ui.theme.VuLine
import com.delta.vuelvo.ui.viewmodel.AppViewModel
import com.delta.vuelvo.ui.viewmodel.CardDetailViewModel
import com.delta.vuelvo.ui.viewmodel.CardsViewModel
import com.delta.vuelvo.ui.viewmodel.RewardsViewModel
import com.delta.vuelvo.ui.viewmodel.ScanViewModel
import com.delta.vuelvo.ui.viewmodel.Tab

@Composable
fun VuelvoApp(appViewModel: AppViewModel = hiltViewModel()) {
    val cardsViewModel: CardsViewModel = hiltViewModel()
    val rewardsViewModel: RewardsViewModel = hiltViewModel()
    val scanViewModel: ScanViewModel = hiltViewModel()

    val cards by cardsViewModel.cards.collectAsStateWithLifecycle()
    val readyCount by cardsViewModel.readyCount.collectAsStateWithLifecycle()
    val available by rewardsViewModel.available.collectAsStateWithLifecycle()
    val history by rewardsViewModel.history.collectAsStateWithLifecycle()

    val tab = appViewModel.tab
    val openCardId = appViewModel.openCardId
    val redeem = appViewModel.redeemReward

    val navInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    // Leave room for the opaque tab-bar surface (BarContentHeight + system nav) plus a margin.
    val contentBottom = BarContentHeight + navInset + 16.dp

    Box(Modifier.fillMaxSize().background(VuBg)) {
        when (tab) {
            Tab.CARDS -> CardsScreen(
                cards = cards,
                readyCount = readyCount,
                onOpen = { appViewModel.openCard(it.id) },
                bottomInset = contentBottom,
            )
            Tab.SCAN -> ScanScreen(
                onScan = scanViewModel::simulateScan,
                pendingScan = appViewModel.pendingExternalScan,
                onPendingConsumed = { appViewModel.consumeExternalScan() },
                onReward = { appViewModel.showRewardCelebration(it) },
                onClose = { appViewModel.selectTab(Tab.CARDS) },
                bottomInset = contentBottom,
            )
            Tab.REWARDS -> RewardsScreen(
                available = available,
                history = history,
                onRedeem = { appViewModel.showRedeem(it) },
                bottomInset = contentBottom,
            )
        }

        TabBar(
            tab = tab,
            onSelect = { appViewModel.selectTab(it) },
            modifier = Modifier.align(Alignment.BottomCenter),
        )

        if (openCardId != null) {
            CardDetailRoute(
                cardId = openCardId,
                onClose = { appViewModel.closeCard() },
                onGoScan = { appViewModel.closeCard(); appViewModel.selectTab(Tab.SCAN) },
            )
        }

        redeem?.let { r ->
            RedeemFlow(
                reward = r,
                onConfirm = { rewardsViewModel.confirmRedeem(r); appViewModel.dismissRedeem() },
                onClose = { appViewModel.dismissRedeem() },
            )
        }

        // Full-screen celebration sits above the tab bar so its buttons aren't clipped.
        appViewModel.rewardCelebration?.let { r ->
            StampAddedModal(
                result = r,
                onClose = { appViewModel.dismissRewardCelebration() },
                onViewReward = {
                    appViewModel.dismissRewardCelebration()
                    appViewModel.selectTab(Tab.REWARDS)
                },
            )
        }
    }

    BackHandler(
        enabled = openCardId != null || redeem != null ||
            appViewModel.rewardCelebration != null || tab != Tab.CARDS,
    ) {
        when {
            appViewModel.rewardCelebration != null -> appViewModel.dismissRewardCelebration()
            redeem != null -> appViewModel.dismissRedeem()
            openCardId != null -> appViewModel.closeCard()
            else -> appViewModel.selectTab(Tab.CARDS)
        }
    }
}

@Composable
private fun CardDetailRoute(
    cardId: String,
    onClose: () -> Unit,
    onGoScan: () -> Unit,
    viewModel: CardDetailViewModel = hiltViewModel(),
) {
    androidx.compose.runtime.LaunchedEffect(cardId) { viewModel.setCardId(cardId) }
    val card by viewModel.card.collectAsStateWithLifecycle()
    card?.let { CardDetail(card = it, onClose = onClose, onGoScan = onGoScan) }
}

@Composable
private fun TabBar(tab: Tab, onSelect: (Tab) -> Unit, modifier: Modifier = Modifier) {
    val navInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Box(modifier.fillMaxWidth().height(FabOverhang + BarContentHeight + navInset)) {
        // opaque bar surface, pinned to the bottom
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(BarContentHeight + navInset)
                .background(VuCard)
                .drawBehind {
                    drawLine(VuLine, Offset(0f, 0f), Offset(size.width, 0f), strokeWidth = 1.dp.toPx())
                }
                .padding(top = 6.dp, bottom = navInset + 6.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            TabItem(Modifier.weight(1f), "Tarjetas", VuelvoIcons.StampCard, tab == Tab.CARDS) { onSelect(Tab.CARDS) }
            // center slot holds just the label; the button is overlaid above
            Box(Modifier.weight(1f), contentAlignment = Alignment.BottomCenter) {
                Text(
                    "Escanear",
                    fontSize = 11.sp,
                    fontWeight = if (tab == Tab.SCAN) FontWeight.ExtraBold else FontWeight.Bold,
                    color = if (tab == Tab.SCAN) VuAccentDeep else VuInk2,
                )
            }
            TabItem(Modifier.weight(1f), "Recompensas", VuelvoIcons.Gift, tab == Tab.REWARDS) { onSelect(Tab.REWARDS) }
        }

        // raised scan button — overflows above the bar but stays inside the box, so it's tappable
        Box(
            Modifier
                .align(Alignment.TopCenter)
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
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onSelect(Tab.SCAN) },
                contentAlignment = Alignment.Center,
            ) {
                Icon(VuelvoIcons.Nfc, null, Modifier.size(32.dp), tint = Color.White)
            }
        }
    }
}

/** Opaque tab-bar surface height (excludes the system nav inset, added separately). */
private val BarContentHeight = 66.dp

/** How far the raised scan button overflows above the bar surface. */
private val FabOverhang = 28.dp

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
