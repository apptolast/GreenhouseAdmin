package com.apptolast.greenhouse.admin.data.remote

import com.apptolast.greenhouse.admin.BuildKonfig
import com.apptolast.greenhouse.admin.data.local.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Creates a configured HTTP client with JSON serialization, logging, and JWT auth.
 *
 * @param tokenStorage The token storage to retrieve JWT token from.
 */
fun createHttpClient(tokenStorage: TokenStorage): HttpClient {
    val client = createPlatformHttpClient().config {
        // JSON serialization
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = false
            })
        }

        // Request/Response logging (use LogLevel.ALL for debugging)
        install(Logging) {
            level = LogLevel.INFO
        }

        // Default request configuration
        defaultRequest {
            // Ensure base URL ends with / for proper path concatenation
            val baseUrl = BuildKonfig.API_BASE_URL.let {
                if (it.endsWith("/")) it else "$it/"
            }
            url(baseUrl)
            contentType(ContentType.Application.Json)
        }
    }

    // Add Authorization header interceptor
    // This ensures the token is read fresh from storage on each request
    client.plugin(HttpSend).intercept { request ->
        // Skip auth header for auth endpoints (they don't need it)
        val isAuthEndpoint = request.url.toString().contains("/api/auth/")

        if (!isAuthEndpoint) {
            tokenStorage.getAccessToken()?.let { token ->
                request.headers.append(HttpHeaders.Authorization, "Bearer $token")
            }
        }

        execute(request)
    }

    return client
}
