package com.apptolast.greenhouse.admin.data.model

/**
 * Form data for creating or editing an alert.
 */
data class AlertFormData(
    val title: String = "",
    val severity: AlertSeverity = AlertSeverity.LOW,
    val status: AlertStatus = AlertStatus.UNREAD
) {
    /**
     * Validation errors for alert form fields.
     */
    data class ValidationErrors(
        val title: String? = null
    ) {
        val hasErrors: Boolean
            get() = title != null
    }

    /**
     * Validates the form data and returns any errors.
     */
    fun validate(): ValidationErrors {
        return ValidationErrors(
            title = when {
                title.isBlank() -> "error_title_required"
                title.length < 2 -> "error_name_min_length"
                else -> null
            }
        )
    }

    val isValid: Boolean
        get() = !validate().hasErrors
}
