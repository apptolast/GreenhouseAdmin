package com.apptolast.greenhouse.admin.di

import org.koin.core.module.Module

/**
 * Platform-specific Koin module.
 * Each platform provides its own implementation for platform-specific dependencies.
 */
expect fun platformModule(): Module
