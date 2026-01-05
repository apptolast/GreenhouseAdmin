package com.apptolast.greenhouse.admin.data.local

import android.content.ClipData
import android.content.Context
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import android.content.ClipboardManager as AndroidClipboardManager

/**
 * Android implementation of ClipboardManager using Android's ClipboardManager service.
 */
actual class ClipboardManager : KoinComponent {
    private val context: Context by inject()

    actual fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as AndroidClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
    }
}
