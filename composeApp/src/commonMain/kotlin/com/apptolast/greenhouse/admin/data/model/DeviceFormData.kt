package com.apptolast.greenhouse.admin.data.model

/**
 * Form data for creating or editing a device.
 * Manages form state and validation.
 */
data class DeviceFormData(
    val greenhouseId: String = "",
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
        val categoryId: String? = null,
        val typeId: String? = null
    ) {
        val hasErrors: Boolean
            get() = greenhouseId != null || categoryId != null || typeId != null
    }

    /**
     * Validates the form and returns any errors.
     */
    fun validate(): ValidationErrors {
        return ValidationErrors(
            greenhouseId = if (greenhouseId.isBlank()) "error_greenhouse_required" else null,
            categoryId = if (categoryId == null) "error_category_required" else null,
            typeId = if (typeId == null) "error_type_required" else null
        )
    }

    /**
     * Returns true if all required fields are valid.
     */
    val isValid: Boolean
        get() = greenhouseId.isNotBlank() && categoryId != null && typeId != null
}
