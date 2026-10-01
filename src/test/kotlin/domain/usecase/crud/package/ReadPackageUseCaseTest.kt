package domain.usecase.crud.`package`

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.usecase.crud.`package`.ReadPackageUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReadPackageUseCaseTest {
    private val repository = mockk<PackageRepository>()
    private val useCase = ReadPackageUseCase(repository)

    private val id = "PKG-123456"
    private val warehouse = Warehouse("WH-123", "Main Hub", "WEST", 31.5, 34.5)
    private val packageItem = Package(
        id = id,
        weight = 10.0,
        origin = warehouse,
        destination = warehouse,
        priority = Priority.STANDARD
    )

    @Test
    fun `valid id returns package successfully`() = runTest {
        // Given
        coEvery { repository.getPackageById(id) } returns packageItem

        // When
        val result = useCase(id)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(packageItem, result.getOrNull())
        coVerify(exactly = 1) { repository.getPackageById(id) }
    }

    @Test
    fun `invalid id returns failed result without calling repository`() = runBlocking {
        // Given
        val invalidId = "wrong-id"

        // When
        val result = useCase(invalidId)

        // Then
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { repository.getPackageById(any()) }
    }

    @Test
    fun `repository failure returns failed result`() = runTest {
        // Given
        val exception = RuntimeException("Read failed")
        coEvery { repository.getPackageById(id) } throws exception

        // When
        val result = useCase(id)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
