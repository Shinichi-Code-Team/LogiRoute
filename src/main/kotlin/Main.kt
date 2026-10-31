package com.example.logiroute

import com.example.logiroute.di.networkModule
import com.example.logiroute.di.repositoryModule
import com.example.logiroute.di.useCaseModule
import com.example.logiroute.di.validatorModule
import com.example.logiroute.domain.cache.RoutePathCache
import com.example.logiroute.presentation.cli.LogiRouteCli
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
        LogiRouteCli(
            readWarehouseUseCase = koin.get(),
            readVehicleUseCase = koin.get(),
            findOptimalPathUseCase = koin.get(),
            classifyPackageSectorUseCase = koin.get(),
            trackActiveFleetUseCase = koin.get(),
            optimizeLastMileRouteUseCase = koin.get(),
            routePathCache = koin.get<RoutePathCache>()
        ).run()
    } finally {
        stopKoin()
    }
}
