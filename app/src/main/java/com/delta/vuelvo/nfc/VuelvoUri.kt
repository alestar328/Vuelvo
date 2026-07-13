package com.delta.vuelvo.nfc

import android.net.Uri

/** Parsed payload of a `vuelvo://stamp?uuid=…&id=…&name=…&max=…&reward=…&logo=…&cover=…` URI. */
data class StampPayload(
    val id: String,
    val name: String,
    val max: Int,
    val reward: String,
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

    fun parse(raw: String?): StampPayload? = raw?.let { runCatching { parse(Uri.parse(it)) }.getOrNull() }

    fun parse(uri: Uri?): StampPayload? {
        if (uri == null) return null
        if (!uri.scheme.equals(SCHEME, ignoreCase = true)) return null
        if (!uri.host.equals(HOST, ignoreCase = true)) return null

        val id = uri.getQueryParameter("id")?.takeIf { it.isNotBlank() } ?: return null
        val name = uri.getQueryParameter("name")?.takeIf { it.isNotBlank() } ?: id
        val max = uri.getQueryParameter("max")?.toIntOrNull()?.takeIf { it > 0 } ?: DEFAULT_MAX
        val reward = uri.getQueryParameter("reward")?.takeIf { it.isNotBlank() } ?: "Recompensa"
        val uuid = uri.getQueryParameter("uuid")?.takeIf { it.isNotBlank() }
        val logoRef = uri.getQueryParameter("logo")?.takeIf { it.isNotBlank() }
        val coverRef = uri.getQueryParameter("cover")?.takeIf { it.isNotBlank() }
        return StampPayload(
            id = id, name = name, max = max, reward = reward, uuid = uuid,
            logoRef = logoRef, coverRef = coverRef,
        )
    }
}
