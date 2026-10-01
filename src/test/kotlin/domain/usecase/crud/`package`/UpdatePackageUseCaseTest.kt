package domain.usecase.crud.`package`

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.request.UpdatePackageInput
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.services.`package`.UpdatePackageUseCase
import com.example.logiroute.domain.validator.PackageUpdateValidator
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UpdatePackageUseCaseTest {
    private val repository = mockk<PackageRepository>()
    private val validator = PackageUpdateValidator()
    private val useCase = UpdatePackageUseCase(repository, validator)

    private val id = "PKG-123456"
    private val warehouse = Warehouse("WH-123", "Main Hub", "WEST", 31.5, 34.5)

    @Test
    fun `valid update returns package successfully`() = runBlocking {
        // Given
        val input = UpdatePackageInput(weight = 15.0)
        val updatedPackage = Package(
            id = id,
            weight = 15.0,
            origin = warehouse,
            destination = warehouse,
            priority = Priority.STANDARD
        )

        coEvery { repository.updatePackage(id, input) } returns updatedPackage

        // When
        val result = useCase(id, input)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(updatedPackage, result.getOrNull())
        coVerify(exactly = 1) { repository.updatePackage(id, input) }
    }

    @Test
    fun `invalid update returns failed result without calling repository`() = runBlocking {
        // Given
        val input = UpdatePackageInput(weight = 0.0)

        // When
        val result = useCase(id, input)

        // Then
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { repository.updatePackage(any(), any()) }
    }

    @Test
    fun `empty update returns failed result without calling repository`() = runBlocking {
        // Given
        val input = UpdatePackageInput()

        // When
        val result = useCase(id, input)

        // Then
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { repository.updatePackage(any(), any()) }
    }

    @Test
    fun `repository failure returns failed result`() = runBlocking {
        // Given
        val input = UpdatePackageInput(weight = 15.0)
        val exception = RuntimeException("Update failed")

        coEvery { repository.updatePackage(id, input) } throws exception

        // When
        val result = useCase(id, input)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
