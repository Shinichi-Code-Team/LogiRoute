package domain.usecase.vehicle

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.usecase.crud.vehicle.ReadVehicleUseCase
import com.example.logiroute.domain.validation.ValidationResultMapper
import com.example.logiroute.domain.validation.ValidationRules
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReadVehicleUseCaseTest {

    private val repository = mockk<VehicleRepository>()
    private val useCase = ReadVehicleUseCase(
        repository,
        ValidationRules(),
        ValidationResultMapper()
    )
    @Test
    fun `valid id returns vehicle successfully`() = runTest {
        // Given
        val id = "TRK-1234"

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
            repository.getVehicleById(id)
        } returns vehicle

        // When
        val result = useCase(id)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(vehicle, result.getOrNull())

        coVerify(exactly = 1) {
            repository.getVehicleById(id)
        }
    }

    @Test
    fun `invalid id returns failed result without calling repository`() = runTest {
        // Given
        val id = "wrong-id"

        // When
        val result = useCase(id)

        // Then
        assertTrue(result.isFailure)

        coVerify(exactly = 0) {
            repository.getVehicleById(any())
        }
    }

    @Test
    fun `repository failure returns failed result`() = runTest {
        // Given
        val id = "TRK-1234"
        val exception = RuntimeException("Read failed")

        coEvery {
            repository.getVehicleById(id)
        } throws exception

        // When
        val result = useCase(id)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}