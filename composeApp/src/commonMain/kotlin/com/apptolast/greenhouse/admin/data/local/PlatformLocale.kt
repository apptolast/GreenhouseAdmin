package com.apptolast.greenhouse.admin.data.local

/**
 * Applies the given locale to the platform so that Compose Resources
 * pick up the correct string resources on recomposition.
 */
expect fun applyPlatformLocale(languageTag: String)

/**
 * Returns the system's current language tag (e.g. "en", "es").
 */
expect fun getSystemLanguageTag(): String
