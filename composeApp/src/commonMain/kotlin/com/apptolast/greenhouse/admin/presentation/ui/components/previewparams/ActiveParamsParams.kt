package com.apptolast.greenhouse.admin.presentation.ui.components.previewparams

import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

/**
 * Data provider that generates true/false options
 */
class ActiveParamsParams : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean> = sequenceOf(true, false)
}

/**
 * Data provider that generates all possible cases:
 * 1. Info, 2. Warning, 3. Error, 4. Critical, 5. Unknown (null)
 */
class SeverityChipProvider : PreviewParameterProvider<Short> {
    override val values: Sequence<Short> = sequenceOf(1, 2, 3, 4, 5)
}
