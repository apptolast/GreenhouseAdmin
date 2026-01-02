package com.apptolast.greenhouse.admin.data.model

/**
 * Form data for creating or editing a greenhouse.
 * Manages form state and validation.
 */
data class GreenhouseFormData(
    val name: String = "",
    val description: String = "",
    val status: GreenhouseStatus = GreenhouseStatus.ACTIVE
) {
    /**
     * Validation errors for each field.
     */
    data class ValidationErrors(
        val name: String? = null,
        val description: String? = null
    ) {
        val hasErrors: Boolean
            get() = name != null || description != null
    }

    /**
     * Validates the form and returns any errors.
     */
    fun validate(): ValidationErrors {
        return ValidationErrors(
            name = if (name.length < 2) "error_name_min_length" else null,
            description = null // Description is optional
        )
    }

    /**
     * Returns true if all required fields are valid.
     */
    val isValid: Boolean
        get() = name.length >= 2
}
