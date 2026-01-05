package com.apptolast.greenhouse.admin.data.local

import platform.UIKit.UIPasteboard

/**
 * iOS implementation of ClipboardManager using UIPasteboard.
 */
actual class ClipboardManager {
    actual fun copyToClipboard(text: String) {
        UIPasteboard.generalPasteboard.string = text
    }
}
