package com.delta.vuelvo.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/**
 * ¿El color de tarjeta que eligió el comercio es demasiado oscuro para la tinta oscura por defecto?
 *
 * El comercio ofrece Negro entre sus colores y el hex viaja en el tag (`tile=`), así que la decisión
 * se toma por luminancia en vez de duplicar aquí la tabla de colores del comercio: cualquier color
 * que añadan allá — o que mande iOS — se resuelve solo. Sobre estas tarjetas se pinta tinta blanca,
 * el mismo tratamiento que ya usaban las que llevan foto de portada.
 */
fun Color.isDarkSurface(): Boolean = luminance() < 0.4f
