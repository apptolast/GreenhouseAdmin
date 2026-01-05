package com.apptolast.greenhouse.admin.presentation.viewmodel

/**
 * Sealed interface representing all possible user intents/events on the Login screen.
 * Following MVI pattern for unidirectional data flow.
 */
sealed interface LoginEvent {
    /**
     * User typed in the username field.
     */
    data class OnUsernameChanged(val username: String) : LoginEvent

    /**
     * User typed in the password field.
     */
    data class OnPasswordChanged(val password: String) : LoginEvent

    /**
     * User clicked the login button.
     */
    data object OnLoginClicked : LoginEvent

    /**
     * User toggled password visibility.
     */
    data object OnTogglePasswordVisibility : LoginEvent

    /**
     * Dismiss current error message.
     */
    data object DismissError : LoginEvent

    /**
     * Navigation to main screen completed, reset navigation flag.
     */
    data object OnNavigationHandled : LoginEvent
}
