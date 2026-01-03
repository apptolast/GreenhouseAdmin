package com.apptolast.greenhouse.admin.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js

/**
 * Creates an HTTP client using JS engine for JavaScript target.
 */
actual fun createPlatformHttpClient(): HttpClient = HttpClient(Js)
