package domain.usecase

import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.repository.WarehouseRepository
import com.example.logiroute.domain.usecase.FindFewestHopsBidirectionalRouteUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FindFewestHopsBidirectionalRouteUseCaseTest {

    private val warehouseRepository: WarehouseRepository = mockk()
    private val routeRepository: RouteRepository = mockk()

    private val useCase = FindFewestHopsBidirectionalRouteUseCase(
        warehouseRepository = warehouseRepository,
        routeRepository = routeRepository
    )

    private val warehouseA =
        Warehouse("WH-001", "Central Hub", "NORTH", 31.5, 34.5)

    private val warehouseB =
        Warehouse("WH-002", "East Hub", "EAST", 31.6, 34.6)

    private val warehouseC =
        Warehouse("WH-003", "South Hub", "SOUTH", 31.4, 34.4)

    private val warehouseD =
        Warehouse("WH-004", "West Hub", "WEST", 31.3, 34.3)

    private val warehouseE =
        Warehouse("WH-005", "Remote Hub", "NORTH", 31.7, 34.7)

    private fun givenGraph(
        routes: List<Route>,
        warehouses: List<Warehouse> =
            listOf(
                warehouseA,
                warehouseB,
                warehouseC,
                warehouseD,
                warehouseE
            )
    ) {
        coEvery {
            warehouseRepository.getAllWarehouses()
        } returns warehouses

        coEvery {
            routeRepository.getAllRoutes()
        } returns routes
    }

    @Test
    fun `should choose fewer hops even when direct route is more expensive`() =
        runTest {
            givenGraph(
                routes = listOf(
                    Route("RT-00001", warehouseA, warehouseB, 2.0, 1),
                    Route("RT-00002", warehouseB, warehouseC, 2.0, 1),
                    Route("RT-00003", warehouseA, warehouseC, 20.0, 10)
                )
            )

            val path = useCase(warehouseA, warehouseC)

            assertEquals(listOf(warehouseA, warehouseC), path)
        }

    @Test
    fun `should reconstruct path without duplicating meeting point`() =
        runTest {
            givenGraph(
                routes = listOf(
                    Route("RT-00001", warehouseA, warehouseB, 2.0, 1),
                    Route("RT-00002", warehouseB, warehouseC, 2.0, 1),
                    Route("RT-00003", warehouseC, warehouseD, 2.0, 1)
                )
            )

            val path = useCase(warehouseA, warehouseD)

            assertEquals(
                listOf(warehouseA, warehouseB, warehouseC, warehouseD),
                path
            )
        }

    @Test
    fun `should choose shorter path when longer branch appears first`() =
        runTest {
            givenGraph(
                routes = listOf(
                    Route("RT-00001", warehouseA, warehouseB, 1.0, 1),
                    Route("RT-00002", warehouseB, warehouseC, 1.0, 1),
                    Route("RT-00003", warehouseC, warehouseE, 1.0, 1),
                    Route("RT-00004", warehouseA, warehouseD, 10.0, 1),
                    Route("RT-00005", warehouseD, warehouseE, 10.0, 1)
                )
            )

            val path = useCase(warehouseA, warehouseE)

            assertEquals(
                listOf(warehouseA, warehouseD, warehouseE),
                path
            )
        }

    @Test
    fun `should return empty path when destination is unreachable`() = runTest {
        givenGraph(
            routes = listOf(
                Route("RT-00001", warehouseA, warehouseB, 2.0, 1),
                Route("RT-00002", warehouseC, warehouseD, 2.0, 1)
            )
        )

        val path = useCase(warehouseA, warehouseD)

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
    fun `should find path when graph contains a cycle`() = runTest {
        givenGraph(
            routes = listOf(
                Route("RT-00001", warehouseA, warehouseB, 2.0, 1),
                Route("RT-00002", warehouseB, warehouseA, 2.0, 1),
                Route("RT-00003", warehouseB, warehouseC, 2.0, 1),
                Route("RT-00004", warehouseC, warehouseD, 2.0, 1)
            )
        )

        val path = useCase(warehouseA, warehouseD)

        assertEquals(
            listOf(warehouseA, warehouseB, warehouseC, warehouseD),
            path
        )
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

    @Test
    fun `should return empty path when source is missing`() = runTest {
        givenGraph(
            routes = emptyList(),
            warehouses = listOf(warehouseB, warehouseC)
        )

        val path = useCase(warehouseA, warehouseC)

        assertTrue(path.isEmpty())
    }

    @Test
    fun `should return empty path when destination is missing`() = runTest {
        givenGraph(
            routes = emptyList(),
            warehouses = listOf(warehouseA, warehouseB)
        )

        val path = useCase(warehouseA, warehouseC)

        assertTrue(path.isEmpty())
    }

    @Test
    fun `should load routes when warehouses have no outgoing routes`() =
        runTest {
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
}