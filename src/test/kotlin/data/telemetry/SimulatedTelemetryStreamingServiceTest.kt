package data.telemetry

import com.example.logiroute.data.telemetry.SimulatedTelemetryStreamingService
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SimulatedTelemetryStreamingServiceTest {

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
    fun `should emit the requested number of telemetry updates`() = runTest {
        val service = SimulatedTelemetryStreamingService(
            intervalMillis = 100L
        )

        val updates = service
            .streamFleet(listOf(vehicle))
            .take(3)
            .toList()

        assertEquals(3, updates.size)
    }

    @Test
    fun `should keep generated telemetry values within valid ranges`() = runTest {
        val service = SimulatedTelemetryStreamingService(
            intervalMillis = 100L
        )

        val updates = service
            .streamFleet(listOf(vehicle))
            .take(5)
            .toList()

        assertTrue(
            updates.all { update ->
                update.latitude in -90.0..90.0 &&
                        update.longitude in -180.0..180.0 &&
                        update.speedKph >= 0.0 &&
                        update.fuelPercent in 0.0..100.0
            }
        )
    }

    @Test
    fun `should restart the cold stream for each collection`() = runTest {
        val service = SimulatedTelemetryStreamingService(
            intervalMillis = 100L
        )
        val stream = service.streamFleet(listOf(vehicle))

        val firstCollection = stream.take(2).toList()
        val secondCollection = stream.take(2).toList()

        assertEquals(firstCollection, secondCollection)
    }
}