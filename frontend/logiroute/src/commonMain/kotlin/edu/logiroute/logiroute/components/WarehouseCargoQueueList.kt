package edu.logiroute.logiroute.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import edu.logiroute.logiroute.previews.SampleData
import edu.logiroute.logiroute.theme.ByteBloomTokens

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WarehouseCargoQueueList(
    cargoQueue: List<Package>,
    modifier: Modifier = Modifier
) {
    val groupedCargo = remember(cargoQueue) {
        cargoQueue.groupBy { it.priority }
            .toSortedMap(compareBy { priority ->
                when (priority) {
                    Priority.URGENT -> 0
                    Priority.STANDARD -> 1
                    Priority.LOW -> 2
                }
            })
    }

    if (cargoQueue.isEmpty()) {
        WarehouseEmptyState(modifier = modifier)
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(ByteBloomTokens.SpaceExtraLarge),
        verticalArrangement = Arrangement.spacedBy(ByteBloomTokens.SpaceMedium)
    ) {
        groupedCargo.forEach { (priority, packages) ->
            if (packages.isNotEmpty()) {
                stickyHeader(key = "header_${priority.name}") {
                    WarehouseCargoHeader(
                        title = "${priority.name} CARGO",
                        count = packages.size
                    )
                }

                items(
                    items = packages,
                    key = { pkg -> pkg.id },
                    contentType = { "PackageItem" }
                ) { pkg ->
                    WarehousePackageItemRow(pkg = pkg)
                }
            }
        }
    }
}

@Composable
private fun WarehouseEmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(ByteBloomTokens.SpaceExtraLarge),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No Pending Cargo in Queue",
            color = ByteBloomTokens.TextDisabled,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun WarehouseCargoHeader(
    title: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(ByteBloomTokens.BackgroundBase)
            .border(
                width = ByteBloomTokens.BorderWidthThin,
                color = ByteBloomTokens.Border,
                shape = RoundedCornerShape(ByteBloomTokens.CornerRadiusSmall)
            )
            .padding(
                horizontal = ByteBloomTokens.SpaceLarge,
                vertical = ByteBloomTokens.SpaceMedium
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = ByteBloomTokens.TextPrimary,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "($count)",
            color = ByteBloomTokens.PrimaryAccent,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun WarehousePackageItemRow(
    pkg: Package,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ByteBloomTokens.SpaceExtraSmall)
    ) {
        PackagePriorityBadge(pkg = pkg)
        Text(
            text = "Destination: ${pkg.destination.name}",
            color = ByteBloomTokens.TextSecondary,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(start = ByteBloomTokens.SpaceSmall)
        )
    }
}

@Preview
@Composable
private fun PreviewWarehouseCargoQueueList() {
    val sampleList = listOf(
        SampleData.urgentPackage,
        SampleData.standardPackage,
        SampleData.lowPackage
    )
    WarehouseCargoQueueList(cargoQueue = sampleList)
}