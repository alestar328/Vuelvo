package com.delta.vuelvo.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.delta.vuelvo.ui.icons.VuelvoIcons

enum class CommerceIcon { COFFEE, BREAD, SCISSORS, FORK, ICECREAM }

val CommerceIcon.vector: ImageVector
    get() = when (this) {
        CommerceIcon.COFFEE -> VuelvoIcons.Coffee
        CommerceIcon.BREAD -> VuelvoIcons.Bread
        CommerceIcon.SCISSORS -> VuelvoIcons.Scissors
        CommerceIcon.FORK -> VuelvoIcons.Fork
        CommerceIcon.ICECREAM -> VuelvoIcons.IceCream
    }

data class StampCard(
    val id: String,
    val name: String,
    val category: String,
    val icon: CommerceIcon,
    val tile: Color,
    val ink: Color,
    val stamps: Int,
    val max: Int,
    val reward: String,
) {
    val ready: Boolean get() = stamps >= max
}

enum class RewardStatus { AVAILABLE, REDEEMED }

data class Reward(
    val id: String,
    val cardId: String,
    val name: String,
    val commerce: String,
    val icon: CommerceIcon,
    val tile: Color,
    val ink: Color,
    val status: RewardStatus,
    val date: String,
)

/**
 * Initial catalogue seeded into Room on first launch. Card ids match the `vuelvo://stamp?id=…`
 * scheme used by the NFC tags and deep links, and reward `cardId`s reference these ids.
 */
object VuelvoData {
    val cards = listOf(
        StampCard("cafe-nostrum", "Cafè Nostrum", "Cafetería", CommerceIcon.COFFEE, Color(0xFFF3E9DF), Color(0xFF9A6A43), 7, 10, "Café gratis"),
        StampCard("forn-bonet", "Forn Bonet", "Panadería", CommerceIcon.BREAD, Color(0xFFF6EEDC), Color(0xFFB8862B), 4, 8, "Barra de pan gratis"),
        StampCard("studio-melic", "Studio Mèlic", "Peluquería", CommerceIcon.SCISSORS, Color(0xFFE9EDF1), Color(0xFF5C6B7B), 9, 10, "Corte de pelo gratis"),
        StampCard("la-taverna", "La Taverna", "Restaurante", CommerceIcon.FORK, Color(0xFFF6E7E1), Color(0xFFBC5A40), 2, 12, "Postre + café"),
        StampCard("gelats-pol", "Gelats Pol", "Heladería", CommerceIcon.ICECREAM, Color(0xFFF8E6EE), Color(0xFFCD5B8C), 10, 10, "Helado gratis"),
    )

    val rewards = listOf(
        Reward("reward-gelats-1", "gelats-pol", "Helado gratis", "Gelats Pol", CommerceIcon.ICECREAM, Color(0xFFF8E6EE), Color(0xFFCD5B8C), RewardStatus.AVAILABLE, "Hoy"),
        Reward("reward-cafe-1", "cafe-nostrum", "Café gratis", "Cafè Nostrum", CommerceIcon.COFFEE, Color(0xFFF3E9DF), Color(0xFF9A6A43), RewardStatus.REDEEMED, "12 may"),
        Reward("reward-melic-1", "studio-melic", "Corte de pelo gratis", "Studio Mèlic", CommerceIcon.SCISSORS, Color(0xFFE9EDF1), Color(0xFF5C6B7B), RewardStatus.REDEEMED, "3 abr"),
    )

    /** Hardcoded tag used as a fallback when the device has no NFC adapter (e.g. emulator). */
    const val FALLBACK_TAG_URI =
        "vuelvo://stamp?id=cafe-nostrum&name=Caf%C3%A8%20Nostrum&max=10&reward=Caf%C3%A9%20gratis"
}
