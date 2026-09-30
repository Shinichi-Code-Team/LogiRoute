package domain.usecase.warehouse

import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.request.UpdateWarehouseInput
import com.example.logiroute.domain.repository.WarehouseRepository
import com.example.logiroute.domain.services.warehouse.UpdateWarehouseUseCase
import com.example.logiroute.domain.validator.WarehouseUpdateValidator
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UpdateWarehouseUseCaseTest {

    private val repository = mockk<WarehouseRepository>()
    private val validator = WarehouseUpdateValidator()

    private val useCase = UpdateWarehouseUseCase(
        repository,
        validator
    )

    @Test
    fun `valid update returns warehouse successfully`() = runBlocking {
        // Given
        val id = "WH-123"

        val input = UpdateWarehouseInput(
            name = "Updated Warehouse"
        )

        val updatedWarehouse = Warehouse(
            id = "WH-123",
            name = "Updated Warehouse",
            regionalZone = "WEST",
            latitude = 31.5,
            longitude = 34.5
        )

        coEvery {
            repository.updateWarehouse(id, input)
        } returns updatedWarehouse

        // When
        val result = useCase(id, input)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(updatedWarehouse, result.getOrNull())

        coVerify(exactly = 1) {
            repository.updateWarehouse(id, input)
        }
    }

    @Test
    fun `invalid update returns failed result without calling repository`() = runBlocking {
        // Given
        val id = "WH-123"

        val input = UpdateWarehouseInput(
            latitude = 91.0
        )

        // When
        val result = useCase(id, input)

        // Then
        assertTrue(result.isFailure)

        coVerify(exactly = 0) {
            repository.updateWarehouse(any(), any())
        }
    }

    @Test
    fun `empty update returns failed result without calling repository`() = runBlocking {
        // Given
        val id = "WH-123"
        val input = UpdateWarehouseInput()

        // When
        val result = useCase(id, input)

        // Then
        assertTrue(result.isFailure)

        coVerify(exactly = 0) {
            repository.updateWarehouse(any(), any())
        }
    }

    @Test
    fun `repository failure returns failed result`() = runBlocking {
        // Given
        val id = "WH-123"

        val input = UpdateWarehouseInput(
            name = "Updated Warehouse"
        )

        val exception = RuntimeException("Update failed")

        coEvery {
            repository.updateWarehouse(id, input)
        } throws exception

        // When
        val result = useCase(id, input)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}