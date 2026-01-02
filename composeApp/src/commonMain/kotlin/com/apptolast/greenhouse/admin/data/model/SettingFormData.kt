package com.apptolast.greenhouse.admin.data.model

/**
 * Form data class for creating/editing settings with validation.
 */
data class SettingFormData(
    val key: String = "",
    val value: String = "",
    val description: String = ""
) {
    /**
     * Validation errors container.
     */
    data class ValidationErrors(
        val key: String? = null,
        val value: String? = null
    ) {
        /**
         * Returns true if there are any validation errors.
         */
        val hasErrors: Boolean
            get() = key != null || value != null
    }

    /**
     * Validates the form data and returns validation errors.
     */
    fun validate(): ValidationErrors {
        return ValidationErrors(
            key = when {
                key.isBlank() -> "error_key_required"
                key.length < 2 -> "error_key_min_length"
                !key.matches(Regex("^[a-zA-Z][a-zA-Z0-9_]*$")) -> "error_key_invalid"
                else -> null
            },
            value = when {
                value.isBlank() -> "error_value_required"
                else -> null
            }
        )
    }

    /**
     * Returns true if the form data is valid.
     */
    val isValid: Boolean
        get() = !validate().hasErrors
}
