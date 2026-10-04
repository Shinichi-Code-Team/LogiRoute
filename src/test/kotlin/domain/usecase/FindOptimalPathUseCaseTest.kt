package domain.usecase

import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.repository.WarehouseRepository
import com.example.logiroute.domain.usecase.FindOptimalPathUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FindOptimalPathUseCaseTest {

    private val warehouseRepository: WarehouseRepository = mockk()
    private val routeRepository: RouteRepository = mockk()

    private val useCase = FindOptimalPathUseCase(
        warehousesRepository = warehouseRepository,
        routeRepository = routeRepository,
        routeWeight = { route -> route.distanceKm }
    )

    private val warehouseA =
        Warehouse("WH-001", "Central Hub", "NORTH", 31.5, 34.5)

    private val warehouseB =
        Warehouse("WH-002", "East Hub", "EAST", 31.6, 34.6)

    private val warehouseC =
        Warehouse("WH-003", "South Hub", "SOUTH", 31.4, 34.4)

    private fun givenGraph(
        routes: List<Route>,
        warehouses: List<Warehouse> =
            listOf(warehouseA, warehouseB, warehouseC)
    ) {
        coEvery {
            warehouseRepository.getAllWarehouses()
        } returns warehouses

        coEvery {
            routeRepository.getAllRoutes()
        } returns routes
    }

    @Test
    fun `should choose cheaper indirect path over direct path`() = runTest {
        givenGraph(
            routes = listOf(
                Route("RT-00001", warehouseA, warehouseC, 10.0, 1),
                Route("RT-00002", warehouseA, warehouseB, 2.0, 4),
                Route("RT-00003", warehouseB, warehouseC, 3.0, 4)
            )
        )

        val path = useCase(warehouseA, warehouseC)

        assertEquals(
            listOf(warehouseA, warehouseB, warehouseC),
            path
        )
    }

    @Test
    fun `should return empty path when destination is unreachable`() = runTest {
        givenGraph(
            routes = listOf(
                Route("RT-00001", warehouseA, warehouseB, 2.0, 4)
            )
        )

        val path = useCase(warehouseA, warehouseC)

        assertTrue(path.isEmpty())
    }

    @Test
    fun `should return source when source equals destination`() = runTest {
        givenGraph(
            routes = emptyList(),
            warehouses = listOf(warehouseA)
        )

        val path = useCase(warehouseA, warehouseA)

        assertEquals(listOf(warehouseA), path)
    }

    @Test
    fun `should choose path by delay when delay is the weight`() = runTest {
        givenGraph(
            routes = listOf(
                Route("RT-00001", warehouseA, warehouseC, 10.0, 1),
                Route("RT-00002", warehouseA, warehouseB, 2.0, 4),
                Route("RT-00003", warehouseB, warehouseC, 3.0, 4)
            )
        )

        val delayUseCase = FindOptimalPathUseCase(
            warehousesRepository = warehouseRepository,
            routeRepository = routeRepository,
            routeWeight = { route ->
                route.typicalDelayMin.toDouble()
            }
        )

        val path = delayUseCase(warehouseA, warehouseC)

        assertEquals(listOf(warehouseA, warehouseC), path)
    }

    @Test
    fun `should find optimal path when graph contains a cycle`() = runTest {
        givenGraph(
            routes = listOf(
                Route("RT-00001", warehouseA, warehouseB, 2.0, 1),
                Route("RT-00002", warehouseB, warehouseA, 2.0, 1),
                Route("RT-00003", warehouseB, warehouseC, 3.0, 1)
            )
        )

        val path = useCase(warehouseA, warehouseC)

        assertEquals(
            listOf(warehouseA, warehouseB, warehouseC),
            path
        )
    }

    @Test
    fun `should load routes when warehouses have no outgoing routes`() = runTest {
        val routeOrigin = warehouseA.copy(name = "Remote Central Hub")
        val routeDestination = warehouseB.copy(name = "Remote East Hub")

        givenGraph(
            routes = listOf(
                Route(
                    "RT-00001",
                    routeOrigin,
                    routeDestination,
                    2.0,
                    1
                )
            ),
            warehouses = listOf(warehouseA, warehouseB)
        )

        assertTrue(warehouseA.outgoingRoutes.isEmpty())
        assertTrue(warehouseB.outgoingRoutes.isEmpty())

        val path = useCase(warehouseA, warehouseB)

        assertEquals(listOf(warehouseA, warehouseB), path)
    }

    @Test
    fun `should not traverse a directed route backwards`() = runTest {
        givenGraph(
            routes = listOf(
                Route("RT-00001", warehouseA, warehouseB, 2.0, 1)
            )
        )

        val path = useCase(warehouseB, warehouseA)

        assertTrue(path.isEmpty())
    }
}