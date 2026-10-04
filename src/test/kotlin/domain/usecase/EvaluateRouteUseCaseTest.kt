package domain.usecase

import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.usecase.EvaluateRouteUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EvaluateRouteUseCaseTest {

    private val routeRepository = mockk<RouteRepository>()
    private val useCase = EvaluateRouteUseCase(routeRepository)

    @Test
    fun `should sum the distance of every route segment`() = runTest {
        // Given
        val first = warehouse("WH-123")
        val second = warehouse("WH-456")
        val third = warehouse("WH-789")

        coEvery { routeRepository.getAllRoutes() } returns listOf(
            route("RT-00001", first, second, 10.0),
            route("RT-00002", second, third, 20.0)
        )

        // When
        val result = useCase(listOf(first, second, third))

        // Then
        assertEquals(30.0, result)
    }

    @Test
    fun `should return zero when the path has fewer than two warehouses`() = runTest {
        // Given
        val path = listOf(warehouse("WH-123"))

        // When
        val result = useCase(path)

        // Then
        assertEquals(0.0, result)
    }

    @Test
    fun `should throw when a route segment does not exist`() = runTest {
        // Given
        val first = warehouse("WH-123")
        val second = warehouse("WH-456")
        coEvery { routeRepository.getAllRoutes() } returns emptyList()

        // When / Then
        assertFailsWith<LogisticsException.RouteSegmentNotFoundException> {
            useCase(listOf(first, second))
        }
    }

    private fun warehouse(id: String) = Warehouse(
        id = id,
        name = "Test Warehouse",
        regionalZone = "WEST",
        latitude = 31.5,
        longitude = 34.5
    )

    private fun route(
        id: String,
        origin: Warehouse,
        destination: Warehouse,
        distanceKm: Double
    ) = Route(
        id = id,
        origin = origin,
        destination = destination,
        distanceKm = distanceKm,
        typicalDelayMin = 10
    )
}