package domain.usecase.crud.route

import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.usecase.crud.route.DeleteRouteUseCase
import com.example.logiroute.domain.validation.ValidationResultMapper
import com.example.logiroute.domain.validation.ValidationRules
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeleteRouteUseCaseTest {
    private val repository = mockk<RouteRepository>()
    private val useCase = DeleteRouteUseCase(
        repository,
        ValidationRules(),
        ValidationResultMapper()
    )
    private val id = "RT-12345"

    @Test
    fun `valid id deletes route successfully`() = runTest {
        // Given
        coEvery { repository.deleteRoute(id) } returns Unit

        // When
        val result = useCase(id)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.deleteRoute(id) }
    }

    @Test
    fun `invalid id returns failed result without calling repository`() = runTest {
        // Given
        val invalidId = "wrong-id"

        // When
        val result = useCase(invalidId)

        // Then
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { repository.deleteRoute(any()) }
    }

    @Test
    fun `repository failure returns failed result`() = runTest {
        // Given
        val exception = RuntimeException("Delete failed")
        coEvery { repository.deleteRoute(id) } throws exception

        // When
        val result = useCase(id)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
