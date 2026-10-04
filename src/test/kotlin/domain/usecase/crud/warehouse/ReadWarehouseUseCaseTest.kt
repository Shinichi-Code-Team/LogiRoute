package domain.usecase.crud.warehouse

import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.WarehouseRepository
import com.example.logiroute.domain.usecase.crud.warehouse.ReadWarehouseUseCase
import com.example.logiroute.domain.validation.ValidationResultMapper
import com.example.logiroute.domain.validation.ValidationRules
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReadWarehouseUseCaseTest {

    private val repository = mockk<WarehouseRepository>()
    private val useCase = ReadWarehouseUseCase(
        repository,
        ValidationRules(),
        ValidationResultMapper()
    )
    @Test
    fun `valid id returns warehouse successfully`() = runTest {
        // Given
        val id = "WH-123"

        val warehouse = Warehouse(
            id = "WH-123",
            name = "Test Warehouse",
            regionalZone = "WEST",
            latitude = 31.5,
            longitude = 34.5
        )

        coEvery {
            repository.getWarehouseById(id)
        } returns warehouse

        // When
        val result = useCase(id)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(warehouse, result.getOrNull())

        coVerify(exactly = 1) {
            repository.getWarehouseById(id)
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
            repository.getWarehouseById(any())
        }
    }

    @Test
    fun `repository failure returns failed result`() = runTest {
        // Given
        val id = "WH-123"
        val exception = RuntimeException("Read failed")

        coEvery {
            repository.getWarehouseById(id)
        } throws exception

        // When
        val result = useCase(id)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}