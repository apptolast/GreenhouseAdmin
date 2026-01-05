package com.apptolast.greenhouse.admin.data.model

/**
 * Form data for creating or editing a device.
 * Manages form state and validation.
 */
data class DeviceFormData(
    val greenhouseId: String = "",
    val name: String = "",
    val categoryId: Short? = Device.CATEGORY_SENSOR,
    val typeId: Short? = null,
    val unitId: Short? = null,
    val isActive: Boolean = true
) {
    /**
     * Validation errors for each field.
     */
    data class ValidationErrors(
        val greenhouseId: String? = null,
        val name: String? = null
    ) {
        val hasErrors: Boolean
            get() = greenhouseId != null || name != null
    }

    /**
     * Validates the form and returns any errors.
     */
    fun validate(): ValidationErrors {
        return ValidationErrors(
            greenhouseId = if (greenhouseId.isBlank()) "error_greenhouse_required" else null,
            name = if (name.isBlank()) "error_name_required" else null
        )
    }

    /**
     * Returns true if all required fields are valid.
     */
    val isValid: Boolean
        get() = greenhouseId.isNotBlank() && name.isNotBlank()
}
