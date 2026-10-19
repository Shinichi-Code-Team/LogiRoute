package com.example.logiroute

import com.example.logiroute.di.networkModule
import com.example.logiroute.di.repositoryModule
import com.example.logiroute.di.useCaseModule
import com.example.logiroute.di.validatorModule
import com.example.logiroute.domain.usecase.ClassifyPackageSectorUseCase
import com.example.logiroute.domain.usecase.FindOptimalPathUseCase
import com.example.logiroute.domain.usecase.TrackActiveFleetUseCase
import com.example.logiroute.domain.usecase.crud.vehicle.ReadVehicleUseCase
import com.example.logiroute.domain.usecase.crud.warehouse.ReadWarehouseUseCase
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
        val cli = LogiRouteCli(
            readWarehouseUseCase =
                koin.get<ReadWarehouseUseCase>(),
            readVehicleUseCase =
                koin.get<ReadVehicleUseCase>(),
            findOptimalPathUseCase =
                koin.get<FindOptimalPathUseCase>(),
            classifyPackageSectorUseCase =
                koin.get<ClassifyPackageSectorUseCase>(),
            trackActiveFleetUseCase =
                koin.get<TrackActiveFleetUseCase>()
        )
        cli.run()

    } finally {
        stopKoin()
    }
}