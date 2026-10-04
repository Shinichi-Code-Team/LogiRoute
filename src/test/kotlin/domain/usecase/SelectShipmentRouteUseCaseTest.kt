package domain.usecase

import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.model.request.ShipmentGroupRequest
import com.example.logiroute.domain.model.request.ShipmentService
import com.example.logiroute.domain.model.result.ShipmentRouteResult
import com.example.logiroute.domain.usecase.FindFewestHopsRouteUseCase
import com.example.logiroute.domain.usecase.FindOptimalPathUseCase
import com.example.logiroute.domain.usecase.SelectShipmentRouteUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SelectShipmentRouteUseCaseTest {

    private val distancePathUseCase = mockk<FindOptimalPathUseCase>()
    private val delayPathUseCase = mockk<FindOptimalPathUseCase>()
    private val fewestHopsRouteUseCase = mockk<FindFewestHopsRouteUseCase>()

    private val useCase = SelectShipmentRouteUseCase(
        distancePathUseCase = distancePathUseCase,
        delayPathUseCase = delayPathUseCase,
        fewestHopsRouteUseCase = fewestHopsRouteUseCase
    )

    private val origin =
        Warehouse("WH-123", "Origin Hub", "WEST", 31.5, 34.5)

    private val destination =
        Warehouse("WH-456", "Destination Hub", "EAST", 31.6, 34.6)

    @Test
    fun `should choose the distance route for an eco shipment`() = runTest {
        // Given
        val expectedPath = listOf(origin, destination)
        coEvery {
            distancePathUseCase(origin, destination)
        } returns expectedPath

        // When
        val result = useCase(request(ShipmentService.ECO))

        // Then
        assertEquals(
            ShipmentRouteResult(expectedPath, "MIN_DISTANCE"),
            result
        )
    }

    @Test
    fun `should choose the delay route for an express shipment`() = runTest {
        // Given
        val expectedPath = listOf(origin, destination)
        coEvery {
            delayPathUseCase(origin, destination)
        } returns expectedPath

        // When
        val result = useCase(request(ShipmentService.EXPRESS))

        // Then
        assertEquals(
            ShipmentRouteResult(expectedPath, "MIN_EXPECTED_DELAY"),
            result
        )
    }

    @Test
    fun `should choose the fewest hops route for a fragile shipment`() = runTest {
        // Given
        val expectedPath = listOf(origin, destination)
        coEvery {
            fewestHopsRouteUseCase(origin, destination)
        } returns expectedPath

        // When
        val result = useCase(request(ShipmentService.FRAGILE))

        // Then
        assertEquals(
            ShipmentRouteResult(expectedPath, "MIN_HOPS"),
            result
        )
    }

    @Test
    fun `should throw when the selected routing strategy returns no route`() = runTest {
        // Given
        coEvery {
            distancePathUseCase(origin, destination)
        } returns emptyList()

        // When / Then
        assertFailsWith<LogisticsException.RouteNotFoundException> {
            useCase(request(ShipmentService.ECO))
        }
    }

    private fun request(service: ShipmentService) =
        ShipmentGroupRequest(
            packages = emptyList(),
            origin = origin,
            destination = destination,
            service = service
        )
}