import com.example.logiroute.di.networkModule
import com.example.logiroute.di.repositoryModule
import com.example.logiroute.di.useCaseModule
import com.example.logiroute.di.validatorModule
import com.example.logiroute.domain.cache.RoutePathCache
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.usecase.SpatialWarehouseLookupUseCase
import com.example.logiroute.domain.usecase.crud.warehouse.ReadWarehouseUseCase
import com.example.logiroute.presentation.cli.FleetCli
import com.example.logiroute.presentation.cli.LastMileCli
import com.example.logiroute.presentation.cli.LogiRouteCli
import com.example.logiroute.presentation.cli.PackageCli
import com.example.logiroute.presentation.cli.RouteCli
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

suspend fun main() {
    val koin = startKoin {
        modules(
            networkModule,
            repositoryModule,
            validatorModule,
            useCaseModule
        )
    }.koin

    try {
        runGeohashDemo()
//        val warehouseUseCase = koin.get<ReadWarehouseUseCase>()
//        LogiRouteCli(
//            routeCli = RouteCli(
//                readWarehouseUseCase = warehouseUseCase,
//                findOptimalPathUseCase = koin.get(),
//                routePathCache = koin.get<RoutePathCache>()
//            ),
//            packageCli = PackageCli(
//                classifyPackageSectorUseCase = koin.get()
//            ),
//            fleetCli = FleetCli(
//                readVehicleUseCase = koin.get(),
//                trackActiveFleetUseCase = koin.get()
//            ),
//            lastMileCli = LastMileCli(
//                readWarehouseUseCase = warehouseUseCase,
//                optimizeLastMileRouteUseCase = koin.get()
//            )
//        ).run()
    } finally {
        stopKoin()
    }
}

private fun runGeohashDemo() {
    val spatialLookupUseCase = SpatialWarehouseLookupUseCase()
    val sampleWarehouses = createSampleWarehouses()
    val queryLatitude = 31.5017
    val queryLongitude = 34.4668
    val precisionsToTest = listOf(5, 7)

    for (precision in precisionsToTest) {
        val lookupResult = spatialLookupUseCase(
            latitude = queryLatitude,
            longitude = queryLongitude,
            warehouses = sampleWarehouses,
            precision = precision,
            includeNeighbors = true
        )

        println("=== Geohash Spatial Lookup (precision=$precision) ===")
        println("Target Geohash: ${lookupResult.targetGeohash}")
        println("Neighbors: ${lookupResult.neighborGeohashes}")
        println("Matched Warehouses (${lookupResult.nearbyWarehouses.size}):")
        lookupResult.nearbyWarehouses.forEach { warehouse ->
            println(
                " - ${warehouse.id} | ${warehouse.name} " +
                        "(${warehouse.latitude}, ${warehouse.longitude})"
            )
        }
    }
}

private fun createSampleWarehouses(): List<Warehouse> = listOf(
    Warehouse(
        id = "WH-101",
        name = "Central Hub",
        regionalZone = "NORTH",
        latitude = 31.5017,
        longitude = 34.4668
    ),
    Warehouse(
        id = "WH-102",
        name = "Coastal Hub",
        regionalZone = "WEST",
        latitude = 31.5120,
        longitude = 34.4550
    ),
    Warehouse(
        id = "WH-103",
        name = "South Hub",
        regionalZone = "SOUTH",
        latitude = 31.4200,
        longitude = 34.3500
    )
)
