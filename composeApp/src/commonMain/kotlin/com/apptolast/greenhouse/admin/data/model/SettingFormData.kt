package com.apptolast.greenhouse.admin.data.model

/**
 * Form data class for creating/editing settings with validation.
 * Manages form state for greenhouse parameter threshold configuration.
 */
data class SettingFormData(
    val greenhouseId: Long? = null,
    val parameterId: Short? = null,
    val actuatorStateId: Short? = null,
    val value: String = "",
    val isActive: Boolean = true
) {
    /**
     * Validation errors container.
     */
    data class ValidationErrors(
        val greenhouseId: String? = null,
        val parameterId: String? = null,
        val actuatorStateId: String? = null
    ) {
        /**
         * Returns true if there are any validation errors.
         */
        val hasErrors: Boolean
            get() = greenhouseId != null || parameterId != null || actuatorStateId != null
    }

    /**
     * Validates the form data and returns validation errors.
     */
    fun validate(): ValidationErrors {
        return ValidationErrors(
            greenhouseId = if (greenhouseId == null) "error_greenhouse_required" else null,
            parameterId = if (parameterId == null) "error_parameter_required" else null,
            actuatorStateId = if (actuatorStateId == null) "error_actuator_state_required" else null
        )
    }

    /**
     * Returns true if the form data is valid.
     */
    val isValid: Boolean
        get() = !validate().hasErrors
}
