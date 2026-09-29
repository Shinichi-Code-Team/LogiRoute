package edu.logiroute.logiroute.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.logiroute.domain.model.Vehicle
import edu.logiroute.logiroute.previews.SampleData
import edu.logiroute.logiroute.theme.ByteBloomTokens

@Composable
fun VehicleDetailCard(
    vehicle: Vehicle,
    modifier: Modifier = Modifier,
    currentLoadKg: Double = 0.0
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(ByteBloomTokens.CornerRadiusMedium),
        border = BorderStroke(
            width = ByteBloomTokens.BorderWidthThin,
            color = ByteBloomTokens.Border
        ),
        colors = CardDefaults.cardColors(
            containerColor = ByteBloomTokens.ElevatedCardSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(ByteBloomTokens.SpaceLarge),
            verticalArrangement = Arrangement.spacedBy(ByteBloomTokens.SpaceLarge)
        ) {
            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = vehicle.id,
                    color = ByteBloomTokens.TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .border(
                            width = ByteBloomTokens.BorderWidthThin,
                            color = ByteBloomTokens.Highlight,
                            shape = RoundedCornerShape(ByteBloomTokens.CornerRadiusSmall)
                        )
                        .padding(
                            horizontal = ByteBloomTokens.SpaceSmall,
                            vertical = ByteBloomTokens.SpaceMicro
                        )
                ) {
                    Text(
                        text = vehicle.currentHub.name,
                        color = ByteBloomTokens.Highlight,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = ByteBloomTokens.PrimaryAccent,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("$${vehicle.costPerKm}")
                        }
                        withStyle(SpanStyle(color = ByteBloomTokens.TextSecondary)) {
                            append(" /km")
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = ByteBloomTokens.TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("${vehicle.maxCapacityKg.toInt()}")
                        }
                        withStyle(SpanStyle(color = ByteBloomTokens.TextSecondary)) {
                            append(" kg max")
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            VehicleCapacityIndicator(
                currentLoadKg = currentLoadKg,
                maxCapacityKg = vehicle.maxCapacityKg
            )
        }
    }
}

@Preview
@Composable
private fun PreviewVehicleDetailCardSafe() {
    VehicleDetailCard(
        vehicle = SampleData.normalLoadVehicle,
        currentLoadKg = 2500.0
    )
}

@Preview
@Composable
private fun PreviewVehicleDetailCardHeavy() {
    VehicleDetailCard(
        vehicle = SampleData.heavyLoadVehicle,
        currentLoadKg = 8200.0
    )
}

@Preview
@Composable
private fun PreviewVehicleDetailCardOverloaded() {
    VehicleDetailCard(
        vehicle = SampleData.overloadedVehicle,
        currentLoadKg = 10500.0
    )
}