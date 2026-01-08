package com.apptolast.greenhouse.admin.data.model

/**
 * Form data class for creating/editing settings with validation.
 * Manages form state for greenhouse parameter threshold configuration.
 */
data class SettingFormData(
    val greenhouseId: Long? = null,
    val parameterId: Short? = null,
    val periodId: Short? = null,
    val minValue: String = "",
    val maxValue: String = "",
    val isActive: Boolean = true
) {
    /**
     * Validation errors container.
     */
    data class ValidationErrors(
        val greenhouseId: String? = null,
        val parameterId: String? = null,
        val periodId: String? = null,
        val minMax: String? = null
    ) {
        /**
         * Returns true if there are any validation errors.
         */
        val hasErrors: Boolean
            get() = greenhouseId != null || parameterId != null ||
                    periodId != null || minMax != null
    }

    /**
     * Validates the form data and returns validation errors.
     */
    fun validate(): ValidationErrors {
        val min = minValue.toDoubleOrNull()
        val max = maxValue.toDoubleOrNull()

        return ValidationErrors(
            greenhouseId = if (greenhouseId == null) "error_greenhouse_required" else null,
            parameterId = if (parameterId == null) "error_parameter_required" else null,
            periodId = if (periodId == null) "error_period_required" else null,
            minMax = when {
                min != null && max != null && min > max -> "error_min_greater_than_max"
                else -> null
            }
        )
    }

    /**
     * Returns true if the form data is valid.
     */
    val isValid: Boolean
        get() = !validate().hasErrors

    /**
     * Returns the minValue as Double or null if empty/invalid.
     */
    val minValueDouble: Double?
        get() = minValue.toDoubleOrNull()

    /**
     * Returns the maxValue as Double or null if empty/invalid.
     */
    val maxValueDouble: Double?
        get() = maxValue.toDoubleOrNull()
}
