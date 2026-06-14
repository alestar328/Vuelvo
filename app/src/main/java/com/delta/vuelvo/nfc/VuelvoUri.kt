package com.delta.vuelvo.nfc

import android.net.Uri

/** Parsed payload of a `vuelvo://stamp?id=…&name=…&max=…&reward=…` URI. */
data class StampPayload(
    val id: String,
    val name: String,
    val max: Int,
    val reward: String,
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
        return StampPayload(id = id, name = name, max = max, reward = reward)
    }
}
