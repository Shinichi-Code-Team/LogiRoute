package com.example.logiroute.presentation.cli

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.usecase.TrackActiveFleetUseCase
import com.example.logiroute.domain.usecase.crud.vehicle.ReadVehicleUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class FleetCli(
    private val readVehicleUseCase: ReadVehicleUseCase,
    private val trackActiveFleetUseCase: TrackActiveFleetUseCase
) {
    suspend fun run() = coroutineScope {
        val fleetScope = this
        var telemetryJob: Job? = null

        try {
            while (true) {
                printMenu(telemetryJob?.isActive == true)
                when (readCliInput("Choose a fleet command:") ?: "0") {
                    "1" -> safelyRunCliCommand {
                        telemetryJob = startTelemetryStreaming(fleetScope, telemetryJob)
                    }
                    "2" -> stopTelemetry(telemetryJob).also { telemetryJob = null }
                    "0" -> return@coroutineScope
                    else -> println("Unknown option. Choose one of the listed commands.")
                }
            }
        } finally {
            telemetryJob?.cancelAndJoin()
        }
    }

    private fun printMenu(isStreaming: Boolean) {
        println()
        println("=== Fleet Telemetry CLI ===")
        println("1. Start delay-notification stream")
        println("2. Stop fleet stream${if (isStreaming) " (running)" else ""}")
        println("0. Back to main menu")
    }

    private suspend fun startTelemetryStreaming(
        scope: CoroutineScope,
        currentJob: Job?
    ): Job? {
        if (currentJob?.isActive == true) {
            println("Telemetry streaming is already running.")
            return currentJob
        }

        val vehicles = loadVehicles() ?: return null
        println("Streaming delay notifications. Choose 2 to stop.")

        return scope.launch {
            try {
                trackActiveFleetUseCase(vehicles).collect { notification ->
                    println(
                        "[DELAY] vehicle=${notification.vehicleId}, " +
                            "speed=${notification.speedKph} km/h, " +
                            "position=(${notification.latitude}, ${notification.longitude}), " +
                            "message=${notification.message}"
                    )
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (error: Exception) {
                println("Telemetry stream failed: ${error.message}")
            }
        }
    }

    private suspend fun loadVehicles(): List<Vehicle>? {
        val vehicleIds = readCliInput("Vehicle IDs separated by commas:")
            ?.split(",")
            ?.map { id -> id.trim() }
            ?.filter { id -> id.isNotEmpty() }
            .orEmpty()

        if (vehicleIds.isEmpty()) {
            println("Enter at least one vehicle ID.")
            return null
        }

        val vehicles = coroutineScope {
            vehicleIds.map { id -> async { loadVehicle(id) } }
                .awaitAll()
                .filterNotNull()
        }

        if (vehicles.isEmpty()) {
            println("No valid vehicles were loaded.")
            return null
        }

        return vehicles
    }

    private suspend fun loadVehicle(id: String): Vehicle? =
        readVehicleUseCase(id).fold(
            onSuccess = { vehicle ->
                vehicle ?: run {
                    println("Vehicle $id was not found.")
                    null
                }
            },
            onFailure = { error ->
                println("Could not load vehicle $id: ${error.message}")
                null
            }
        )

    private suspend fun stopTelemetry(job: Job?) {
        if (job?.isActive == true) {
            job.cancelAndJoin()
            println("Telemetry streaming stopped.")
            return
        }
        println("Telemetry streaming is not running.")
    }
}
