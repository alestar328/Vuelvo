package com.delta.vuelvo.nfc

import android.net.Uri

/** Parsed payload of a `vuelvo://stamp?code=…&id=…&name=…&cat=…&color=…&tile=…&ink=…&max=…&reward=…&addr=…&tel=…&logo=…&cover=…` URI. */
data class StampPayload(
    val id: String,
    val name: String,
    val max: Int,
    val reward: String,
    /** Establishment type the comercio picked ("Cafetería", "Panadería"…); null on tags written
     * before `cat=` existed. */
    val category: String? = null,
    /** Per-tag unique identifier carried by the deep link; null on older tags that omit it. */
    val uuid: String? = null,
    /** Merchant's businessCode — key of its `businesses/{code}` Firestore record; null on tags written
     * before this field existed. Drives the active-comercio check, see [com.delta.vuelvo.data.BusinessStatusChecker]. */
    val code: String? = null,
    /** Firebase Storage object reference for the logo/cover (e.g. "ABCDE12345_logo", no extension) —
     * null when the comercio didn't set one. See [com.delta.vuelvo.data.VuelvoStorage] for the resolved URL. */
    val logoRef: String? = null,
    val coverRef: String? = null,
    /** Card colour the comercio picked, as the palette id (`violet`, `black`…); null on older tags. */
    val colorId: String? = null,
    /** Resolved card colours as RRGGBB (no `#`) — the comercio writes both so any client can paint the
     * card without shipping the palette table. Null on tags written before these existed. */
    val tileHex: String? = null,
    val inkHex: String? = null,
    /** Datos de contacto del comercio (`addr=` / `tel=`), mostrados en la cabecera del detalle de la
     * tarjeta. Null cuando el comercio no los rellenó o el tag es anterior a que existieran. */
    val address: String? = null,
    val phone: String? = null,
)

/**
 * Parses the Vuelvo stamp scheme. Returns null for anything that is not a
 * well-formed `vuelvo://stamp` URI carrying at least an `id`.
 */
object VuelvoUri {
    private const val SCHEME = "vuelvo"
    private const val HOST = "stamp"
    private const val DEFAULT_MAX = 10

    fun parse(raw: String?): StampPayload? = raw?.let { runCatching { parse(Uri.parse(it)) }.getOrNull() }

    fun parse(uri: Uri?): StampPayload? {
        if (uri == null) return null
        if (!uri.scheme.equals(SCHEME, ignoreCase = true)) return null
        if (!uri.host.equals(HOST, ignoreCase = true)) return null

        val id = uri.getQueryParameter("id")?.takeIf { it.isNotBlank() } ?: return null
        val name = uri.getQueryParameter("name")?.takeIf { it.isNotBlank() } ?: id
        val max = uri.getQueryParameter("max")?.toIntOrNull()?.takeIf { it > 0 } ?: DEFAULT_MAX
        val reward = uri.getQueryParameter("reward")?.takeIf { it.isNotBlank() } ?: "Recompensa"
        val category = uri.getQueryParameter("cat")?.takeIf { it.isNotBlank() }
        val uuid = uri.getQueryParameter("uuid")?.takeIf { it.isNotBlank() }
        val code = uri.getQueryParameter("code")?.takeIf { it.isNotBlank() }
        val logoRef = uri.getQueryParameter("logo")?.takeIf { it.isNotBlank() }
        val coverRef = uri.getQueryParameter("cover")?.takeIf { it.isNotBlank() }
        val colorId = uri.getQueryParameter("color")?.takeIf { it.isNotBlank() }
        val tileHex = uri.getQueryParameter("tile")?.takeIf { it.isNotBlank() }
        val inkHex = uri.getQueryParameter("ink")?.takeIf { it.isNotBlank() }
        val address = uri.getQueryParameter("addr")?.trim()?.takeIf { it.isNotBlank() }
        val phone = uri.getQueryParameter("tel")?.trim()?.takeIf { it.isNotBlank() }
        return StampPayload(
            id = id, name = name, max = max, reward = reward, category = category,
            uuid = uuid, code = code, logoRef = logoRef, coverRef = coverRef,
            colorId = colorId, tileHex = tileHex, inkHex = inkHex,
            address = address, phone = phone,
        )
    }
}
