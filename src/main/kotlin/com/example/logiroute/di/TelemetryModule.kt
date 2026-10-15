package com.example.logiroute.di

import com.example.logiroute.data.telemetry.SimulatedTelemetryStreamingService
import com.example.logiroute.domain.service.TelemetryStreamingService
import org.koin.dsl.module

val telemetryModule = module {
    single<TelemetryStreamingService> {
        SimulatedTelemetryStreamingService()
    }
}