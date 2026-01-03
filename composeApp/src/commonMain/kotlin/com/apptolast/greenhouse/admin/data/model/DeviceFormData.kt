package com.apptolast.greenhouse.admin.data.model

/**
 * Form data for creating or editing a device.
 * Manages form state and validation.
 */
data class DeviceFormData(
    val name: String = "",
    val type: DeviceType = DeviceType.SENSOR,
    val status: DeviceStatus = DeviceStatus.ONLINE
) {
    /**
     * Validation errors for each field.
     */
    data class ValidationErrors(
        val name: String? = null
    ) {
        val hasErrors: Boolean
            get() = name != null
    }

    /**
     * Validates the form and returns any errors.
     */
    fun validate(): ValidationErrors {
        return ValidationErrors(
            name = if (name.length < 2) "error_name_min_length" else null
        )
    }

    /**
     * Returns true if all required fields are valid.
     */
    val isValid: Boolean
        get() = name.length >= 2
}
