package domain.usecase.vehicle

import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.usecase.crud.vehicle.DeleteVehicleUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeleteVehicleUseCaseTest {

    private val repository = mockk<VehicleRepository>()
    private val useCase = DeleteVehicleUseCase(repository)

    @Test
    fun `valid id deletes vehicle successfully`() = runTest {
        // Given
        val id = "TRK-1234"

        coEvery {
            repository.deleteVehicle(id)
        } returns Unit

        // When
        val result = useCase(id)

        // Then
        assertTrue(result.isSuccess)

        coVerify(exactly = 1) {
            repository.deleteVehicle(id)
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
            repository.deleteVehicle(any())
        }
    }

    @Test
    fun `repository failure returns failed result`() = runTest {
        // Given
        val id = "TRK-1234"
        val exception = RuntimeException("Delete failed")

        coEvery {
            repository.deleteVehicle(id)
        } throws exception

        // When
        val result = useCase(id)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}