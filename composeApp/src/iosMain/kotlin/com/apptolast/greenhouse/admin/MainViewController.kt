package com.apptolast.greenhouse.admin

import androidx.compose.ui.window.ComposeUIViewController
import com.apptolast.greenhouse.admin.di.initKoin
import com.apptolast.greenhouse.admin.presentation.ui.App

fun initKoin() {
    initKoin(appDeclaration = null)
}

fun MainViewController() = ComposeUIViewController { App() }