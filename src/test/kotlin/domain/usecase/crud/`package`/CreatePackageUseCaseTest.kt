package domain.usecase.crud.`package`

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.services.`package`.CreatePackageUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CreatePackageUseCaseTest {
    private val repository = mockk<PackageRepository>()
    private val useCase = CreatePackageUseCase(repository)

    private val warehouse = Warehouse("WH-123", "Main Hub", "WEST", 31.5, 34.5)
    private val packageItem = Package(
        id = "PKG-123456",
        weight = 10.0,
        origin = warehouse,
        destination = warehouse,
        priority = Priority.STANDARD
    )

    @Test
    fun `repository success returns successful result`() = runBlocking {
        // Given
        coEvery { repository.createPackage(packageItem) } returns packageItem

        // When
        val result = useCase(packageItem)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(packageItem, result.getOrNull())
        coVerify(exactly = 1) { repository.createPackage(packageItem) }
    }

    @Test
    fun `repository failure returns failed result`() = runBlocking {
        // Given
        val exception = RuntimeException("Create failed")
        coEvery { repository.createPackage(packageItem) } throws exception

        // When
        val result = useCase(packageItem)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 1) { repository.createPackage(packageItem) }
    }
}
