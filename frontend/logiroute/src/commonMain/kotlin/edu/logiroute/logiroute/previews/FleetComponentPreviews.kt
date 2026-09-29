package edu.logiroute.logiroute.previews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import edu.logiroute.logiroute.components.RouteDetailCard
import edu.logiroute.logiroute.components.VehicleCapacityIndicator
import edu.logiroute.logiroute.components.VehicleDetailCard
import edu.logiroute.logiroute.theme.ByteBloomTokens

@Preview
@Composable
fun PreviewAllFleetComponentsOverview() {
    Column(
        modifier = Modifier
            .background(ByteBloomTokens.BackgroundBase)
            .padding(ByteBloomTokens.SpaceExtraLarge),
        verticalArrangement = Arrangement.spacedBy(ByteBloomTokens.SpaceExtraLarge)
    ) {
        VehicleCapacityIndicator(
            currentLoadKg = 8200.0,
            maxCapacityKg = 10000.0
        )

        VehicleDetailCard(
            vehicle = SampleData.normalLoadVehicle,
            currentLoadKg = 2500.0
        )

        VehicleDetailCard(
            vehicle = SampleData.heavyLoadVehicle,
            currentLoadKg = 8200.0
        )

        RouteDetailCard(route = SampleData.shortRoute)

        RouteDetailCard(route = SampleData.longRouteWithDelay)
    }
}