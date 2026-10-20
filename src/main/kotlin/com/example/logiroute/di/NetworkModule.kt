package com.example.logiroute.di

import com.example.logiroute.data.remote.datasource.*
import com.example.logiroute.data.remote.datasource.impl.SupabasePackageRemoteDataSource
import com.example.logiroute.data.remote.datasource.impl.SupabaseWarehouseRemoteDataSource
import com.example.logiroute.data.remote.datasource.vehicle.RemoteVehicleDataSource
import com.example.logiroute.data.remote.mapper.PackageDtoMapper
import com.example.logiroute.data.remote.mapper.RouteDtoMapper
import com.example.logiroute.data.remote.mapper.VehicleDtoMapper
import com.example.logiroute.data.remote.mapper.WarehouseDtoMapper
import com.example.logiroute.data.remote.ratelimit.RateLimiter
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

private const val MAX_BUCKET_CAPACITY = 10
private const val REFILL_RATE_PER_SECOND = 5.0
val networkModule = module {
    singleOf(::setupSupabaseSpecs)
    singleOf(::setupSupabaseClient)
    single { RateLimiter(maxBucketCapacity = MAX_BUCKET_CAPACITY, refillRatePerSecond = REFILL_RATE_PER_SECOND) }

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
private object SupabaseConfig {
    const val URL = "https://aynippbeuvtkejoixnap.supabase.co"
    const val KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImF5bmlwcGJldXZ0a2Vqb2l4bmFwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODkzODU2MTcsImV4cCI6MjEwNDk2MTYxN30.V62ol4avge2EGB2FM8aNah45t8Zrl2ZYNulaYCFXJsM"
}
private fun setupSupabaseSpecs(): SupabaseSpecs =
    SupabaseSpecs(
        supabaseUrl = SupabaseConfig.URL,
        supabaseKey = SupabaseConfig.KEY
    )
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