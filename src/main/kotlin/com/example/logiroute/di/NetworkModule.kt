package com.example.logiroute.di

import com.example.logiroute.data.remote.datasource.*
import com.example.logiroute.data.remote.datasource.impl.SupabasePackageRemoteDataSource
import com.example.logiroute.data.remote.datasource.impl.SupabaseWarehouseRemoteDataSource
import com.example.logiroute.data.remote.datasource.vehicle.RemoteVehicleDataSource
import com.example.logiroute.data.remote.mapper.PackageDtoMapper
import com.example.logiroute.data.remote.mapper.RouteDtoMapper
import com.example.logiroute.data.remote.mapper.VehicleDtoMapper
import com.example.logiroute.data.remote.mapper.WarehouseDtoMapper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val networkModule = module {
    singleOf(::setupSupabaseSpecs)
    singleOf(::setupSupabaseClient)


    singleOf(::SupabaseWarehouseRemoteDataSource) {
        bind<RemoteWarehouseDataSource>()
    }

    singleOf(::SupabaseRouteRemoteDataSource) {
        bind<RemoteRouteDataSource>()
    }

    singleOf(::SupabasePackageRemoteDataSource) {
        bind<RemotePackageDataSource>()
    }

    singleOf(::SupabaseVehicleRemoteDataSource) {
        bind<RemoteVehicleDataSource>()
    }
    singleOf(::WarehouseDtoMapper)
    singleOf(::RouteDtoMapper)
    singleOf(::PackageDtoMapper)
    singleOf(::VehicleDtoMapper)
}

private data class SupabaseSpecs(
    val supabaseUrl: String,
    val supabaseKey: String,
)

private fun setupSupabaseSpecs(): SupabaseSpecs {
    val supabaseUrl = System.getenv("SUPABASE_URL") ?: error("SUPABASE_URL environment variable is missing")
    val supabaseKey = System.getenv("SUPABASE_KEY") ?: error("SUPABASE_KEY environment variable is missing")
    return SupabaseSpecs(supabaseUrl, supabaseKey)
}

private fun setupSupabaseClient(
    supabaseSpecs: SupabaseSpecs
): SupabaseClient {
    return createSupabaseClient(
        supabaseUrl = supabaseSpecs.supabaseUrl,
        supabaseKey = supabaseSpecs.supabaseKey
    ) {
        install(Postgrest)
    }
}