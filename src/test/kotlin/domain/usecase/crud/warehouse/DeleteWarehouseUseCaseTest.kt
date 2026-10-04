package domain.usecase.crud.warehouse


import com.example.logiroute.domain.repository.WarehouseRepository
import com.example.logiroute.domain.usecase.crud.warehouse.DeleteWarehouseUseCase
import com.example.logiroute.domain.validation.ValidationResultMapper
import com.example.logiroute.domain.validation.ValidationRules
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeleteWarehouseUseCaseTest {

    private val repository = mockk<WarehouseRepository>()
    private val useCase = DeleteWarehouseUseCase(
        repository,
        ValidationRules(),
        ValidationResultMapper()
    )

    @Test
    fun `valid id deletes warehouse successfully`() = runTest {
        // Given
        val id = "WH-123"

        coEvery {
            repository.deleteWarehouse(id)
        } returns Unit

        // When
        val result = useCase(id)

        // Then
        assertTrue(result.isSuccess)

        coVerify(exactly = 1) {
            repository.deleteWarehouse(id)
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
            repository.deleteWarehouse(any())
        }
    }

    @Test
    fun `repository failure returns failed result`() = runTest {
        // Given
        val id = "WH-123"
        val exception = RuntimeException("Delete failed")

        coEvery {
            repository.deleteWarehouse(id)
        } throws exception

        // When
        val result = useCase(id)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}