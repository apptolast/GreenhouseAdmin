package com.apptolast.greenhouse.admin.data.model

/**
 * Form data for creating or editing a device.
 * Manages form state and validation.
 * Note: Devices are associated with sectors, not greenhouses directly.
 */
data class DeviceFormData(
    val sectorId: Long? = null,
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
        val sectorId: String? = null,
        val name: String? = null,
        val categoryId: String? = null,
        val typeId: String? = null
    ) {
        val hasErrors: Boolean
            get() = sectorId != null || name != null || categoryId != null || typeId != null
    }

    /**
     * Validates the form and returns any errors.
     */
    fun validate(): ValidationErrors {
        return ValidationErrors(
            sectorId = if (sectorId == null) "error_sector_required" else null,
            name = if (name.length > MAX_NAME_LENGTH) "error_name_max_length" else null,
            categoryId = if (categoryId == null) "error_category_required" else null,
            typeId = if (typeId == null) "error_type_required" else null
        )
    }

    /**
     * Returns true if all required fields are valid.
     */
    val isValid: Boolean
        get() = sectorId != null && categoryId != null && typeId != null && name.length <= MAX_NAME_LENGTH

    companion object {
        const val MAX_NAME_LENGTH = 100
    }
}
