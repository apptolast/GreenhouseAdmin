@file:OptIn(ExperimentalWasmJsInterop::class)

package com.apptolast.greenhouse.admin.data.local

import kotlinx.browser.window

@JsFun("(tag) => { window.__customLocale = tag; }")
private external fun setWindowLocale(tag: JsString)

actual fun applyPlatformLocale(languageTag: String) {
    setWindowLocale(languageTag.toJsString())
}

actual fun getSystemLanguageTag(): String = window.navigator.language.take(2)
