package com.apptolast.greenhouse.admin.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

/**
 * Creates an HTTP client using OkHttp engine for Android.
 */
actual fun createPlatformHttpClient(): HttpClient = HttpClient(OkHttp)
