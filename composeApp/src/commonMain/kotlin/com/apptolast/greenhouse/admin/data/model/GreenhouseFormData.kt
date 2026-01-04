package com.apptolast.greenhouse.admin.data.model

/**
 * Form data for creating or editing a greenhouse.
 * Manages form state and validation.
 */
data class GreenhouseFormData(
    val name: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val areaM2: String = "",
    val timezone: String = "Europe/Madrid",
    val isActive: Boolean = true,
    val isEditMode: Boolean = false
) {
    /**
     * Validation errors for each field.
     */
    data class ValidationErrors(
        val name: String? = null,
        val latitude: String? = null,
        val longitude: String? = null,
        val areaM2: String? = null
    ) {
        val hasErrors: Boolean
            get() = name != null || latitude != null || longitude != null || areaM2 != null
    }

    /**
     * Validates the form and returns any errors.
     */
    fun validate(): ValidationErrors {
        return ValidationErrors(
            name = if (name.length < 2) "error_name_min_length" else null,
            latitude = validateCoordinate(latitude, isLatitude = true),
            longitude = validateCoordinate(longitude, isLatitude = false),
            areaM2 = validateArea(areaM2)
        )
    }

    private fun validateCoordinate(value: String, isLatitude: Boolean): String? {
        if (value.isBlank()) return null // Optional field
        val num = value.toDoubleOrNull() ?: return "error_coordinate_invalid"
        return when {
            isLatitude && (num < -90 || num > 90) -> "error_latitude_range"
            !isLatitude && (num < -180 || num > 180) -> "error_longitude_range"
            else -> null
        }
    }

    private fun validateArea(value: String): String? {
        if (value.isBlank()) return null // Optional field
        val num = value.toDoubleOrNull() ?: return "error_area_invalid"
        return if (num <= 0) "error_area_positive" else null
    }

    /**
     * Returns true if all required fields are valid.
     */
    val isValid: Boolean
        get() = !validate().hasErrors

    /**
     * Returns Location object if both coordinates are valid, null otherwise.
     */
    val location: Location?
        get() {
            val lat = latitude.toDoubleOrNull()
            val lon = longitude.toDoubleOrNull()
            return if (lat != null && lon != null) Location(lat, lon) else null
        }

    /**
     * Returns areaM2 as Double if valid, null otherwise.
     */
    val areaM2AsDouble: Double?
        get() = areaM2.toDoubleOrNull()

    companion object {
        /**
         * Creates form data pre-filled from an existing greenhouse for edit mode.
         */
        fun fromGreenhouse(greenhouse: Greenhouse): GreenhouseFormData {
            return GreenhouseFormData(
                name = greenhouse.name,
                latitude = greenhouse.location?.lat?.toString() ?: "",
                longitude = greenhouse.location?.lon?.toString() ?: "",
                areaM2 = greenhouse.areaM2?.toString() ?: "",
                timezone = greenhouse.timezone ?: "Europe/Madrid",
                isActive = greenhouse.isActive,
                isEditMode = true
            )
        }
    }
}
