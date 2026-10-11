package edu.logiroute.logiroute.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.logiroute.domain.model.Warehouse
import edu.logiroute.logiroute.previews.SampleData
import edu.logiroute.logiroute.theme.ByteBloomTokens

@Composable
fun WarehouseOperationalOverview(
    warehouse: Warehouse,
    modifier: Modifier = Modifier
) {
    val totalCargoWeight = remember(warehouse.cargoQueue) {
        warehouse.cargoQueue.sumOf { it.weight }
    }

    val totalFleetCapacity = remember(warehouse.stationedVehicles) {
        warehouse.stationedVehicles.sumOf { it.maxCapacityKg }
    }

    val isOverloaded = totalCargoWeight > totalFleetCapacity
    val overloadDifference = totalCargoWeight - totalFleetCapacity

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ByteBloomTokens.SurfaceBase)
            .padding(ByteBloomTokens.SpaceExtraLarge),
        verticalArrangement = Arrangement.spacedBy(ByteBloomTokens.SpaceLarge)
    ) {
        WarehouseSummaryCard(warehouse = warehouse)

        if (isOverloaded) {
            WarehouseOverloadBanner(overloadKg = overloadDifference)
        }

        BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
            val isWideScreen = maxWidth >= 840.dp

            if (isWideScreen) {
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(ByteBloomTokens.SpaceLarge)
                ) {
                    WarehouseCargoQueueList(
                        cargoQueue = warehouse.cargoQueue,
                        modifier = Modifier.weight(0.4f)
                    )
                    ActiveFleetGrid(
                        vehicles = warehouse.stationedVehicles,
                        modifier = Modifier.weight(0.6f)
                    )
                }
            } else {
                Column(
                    modifier = modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(ByteBloomTokens.SpaceLarge)
                ) {
                    WarehouseCargoQueueList(cargoQueue = warehouse.cargoQueue)
                    ActiveFleetGrid(vehicles = warehouse.stationedVehicles)
                }
            }
        }
    }
}

@Composable
private fun WarehouseOverloadBanner(
    overloadKg: Double,
    modifier: Modifier = Modifier
) {
    val formattedKg = String.format("___", overloadKg)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = ByteBloomTokens.ErrorRed,
                shape = RoundedCornerShape(ByteBloomTokens.CornerRadiusMedium)
            )
            .border(
                width = ByteBloomTokens.BorderWidthThin,
                color = ByteBloomTokens.Border,
                shape = RoundedCornerShape(ByteBloomTokens.CornerRadiusMedium)
            )
            .padding(ByteBloomTokens.SpaceLarge),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "⚠️ Warehouse Overloaded: Cargo queue weight exceeds stationed fleet capacity by $overloadKg kg.",
            color = ByteBloomTokens.OnPrimary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(widthDp = 400, heightDp = 800)
@Composable
private fun PreviewWarehouseOperationalOverviewMobile() {
    WarehouseOperationalOverview(warehouse = SampleData.warehouseWithCargo)
}

@Preview(widthDp = 1000, heightDp = 700)
@Composable
private fun PreviewWarehouseOperationalOverviewDesktop() {
    WarehouseOperationalOverview(warehouse = SampleData.warehouseWithCargo)
}