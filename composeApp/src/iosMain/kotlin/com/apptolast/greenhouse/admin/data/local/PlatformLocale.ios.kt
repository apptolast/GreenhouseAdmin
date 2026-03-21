package com.apptolast.greenhouse.admin.data.local

import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults
import platform.Foundation.preferredLanguages

actual fun applyPlatformLocale(languageTag: String) {
    NSUserDefaults.standardUserDefaults.setObject(listOf(languageTag), forKey = "AppleLanguages")
}

actual fun getSystemLanguageTag(): String {
    val languages = NSLocale.preferredLanguages
    return (languages.firstOrNull() as? String)?.take(2) ?: "en"
}
