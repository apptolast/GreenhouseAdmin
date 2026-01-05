package com.apptolast.greenhouse.admin.data.model

/**
 * Form data for creating or editing a user.
 * Manages form state and validation.
 */
data class UserFormData(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val role: UserRole = UserRole.OPERATOR,
    val isActive: Boolean = true,
    val isEditMode: Boolean = false
) {
    /**
     * Validation errors for each field.
     */
    data class ValidationErrors(
        val username: String? = null,
        val email: String? = null,
        val password: String? = null
    ) {
        val hasErrors: Boolean
            get() = username != null || email != null || password != null
    }

    /**
     * Validates the form and returns any errors.
     * Password is only required in create mode (not edit mode).
     */
    fun validate(): ValidationErrors {
        return ValidationErrors(
            username = if (username.length < 3) "error_username_min_length" else null,
            email = if (!isValidEmail(email)) "error_email_invalid" else null,
            password = if (!isEditMode && password.isBlank()) "error_password_required" else null
        )
    }

    /**
     * Returns true if all required fields are valid.
     */
    val isValid: Boolean
        get() = username.length >= 3 &&
                isValidEmail(email) &&
                (isEditMode || password.isNotBlank())

    companion object {
        private val EMAIL_REGEX = Regex(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        )

        fun isValidEmail(email: String): Boolean {
            return email.isNotBlank() && EMAIL_REGEX.matches(email)
        }

        /**
         * Creates form data pre-filled from an existing user for edit mode.
         */
        fun fromUser(user: User): UserFormData {
            return UserFormData(
                username = user.username,
                email = user.email,
                password = "",
                role = user.role,
                isActive = user.isActive,
                isEditMode = true
            )
        }
    }
}
