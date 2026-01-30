package com.apptolast.greenhouse.admin.data.model

/**
 * Form data for creating or editing a sector.
 * Manages form state and validation.
 */
data class SectorFormData(
    val name: String = "",
    val greenhouseId: Long? = null
) {
    /**
     * Validation errors for each field.
     */
    data class ValidationErrors(
        val name: String? = null,
        val greenhouseId: String? = null
    ) {
        val hasErrors: Boolean
            get() = name != null || greenhouseId != null
    }

    /**
     * Validates the form and returns any errors.
     */
    fun validate(): ValidationErrors {
        return ValidationErrors(
            name = if (name.length < 2) "error_sector_name_min_length" else null,
            greenhouseId = if (greenhouseId == null) "error_greenhouse_required" else null
        )
    }

    /**
     * Returns true if all required fields are valid.
     */
    val isValid: Boolean
        get() = name.length >= 2 && greenhouseId != null
}
