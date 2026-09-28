package edu.logiroute.logiroute.previews

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse

object SampleData {
    val centralWarehouse = Warehouse(
        id = "WH-101",
        name = "Gaza Central Warehouse",
        regionalZone = "CENTRAL",
        latitude = 0.0,
        longitude = 0.0
    )

    val northWarehouse = Warehouse(
        id = "WH-002",
        name = "North Depot",
        regionalZone = "NORTH",
        latitude = 0.0,
        longitude = 0.0
    )

    val southWarehouse = Warehouse(
        id = "WH-003",
        name = "South Hub",
        regionalZone = "SOUTH",
        latitude = 0.0,
        longitude = 0.0
    )

    val urgentPackage = Package(
        id = "PKG-8821",
        weight = 14.5,
        origin = centralWarehouse,
        destination = centralWarehouse,
        priority = Priority.URGENT
    )

    val standardPackage = Package(
        id = "PKG-3410",
        weight = 8.0,
        origin = northWarehouse,
        destination = northWarehouse,
        priority = Priority.STANDARD
    )

    val lowPackage = Package(
        id = "PKG-1102",
        weight = 5.0,
        origin = southWarehouse,
        destination = southWarehouse,
        priority = Priority.LOW
    )

    val warehouseWithCargo: Warehouse
        get() = Warehouse(
            id = "WH-101",
            name = "Gaza Central Warehouse",
            regionalZone = "CENTRAL",
            latitude = 0.0,
            longitude = 0.0
        ).apply {
            addPackage(
                Package(
                    id = "PKG-8821",
                    weight = 14.5,
                    origin = this,
                    destination = this,
                    priority = Priority.URGENT
                )
            )
        }

    val emptyWarehouse = northWarehouse
}