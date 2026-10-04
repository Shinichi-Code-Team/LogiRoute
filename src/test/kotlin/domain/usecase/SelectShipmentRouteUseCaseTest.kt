package domain.usecase

import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.model.request.ShipmentGroupRequest
import com.example.logiroute.domain.model.request.ShipmentService
import com.example.logiroute.domain.usecase.FindFewestHopsRouteUseCase
import com.example.logiroute.domain.usecase.FindOptimalPathUseCase
import com.example.logiroute.domain.usecase.SelectShipmentRouteUseCase
import io.mockk.coEvery
import io.mockk.coVerify
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
    fun `eco service uses distance path use case`() = runTest {
        // Given
        val expectedPath = listOf(origin, destination)

        coEvery {
            distancePathUseCase(origin, destination)
        } returns expectedPath

        // When
        val result = useCase(request(ShipmentService.ECO))

        // Then
        assertEquals(expectedPath, result.path)
        assertEquals("MIN_DISTANCE", result.routingObjective)

        coVerify(exactly = 1) {
            distancePathUseCase(origin, destination)
        }
        coVerify(exactly = 0) {
            delayPathUseCase(any(), any())
        }
        coVerify(exactly = 0) {
            fewestHopsRouteUseCase(any(), any())
        }
    }

    @Test
    fun `express service uses delay path use case`() = runTest {
        // Given
        val expectedPath = listOf(origin, destination)

        coEvery {
            delayPathUseCase(origin, destination)
        } returns expectedPath

        // When
        val result = useCase(request(ShipmentService.EXPRESS))

        // Then
        assertEquals(expectedPath, result.path)
        assertEquals("MIN_EXPECTED_DELAY", result.routingObjective)

        coVerify(exactly = 1) {
            delayPathUseCase(origin, destination)
        }
        coVerify(exactly = 0) {
            distancePathUseCase(any(), any())
        }
        coVerify(exactly = 0) {
            fewestHopsRouteUseCase(any(), any())
        }
    }

    @Test
    fun `fragile service uses fewest hops use case`() = runTest {
        // Given
        val expectedPath = listOf(origin, destination)

        coEvery {
            fewestHopsRouteUseCase(origin, destination)
        } returns expectedPath

        // When
        val result = useCase(request(ShipmentService.FRAGILE))

        // Then
        assertEquals(expectedPath, result.path)
        assertEquals("MIN_HOPS", result.routingObjective)

        coVerify(exactly = 1) {
            fewestHopsRouteUseCase(origin, destination)
        }
        coVerify(exactly = 0) {
            distancePathUseCase(any(), any())
        }
        coVerify(exactly = 0) {
            delayPathUseCase(any(), any())
        }
    }

    @Test
    fun `throws when selected use case cannot find a route`() = runTest {
        // Given
        coEvery {
            distancePathUseCase(origin, destination)
        } returns emptyList()

        // When / Then
        assertFailsWith<LogisticsException.RouteNotFoundException> {
            useCase(request(ShipmentService.ECO))
        }
    }

    private fun request(
        service: ShipmentService
    ) = ShipmentGroupRequest(
        packages = emptyList(),
        origin = origin,
        destination = destination,
        service = service
    )
}