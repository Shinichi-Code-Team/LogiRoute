package domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.model.result.VehicleUtilization
import com.example.logiroute.domain.usecase.CalculateVehicleUtilizationUseCase
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CalculateVehicleUtilizationUseCaseTest {

    private val useCase = CalculateVehicleUtilizationUseCase()

    private val origin =
        Warehouse("WH-123", "Origin", "WEST", 31.5, 34.5)

    private val destination =
        Warehouse("WH-456", "Destination", "EAST", 31.6, 34.6)

    @Test
    fun `should calculate load and remaining capacity from loaded packages`() {
        // Given
        val loadedPackage = Package(
            id = "PKG-123456",
            weight = 25.0,
            origin = origin,
            destination = destination,
            priority = Priority.STANDARD
        )
        val vehicle = Vehicle(
            id = "TRK-1234",
            maxCapacityKg = 100.0,
            costPerKm = 2.0,
            currentHub = origin,
            loadedPackages = mutableListOf(loadedPackage)
        )

        // When
        val result = useCase(vehicle)

        // Then
        assertEquals(
            VehicleUtilization(
                currentLoadKg = 25.0,
                remainingCapacityKg = 75.0,
                utilizationPercentage = 25.0
            ),
            result
        )
    }

    @Test
    fun `should return zero utilization when vehicle has no loaded packages`() {
        // Given
        val vehicle = Vehicle(
            id = "TRK-1234",
            maxCapacityKg = 100.0,
            costPerKm = 2.0,
            currentHub = origin
        )

        // When
        val result = useCase(vehicle)

        // Then
        assertEquals(
            VehicleUtilization(
                currentLoadKg = 0.0,
                remainingCapacityKg = 100.0,
                utilizationPercentage = 0.0
            ),
            result
        )
    }

    @Test
    fun `should reject a vehicle with zero capacity`() {
        // Given
        val vehicle = mockk<Vehicle>()
        every { vehicle.maxCapacityKg } returns 0.0

        // When / Then
        assertFailsWith<LogisticsException.InvalidCapacityException> {
            useCase(vehicle)
        }
    }
}