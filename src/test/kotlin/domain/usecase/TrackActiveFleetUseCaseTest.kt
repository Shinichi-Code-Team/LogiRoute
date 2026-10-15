package domain.usecase

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.telemetry.VehicleTelemetry
import com.example.logiroute.domain.service.TelemetryStreamingService
import com.example.logiroute.domain.usecase.TrackActiveFleetUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TrackActiveFleetUseCaseTest {

    private val vehicle = Vehicle(
        id = "TRK-0001",
        maxCapacityKg = 1_000.0,
        costPerKm = 2.0,
        currentHub = Warehouse(
            id = "WH-001",
            name = "Test Hub",
            regionalZone = "NORTH",
            latitude = 32.0,
            longitude = 35.0
        )
    )

    @Test
    fun `should emit notification for telemetry below the speed threshold`() =
        runTest {
            val service = FakeTelemetryStreamingService(
                updates = listOf(telemetry(speedKph = 5.0))
            )
            val useCase = TrackActiveFleetUseCase(
                telemetryStreamingService = service,
                delaySpeedThresholdKph = 15.0
            )

            val notifications = useCase(listOf(vehicle)).toList()

            assertEquals(1, notifications.size)
        }

    @Test
    fun `should filter telemetry at or above the speed threshold`() =
        runTest {
            val service = FakeTelemetryStreamingService(
                updates = listOf(
                    telemetry(speedKph = 15.0),
                    telemetry(speedKph = 25.0, sequence = 1L)
                )
            )
            val useCase = TrackActiveFleetUseCase(
                telemetryStreamingService = service,
                delaySpeedThresholdKph = 15.0
            )

            val notifications = useCase(listOf(vehicle)).toList()

            assertEquals(emptyList(), notifications)
        }

    @Test
    fun `should preserve vehicle and location data in delay notification`() =
        runTest {
            val update = telemetry(
                vehicleId = vehicle.id,
                latitude = 32.1,
                longitude = 35.1,
                speedKph = 4.0
            )
            val useCase = TrackActiveFleetUseCase(
                telemetryStreamingService =
                    FakeTelemetryStreamingService(listOf(update)),
                delaySpeedThresholdKph = 15.0
            )

            val notification = useCase(listOf(vehicle)).toList().single()

            assertEquals(
                listOf(vehicle.id, 32.1, 35.1),
                listOf(
                    notification.vehicleId,
                    notification.latitude,
                    notification.longitude
                )
            )
        }

    private fun telemetry(
        vehicleId: String = vehicle.id,
        latitude: Double = 32.0,
        longitude: Double = 35.0,
        speedKph: Double,
        sequence: Long = 0L
    ) = VehicleTelemetry(
        vehicleId = vehicleId,
        latitude = latitude,
        longitude = longitude,
        speedKph = speedKph,
        fuelPercent = 80.0,
        sequence = sequence
    )

    private class FakeTelemetryStreamingService(
        private val updates: List<VehicleTelemetry>
    ) : TelemetryStreamingService {
        override fun streamFleet(
            vehicles: List<Vehicle>
        ): Flow<VehicleTelemetry> =
            updates.asFlow()
    }
}