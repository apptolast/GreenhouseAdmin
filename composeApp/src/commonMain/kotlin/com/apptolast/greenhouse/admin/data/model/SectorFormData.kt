package com.apptolast.greenhouse.admin.data.model

/**
 * Form data for creating or editing a sector.
 * Manages form state and validation.
 */
data class SectorFormData(
    val name: String = "",
    val greenhouseId: String = "",
    val area: String = "" // String for text field input
) {
    /**
     * Validation errors for each field.
     */
    data class ValidationErrors(
        val name: String? = null,
        val greenhouseId: String? = null,
        val area: String? = null
    ) {
        val hasErrors: Boolean
            get() = name != null || greenhouseId != null || area != null
    }

    /**
     * Validates the form and returns any errors.
     */
    fun validate(): ValidationErrors {
        return ValidationErrors(
            name = if (name.length < 2) "error_name_min_length" else null,
            greenhouseId = if (greenhouseId.isBlank()) "error_greenhouse_required" else null,
            area = validateArea()
        )
    }

    private fun validateArea(): String? {
        if (area.isBlank()) return "error_area_required"
        val areaValue = area.toDoubleOrNull()
        return when {
            areaValue == null -> "error_area_invalid"
            areaValue <= 0 -> "error_area_positive"
            else -> null
        }
    }

    /**
     * Returns the area as Double, or 0.0 if invalid.
     */
    val areaValue: Double
        get() = area.toDoubleOrNull() ?: 0.0

    /**
     * Returns true if all required fields are valid.
     */
    val isValid: Boolean
        get() = name.length >= 2 && greenhouseId.isNotBlank() && area.toDoubleOrNull()?.let { it > 0 } == true
}
