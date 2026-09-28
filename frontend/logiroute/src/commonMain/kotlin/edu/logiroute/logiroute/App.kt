package edu.logiroute.logiroute

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import edu.logiroute.logiroute.components.PackagePriorityBadge
import edu.logiroute.logiroute.components.WarehouseIdentityBadge
import edu.logiroute.logiroute.components.WarehouseSummaryCard
import edu.logiroute.logiroute.previews.SampleData
import edu.logiroute.logiroute.theme.ByteBloomTokens
import logiroute.frontend.logiroute.generated.resources.Res
import logiroute.frontend.logiroute.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource

@Composable
@Preview
fun App() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }
            AnimatedVisibility(showContent) {
                val greeting = remember { "Hello \"DDDDDDDDDDDDDD " }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: $greeting")
                }
            }

            WeekOneComponentsTestSuite()
        }
    }
}

@Composable
private fun WeekOneComponentsTestSuite() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ByteBloomTokens.BackgroundBase)
            .verticalScroll(rememberScrollState())
            .padding(ByteBloomTokens.SpaceExtraLarge),
        verticalArrangement = Arrangement.spacedBy(ByteBloomTokens.SpaceExtraLarge)
    ) {
        Text(
            text = "Week 1 Components Test Lounge",
            color = ByteBloomTokens.TextPrimary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "1. Package Priority Badges",
            color = ByteBloomTokens.TextSecondary,
            style = MaterialTheme.typography.titleMedium
        )
        PackagePriorityBadge(pkg = SampleData.urgentPackage)
        PackagePriorityBadge(pkg = SampleData.standardPackage)
        PackagePriorityBadge(pkg = SampleData.lowPackage)

        Text(
            text = "2. Warehouse Identity Badge",
            color = ByteBloomTokens.TextSecondary,
            style = MaterialTheme.typography.titleMedium
        )
        WarehouseIdentityBadge(warehouse = SampleData.centralWarehouse)

        Text(
            text = "3. Warehouse Summary Cards",
            color = ByteBloomTokens.TextSecondary,
            style = MaterialTheme.typography.titleMedium
        )
        WarehouseSummaryCard(warehouse = SampleData.warehouseWithCargo)
        WarehouseSummaryCard(warehouse = SampleData.emptyWarehouse)
    }
}