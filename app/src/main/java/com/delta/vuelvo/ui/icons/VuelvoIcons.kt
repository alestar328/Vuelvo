package com.delta.vuelvo.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/**
 * Line-icon set recreated from the design bundle (vuelvo-icons.jsx). Every icon is
 * a 24x24 stroke vector drawn in black so callers can recolor it via Icon(tint = ...).
 */
private fun strokeIcon(vararg d: String, sw: Float = 2f): ImageVector =
    ImageVector.Builder(
        defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f,
    ).apply {
        d.forEach {
            addPath(
                pathData = addPathNodes(it),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = sw,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
        }
    }.build()

private fun mixedIcon(strokes: List<String>, fills: List<String>, sw: Float = 2f): ImageVector =
    ImageVector.Builder(
        defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f,
    ).apply {
        strokes.forEach {
            addPath(
                pathData = addPathNodes(it),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = sw,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
        }
        fills.forEach {
            addPath(pathData = addPathNodes(it), fill = SolidColor(Color.Black))
        }
    }.build()

private fun circle(cx: Double, cy: Double, r: Double) =
    "M ${cx - r} $cy a $r $r 0 1 0 ${2 * r} 0 a $r $r 0 1 0 ${-2 * r} 0"

private fun rrect(x: Double, y: Double, w: Double, h: Double, rx: Double): String {
    val ix = w - 2 * rx
    val iy = h - 2 * rx
    return "M ${x + rx} $y h $ix a $rx $rx 0 0 1 $rx $rx v $iy a $rx $rx 0 0 1 ${-rx} $rx " +
        "h ${-ix} a $rx $rx 0 0 1 ${-rx} ${-rx} v ${-iy} a $rx $rx 0 0 1 $rx ${-rx} z"
}

object VuelvoIcons {
    // Category icons
    val Coffee by lazy {
        strokeIcon(
            "M5 9h11v5a4 4 0 0 1-4 4H9a4 4 0 0 1-4-4V9Z",
            "M16 10h2.2a2.3 2.3 0 0 1 0 4.6H16",
            "M8 3.2c-.5.7-.5 1.4 0 2.1M11.5 3.2c-.5.7-.5 1.4 0 2.1",
        )
    }
    val Bread by lazy {
        strokeIcon(
            "M4.5 11.5c-1.4-2 .1-4.8 2.6-4.8 1 0 1.7.4 2.2 1 .5-1 1.6-1.7 2.9-1.7 1.9 0 3.1 1.4 3.1 3 1.6.2 2.7 1.4 2.7 2.9 0 .9-.4 1.6-1 2.1L9 19.5a2 2 0 0 1-2.6-.2l-2.5-2.6a2 2 0 0 1 0-2.8l.6-.6Z",
            "M9.5 10.5 7 13M13 11l-2.5 2.5",
        )
    }
    val Scissors by lazy {
        strokeIcon(
            circle(6.0, 6.0, 2.6),
            circle(6.0, 18.0, 2.6),
            "M8.3 7.6 20 17M8.3 16.4 20 7M11 12l2.2 1.8",
        )
    }
    val Fork by lazy {
        strokeIcon(
            "M7 3v6a2 2 0 0 0 2 2v0M9 3v8M7 3v4M9 21V11",
            "M16 3c-1.6 0-2.5 1.6-2.5 4s.9 3.5 2.5 3.5S18.5 9.4 18.5 7 17.6 3 16 3ZM16 10.5V21",
        )
    }
    val IceCream by lazy {
        strokeIcon(
            "M8 9a4 4 0 0 1 8 0",
            "M7.6 9h8.8L12 21 7.6 9Z",
            "M9 13h6",
        )
    }

    // UI icons
    val StampCard by lazy {
        mixedIcon(
            listOf(rrect(3.0, 5.0, 18.0, 14.0, 3.0), circle(8.0, 12.0, 1.6), circle(13.0, 12.0, 1.6), "M16.6 12h1.4"),
            emptyList(),
        )
    }
    val Nfc by lazy {
        mixedIcon(
            listOf(
                "M6 8.5C7.6 7 9.7 6 12 6s4.4 1 6 2.5",
                "M8.3 11c1-.9 2.3-1.5 3.7-1.5s2.7.6 3.7 1.5",
                "M10.6 13.5c.4-.3.9-.5 1.4-.5s1 .2 1.4.5",
            ),
            listOf(circle(12.0, 16.5, 0.6)),
        )
    }
    val Gift by lazy {
        strokeIcon(
            "M4 11h16v8a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1v-8Z",
            "M3 8h18v3H3zM12 8v12",
            "M12 8S10.5 4 8.3 4.6C6.8 5 7 7.4 9 8M12 8s1.5-4 3.7-3.4C17.2 5 17 7.4 15 8",
        )
    }
    val Check by lazy { strokeIcon("M4.5 12.5 9.5 17.5 19.5 6.5") }
    val Chevron by lazy { strokeIcon("M9 5l7 7-7 7") }
    val ChevronLeft by lazy { strokeIcon("M15 5l-7 7 7 7") }
    val Sparkle by lazy {
        strokeIcon(
            "M12 3c.5 4 1.5 5 5.5 5.5-4 .5-5 1.5-5.5 5.5-.5-4-1.5-5-5.5-5.5 4-.5 5-1.5 5.5-5.5Z",
            "M18.5 14c.25 2 .75 2.5 2.75 2.75-2 .25-2.5.75-2.75 2.75-.25-2-.75-2.5-2.75-2.75 2-.25 2.5-.75 2.75-2.75Z",
        )
    }
}
