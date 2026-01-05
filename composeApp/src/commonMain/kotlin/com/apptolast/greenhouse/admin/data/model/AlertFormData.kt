package com.apptolast.greenhouse.admin.data.model

/**
 * Form data for creating or editing an alert.
 */
data class AlertFormData(
    val greenhouseId: String = "",
    val alertTypeId: Short? = null,
    val severityId: Short? = null,
    val message: String = ""
) {
    /**
     * Validation errors for alert form fields.
     */
    data class ValidationErrors(
        val greenhouseId: String? = null,
        val message: String? = null
    ) {
        val hasErrors: Boolean
            get() = greenhouseId != null || message != null
    }

    /**
     * Validates the form data and returns any errors.
     */
    fun validate(): ValidationErrors {
        return ValidationErrors(
            greenhouseId = if (greenhouseId.isBlank()) "error_greenhouse_required" else null,
            message = if (message.isBlank()) "error_message_required" else null
        )
    }

    val isValid: Boolean
        get() = !validate().hasErrors
}
