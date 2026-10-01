package domain.usecase.crud.route

import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.services.route.CreateRouteUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CreateRouteUseCaseTest {
    private val repository = mockk<RouteRepository>()
    private val useCase = CreateRouteUseCase(repository)

    private val warehouse = Warehouse("WH-123", "Main Hub", "WEST", 31.5, 34.5)
    private val route = Route(
        id = "RT-12345",
        origin = warehouse,
        destination = warehouse,
        distanceKm = 100.0,
        typicalDelayMin = 15
    )

    @Test
    fun `repository success returns successful result`() = runBlocking {
        // Given
        coEvery { repository.createRoute(route) } returns route

        // When
        val result = useCase(route)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(route, result.getOrNull())
        coVerify(exactly = 1) { repository.createRoute(route) }
    }

    @Test
    fun `repository failure returns failed result`() = runBlocking {
        // Given
        val exception = RuntimeException("Create failed")
        coEvery { repository.createRoute(route) } throws exception

        // When
        val result = useCase(route)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 1) { repository.createRoute(route) }
    }
}
