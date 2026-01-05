package com.apptolast.greenhouse.admin.data.remote

import io.ktor.client.HttpClient

/**
 * Creates a platform-specific HTTP client.
 * Each platform provides its own engine implementation.
 */
expect fun createPlatformHttpClient(): HttpClient
