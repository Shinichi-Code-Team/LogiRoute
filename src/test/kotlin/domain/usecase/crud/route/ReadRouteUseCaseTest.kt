package domain.usecase.crud.route

import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.services.route.ReadRouteUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReadRouteUseCaseTest {
    private val repository = mockk<RouteRepository>()
    private val useCase = ReadRouteUseCase(repository)

    private val id = "RT-12345"
    private val warehouse = Warehouse("WH-123", "Main Hub", "WEST", 31.5, 34.5)
    private val route = Route(
        id = id,
        origin = warehouse,
        destination = warehouse,
        distanceKm = 100.0,
        typicalDelayMin = 15
    )

    @Test
    fun `valid id returns route successfully`() = runBlocking {
        // Given
        coEvery { repository.getRouteById(id) } returns route

        // When
        val result = useCase(id)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(route, result.getOrNull())
        coVerify(exactly = 1) { repository.getRouteById(id) }
    }

    @Test
    fun `invalid id returns failed result without calling repository`() = runBlocking {
        // Given
        val invalidId = "wrong-id"

        // When
        val result = useCase(invalidId)

        // Then
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { repository.getRouteById(any()) }
    }

    @Test
    fun `repository failure returns failed result`() = runBlocking {
        // Given
        val exception = RuntimeException("Read failed")
        coEvery { repository.getRouteById(id) } throws exception

        // When
        val result = useCase(id)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
