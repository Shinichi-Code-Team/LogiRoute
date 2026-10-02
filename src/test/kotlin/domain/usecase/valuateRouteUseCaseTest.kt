package domain.usecase

import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.usecase.EvaluateRouteUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EvaluateRouteUseCaseTest {

    private val routeRepository = mockk<RouteRepository>()
    private val useCase = EvaluateRouteUseCase(routeRepository)

    @Test
    fun `sums distances of all route segments`() = runTest {
        val first = warehouse("WH-123")
        val second = warehouse("WH-456")
        val third = warehouse("WH-789")

        coEvery { routeRepository.getAllRoutes() } returns listOf(
            route("RT-00001", first, second, 10.0),
            route("RT-00002", second, third, 20.0)
        )
        val result = useCase(listOf(first, second, third))
        assertEquals(30.0, result)
    }

    @Test
    fun `returns zero for a path with fewer than two warehouses`() = runTest {
        val result = useCase(listOf(warehouse("WH-123")))
        assertEquals(0.0, result)
        coVerify(exactly = 0) { routeRepository.getAllRoutes() }
    }

    @Test
    fun `throws when a route segment does not exist`() = runTest {
        val first = warehouse("WH-123")
        val second = warehouse("WH-456")
        coEvery { routeRepository.getAllRoutes() } returns emptyList()
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