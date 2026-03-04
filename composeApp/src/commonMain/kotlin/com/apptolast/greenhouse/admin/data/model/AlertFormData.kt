package com.apptolast.greenhouse.admin.data.model

/**
 * Form data for creating or editing an alert.
 */
data class AlertFormData(
    val alertTypeId: Short? = null,
    val severityId: Short? = null,
    val message: String = "",
    val description: String = ""
) {
    /**
     * Validation errors for alert form fields.
     */
    data class ValidationErrors(
        val content: String? = null
    ) {
        val hasErrors: Boolean
            get() = content != null
    }

    /**
     * Validates the form data and returns any errors.
     * Note: Either message or description must be provided.
     */
    fun validate(): ValidationErrors {
        return ValidationErrors(
            content = if (message.isBlank() && description.isBlank()) "error_content_required" else null
        )
    }

    val isValid: Boolean
        get() = !validate().hasErrors
}
