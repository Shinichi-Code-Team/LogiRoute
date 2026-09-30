package domain.usecase.crud.`package`

import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.services.`package`.DeletePackageUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeletePackageUseCaseTest {
    private val repository = mockk<PackageRepository>()
    private val useCase = DeletePackageUseCase(repository)

    private val id = "PKG-123456"

    @Test
    fun `valid id deletes package successfully`() = runBlocking {
        // Given
        coEvery { repository.deletePackage(id) } returns Unit

        // When
        val result = useCase(id)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.deletePackage(id) }
    }

    @Test
    fun `invalid id returns failed result without calling repository`() = runBlocking {
        // Given
        val invalidId = "wrong-id"

        // When
        val result = useCase(invalidId)

        // Then
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { repository.deletePackage(any()) }
    }

    @Test
    fun `repository failure returns failed result`() = runBlocking {
        // Given
        val exception = RuntimeException("Delete failed")
        coEvery { repository.deletePackage(id) } throws exception

        // When
        val result = useCase(id)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
