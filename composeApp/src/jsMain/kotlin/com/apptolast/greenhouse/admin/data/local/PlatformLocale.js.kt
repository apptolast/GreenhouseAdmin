package com.apptolast.greenhouse.admin.data.local

import kotlinx.browser.window

actual fun applyPlatformLocale(languageTag: String) {
    window.asDynamic().__customLocale = languageTag
}

actual fun getSystemLanguageTag(): String = window.navigator.language.take(2)
