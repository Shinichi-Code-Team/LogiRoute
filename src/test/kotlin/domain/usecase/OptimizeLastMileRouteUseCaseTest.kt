package domain.usecase

import com.example.logiroute.domain.model.LastMileWaypoint
import com.example.logiroute.domain.model.request.OptimizeLastMileRouteRequest
import com.example.logiroute.domain.usecase.OptimizeLastMileRouteUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class OptimizeLastMileRouteUseCaseTest {

    private val useCase = OptimizeLastMileRouteUseCase()
    private val origin = waypoint(
        id = "WH-ORIGIN",
        longitude = 0.0
    )

    @Test
    fun `should keep the origin as the first and last waypoint`() {
        val result = useCase(requestWith(stopsInShuffledOrder()))

        assertEquals(
            listOf(origin.id, origin.id),
            listOf(result.route.first().id, result.route.last().id)
        )
    }

    @Test
    fun `should reduce route distance for an unordered delivery route`() {
        val result = useCase(requestWith(stopsInShuffledOrder()))

        assertTrue(result.distanceAfterKm < result.distanceBeforeKm)
    }

    @Test
    fun `should include every delivery stop exactly once`() {
        val stops = stopsInShuffledOrder()
        val result = useCase(requestWith(stops))

        assertEquals(
            stops.map(LastMileWaypoint::id).sorted(),
            result.route.drop(1)
                .dropLast(1)
                .map(LastMileWaypoint::id)
                .sorted()
        )
    }

    @Test
    fun `should report zero distance saved for an already optimized route`() {
        val result = useCase(requestWith(stopsInOrder()))

        assertEquals(
            result.distanceBeforeKm,
            result.distanceAfterKm
        )
    }

    @Test
    fun `should reject a route with fewer than fifteen delivery stops`() {
        val request = requestWith(stopsInOrder().take(14))

        assertFailsWith<IllegalArgumentException> {
            useCase(request)
        }
    }

    @Test
    fun `should reject a delivery stop that uses the origin ID`() {
        val stopsWithOriginId = stopsInOrder()
            .drop(1)
            .plus(
                waypoint(
                    id = origin.id,
                    longitude = 0.16
                )
            )

        val request = requestWith(stopsWithOriginId)

        assertFailsWith<IllegalArgumentException> {
            useCase(request)
        }
    }

    private fun requestWith(
        stops: List<LastMileWaypoint>
    ) = OptimizeLastMileRouteRequest(
        origin = origin,
        deliveryStops = stops
    )

    private fun stopsInOrder(): List<LastMileWaypoint> =
        (1..15)
            .map { number ->
                waypoint(
                    id = "STOP-${number.toString().padStart(2, '0')}",
                    longitude = number * 0.01
                )
            }

    private fun stopsInShuffledOrder(): List<LastMileWaypoint> =
        listOf(8, 1, 12, 3, 10, 5, 14, 2, 9, 4, 13, 6, 11, 7, 15)
            .map { number ->
                waypoint(
                    id = "STOP-${number.toString().padStart(2, '0')}",
                    longitude = number * 0.01
                )
            }

    private fun waypoint(
        id: String,
        longitude: Double
    ) = LastMileWaypoint(
        id = id,
        latitude = 0.0,
        longitude = longitude
    )
}