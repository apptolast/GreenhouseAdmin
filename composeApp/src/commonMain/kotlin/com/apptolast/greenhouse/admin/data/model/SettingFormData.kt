package com.apptolast.greenhouse.admin.data.model

/**
 * Form data class for creating/editing settings with validation.
 * Manages form state for sector parameter threshold configuration.
 */
data class SettingFormData(
    val sectorId: Long? = null,
    val parameterId: Short? = null,
    val actuatorStateId: Short? = null,
    val value: String = "",
    val description: String = "",
    val isActive: Boolean = true
) {
    /**
     * Validation errors container.
     */
    data class ValidationErrors(
        val sectorId: String? = null,
        val parameterId: String? = null,
        val actuatorStateId: String? = null
    ) {
        /**
         * Returns true if there are any validation errors.
         */
        val hasErrors: Boolean
            get() = sectorId != null || parameterId != null || actuatorStateId != null
    }

    /**
     * Validates the form data and returns validation errors.
     */
    fun validate(): ValidationErrors {
        return ValidationErrors(
            sectorId = if (sectorId == null) "error_sector_required" else null,
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
