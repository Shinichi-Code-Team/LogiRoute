package com.example.logiroute.di
import com.example.logiroute.data.repository.PackageRepositoryImpl
import com.example.logiroute.data.repository.RouteRepositoryImpl
import com.example.logiroute.data.repository.VehicleRepositoryImpl
import com.example.logiroute.data.repository.WarehouseRepositoryImpl
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.repository.WarehouseRepository
import org.koin.dsl.module
val repositoryModule = module {
    single<WarehouseRepository> {
        WarehouseRepositoryImpl(
            remoteDataSource = get(),
            dtoMapper = get()
        )
    }

    single<RouteRepository> {
        RouteRepositoryImpl(
            remoteDataSource = get(),
            warehouseRepository = get(),
            dtoMapper = get()
        )
    }

    single<PackageRepository> {
        PackageRepositoryImpl(
            remoteDataSource = get(),
            warehouseRepository = get(),
            dtoMapper = get()
        )
    }

    single<VehicleRepository> {
        VehicleRepositoryImpl(
            remoteDataSource = get(),
            warehouseRepository = get(),
            dtoMapper = get()
        )
    }
}