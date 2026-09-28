package edu.logiroute.logiroute.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object ByteBloomTokens {
    // Brand Colors
    val PrimaryAccent = Color(0xFF92DE79)   // LightGreen
    val OnPrimary = Color(0xFF121921)       // InkBlack
    val SecondaryAccent = Color(0xFF316EBF) // SapphireSky
    val Highlight = Color(0xFF73D7B5)       // CyberSprout
    val BackgroundBase = Color(0xFF121921)  // InkBlack
    val SurfaceBase = Color(0xFF0D141C)     // JetBlack
    val ElevatedCardSurface = Color(0xFF27414E) // CharcoalBlue
    val Border = Color(0xFF40454E)          // Border
    val TextPrimary = Color(0xFFE0E0E0)     // Primary Text
    val TextSecondary = Color(0xFFA0A0A0)   // Secondary Text

    // Semantic Colors
    val ErrorRed = Color(0xFFFFB4AB)        // ErrorRed
    val WarningAmber = Color(0xFFDBB859)    // WarningAmber
    val SuccessGreen = Color(0xFF92DE79)    // SuccessGreen
    val InfoBlue = Color(0xFF2196F3)        // InfoBlue
    val TextDisabled = Color(0xFF606060)   // Disabled Text

    // Reusable Spacing & Layout Tokens (No Hardcoded Dp in Components)
    val SpaceMicro: Dp = 2.dp
    val SpaceExtraSmall: Dp = 4.dp
    val SpaceSmall: Dp = 6.dp
    val SpaceMedium: Dp = 8.dp
    val SpaceLarge: Dp = 12.dp
    val SpaceExtraLarge: Dp = 16.dp
    val SpaceHuge: Dp = 24.dp

    // Component Radii & Heights
    val CornerRadiusSmall: Dp = 4.dp
    val CornerRadiusMedium: Dp = 8.dp
    val CornerRadiusLarge: Dp = 12.dp

    val BorderWidthThin: Dp = 1.dp
    val CapacityIndicatorTrackHeight: Dp = 10.dp
}