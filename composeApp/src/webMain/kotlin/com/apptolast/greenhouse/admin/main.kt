package com.apptolast.greenhouse.admin

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.apptolast.greenhouse.admin.di.initKoin
import com.apptolast.greenhouse.admin.presentation.ui.App

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin()
    ComposeViewport {
        App()
    }
}