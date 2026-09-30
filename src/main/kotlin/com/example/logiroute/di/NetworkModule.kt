package com.example.logiroute.di
import com.example.logiroute.data.remote.datasource.RemotePackageDataSource import com.example.logiroute.data.remote.datasource.RemoteRouteDataSource import com.example.logiroute.data.remote.datasource.RemoteWarehouseDataSource import com.example.logiroute.data.remote.datasource.SupabaseRouteRemoteDataSource import com.example.logiroute.data.remote.datasource.SupabaseVehicleRemoteDataSource import com.example.logiroute.data.remote.datasource.impl.SupabasePackageRemoteDataSource import com.example.logiroute.data.remote.datasource.impl.SupabaseWarehouseRemoteDataSource import com.example.logiroute.data.remote.datasource.vehicle.RemoteVehicleDataSource import com.example.logiroute.data.remote.mapper.PackageDtoMapper import com.example.logiroute.data.remote.mapper.RouteDtoMapper import com.example.logiroute.data.remote.mapper.VehicleDtoMapper import com.example.logiroute.data.remote.mapper.WarehouseDtoMapper import org.koin.dsl.module
val networkModule = module {
    single<RemoteWarehouseDataSource> {
        SupabaseWarehouseRemoteDataSource()
    }

    single<RemoteRouteDataSource> {
        SupabaseRouteRemoteDataSource()
    }

    single<RemotePackageDataSource> {
        SupabasePackageRemoteDataSource()
    }

    single<RemoteVehicleDataSource> {
        SupabaseVehicleRemoteDataSource()
    }

    single {
        WarehouseDtoMapper()
    }

    single {
        RouteDtoMapper()
    }

    single {
        PackageDtoMapper()
    }

    single {
        VehicleDtoMapper()
    }
}