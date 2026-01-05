package com.apptolast.greenhouse.admin.data.local

/**
 * Platform-specific clipboard manager for copying text to the system clipboard.
 * Uses expect/actual pattern for multiplatform implementation.
 */
expect class ClipboardManager() {
    /**
     * Copies the given text to the system clipboard.
     * @param text The text to copy to clipboard
     */
    fun copyToClipboard(text: String)
}
