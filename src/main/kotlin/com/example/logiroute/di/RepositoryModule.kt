package com.example.logiroute.di

import com.example.logiroute.data.repository.PackageRepositoryImpl
import com.example.logiroute.data.repository.RouteRepositoryImpl
import com.example.logiroute.data.repository.VehicleRepositoryImpl
import com.example.logiroute.data.repository.WarehouseRepositoryImpl
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.repository.WarehouseRepository
import com.example.logiroute.data.telemetry.SimulatedTelemetryStreamingService
import com.example.logiroute.domain.service.TelemetryStreamingService

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::WarehouseRepositoryImpl) {
        bind<WarehouseRepository>()
    }

    singleOf(::RouteRepositoryImpl) {
        bind<RouteRepository>()
    }

    singleOf(::PackageRepositoryImpl) {
        bind<PackageRepository>()
    }

    singleOf(::VehicleRepositoryImpl) {
        bind<VehicleRepository>()
    }
    single<TelemetryStreamingService> {
        SimulatedTelemetryStreamingService()
    }
}