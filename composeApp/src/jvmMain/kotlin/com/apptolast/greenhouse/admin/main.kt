package com.apptolast.greenhouse.admin

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.apptolast.greenhouse.admin.di.initKoin
import com.apptolast.greenhouse.admin.presentation.ui.App

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Greenhouse Admin",
        ) {
            App()
        }
    }
}