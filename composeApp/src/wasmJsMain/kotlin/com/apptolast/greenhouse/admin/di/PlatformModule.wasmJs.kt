package com.apptolast.greenhouse.admin.di

import com.apptolast.greenhouse.admin.data.local.ClipboardManager
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    // WebAssembly-specific dependencies
    single { ClipboardManager() }
}
