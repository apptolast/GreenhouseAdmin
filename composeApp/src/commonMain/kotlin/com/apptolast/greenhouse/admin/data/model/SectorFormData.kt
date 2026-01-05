package com.apptolast.greenhouse.admin.data.model

/**
 * Form data for creating or editing a sector.
 * Manages form state and validation.
 */
data class SectorFormData(
    val variety: String = "",
    val greenhouseId: String = ""
) {
    /**
     * Validation errors for each field.
     */
    data class ValidationErrors(
        val variety: String? = null,
        val greenhouseId: String? = null
    ) {
        val hasErrors: Boolean
            get() = variety != null || greenhouseId != null
    }

    /**
     * Validates the form and returns any errors.
     */
    fun validate(): ValidationErrors {
        return ValidationErrors(
            variety = if (variety.length < 2) "error_variety_min_length" else null,
            greenhouseId = if (greenhouseId.isBlank()) "error_greenhouse_required" else null
        )
    }

    /**
     * Returns true if all required fields are valid.
     */
    val isValid: Boolean
        get() = variety.length >= 2 && greenhouseId.isNotBlank()
}
