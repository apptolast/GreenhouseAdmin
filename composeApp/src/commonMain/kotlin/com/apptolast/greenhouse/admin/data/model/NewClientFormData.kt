package com.apptolast.greenhouse.admin.data.model

/**
 * Form data for creating a new client.
 * Manages form state and validation.
 */
data class NewClientFormData(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val province: String = "",
    val country: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val status: ClientStatus = ClientStatus.PENDING
) {
    /**
     * Creates a Location object from the latitude and longitude strings.
     * Returns null if either coordinate is blank or invalid.
     */
    fun toLocation(): Location? {
        val lat = latitude.toDoubleOrNull()
        val lon = longitude.toDoubleOrNull()
        return if (lat != null && lon != null) Location(lat, lon) else null
    }

    /**
     * Validation errors for each field.
     */
    data class ValidationErrors(
        val name: String? = null,
        val email: String? = null,
        val phone: String? = null,
        val province: String? = null,
        val country: String? = null
    ) {
        val hasErrors: Boolean
            get() = name != null || email != null || phone != null ||
                    province != null || country != null
    }

    /**
     * Validates the form and returns any errors.
     */
    fun validate(): ValidationErrors {
        return ValidationErrors(
            name = if (name.length < 2) "error_name_min_length" else null,
            email = if (!isValidEmail(email)) "error_email_invalid" else null,
            phone = if (phone.isBlank()) "error_phone_required" else null,
            province = if (province.isBlank()) "error_province_required" else null,
            country = if (country.isBlank()) "error_country_required" else null
        )
    }

    /**
     * Returns true if all required fields are valid.
     */
    val isValid: Boolean
        get() = name.length >= 2 &&
                isValidEmail(email) &&
                phone.isNotBlank() &&
                province.isNotBlank() &&
                country.isNotBlank()

    companion object {
        private val EMAIL_REGEX = Regex(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        )

        fun isValidEmail(email: String): Boolean {
            return email.isNotBlank() && EMAIL_REGEX.matches(email)
        }
    }
}
