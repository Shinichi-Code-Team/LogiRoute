package domain.usecase

import com.example.logiroute.domain.algorithm.routing.BfsRouter
import com.example.logiroute.domain.algorithm.routing.DijkstraRouter
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.model.request.ShipmentGroupRequest
import com.example.logiroute.domain.model.request.ShipmentService
import com.example.logiroute.domain.usecase.SelectShipmentRouteUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SelectShipmentRouteUseCaseTest {

    private val distanceRouter = mockk<DijkstraRouter>()
    private val delayRouter = mockk<DijkstraRouter>()
    private val bfsRouter = mockk<BfsRouter>()

    private val useCase = SelectShipmentRouteUseCase(
        distanceRouter = distanceRouter,
        delayRouter = delayRouter,
        bfsRouter = bfsRouter
    )

    @Test
    fun `eco service uses distance router`() = runTest {
        val (origin, destination) = warehouses()
        val expectedPath = listOf(origin, destination)

        coEvery {
            distanceRouter.findRoute(origin, destination)
        } returns expectedPath

        val result = useCase(request(origin, destination, ShipmentService.ECO))

        assertEquals(expectedPath, result.path)
        assertEquals("MIN_DISTANCE", result.routingObjective)
        coVerify(exactly = 1) { distanceRouter.findRoute(origin, destination) }
        coVerify(exactly = 0) { delayRouter.findRoute(any(), any()) }
        coVerify(exactly = 0) { bfsRouter.findRoute(any(), any()) }
    }

    @Test
    fun `express service uses delay router`() = runTest {
        val (origin, destination) = warehouses()
        val expectedPath = listOf(origin, destination)

        coEvery {
            delayRouter.findRoute(origin, destination)
        } returns expectedPath

        val result = useCase(request(origin, destination, ShipmentService.EXPRESS))

        assertEquals(expectedPath, result.path)
        assertEquals("MIN_EXPECTED_DELAY", result.routingObjective)
        coVerify(exactly = 1) { delayRouter.findRoute(origin, destination) }
    }

    @Test
    fun `fragile service uses fewest hops router`() = runTest {
        val (origin, destination) = warehouses()
        val expectedPath = listOf(origin, destination)

        coEvery {
            bfsRouter.findRoute(origin, destination)
        } returns expectedPath

        val result = useCase(request(origin, destination, ShipmentService.FRAGILE))

        assertEquals(expectedPath, result.path)
        assertEquals("MIN_HOPS", result.routingObjective)
        coVerify(exactly = 1) { bfsRouter.findRoute(origin, destination) }
    }

    @Test
    fun `throws when selected router cannot find a route`() = runTest {
        val (origin, destination) = warehouses()

        coEvery {
            distanceRouter.findRoute(origin, destination)
        } returns emptyList()

        assertFailsWith<LogisticsException.RouteNotFoundException> {
            useCase(request(origin, destination, ShipmentService.ECO))
        }
    }

    private fun request(
        origin: Warehouse,
        destination: Warehouse,
        service: ShipmentService
    ) = ShipmentGroupRequest(
        packages = emptyList(),
        origin = origin,
        destination = destination,
        service = service
    )

    private fun warehouses(): Pair<Warehouse, Warehouse> = Pair(
        warehouse("WH-123"),
        warehouse("WH-456")
    )

    private fun warehouse(id: String) = Warehouse(
        id = id,
        name = "Test Warehouse",
        regionalZone = "WEST",
        latitude = 31.5,
        longitude = 34.5
    )
}