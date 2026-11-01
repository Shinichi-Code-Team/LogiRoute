package com.example.logiroute

import com.example.logiroute.di.networkModule
import com.example.logiroute.di.repositoryModule
import com.example.logiroute.di.useCaseModule
import com.example.logiroute.di.validatorModule
import com.example.logiroute.domain.cache.RoutePathCache
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
        val warehouseUseCase = koin.get<ReadWarehouseUseCase>()
        LogiRouteCli(
            routeCli = RouteCli(
                readWarehouseUseCase = warehouseUseCase,
                findOptimalPathUseCase = koin.get(),
                routePathCache = koin.get<RoutePathCache>()
            ),
            packageCli = PackageCli(
                classifyPackageSectorUseCase = koin.get()
            ),
            fleetCli = FleetCli(
                readVehicleUseCase = koin.get(),
                trackActiveFleetUseCase = koin.get()
            ),
            lastMileCli = LastMileCli(
                readWarehouseUseCase = warehouseUseCase,
                optimizeLastMileRouteUseCase = koin.get()
            )
        ).run()
    } finally {
        stopKoin()
    }
}
