package edu.logiroute.logiroute.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.logiroute.domain.model.Warehouse
import edu.logiroute.logiroute.previews.SampleData
import edu.logiroute.logiroute.theme.ByteBloomTokens

@Composable
fun WarehouseSummaryCard(
    warehouse: Warehouse,
    modifier: Modifier = Modifier
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
            WarehouseIdentityBadge(warehouse = warehouse)
            MetricsRow(warehouse = warehouse)
            CargoPreviewSection(warehouse = warehouse)
        }
    }
}

@Composable
private fun MetricsRow(warehouse: Warehouse) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Packages: ${warehouse.cargoQueue.size}",
            color = ByteBloomTokens.TextPrimary
        )
        Text(
            text = "Vehicles: ${warehouse.stationedVehicles.size}",
            color = ByteBloomTokens.TextPrimary
        )
    }
}

@Composable
private fun CargoPreviewSection(warehouse: Warehouse) {
    warehouse.cargoQueue.firstOrNull()?.let { firstPackage ->
        PackagePriorityBadge(pkg = firstPackage)
    } ?: Text(
        text = "No Pending Cargo",
        color = ByteBloomTokens.TextDisabled
    )
}

@Preview
@Composable
private fun PreviewWarehouseSummaryCardLoaded() {
    WarehouseSummaryCard(warehouse = SampleData.warehouseWithCargo)
}

@Preview
@Composable
private fun PreviewWarehouseSummaryCardEmpty() {
    WarehouseSummaryCard(warehouse = SampleData.emptyWarehouse)
}