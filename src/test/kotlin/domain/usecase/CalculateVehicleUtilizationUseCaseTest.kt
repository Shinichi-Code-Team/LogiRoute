package domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.usecase.CalculateVehicleUtilizationUseCase
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CalculateVehicleUtilizationUseCaseTest {

    private val useCase = CalculateVehicleUtilizationUseCase()

    @Test
    fun `calculates load remaining capacity and utilization`() {
        val warehouse = warehouse()
        val packageItem = Package(
            id = "PKG-123456",
            weight = 25.0,
            origin = warehouse,
            destination = warehouse("WH-456"),
            priority = Priority.STANDARD
        )
        val vehicle = Vehicle(
            id = "TRK-1234",
            maxCapacityKg = 100.0,
            costPerKm = 2.0,
            currentHub = warehouse,
            loadedPackages = mutableListOf(packageItem)
        )
        val result = useCase(vehicle)
        assertEquals(25.0, result.currentLoadKg)
        assertEquals(75.0, result.remainingCapacityKg)
        assertEquals(25.0, result.utilizationPercentage)
    }

    @Test
    fun `zero capacity throws invalid capacity exception`() {
        val vehicle = mockk<Vehicle>()
        every { vehicle.maxCapacityKg } returns 0.0
        assertFailsWith<LogisticsException.InvalidCapacityException> {
            useCase(vehicle)
        }
    }

    private fun warehouse(id: String = "WH-123") = Warehouse(
        id = id,
        name = "Test Warehouse",
        regionalZone = "WEST",
        latitude = 31.5,
        longitude = 34.5
    )
}