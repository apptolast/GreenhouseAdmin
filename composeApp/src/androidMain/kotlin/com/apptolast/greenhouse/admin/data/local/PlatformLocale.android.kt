package com.apptolast.greenhouse.admin.data.local

import java.util.Locale

actual fun applyPlatformLocale(languageTag: String) {
    Locale.setDefault(Locale(languageTag))
}

actual fun getSystemLanguageTag(): String = Locale.getDefault().language
