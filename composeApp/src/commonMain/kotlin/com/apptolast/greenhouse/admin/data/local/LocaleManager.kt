package com.apptolast.greenhouse.admin.data.local

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Supported languages for the application.
 */
enum class SupportedLanguage(val tag: String, val displayName: String) {
    ENGLISH("en", "English"),
    SPANISH("es", "Español");

    companion object {
        /**
         * Finds the matching supported language for a given language tag,
         * falling back to English if not found.
         */
        fun fromTag(tag: String): SupportedLanguage =
            entries.find { tag.startsWith(it.tag) } ?: ENGLISH
    }
}

/**
 * Manages the application locale state and applies platform-specific locale changes.
 * Registered as a Koin singleton so the locale persists across screens.
 */
class LocaleManager {
    private val _currentLanguage = MutableStateFlow(detectSystemLanguage())
    val currentLanguage: StateFlow<SupportedLanguage> = _currentLanguage.asStateFlow()

    /**
     * Changes the application language and applies it to the platform.
     */
    fun changeLanguage(language: SupportedLanguage) {
        applyPlatformLocale(language.tag)
        _currentLanguage.value = language
    }

    private fun detectSystemLanguage(): SupportedLanguage =
        SupportedLanguage.fromTag(getSystemLanguageTag())
}
