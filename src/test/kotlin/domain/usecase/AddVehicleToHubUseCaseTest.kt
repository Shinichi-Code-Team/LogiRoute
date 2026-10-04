package domain.usecase

import AddVehicleToHubUseCase
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.VehicleRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AddVehicleToHubUseCaseTest {

    private val vehicleRepository: VehicleRepository = mockk()
    private val useCase = AddVehicleToHubUseCase(vehicleRepository)

    private val warehouse = Warehouse("WH-001", "Central Hub", "NORTH", 31.5, 34.5)
    private val vehicle = Vehicle("TRK-0001", 1000.0, 2.5, warehouse)


    @Test
    fun `should return the vehicle when repository adds it successfully`() = runTest {
        // Given
        coEvery { vehicleRepository.addVehicle(vehicle) } returns vehicle

        // When
        val result = useCase(vehicle)

        // Then
        assertEquals(Result.success(vehicle), result)
    }

    @Test
    fun `should return repository error when adding the vehicle fails`() = runTest {
        // Given
        val error = RuntimeException("Database error")
        coEvery { vehicleRepository.addVehicle(vehicle) } throws error

        // When
        val result = useCase(vehicle)

        // Then
        assertEquals(Result.failure(error), result)
    }
}