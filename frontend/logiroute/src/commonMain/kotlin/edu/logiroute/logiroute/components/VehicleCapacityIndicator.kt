package edu.logiroute.logiroute.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import edu.logiroute.logiroute.theme.ByteBloomTokens
import kotlin.math.roundToInt

@Composable
fun VehicleCapacityIndicator(
    currentLoadKg: Double,
    maxCapacityKg: Double,
    modifier: Modifier = Modifier
) {
    val rawRatio = if (maxCapacityKg > 0) currentLoadKg / maxCapacityKg else 0.0
    val visualProgress = rawRatio.toFloat().coerceIn(0f, 1f)
    val percentageInt = (rawRatio * 100).roundToInt()

    val progressColor = when {
        rawRatio < 0.70 -> ByteBloomTokens.SuccessGreen
        rawRatio <= 0.90 -> ByteBloomTokens.WarningAmber
        else -> ByteBloomTokens.ErrorRed
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ByteBloomTokens.SpaceMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CapacityTrackBox(
            visualProgress = visualProgress,
            progressColor = progressColor,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "$percentageInt%",
            color = ByteBloomTokens.TextSecondary,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun CapacityTrackBox(
    visualProgress: Float,
    progressColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(ByteBloomTokens.CapacityIndicatorTrackHeight)
            .drawBehind {
                val cornerRadius = CornerRadius(size.height / 2, size.height / 2)

                drawRoundRect(
                    color = ByteBloomTokens.SurfaceBase,
                    size = size,
                    cornerRadius = cornerRadius
                )

                if (visualProgress > 0f) {
                    drawRoundRect(
                        color = progressColor,
                        size = Size(width = size.width * visualProgress, height = size.height),
                        cornerRadius = cornerRadius
                    )
                }
            }
    )
}

@Preview
@Composable
private fun PreviewVehicleCapacityIndicatorSafe() {
    VehicleCapacityIndicator(
        currentLoadKg = 2500.0,
        maxCapacityKg = 10000.0
    )
}

@Preview
@Composable
private fun PreviewVehicleCapacityIndicatorHeavy() {
    VehicleCapacityIndicator(
        currentLoadKg = 8200.0,
        maxCapacityKg = 10000.0
    )
}

@Preview
@Composable
private fun PreviewVehicleCapacityIndicatorOverloaded() {
    VehicleCapacityIndicator(
        currentLoadKg = 10500.0,
        maxCapacityKg = 10000.0
    )
}