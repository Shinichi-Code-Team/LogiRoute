package edu.logiroute.logiroute.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.logiroute.domain.model.Vehicle
import edu.logiroute.logiroute.previews.SampleData
import edu.logiroute.logiroute.theme.ByteBloomTokens

@Composable
fun ActiveFleetGrid(
    vehicles: List<Vehicle>,
    modifier: Modifier = Modifier,
    vehicleLoads: Map<String, Double> = emptyMap()
) {
    if (vehicles.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(ByteBloomTokens.SpaceExtraLarge),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No Active Vehicles in Fleet",
                color = ByteBloomTokens.TextDisabled,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 280.dp),
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(ByteBloomTokens.SpaceExtraLarge),
        horizontalArrangement = Arrangement.spacedBy(ByteBloomTokens.SpaceMedium),
        verticalArrangement = Arrangement.spacedBy(ByteBloomTokens.SpaceMedium)
    ) {
        items(
            items = vehicles,
            key = { vehicle -> vehicle.id }
        ) { vehicle ->
            val currentLoad = vehicleLoads[vehicle.id] ?: 0.0
            val loadRatio = if (vehicle.maxCapacityKg > 0) currentLoad / vehicle.maxCapacityKg else 0.0

            val borderStroke = when {
                loadRatio > 0.85 -> BorderStroke(
                    width = ByteBloomTokens.BorderWidthThick,
                    color = ByteBloomTokens.WarningAmber
                )
                else -> BorderStroke(
                    width = ByteBloomTokens.BorderWidthThin,
                    color = ByteBloomTokens.Border
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ByteBloomTokens.CornerRadiusMedium),
                border = borderStroke,
                colors = CardDefaults.cardColors(
                    containerColor = ByteBloomTokens.ElevatedCardSurface
                )
            ) {
                VehicleDetailCard(
                    vehicle = vehicle,
                    currentLoadKg = currentLoad
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewActiveFleetGrid() {
    val sampleVehicles = listOf(
        SampleData.normalLoadVehicle,
        SampleData.heavyLoadVehicle,
        SampleData.overloadedVehicle
    )
    val sampleLoads = mapOf(
        "VEH-101" to 2500.0,
        "VEH-204" to 8900.0,
        "VEH-999" to 10500.0
    )
    ActiveFleetGrid(
        vehicles = sampleVehicles,
        vehicleLoads = sampleLoads
    )
}