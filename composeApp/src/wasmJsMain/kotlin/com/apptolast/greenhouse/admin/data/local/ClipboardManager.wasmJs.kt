@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package com.apptolast.greenhouse.admin.data.local

import kotlinx.browser.window

/**
 * WebAssembly/JS implementation of ClipboardManager using navigator.clipboard API.
 */
actual class ClipboardManager {
    actual fun copyToClipboard(text: String) {
        window.navigator.clipboard.writeText(text)
    }
}
