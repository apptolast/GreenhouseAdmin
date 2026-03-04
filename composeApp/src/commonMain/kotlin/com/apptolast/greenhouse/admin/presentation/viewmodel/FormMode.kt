package com.apptolast.greenhouse.admin.presentation.viewmodel

/**
 * Generic mode for entity form dialogs.
 * Replaces entity-specific FormMode interfaces (UserFormMode, GreenhouseFormMode, etc.)
 */
sealed interface FormMode<out T> {
    data object Create : FormMode<Nothing>
    data class Edit<T>(val entity: T) : FormMode<T>
}
