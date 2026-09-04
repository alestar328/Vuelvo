package com.delta.vuelvo.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.delta.vuelvo.data.StampCard
import com.delta.vuelvo.ui.components.CardLogoOrSymbol
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
import com.delta.vuelvo.ui.theme.isDarkSurface

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
        val hasCover = card.coverUrl != null
        // El color del comercio puede ser oscuro (Negro): entonces el hero va con tinta blanca,
        // igual que cuando hay foto de portada.
        val darkHero = hasCover || card.tile.isDarkSurface()
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)),
        ) {
            // backdrop: the comercio's cover photo (darkened for legibility) if the tag included one,
            // else the existing tile-color gradient
            if (card.coverUrl != null) {
                AsyncImage(
                    model = card.coverUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                )
                Box(
                    Modifier
                        .matchParentSize()
                        .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.4f), Color.Black.copy(alpha = 0.1f)))),
                )
            } else {
                Box(Modifier.matchParentSize().background(card.tile))
            }

            Column(
                Modifier
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
                    CardLogoOrSymbol(
                        card = card,
                        size = 58.dp,
                        symbolSize = 30.dp,
                        shape = RoundedCornerShape(17.dp),
                        background = if (darkHero && !hasCover) Color.White.copy(alpha = 0.14f) else Color.White,
                        elevation = 6.dp,
                    )
                    Column {
                        Text(
                            card.name, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp, color = if (darkHero) Color.White else VuInk,
                        )
                        Text(
                            card.category, fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold,
                            color = if (darkHero) Color.White.copy(alpha = 0.85f) else VuInk2,
                        )
                    }
                }
                // Datos de contacto del comercio (`addr=`/`tel=` del tag). Van bajo el nombre, a lo
                // ancho de la cabecera: en la columna del logo el texto quedaría partido en dos líneas.
                ContactLines(card = card, darkHero = darkHero)
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
                    // El premio que el comercio escribió en su tag (`reward=`); "Recompensa" solo
                    // aparece en tags viejos, escritos antes de que el campo fuera editable.
                    Text(
                        card.reward.ifBlank { "Recompensa" },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (ready) Color.White else VuInk,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
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

/**
 * Dirección y teléfono del comercio en la cabecera, cada uno con su icono y pulsable: la dirección
 * abre la app de mapas y el teléfono ofrece llamar o escribir por WhatsApp. No se pinta nada si el
 * comercio no rellenó ninguno de los dos — la mayoría de tags antiguos no los llevan.
 */
@Composable
private fun ContactLines(card: StampCard, darkHero: Boolean) {
    val address = card.address?.takeIf { it.isNotBlank() }
    val phone = card.phone?.takeIf { it.isNotBlank() }
    if (address == null && phone == null) return

    val context = LocalContext.current
    var askPhoneAction by remember { mutableStateOf(false) }
    val tint = if (darkHero) Color.White.copy(alpha = 0.9f) else VuInk2
    // Pastilla translúcida: sobre la foto o el color de la cabecera es lo que delata que se puede tocar.
    val pill = if (darkHero) Color.White.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.55f)

    Column(
        Modifier.padding(top = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (address != null) {
            ContactLine(VuelvoIcons.MapPin, address, tint, pill) { context.openInMaps(address) }
        }
        if (phone != null) {
            ContactLine(VuelvoIcons.Phone, phone, tint, pill) { askPhoneAction = true }
        }
    }

    if (phone != null && askPhoneAction) {
        PhoneActionsDialog(phone = phone, onDismiss = { askPhoneAction = false })
    }
}

@Composable
private fun ContactLine(
    icon: ImageVector,
    text: String,
    tint: Color,
    background: Color,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(icon, null, Modifier.size(16.dp), tint = tint)
        Text(
            text,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = tint,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Llamar o abrir WhatsApp con el teléfono del comercio. */
@Composable
private fun PhoneActionsDialog(phone: String, onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Contactar", fontWeight = FontWeight.Bold) },
        text = { Text(phone) },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = { onDismiss(); context.openWhatsApp(phone) }) {
                    Text("WhatsApp", color = VuAccentDeep, fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = { onDismiss(); context.dialPhone(phone) }) {
                    Text("Llamar", color = VuAccentDeep, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = VuInk2) }
        },
    )
}

/**
 * Lanza el primero de [intents] que alguna app sepa atender. Se prueba lanzándolos, no con
 * `resolveActivity`: desde Android 11 esa consulta está filtrada por visibilidad de paquetes y
 * devolvería null para apps perfectamente instaladas.
 */
private fun Context.startFirst(vararg intents: Intent, noneMessage: String) {
    for (intent in intents) {
        if (runCatching { startActivity(intent) }.isSuccess) return
    }
    Toast.makeText(this, noneMessage, Toast.LENGTH_SHORT).show()
}

/** Abre la dirección del comercio en la app de mapas del móvil, o en Google Maps web como plan B. */
private fun Context.openInMaps(address: String) {
    val query = Uri.encode(address)
    startFirst(
        Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$query")),
        Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=$query")),
        noneMessage = "No se pudo abrir el mapa",
    )
}

/** Abre el marcador con el número puesto — ACTION_DIAL no necesita permiso de llamada. */
private fun Context.dialPhone(phone: String) {
    val number = phone.filter { it.isDigit() || it == '+' }
    startFirst(
        Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")),
        noneMessage = "Este dispositivo no puede llamar",
    )
}

/** Abre el chat de WhatsApp con el comercio; sin WhatsApp instalado, wa.me se abre en el navegador. */
private fun Context.openWhatsApp(phone: String) {
    startFirst(
        Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${whatsAppNumber(phone)}")),
        noneMessage = "No se pudo abrir WhatsApp",
    )
}

/**
 * Número tal cual lo pide wa.me: solo dígitos, prefijo de país incluido y sin `+`. El tag ya trae el
 * teléfono internacionalizado por el comercio (`+34 600123456`), así que aquí no se adivina ningún
 * país — Vuelvo se usa en España, Argentina, Perú…, y suponer uno mandaría al cliente a un número
 * ajeno. Un tag viejo, escrito sin prefijo, es lo único que puede acabar en un número inválido: en
 * ese caso WhatsApp (o el navegador) lo dice, en vez de abrir un chat equivocado.
 */
private fun whatsAppNumber(phone: String): String = phone.filter { it.isDigit() }
