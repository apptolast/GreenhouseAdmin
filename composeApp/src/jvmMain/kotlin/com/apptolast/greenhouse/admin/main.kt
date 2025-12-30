package com.apptolast.greenhouse.admin

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Greenhouse Admin",
    ) {
        App()
    }
}