package com.example.logiroute.presentation.cli

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.usecase.ClassifyPackageSectorUseCase
import com.example.logiroute.domain.usecase.ClassifySectorRequest
import com.example.logiroute.domain.usecase.FindOptimalPathUseCase
import com.example.logiroute.domain.usecase.TrackActiveFleetUseCase
import com.example.logiroute.domain.usecase.crud.vehicle.ReadVehicleUseCase
import com.example.logiroute.domain.usecase.crud.warehouse.ReadWarehouseUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LogiRouteCli(
    private val readWarehouseUseCase: ReadWarehouseUseCase,
    private val readVehicleUseCase: ReadVehicleUseCase,
    private val findOptimalPathUseCase: FindOptimalPathUseCase,
    private val classifyPackageSectorUseCase: ClassifyPackageSectorUseCase,
    private val trackActiveFleetUseCase: TrackActiveFleetUseCase
) {
    suspend fun run() = coroutineScope {
        val shellScope = this
        var isRunning = true
        var telemetryJob: Job? = null

        val commands: Map<String, suspend () -> Unit> = mapOf(
            "1" to {
                safely {
                    calculateRoute()
                }
            },
            "2" to {
                safely {
                    classifyPackage()
                }
            },
            "3" to {
                safely {
                    telemetryJob = startTelemetryStreaming(
                        scope = shellScope,
                        currentJob = telemetryJob
                    )
                }
            },
            "4" to {
                telemetryJob
                    ?.takeIf { job -> job.isActive }
                    ?.cancelAndJoin()

                telemetryJob = null
                println("Telemetry streaming stopped.")
            },
            "0" to {
                isRunning = false
            }
        )

        try {
            while (isRunning) {
                printMenu()

                val command = readInput("Choose an option:") ?: "0"
                val action = commands[command]

                if (action == null) {
                    println("Unknown option. Choose one of the listed commands.")
                } else {
                    action()
                }
            }
        } finally {
            telemetryJob?.cancel()
        }
    }

    private fun printMenu() {
        println()
        println("=== LogiRoute CLI ===")
        println("1. Calculate route")
        println("2. Classify package sector (KNN)")
        println("3. Start fleet delay-notification stream")
        println("4. Stop fleet stream")
        println("0. Exit")
    }

    private suspend fun calculateRoute() {
        val originId = readInput("Origin warehouse ID:") ?: return
        val destinationId = readInput("Destination warehouse ID:") ?: return

        val origin = readWarehouseUseCase(originId)
            .getOrElse { error ->
                println("Could not load origin warehouse: ${error.message}")
                return
            }
            ?: run {
                println("Origin warehouse was not found.")
                return
            }

        val destination = readWarehouseUseCase(destinationId)
            .getOrElse { error ->
                println("Could not load destination warehouse: ${error.message}")
                return
            }
            ?: run {
                println("Destination warehouse was not found.")
                return
            }

        val route = withLoadingIndicator("Calculating route") {
            findOptimalPathUseCase(
                source = origin,
                destination = destination
            )
        }

        if (route.isEmpty()) {
            println("No route connects those warehouses.")
        } else {
            println(
                route.joinToString(" -> ") { warehouse ->
                    "${warehouse.name} (${warehouse.id})"
                }
            )
        }
    }

    private suspend fun classifyPackage() {
        val latitude = readFiniteDouble("Package latitude:")
        val longitude = readFiniteDouble("Package longitude:")
        val weight = readFiniteDouble("Package weight in kg:")

        if (latitude == null || longitude == null || weight == null) {
            println(
                "Enter valid finite numbers for latitude, longitude, and weight."
            )
            return
        }

        if (latitude !in -90.0..90.0 || longitude !in -180.0..180.0) {
            println("Latitude or longitude is outside its valid range.")
            return
        }

        if (weight <= 0.0) {
            println("Package weight must be greater than zero.")
            return
        }

        val request = ClassifySectorRequest(
            latitude = latitude,
            longitude = longitude,
            weight = weight
        )

        classifyPackageSectorUseCase(request).fold(
            onSuccess = { sector ->
                println("Predicted delivery sector: $sector")
            },
            onFailure = { error ->
                println("Package classification failed: ${error.message}")
            }
        )
    }

    private suspend fun startTelemetryStreaming(
        scope: CoroutineScope,
        currentJob: Job?
    ): Job? {
        if (currentJob?.isActive == true) {
            println("Telemetry streaming is already running.")
            return currentJob
        }

        val vehicleIds = readInput(
            "Vehicle IDs separated by commas:"
        )
            ?.split(",")
            ?.map { vehicleId -> vehicleId.trim() }
            ?.filter { vehicleId -> vehicleId.isNotEmpty() }
            .orEmpty()

        if (vehicleIds.isEmpty()) {
            println("Enter at least one vehicle ID.")
            return null
        }

        val vehicles = coroutineScope {
            vehicleIds
                .map { vehicleId ->
                    async {
                        loadVehicle(vehicleId)
                    }
                }
                .awaitAll()
                .filterNotNull()
        }

        if (vehicles.isEmpty()) {
            println("No valid vehicles were loaded.")
            return null
        }

        println("Streaming delay notifications. Choose 4 to stop.")

        return scope.launch {
            try {
                trackActiveFleetUseCase(vehicles).collect { notification ->
                    println(
                        "[DELAY] vehicle=${notification.vehicleId}, " +
                                "speed=${notification.speedKph} km/h, " +
                                "position=(${notification.latitude}, " +
                                "${notification.longitude}), " +
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

    private suspend fun readFiniteDouble(label: String): Double? =
        readInput(label)
            ?.toDoubleOrNull()
            ?.takeIf { value -> value.isFinite() }

    private suspend fun readInput(label: String): String? =
        withContext(Dispatchers.IO) {
            print("$label ")
            readlnOrNull()?.trim()
        }

    private suspend fun <T> withLoadingIndicator(
        message: String,
        action: suspend () -> T
    ): T = coroutineScope {
        val indicator = launch {
            val frames = listOf("|", "/", "-", "\\")
            var frameIndex = 0

            while (isActive) {
                print("\r$message ${frames[frameIndex % frames.size]}")
                frameIndex += 1
                delay(120L)
            }
        }

        val result = try {
            action()
        } finally {
            withContext(NonCancellable) {
                indicator.cancelAndJoin()
            }
            print("\r")
        }

        println("$message completed.")
        result
    }

    private suspend fun safely(action: suspend () -> Unit) {
        try {
            action()
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            println("Command failed: ${error.message}")
        }
    }
}