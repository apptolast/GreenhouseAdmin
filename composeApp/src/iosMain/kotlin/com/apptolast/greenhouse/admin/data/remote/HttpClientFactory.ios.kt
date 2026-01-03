package com.apptolast.greenhouse.admin.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

/**
 * Creates an HTTP client using Darwin engine for iOS.
 */
actual fun createPlatformHttpClient(): HttpClient = HttpClient(Darwin)
