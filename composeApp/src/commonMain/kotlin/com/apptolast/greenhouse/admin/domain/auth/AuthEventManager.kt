package com.apptolast.greenhouse.admin.domain.auth

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Sealed class representing authentication events that can occur during the app lifecycle.
 */
sealed class AuthEvent {
    /**
     * Emitted when the user's session has expired (401/403 from API).
     */
    data object SessionExpired : AuthEvent()
}

/**
 * Manager for authentication events across the application.
 * Used to communicate session expiration from the HTTP layer to the UI layer.
 */
class AuthEventManager {
    private val _authEvents = MutableSharedFlow<AuthEvent>(extraBufferCapacity = 1)

    /**
     * Flow of authentication events for UI consumption.
     */
    val authEvents: SharedFlow<AuthEvent> = _authEvents.asSharedFlow()

    /**
     * Emits a session expired event to notify listeners that reauthentication is needed.
     */
    suspend fun emitSessionExpired() {
        _authEvents.emit(AuthEvent.SessionExpired)
    }

    /**
     * Non-blocking version of emitSessionExpired for use in non-suspend contexts.
     * Uses tryEmit which works because we have extraBufferCapacity = 1.
     * @return true if the event was emitted successfully
     */
    fun tryEmitSessionExpired(): Boolean {
        return _authEvents.tryEmit(AuthEvent.SessionExpired)
    }
}
