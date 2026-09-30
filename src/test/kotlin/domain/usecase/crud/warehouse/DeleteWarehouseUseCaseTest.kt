package domain.usecase.warehouse


import com.example.logiroute.domain.repository.WarehouseRepository
import com.example.logiroute.domain.services.warehouse.DeleteWarehouseUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeleteWarehouseUseCaseTest {

    private val repository = mockk<WarehouseRepository>()
    private val useCase = DeleteWarehouseUseCase(repository)

    @Test
    fun `valid id deletes warehouse successfully`() = runBlocking {
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
    fun `invalid id returns failed result without calling repository`() = runBlocking {
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
    fun `repository failure returns failed result`() = runBlocking {
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