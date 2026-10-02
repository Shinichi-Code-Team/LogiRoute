package domain.usecase

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.model.request.FindStationedVehiclesRequest
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.usecase.FindStationedVehiclesByCapacityUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FindStationedVehiclesByCapacityUseCaseTest {

    private val vehicleRepository = mockk<VehicleRepository>()
    private val useCase =
        FindStationedVehiclesByCapacityUseCase(vehicleRepository)

    @Test
    fun `returns vehicles matching warehouse and minimum capacity`() = runTest {
        val warehouse = warehouse("WH-123")
        val otherWarehouse = warehouse("WH-456")

        val matchingVehicle = vehicle("TRK-1234", 100.0, warehouse)
        val smallVehicle = vehicle("TRK-2345", 40.0, warehouse)
        val vehicleAtOtherWarehouse = vehicle("TRK-3456", 120.0, otherWarehouse)

        coEvery { vehicleRepository.getAllVehicles() } returns listOf(
            matchingVehicle,
            smallVehicle,
            vehicleAtOtherWarehouse
        )

        val request = FindStationedVehiclesRequest(
            warehouseId = warehouse.id,
            minCapacity = 80.0
        )
        val result = useCase(request)
        assertEquals(listOf(matchingVehicle), result)
        coVerify(exactly = 1) { vehicleRepository.getAllVehicles() }
    }

    @Test
    fun `non positive minimum capacity throws exception`() = runTest {
        val request = FindStationedVehiclesRequest(
            warehouseId = "WH-123",
            minCapacity = 0.0
        )
        assertFailsWith<LogisticsException.InvalidCapacityException> {
            useCase(request)
        }

        coVerify(exactly = 0) { vehicleRepository.getAllVehicles() }
    }

    private fun warehouse(id: String) = Warehouse(
        id = id,
        name = "Test Warehouse",
        regionalZone = "WEST",
        latitude = 31.5,
        longitude = 34.5
    )

    private fun vehicle(
        id: String,
        capacity: Double,
        warehouse: Warehouse
    ) = Vehicle(
        id = id,
        maxCapacityKg = capacity,
        costPerKm = 2.0,
        currentHub = warehouse
    )
}