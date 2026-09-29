package domain.usecase.warehouse

import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.WarehouseRepository
import com.example.logiroute.domain.services.warehouse.CreateWarehouseUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CreateWarehouseUseCaseTest {

    private val repository = mockk<WarehouseRepository>()
    private val useCase = CreateWarehouseUseCase(repository)

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

        coEvery {
            repository.createWarehouse(warehouse)
        } returns warehouse

        // When
        val result = useCase(warehouse)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(warehouse, result.getOrNull())

        coVerify(exactly = 1) {
            repository.createWarehouse(warehouse)
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

        val exception = RuntimeException("Create failed")

        coEvery {
            repository.createWarehouse(warehouse)
        } throws exception

        // When
        val result = useCase(warehouse)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

        coVerify(exactly = 1) {
            repository.createWarehouse(warehouse)
        }
    }
}