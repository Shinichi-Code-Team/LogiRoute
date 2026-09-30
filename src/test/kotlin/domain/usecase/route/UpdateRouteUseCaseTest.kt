package domain.usecase.crud.route

import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.request.UpdateRouteInput
import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.services.route.UpdateRouteUseCase
import com.example.logiroute.domain.validator.RouteUpdateValidator
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UpdateRouteUseCaseTest {
    private val repository = mockk<RouteRepository>()
    private val validator = RouteUpdateValidator()
    private val useCase = UpdateRouteUseCase(repository, validator)

    private val id = "RT-12345"
    private val warehouse = Warehouse("WH-123", "Main Hub", "WEST", 31.5, 34.5)

    @Test
    fun `valid update returns route successfully`() = runBlocking {
        // Given
        val input = UpdateRouteInput(distanceKm = 120.0)
        val updatedRoute = Route(
            id = id,
            origin = warehouse,
            destination = warehouse,
            distanceKm = 120.0,
            typicalDelayMin = 15
        )

        coEvery { repository.updateRoute(id, input) } returns updatedRoute

        // When
        val result = useCase(id, input)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(updatedRoute, result.getOrNull())
        coVerify(exactly = 1) { repository.updateRoute(id, input) }
    }

    @Test
    fun `invalid update returns failed result without calling repository`() = runBlocking {
        // Given
        val input = UpdateRouteInput(distanceKm = -10.0)

        // When
        val result = useCase(id, input)

        // Then
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { repository.updateRoute(any(), any()) }
    }

    @Test
    fun `empty update returns failed result without calling repository`() = runBlocking {
        // Given
        val input = UpdateRouteInput()

        // When
        val result = useCase(id, input)

        // Then
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { repository.updateRoute(any(), any()) }
    }

    @Test
    fun `repository failure returns failed result`() = runBlocking {
        // Given
        val input = UpdateRouteInput(distanceKm = 120.0)
        val exception = RuntimeException("Update failed")

        coEvery { repository.updateRoute(id, input) } throws exception

        // When
        val result = useCase(id, input)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
