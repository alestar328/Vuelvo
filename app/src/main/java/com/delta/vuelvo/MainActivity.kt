package com.delta.vuelvo

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.delta.vuelvo.nfc.VuelvoUri
import com.delta.vuelvo.ui.VuelvoApp
import com.delta.vuelvo.ui.theme.VuelvoTheme
import com.delta.vuelvo.ui.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val appViewModel: AppViewModel by viewModels()
    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        handleDeepLink(intent)
        setContent {
            VuelvoTheme {
                VuelvoApp(appViewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Foreground reader mode: intercept tags while the app is in front.
        nfcAdapter?.enableReaderMode(
            this,
            { tag -> onTagDiscovered(tag) },
            NfcAdapter.FLAG_READER_NFC_A or
                NfcAdapter.FLAG_READER_NFC_B or
                NfcAdapter.FLAG_READER_NFC_F or
                NfcAdapter.FLAG_READER_NFC_V or
                NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
            null,
        )
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)
    }

    /** Handles `vuelvo://stamp` deep links and tags that launched the app via NDEF_DISCOVERED. */
    private fun handleDeepLink(intent: Intent?) {
        VuelvoUri.parse(intent?.data)?.let { appViewModel.onStampPayload(it) }
    }

    private fun onTagDiscovered(tag: Tag) {
        val uri = readNdefUri(tag) ?: return
        VuelvoUri.parse(uri)?.let { payload ->
            runOnUiThread { appViewModel.onStampPayload(payload) }
        }
    }

    /** Reads the URI from the first NDEF record on a tag, or null if unreadable. */
    private fun readNdefUri(tag: Tag): String? {
        val ndef = Ndef.get(tag) ?: return null
        return try {
            ndef.connect()
            val message = ndef.ndefMessage ?: ndef.cachedNdefMessage
            val record = message?.records?.firstOrNull() ?: return null
            record.toUri()?.toString() ?: String(record.payload, Charsets.UTF_8)
        } catch (_: Exception) {
            null
        } finally {
            runCatching { ndef.close() }
        }
    }
}
