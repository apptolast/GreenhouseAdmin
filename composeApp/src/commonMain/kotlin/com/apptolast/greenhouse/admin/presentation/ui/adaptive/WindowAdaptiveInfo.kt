package com.apptolast.greenhouse.admin.presentation.ui.adaptive

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Window size class categories based on Material 3 guidelines.
 * - Compact: width < 600dp (phones in portrait)
 * - Medium: 600dp <= width < 840dp (tablets in portrait)
 * - Expanded: width >= 840dp (tablets in landscape, desktop)
 */
enum class WindowWidthClass {
    COMPACT,
    MEDIUM,
    EXPANDED
}

/**
 * Wrapper class that provides simplified access to window size class information.
 */
@Immutable
data class AppWindowInfo(
    val widthDp: Dp,
    val heightDp: Dp,
    val widthClass: WindowWidthClass,
    val isCompact: Boolean,
    val isMedium: Boolean,
    val isExpanded: Boolean
) {
    companion object {
        // Breakpoint constants (in dp) following Material 3 guidelines
        val COMPACT_MAX_WIDTH = 600.dp
        val MEDIUM_MAX_WIDTH = 840.dp

        /**
         * Creates an AppWindowInfo from the given dimensions.
         */
        fun fromDimensions(widthDp: Dp, heightDp: Dp): AppWindowInfo {
            val widthClass = when {
                widthDp < COMPACT_MAX_WIDTH -> WindowWidthClass.COMPACT
                widthDp < MEDIUM_MAX_WIDTH -> WindowWidthClass.MEDIUM
                else -> WindowWidthClass.EXPANDED
            }

            return AppWindowInfo(
                widthDp = widthDp,
                heightDp = heightDp,
                widthClass = widthClass,
                isCompact = widthClass == WindowWidthClass.COMPACT,
                isMedium = widthClass == WindowWidthClass.MEDIUM,
                isExpanded = widthClass == WindowWidthClass.EXPANDED
            )
        }
    }
}

/**
 * CompositionLocal that provides AppWindowInfo to the composition tree.
 */
val LocalAppWindowInfo = compositionLocalOf<AppWindowInfo> {
    error("LocalAppWindowInfo not provided. Use ProvideAppWindowInfo at the root of your composition.")
}

/**
 * Provides AppWindowInfo to the composition tree using BoxWithConstraints.
 * Place this at the root of your app (in App.kt) to make window size info available everywhere.
 *
 * Example usage:
 * ```
 * ProvideAppWindowInfo {
 *     // Your app content
 *     val windowInfo = LocalAppWindowInfo.current
 *     if (windowInfo.isCompact) {
 *         // Mobile layout
 *     } else {
 *         // Desktop layout
 *     }
 * }
 * ```
 */
@Composable
fun ProvideAppWindowInfo(
    content: @Composable () -> Unit
) {
    BoxWithConstraints {
        val windowInfo = AppWindowInfo.fromDimensions(
            widthDp = maxWidth,
            heightDp = maxHeight
        )
        CompositionLocalProvider(LocalAppWindowInfo provides windowInfo) {
            content()
        }
    }
}

/**
 * Composable function to get the current window info.
 * Must be called within a ProvideAppWindowInfo scope.
 */
@Composable
fun rememberAppWindowInfo(): AppWindowInfo = LocalAppWindowInfo.current

/**
 * Adaptive dimensions that change based on window size class.
 * Use these for consistent spacing across different screen sizes.
 */
object AdaptiveDimens {
    /**
     * Content padding for main screen areas.
     * - Compact: 16.dp
     * - Medium: 20.dp
     * - Expanded: 24.dp
     */
    @Composable
    fun contentPadding(): Dp {
        val windowInfo = LocalAppWindowInfo.current
        return when (windowInfo.widthClass) {
            WindowWidthClass.COMPACT -> 16.dp
            WindowWidthClass.MEDIUM -> 20.dp
            WindowWidthClass.EXPANDED -> 24.dp
        }
    }

    /**
     * Horizontal padding for content.
     * - Compact: 16.dp
     * - Medium: 24.dp
     * - Expanded: 32.dp
     */
    @Composable
    fun horizontalPadding(): Dp {
        val windowInfo = LocalAppWindowInfo.current
        return when (windowInfo.widthClass) {
            WindowWidthClass.COMPACT -> 16.dp
            WindowWidthClass.MEDIUM -> 24.dp
            WindowWidthClass.EXPANDED -> 32.dp
        }
    }

    /**
     * Vertical spacing between items.
     * - Compact: 12.dp
     * - Medium: 16.dp
     * - Expanded: 24.dp
     */
    @Composable
    fun verticalSpacing(): Dp {
        val windowInfo = LocalAppWindowInfo.current
        return when (windowInfo.widthClass) {
            WindowWidthClass.COMPACT -> 12.dp
            WindowWidthClass.MEDIUM -> 16.dp
            WindowWidthClass.EXPANDED -> 24.dp
        }
    }

    /**
     * Card padding.
     * - Compact: 12.dp
     * - Medium: 16.dp
     * - Expanded: 20.dp
     */
    @Composable
    fun cardPadding(): Dp {
        val windowInfo = LocalAppWindowInfo.current
        return when (windowInfo.widthClass) {
            WindowWidthClass.COMPACT -> 12.dp
            WindowWidthClass.MEDIUM -> 16.dp
            WindowWidthClass.EXPANDED -> 20.dp
        }
    }
}

/**
 * Convenience composable that provides access to window info within a BoxWithConstraints scope.
 * Useful for individual components that need to adapt their layout.
 */
@Composable
fun AdaptiveLayout(
    content: @Composable BoxWithConstraintsScope.(AppWindowInfo) -> Unit
) {
    BoxWithConstraints {
        val windowInfo = AppWindowInfo.fromDimensions(
            widthDp = maxWidth,
            heightDp = maxHeight
        )
        content(windowInfo)
    }
}
