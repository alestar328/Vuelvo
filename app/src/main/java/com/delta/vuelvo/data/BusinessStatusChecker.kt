package com.delta.vuelvo.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Checks whether a comercio is currently active before a stamp is applied. Reads the public
 * `businesses/{code}` Firestore document via REST — no Firebase SDK dependency in this app.
 *
 * Policy (matches the other 3 apps in the ecosystem):
 * - missing/blank [code] -> available (nothing to check — includes tags written before this field existed)
 * - HTTP 404 (no such business record — comercio never wrote a tag since this feature shipped) -> available
 * - HTTP 200 with `fields.active.booleanValue` -> that value decides
 * - anything else (network error, timeout, malformed response) -> available (fail-open; a
 *   connectivity problem on the client's side says nothing about whether the comercio is active)
 *
 * Only an explicit `active: false` on an existing document blocks the stamp.
 */
object BusinessStatusChecker {
    private const val PROJECT_ID = "vuelvocommerce"
    private const val TIMEOUT_MS = 6000

    suspend fun isAvailable(code: String?): Boolean {
        if (code.isNullOrBlank()) return true
        return withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null
            try {
                val url = URL(
                    "https://firestore.googleapis.com/v1/projects/$PROJECT_ID/databases/(default)/documents/businesses/$code",
                )
                connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = TIMEOUT_MS
                    readTimeout = TIMEOUT_MS
                }
                when (connection.responseCode) {
                    HttpURLConnection.HTTP_OK -> {
                        val body = connection.inputStream.bufferedReader().use { it.readText() }
                        JSONObject(body)
                            .optJSONObject("fields")
                            ?.optJSONObject("active")
                            ?.optBoolean("booleanValue", false)
                            ?: false
                    }
                    HttpURLConnection.HTTP_NOT_FOUND -> true
                    else -> true
                }
            } catch (_: Exception) {
                true
            } finally {
                connection?.disconnect()
            }
        }
    }
}
