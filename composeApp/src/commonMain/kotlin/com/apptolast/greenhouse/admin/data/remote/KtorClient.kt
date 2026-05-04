package com.apptolast.greenhouse.admin.data.remote

import com.apptolast.greenhouse.admin.BuildKonfig
import com.apptolast.greenhouse.admin.data.local.TokenStorage
import com.apptolast.greenhouse.admin.domain.auth.AuthEventManager
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Creates a configured HTTP client with JSON serialization, logging, and JWT auth.
 *
 * @param tokenStorage The token storage to retrieve JWT token from.
 * @param authEventManager The manager for emitting authentication events.
 */
fun createHttpClient(
    tokenStorage: TokenStorage,
    authEventManager: AuthEventManager
): HttpClient {
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

        // Response validator for handling 401/403 before deserialization.
        // 401 (Unauthorized) means the token is missing/expired/invalid → sign the user out.
        // 403 (Forbidden) means authenticated but lacking permission for this resource →
        // surface as an exception to the caller, but DO NOT clear tokens or sign out;
        // call sites (e.g. dashboard per-tenant aggregations) handle this locally.
        HttpResponseValidator {
            validateResponse { response ->
                val statusCode = response.status
                val isAuthEndpoint = response.call.request.url.toString().contains("/auth/")
                if (isAuthEndpoint) return@validateResponse

                when (statusCode) {
                    HttpStatusCode.Unauthorized -> {
                        tokenStorage.clearTokens()
                        authEventManager.tryEmitSessionExpired()
                        throw AuthenticationException.unauthorized()
                    }
                    HttpStatusCode.Forbidden -> {
                        throw AuthenticationException.forbidden()
                    }
                    else -> Unit
                }
            }
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
        // Skip auth header for auth endpoints (login doesn't need it, logout uses it but is optional)
        val isLoginEndpoint = request.url.toString().contains("/auth/login")

        if (!isLoginEndpoint) {
            tokenStorage.getAccessToken()?.let { token ->
                request.headers.append(HttpHeaders.Authorization, "Bearer $token")
            }
        }

        execute(request)
    }

    return client
}
