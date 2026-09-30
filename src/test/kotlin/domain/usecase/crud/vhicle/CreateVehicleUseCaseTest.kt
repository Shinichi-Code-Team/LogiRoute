package domain.usecase.vehicle

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.usecase.crud.vehicle.CreateVehicleUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CreateVehicleUseCaseTest {

    private val repository = mockk<VehicleRepository>()
    private val useCase = CreateVehicleUseCase(repository)

    @Test
    fun `repository success returns successful result`() = runBlocking {
        // Given
        val warehouse = Warehouse(
            id = "WH-123",
            name = "Test Warehouse",
            regionalZone = "WEST",
            latitude = 31.5,
            longitude = 34.5
        )

        val vehicle = Vehicle(
            id = "TRK-1234",
            maxCapacityKg = 500.0,
            costPerKm = 2.0,
            currentHub = warehouse
        )

        coEvery {
            repository.addVehicle(vehicle)
        } returns vehicle

        // When
        val result = useCase(vehicle)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(vehicle, result.getOrNull())

        coVerify(exactly = 1) {
            repository.addVehicle(vehicle)
        }
    }

    @Test
    fun `repository failure returns failed result`() = runBlocking {
        // Given
        val warehouse = Warehouse(
            id = "WH-123",
            name = "Test Warehouse",
            regionalZone = "WEST",
            latitude = 31.5,
            longitude = 34.5
        )

        val vehicle = Vehicle(
            id = "TRK-1234",
            maxCapacityKg = 500.0,
            costPerKm = 2.0,
            currentHub = warehouse
        )

        val exception = RuntimeException("Create failed")

        coEvery {
            repository.addVehicle(vehicle)
        } throws exception

        // When
        val result = useCase(vehicle)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

        coVerify(exactly = 1) {
            repository.addVehicle(vehicle)
        }
    }
}