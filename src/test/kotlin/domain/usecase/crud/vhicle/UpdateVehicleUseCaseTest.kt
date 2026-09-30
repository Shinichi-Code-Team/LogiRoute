package domain.usecase.vehicle

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.request.UpdateVehicleInput
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.services.vehicle.UpdateVehicleUseCase
import com.example.logiroute.domain.validator.VehicleUpdateValidator
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UpdateVehicleUseCaseTest {

    private val repository = mockk<VehicleRepository>()
    private val validator = VehicleUpdateValidator()

    private val useCase = UpdateVehicleUseCase(
        repository,
        validator
    )

    @Test
    fun `valid update returns vehicle successfully`() = runBlocking {
        // Given
        val id = "TRK-1234"

        val input = UpdateVehicleInput(
            maxCapacityKg = 800.0
        )

        val warehouse = Warehouse(
            id = "WH-123",
            name = "Test Warehouse",
            regionalZone = "WEST",
            latitude = 31.5,
            longitude = 34.5
        )

        val updatedVehicle = Vehicle(
            id = "TRK-1234",
            maxCapacityKg = 800.0,
            costPerKm = 2.0,
            currentHub = warehouse
        )

        coEvery {
            repository.updateVehicle(id, input)
        } returns updatedVehicle

        // When
        val result = useCase(id, input)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(updatedVehicle, result.getOrNull())

        coVerify(exactly = 1) {
            repository.updateVehicle(id, input)
        }
    }

    @Test
    fun `invalid update returns failed result without calling repository`() = runBlocking {
        // Given
        val id = "TRK-1234"

        val input = UpdateVehicleInput(
            maxCapacityKg = 0.0
        )

        // When
        val result = useCase(id, input)

        // Then
        assertTrue(result.isFailure)

        coVerify(exactly = 0) {
            repository.updateVehicle(any(), any())
        }
    }

    @Test
    fun `empty update returns failed result without calling repository`() = runBlocking {
        // Given
        val id = "TRK-1234"
        val input = UpdateVehicleInput()

        // When
        val result = useCase(id, input)

        // Then
        assertTrue(result.isFailure)

        coVerify(exactly = 0) {
            repository.updateVehicle(any(), any())
        }
    }

    @Test
    fun `repository failure returns failed result`() = runBlocking {
        // Given
        val id = "TRK-1234"

        val input = UpdateVehicleInput(
            costPerKm = 3.0
        )

        val exception = RuntimeException("Update failed")

        coEvery {
            repository.updateVehicle(id, input)
        } throws exception

        // When
        val result = useCase(id, input)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}