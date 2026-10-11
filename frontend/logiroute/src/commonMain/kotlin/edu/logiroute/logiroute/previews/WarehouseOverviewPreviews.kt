package edu.logiroute.logiroute.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import edu.logiroute.logiroute.components.WarehouseOperationalOverview

@Preview(name = "Heavy Data Simulation - Desktop", widthDp = 1200, heightDp = 800)
@Composable
private fun PreviewHeavyDataWarehouse() {
    val heavyWarehouse = Warehouse(
        id = "WH-HEAVY-01",
        name = "Gaza Mega Hub (Heavy Load)",
        regionalZone = "CENTRAL",
        latitude = 0.0,
        longitude = 0.0
    ).apply {
        for (i in 1..50) {
            val priority = when (i % 3) {
                0 -> Priority.URGENT
                1 -> Priority.STANDARD
                else -> Priority.LOW
            }
            addPackage(
                Package(
                    id = "PKG-HEAVY-$i",
                    weight = (i * 2.5),
                    origin = this,
                    destination = this,
                    priority = priority
                )
            )
        }
        for (j in 1..12) {
            addVehicle(
                Vehicle(
                    id = "VEH-M-$j",
                    maxCapacityKg = 15000.0,
                    costPerKm = 2.0,
                    currentHub = this,
                    loadedPackages = mutableListOf()
                )
            )
        }
    }

    WarehouseOperationalOverview(warehouse = heavyWarehouse)
}

@Preview(name = "Overloaded State - Mobile", widthDp = 390, heightDp = 840)
@Composable
private fun PreviewOverloadedWarehouse() {
    val overloadedWarehouse = Warehouse(
        id = "WH-OVER-02",
        name = "Border Express Depot",
        regionalZone = "NORTH",
        latitude = 0.0,
        longitude = 0.0
    ).apply {
        addPackage(
            Package(
                id = "PKG-CRIT-99",
                weight = 25000.0,
                origin = this,
                destination = this,
                priority = Priority.URGENT
            )
        )
        addVehicle(
            Vehicle(
                id = "VEH-SMALL-1",
                maxCapacityKg = 5000.0,
                costPerKm = 4.0,
                currentHub = this,
                loadedPackages = mutableListOf()
            )
        )
    }

    WarehouseOperationalOverview(warehouse = overloadedWarehouse)
}

@Preview(name = "Zero State - Empty Warehouse", widthDp = 400, heightDp = 600)
@Composable
private fun PreviewZeroStateWarehouse() {
    val emptyWarehouse = Warehouse(
        id = "WH-EMPTY-03",
        name = "New South Outpost",
        regionalZone = "SOUTH",
        latitude = 0.0,
        longitude = 0.0
    )

    WarehouseOperationalOverview(warehouse = emptyWarehouse)
}