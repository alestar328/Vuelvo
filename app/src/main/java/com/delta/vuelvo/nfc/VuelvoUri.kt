package com.delta.vuelvo.nfc

import android.net.Uri
import com.delta.vuelvo.data.CommerceIcon

/**
 * Parsed payload of a
 * `vuelvo://stamp?uuid=…&id=…&name=…&cat=…&sym=…&color=…&tile=…&ink=…&max=…&reward=…&logo=…&cover=…` URI.
 */
data class StampPayload(
    val id: String,
    val name: String,
    val max: Int,
    val reward: String,
    /** Establishment type the comercio picked ("Cafetería", "Panadería"…); null on older tags. */
    val category: String? = null,
    /** Icon resolved from the tag's `sym=` SF Symbol name; null when absent or unknown. */
    val icon: CommerceIcon? = null,
    /** Card colours resolved from `tile=`/`ink=` (RRGGBB, no '#'); null on older tags. */
    val tileHex: String? = null,
    val inkHex: String? = null,
    /** Per-tag unique identifier carried by the deep link; null on older tags that omit it. */
    val uuid: String? = null,
    /** Firebase Storage object reference for the logo/cover (e.g. "ABCDE12345_logo", no extension) —
     * null when the comercio didn't set one. See [com.delta.vuelvo.data.VuelvoStorage] for the resolved URL. */
    val logoRef: String? = null,
    val coverRef: String? = null,
)

/**
 * Parses the Vuelvo stamp scheme. Returns null for anything that is not a
 * well-formed `vuelvo://stamp` URI carrying at least an `id`.
 */
object VuelvoUri {
    private const val SCHEME = "vuelvo"
    private const val HOST = "stamp"
    private const val DEFAULT_MAX = 10

    /**
     * `sym=` is an SF Symbol name — the comercio app writes the token iOS renders directly, so
     * Android translates it into its own [CommerceIcon]. Unknown names fall back to null, which
     * leaves the card on its default icon instead of guessing.
     */
    private val SYMBOLS = mapOf(
        "cup.and.saucer.fill" to CommerceIcon.COFFEE,
        "takeoutbag.and.cup.and.straw.fill" to CommerceIcon.BREAD,
        "scissors" to CommerceIcon.SCISSORS,
        "fork.knife" to CommerceIcon.FORK,
        "birthday.cake.fill" to CommerceIcon.ICECREAM,
        "bag.fill" to CommerceIcon.STORE,
    )

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
        val icon = uri.getQueryParameter("sym")?.let { SYMBOLS[it.lowercase()] }
        val tileHex = uri.getQueryParameter("tile")?.let(::normalizeHex)
        val inkHex = uri.getQueryParameter("ink")?.let(::normalizeHex)
        val uuid = uri.getQueryParameter("uuid")?.takeIf { it.isNotBlank() }
        val logoRef = uri.getQueryParameter("logo")?.takeIf { it.isNotBlank() }
        val coverRef = uri.getQueryParameter("cover")?.takeIf { it.isNotBlank() }
        return StampPayload(
            id = id, name = name, max = max, reward = reward,
            category = category, icon = icon, tileHex = tileHex, inkHex = inkHex,
            uuid = uuid, logoRef = logoRef, coverRef = coverRef,
        )
    }

    /** "F3E9DF" / "#f3e9df" → "#F3E9DF"; anything that is not a 6-digit hex → null. */
    private fun normalizeHex(raw: String): String? {
        val clean = raw.removePrefix("#").uppercase()
        return if (clean.length == 6 && clean.all { it in "0123456789ABCDEF" }) "#$clean" else null
    }
}
