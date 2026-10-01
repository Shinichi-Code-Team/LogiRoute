package domain.usecase

import com.example.logiroute.com.example.logiroute.domain.model.request.GetWarehouseLoadFactorRequest
import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.repository.WarehouseRepository
import com.example.logiroute.domain.usecase.GetWarehouseLoadFactorUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetWarehouseLoadFactorUseCaseTest {

    private val warehouseRepository = mockk<WarehouseRepository>()
    private val useCase = GetWarehouseLoadFactorUseCase(warehouseRepository)

    @Test
    fun `calculates cargo weight divided by stationed fleet capacity`() = runTest {
        val warehouse = warehouse("WH-123")
        val destination = warehouse("WH-456")

        warehouse.addPackage(
            packageItem("PKG-123456", 20.0, warehouse, destination)
        )
        warehouse.addPackage(
            packageItem("PKG-234567", 10.0, warehouse, destination)
        )
        warehouse.addVehicle(vehicle("TRK-1234", 100.0, warehouse))
        warehouse.addVehicle(vehicle("TRK-2345", 50.0, warehouse))

        coEvery {
            warehouseRepository.getAllWarehouses()
        } returns listOf(warehouse)

        val request = GetWarehouseLoadFactorRequest(warehouse.id)
        val result = useCase(request)
        assertEquals(0.2, result)
        coVerify(exactly = 1) { warehouseRepository.getAllWarehouses() }
    }

    @Test
    fun `throws when warehouse has no fleet capacity`() = runTest {
        val warehouse = warehouse("WH-123")
        coEvery {
            warehouseRepository.getAllWarehouses()
        } returns listOf(warehouse)
        assertFailsWith<LogisticsException.ZeroFleetCapacityException> {
            useCase(GetWarehouseLoadFactorRequest(warehouse.id))
        }
    }

    @Test
    fun `throws when warehouse does not exist`() = runTest {
        coEvery {
            warehouseRepository.getAllWarehouses()
        } returns emptyList()
        assertFailsWith<IllegalArgumentException> {
            useCase(GetWarehouseLoadFactorRequest("WH-123"))
        }
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

    private fun packageItem(
        id: String,
        weight: Double,
        origin: Warehouse,
        destination: Warehouse
    ) = Package(
        id = id,
        weight = weight,
        origin = origin,
        destination = destination,
        priority = Priority.STANDARD
    )
}